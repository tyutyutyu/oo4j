package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.OracleTypeField;
import com.tyutyutyu.oo4j.core.OracleTypeFieldRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class MetadataQuery {

    private static final String OWNER_KEY = "OWNER";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> queryTypes(String schema, TypeCode typeCode) {

        log.debug("queryType - schema: {}, typeCode: {}", schema, typeCode);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema,
                "typeCode", typeCode.name()
        );

        return jdbcTemplate.queryForList(
                "SELECT TYPE_NAME FROM ALL_TYPES WHERE OWNER = :owner AND TYPECODE = :typeCode",
                new MapSqlParameterSource(parameters),
                String.class
        );
    }

    public List<OracleTypeField> queryTypeFields(String schema, String typeName) {

        log.debug("queryTypeFields - schema: {}, typeName: {}", schema, typeName);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema,
                "typeName", typeName
        );

        return jdbcTemplate.query(
                "SELECT ATTR_NAME, ATTR_TYPE_NAME FROM ALL_TYPE_ATTRS WHERE OWNER = :owner AND TYPE_NAME = :typeName ORDER BY ATTR_NO",
                new MapSqlParameterSource(parameters),
                new OracleTypeFieldRowMapper()
        );
    }

    public List<OracleProcedure> queryProcedures(String schema) {

        log.debug("queryProcedures - schema: {}", schema);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema
        );

        return jdbcTemplate.query(
                "SELECT OBJECT_NAME, PROCEDURE_NAME, OBJECT_TYPE FROM all_procedures WHERE OWNER = :owner",
                new MapSqlParameterSource(parameters),
                new OracleProcedureRowMapper(schema)
        );
    }

    public List<OracleProcedureField> queryProcedureFields(String schema, String packageName, String objectName) {

        log.debug("queryProcedureFields - schema: {}, packageName: {}, objectName: {}", schema, packageName, objectName);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(OWNER_KEY, schema);
        parameters.put("packageName", packageName);
        parameters.put("objectName", objectName);
        parameters = Collections.unmodifiableMap(parameters);

        return jdbcTemplate.query(
                "SELECT ARGUMENT_NAME, DATA_TYPE, IN_OUT, TYPE_NAME FROM all_ARGUMENTS " +
                        "WHERE OWNER = :owner " +
                        (Objects.isNull(packageName) ? "AND PACKAGE_NAME IS NULL " : "AND PACKAGE_NAME = :packageName ") +
                        "AND OBJECT_NAME = :objectName " +
                        "AND ARGUMENT_NAME IS NOT NULL " +
                        "ORDER BY POSITION",
                new MapSqlParameterSource(parameters),
                new OracleProcedureFieldRowMapper());
    }

    public enum TypeCode {
        OBJECT
    }
}
