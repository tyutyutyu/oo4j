package com.tyutyutyu.oo4j.core;

import lombok.SneakyThrows;
import oracle.jdbc.driver.OracleDriver;
import org.junit.jupiter.api.extension.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.OracleContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

public class DatabaseIntegrationTestExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private OracleContainer oracle;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void beforeAll(ExtensionContext context) {

        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");

        oracle = new OracleContainer("oracleinanutshell/oracle-xe-11g")
                .withStartupTimeoutSeconds(900)
                .withConnectTimeoutSeconds(900);
        oracle.start();
        String url = oracle.getJdbcUrl();
        String user = oracle.getUsername();
        String pass = oracle.getPassword();
        loadTestData(url, user, pass);

        DataSource dataSource = new SimpleDriverDataSource(new OracleDriver(), url, "oo4j", "oo4j");
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        oracle.stop();
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .equals(NamedParameterJdbcTemplate.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return jdbcTemplate;
    }

    @SneakyThrows
    private static void loadTestData(String url, String user, String pass) {
        DataSource dataSource = new SimpleDriverDataSource(new OracleDriver(), url, user, pass);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        loadFile(jdbcTemplate, "01_init_schema.sql", true);
        loadFile(jdbcTemplate, "02_create_type.sql", true);
        loadFile(jdbcTemplate, "051_create_procedure.sql", false);
        loadFile(jdbcTemplate, "052_create_procedure.sql", false);
        loadFile(jdbcTemplate, "06_create_package_spec.sql", false);
        loadFile(jdbcTemplate, "07_create_package_body.sql", false);
    }

    private static void loadFile(JdbcTemplate jdbcTemplate, String file, boolean split) throws IOException {
        String sql = new String(new ClassPathResource(file).getInputStream().readAllBytes());
        if (split) {
            Arrays.stream(sql.split(";"))
                    .filter(StringUtils::hasText)
                    .forEach(jdbcTemplate::update);
        }else{
            jdbcTemplate.update(sql);
        }
    }

}
