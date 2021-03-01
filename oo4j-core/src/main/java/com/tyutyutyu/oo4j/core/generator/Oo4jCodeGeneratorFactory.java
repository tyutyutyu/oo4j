package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.DefaultNamingStrategy;
import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.result.FileSourceWriter;
import com.tyutyutyu.oo4j.core.template.FreemarkerApi;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import oracle.jdbc.driver.OracleDriver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;

@UtilityClass
public class Oo4jCodeGeneratorFactory {

    @SneakyThrows
    public static Oo4jCodeGenerator create(
            String url,
            String username,
            String password,
            String basePackage,
            Path targetPath
    ) {

        DataSource dataSource = new SimpleDriverDataSource(new OracleDriver(), url, username, password);
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        FileSourceWriter sourceWriter = new FileSourceWriter(
                new FreemarkerApi("/templates/", true),
                targetPath,
                true
        );

        return new Oo4jCodeGenerator(
                basePackage,
                dataSource,
                namingStrategy,
                sourceWriter
        );
    }

}
