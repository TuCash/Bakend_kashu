package com.kashu.tucash.shared.infrastructure.configuration;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * Parses Heroku's DATABASE_URL env var and maps it to Spring's DataSourceProperties when present.
 */
@Configuration
public class HerokuDataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HerokuDataSourceConfiguration.class);

    private final DataSourceProperties dataSourceProperties;

    public HerokuDataSourceConfiguration(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @PostConstruct
    public void configureDataSourceFromHeroku() {
        if (dataSourceProperties.getUrl() != null && !dataSourceProperties.getUrl().isBlank()) {
            return;
        }

        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        try {
            URI dbUri = URI.create(databaseUrl.replace("postgres://", "postgresql://"));
            String[] userInfo = dbUri.getUserInfo().split(":");
            String username = userInfo[0];
            String password = userInfo.length > 1 ? userInfo[1] : "";
            String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

            dataSourceProperties.setUrl(jdbcUrl);
            dataSourceProperties.setUsername(username);
            dataSourceProperties.setPassword(password);
        } catch (Exception ex) {
            LOGGER.error("Unable to parse DATABASE_URL for Heroku deployment: {}", ex.getMessage());
        }
    }
}
