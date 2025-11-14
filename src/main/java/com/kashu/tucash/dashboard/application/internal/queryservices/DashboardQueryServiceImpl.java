package com.kashu.tucash.dashboard.application.internal.queryservices;

import com.kashu.tucash.dashboard.domain.services.DashboardQueryService;
import com.kashu.tucash.dashboard.interfaces.rest.resources.*;
import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.TransactionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public DashboardQueryServiceImpl(TransactionRepository transactionRepository,
                                    UserRepository userRepository,
                                    JdbcTemplate jdbcTemplate) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DashboardPulseResource getPulse(Long userId, LocalDate startDate, LocalDate endDate) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String sql = """
            SELECT
                COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as total_income,
                COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as total_expenses
            FROM transactions
            WHERE user_id = ? AND transaction_date BETWEEN ? AND ?
            """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId, startDate, endDate);

        BigDecimal totalIncome = (BigDecimal) result.get("total_income");
        BigDecimal totalExpenses = (BigDecimal) result.get("total_expenses");
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        BigDecimal savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? balance.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        String periodLabel = startDate.getMonth().name() + " " + startDate.getYear();

        return new DashboardPulseResource(
                user.getCurrency(),
                periodLabel,
                totalIncome,
                totalExpenses,
                balance,
                savingsRate.setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    public TrendSeriesResource getTrends(Long userId, int months) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<MonthlyTrendResource> series = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months - 1).withDayOfMonth(1);

        for (int i = 0; i < months; i++) {
            YearMonth yearMonth = YearMonth.from(startDate.plusMonths(i));
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();

            String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as income,
                    COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as expenses
                FROM transactions
                WHERE user_id = ? AND transaction_date BETWEEN ? AND ?
                """;

            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId, monthStart, monthEnd);

            BigDecimal income = (BigDecimal) result.get("income");
            BigDecimal expenses = (BigDecimal) result.get("expenses");
            BigDecimal balance = income.subtract(expenses);

            String monthLabel = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            series.add(new MonthlyTrendResource(monthLabel, income, expenses, balance));
        }

        return new TrendSeriesResource(user.getCurrency(), series);
    }

    @Override
    public CategoryLeaksResource getLeaks(Long userId, LocalDate startDate, LocalDate endDate, int top) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String sql = """
            SELECT
                c.id as category_id,
                c.name as category_name,
                c.icon as category_icon,
                c.color as category_color,
                SUM(t.amount) as total_amount
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.user_id = ?
              AND t.type = 'EXPENSE'
              AND t.transaction_date BETWEEN ? AND ?
            GROUP BY c.id, c.name, c.icon, c.color
            ORDER BY total_amount DESC
            LIMIT ?
            """;

        List<CategoryLeakResource> leaks = jdbcTemplate.query(sql,
                (rs, rowNum) -> new CategoryLeakResource(
                        rs.getLong("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_icon"),
                        rs.getBigDecimal("total_amount"),
                        BigDecimal.ZERO, // Calculado después
                        rs.getString("category_color")
                ),
                userId, startDate, endDate, top);

        // Calcular total de gastos para porcentajes
        BigDecimal totalExpenses = leaks.stream()
                .map(CategoryLeakResource::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recalcular con porcentajes
        List<CategoryLeakResource> leaksWithPercentage = leaks.stream()
                .map(leak -> {
                    BigDecimal percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                            ? leak.amount().divide(totalExpenses, 4, RoundingMode.HALF_UP)
                                  .multiply(BigDecimal.valueOf(100))
                                  .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new CategoryLeakResource(
                            leak.categoryId(),
                            leak.categoryName(),
                            leak.categoryIcon(),
                            leak.amount(),
                            percentage,
                            leak.color()
                    );
                })
                .toList();

        String periodLabel = startDate.getMonth().name() + " " + startDate.getYear();

        return new CategoryLeaksResource(user.getCurrency(), periodLabel, leaksWithPercentage);
    }
}
