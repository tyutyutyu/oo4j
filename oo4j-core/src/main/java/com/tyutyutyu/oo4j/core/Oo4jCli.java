package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.generator.Oo4jCodeGenerator;
import com.tyutyutyu.oo4j.core.generator.Oo4jCodeGeneratorFactory;
import org.springframework.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Oo4jCli {

    public static void main(String[] args) {

        Command command = new Command();
        new CommandLine(command).execute(args);

        Oo4jCodeGenerator oo4jCodeGenerator = Oo4jCodeGeneratorFactory.create(
                command.databaseJdbcUrl,
                command.databaseJdbcUsername,
                command.databaseJdbcPassword,
                command.basePackage,
                Path.of(command.targetDir)
        );
        oo4jCodeGenerator.generate(
                command.schema,
                command.typeExcludes,
                command.procedureExcludes
        );
    }

    private static class Command implements Callable<Integer> {

        @Option(names = {"-j", "--database-jdbc-url"}, description = "Database JDBC url", required = true)
        String databaseJdbcUrl;

        @Option(names = {"-u", "--database-username"}, description = "Database username")
        String databaseJdbcUsername;

        @Option(names = {"-p", "--database-password"}, description = "Database password")
        String databaseJdbcPassword;

        @Option(names = {"-b", "--base-package"}, description = "Base package", defaultValue = "com.example.database")
        String basePackage;

        @Option(names = {"-o", "--target-directory"}, description = "Target directory")
        String targetDir;

        @Option(names = {"-s", "--schema"}, description = "Schema list (comma separated)", split = ",", required = true)
        Set<String> schema;

        @Option(names = {"-t", "--exclude-types"}, description = "Types to exclude (comma separated)", split = ",", defaultValue = "")
        Set<String> typeExcludes;

        @Option(names = {"-r", "--exclude-procedures"}, description = "Procedures to exclude (comma separated)", split = ",", defaultValue = "")
        Set<String> procedureExcludes;

        @Override
        public Integer call() {
            return 0;
        }
    }

}
