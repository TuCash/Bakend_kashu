package com.kashu.tucash.shared.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        createDatabaseIfNotExists();
    }

    private void createDatabaseIfNotExists() {
        // Extraer el nombre de la base de datos de la URL
        // jdbc:postgresql://localhost:5434/tucash_db -> tucash_db
        String dbName = extractDatabaseName(databaseUrl);

        if (dbName == null) {
            LOGGER.warn("No se pudo extraer el nombre de la base de datos de la URL");
            return;
        }

        // Conectar a la base de datos 'postgres' (que siempre existe) para crear nuestra BD
        String postgresUrl = databaseUrl.substring(0, databaseUrl.lastIndexOf('/')) + "/postgres";

        try (Connection conn = DriverManager.getConnection(postgresUrl, username, password)) {
            LOGGER.info("Verificando si la base de datos '{}' existe...", dbName);

            // Verificar si la base de datos ya existe
            if (!databaseExists(conn, dbName)) {
                LOGGER.info("Base de datos '{}' no existe. Creándola...", dbName);
                createDatabase(conn, dbName);
                LOGGER.info("✅ Base de datos '{}' creada exitosamente", dbName);
            } else {
                LOGGER.info("✅ Base de datos '{}' ya existe", dbName);
            }
        } catch (SQLException e) {
            LOGGER.error("❌ Error al verificar/crear la base de datos: {}", e.getMessage());
            LOGGER.warn("Por favor, crea la base de datos manualmente con: CREATE DATABASE {};", dbName);
        }
    }

    private String extractDatabaseName(String url) {
        try {
            // jdbc:postgresql://localhost:5434/tucash_db -> tucash_db
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash != -1) {
                String dbPart = url.substring(lastSlash + 1);
                // Remover parámetros si existen (ej: ?useSSL=false)
                int questionMark = dbPart.indexOf('?');
                if (questionMark != -1) {
                    dbPart = dbPart.substring(0, questionMark);
                }
                return dbPart;
            }
        } catch (Exception e) {
            LOGGER.error("Error extrayendo nombre de BD: {}", e.getMessage());
        }
        return null;
    }

    private boolean databaseExists(Connection conn, String dbName) throws SQLException {
        String query = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            return rs.next();
        }
    }

    private void createDatabase(Connection conn, String dbName) throws SQLException {
        String createDbSQL = "CREATE DATABASE " + dbName;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createDbSQL);
        }
    }
}
