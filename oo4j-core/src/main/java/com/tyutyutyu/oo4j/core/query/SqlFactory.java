package com.tyutyutyu.oo4j.core.query;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;

@UtilityClass
class SqlFactory {

    enum Sql {
        ARGUMENTS, PROCEDURES, TYPES
    }

    private static final EnumMap<Sql, String> CACHE = new EnumMap<>(Sql.class);

    static String sql(Sql sql) {
        return CACHE.computeIfAbsent(sql, SqlFactory::loadSql);
    }

    @SneakyThrows
    private static String loadSql(Sql sql) {
        try (InputStream is = new ClassPathResource("queries/" + sql.name().toLowerCase() + ".sql").getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
