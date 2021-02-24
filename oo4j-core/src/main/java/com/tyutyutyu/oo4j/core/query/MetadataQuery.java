package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.NoPrivilegeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class MetadataQuery {

    public enum TypeCode {
        OBJECT, COLLECTION
    }

    private static final String OWNER_KEY = "owner";
    private static final String TYPE_NAME_KEY = "typeName";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> queryTypeNames(String schema, TypeCode typeCode) {

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
                TYPE_NAME_KEY, typeName
        );

        return jdbcTemplate.query(
                "SELECT ATTR_NAME, ATTR_TYPE_NAME FROM ALL_TYPE_ATTRS WHERE OWNER = :owner AND TYPE_NAME = :typeName ORDER BY ATTR_NO",
                new MapSqlParameterSource(parameters),
                new ColumnMapRowMapper()
        )
                .stream()
                .map(mapToTypeField(schema))
                .collect(Collectors.toUnmodifiableList());
    }


    public OracleTableType queryTableType(String schema, String typeName) {

        log.debug("queryTableType - schema: {}, typeName: {}", schema, typeName);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema,
                TYPE_NAME_KEY, typeName
        );

        try {
            String elementTypeName = jdbcTemplate.queryForObject(
                    "SELECT ELEM_TYPE_NAME FROM ALL_COLL_TYPES WHERE OWNER = :owner AND TYPE_NAME = :typeName",
                    new MapSqlParameterSource(parameters),
                    String.class
            );

            return new OracleTableType(schema, typeName, elementTypeName);
        } catch (EmptyResultDataAccessException e) {
            throw new NoPrivilegeException(String.format("The database user has no privilege to query information about %s.%s from ALL_COLL_TYPES.", schema, typeName), e);
        }
    }

    public List<OracleProcedure> queryProcedures(String schema) {

        log.debug("queryProcedures - schema: {}", schema);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema
        );

        return jdbcTemplate.query(
                "SELECT OBJECT_NAME, PROCEDURE_NAME, OBJECT_TYPE, SUBPROGRAM_ID, OVERLOAD FROM ALL_PROCEDURES WHERE " +
                        "((OBJECT_TYPE = 'PACKAGE' AND PROCEDURE_NAME IS NOT NULL) OR OBJECT_TYPE != 'PACKAGE') " +
                        "AND OWNER = :owner " +
                        "ORDER BY OBJECT_TYPE DESC, OBJECT_NAME, SUBPROGRAM_ID",
                new MapSqlParameterSource(parameters),
                new OracleProcedureRowMapper(schema)
        );
    }

    public List<OracleProcedureField> queryProcedureFields(OracleProcedure oracleProcedure) {

        log.debug("queryProcedureFields - oracleProcedure: {}", oracleProcedure);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(OWNER_KEY, oracleProcedure.getSchema());
        parameters.put("packageName", oracleProcedure.getObjectName());
        parameters.put("objectName", oracleProcedure.getProcedureName());
        parameters.put("overload", oracleProcedure.getOverload());
        parameters = Collections.unmodifiableMap(parameters);

        return jdbcTemplate.query(
                "SELECT ARGUMENT_NAME, DATA_TYPE, IN_OUT, TYPE_NAME FROM ALL_ARGUMENTS " +
                        "WHERE OWNER = :owner " +
                        (Objects.isNull(oracleProcedure.getObjectName()) ? "AND PACKAGE_NAME IS NULL " : "AND PACKAGE_NAME = :packageName ") +
                        "AND OBJECT_NAME = :objectName " +
                        "AND ARGUMENT_NAME IS NOT NULL " +
                        (oracleProcedure.getOverload() != null ? "AND OVERLOAD = :overload " : "") +
                        "ORDER BY POSITION",
                new MapSqlParameterSource(parameters),
                new ColumnMapRowMapper())
                .stream()
                .map(mapProcedureField(oracleProcedure.getSchema()))
                .collect(Collectors.toUnmodifiableList());
    }

    private Function<Map<String, Object>, OracleTypeField> mapToTypeField(String schema) {
        return map -> {
            OracleType type;
            String typeName = (String) map.get("ATTR_TYPE_NAME");
            if (OracleType.isBasicType(typeName)) {
                type = OracleBasicType.valueOf(typeName);
            } else {
                String typeCode = getTypeCode(schema, typeName);
                type = getType(schema, typeCode, typeName);
            }

            return new OracleTypeField(
                    (String) map.get("ATTR_NAME"),
                    type
            );
        };
    }

    private String getTypeCode(String schema, String typeName) {

        log.debug("getTypeCode - schema: {}, typeName: {}", schema, typeName);

        Map<String, ?> parameters = Map.of(
                OWNER_KEY, schema,
                TYPE_NAME_KEY, typeName
        );

        return jdbcTemplate.queryForObject(
                "SELECT TYPECODE FROM ALL_TYPES WHERE OWNER = :owner AND TYPE_NAME = :typeName",
                new MapSqlParameterSource(parameters),
                String.class
        );
    }


    private Function<Map<String, Object>, OracleProcedureField> mapProcedureField(String schema) {
        return map -> {
            OracleType type = getType(schema, (String) map.get("DATA_TYPE"), (String) map.get("TYPE_NAME"));

            return new OracleProcedureField(
                    (String) map.get("ARGUMENT_NAME"),
                    (String) map.get("IN_OUT"),
                    type
            );
        };
    }

    private OracleType getType(String schema, String dataType, String typeName) {
        Class<? extends OracleType> fieldType = OracleType.getTypeByDataType(dataType);
        OracleType type;
        if (fieldType == OracleBasicType.class) {
            type = OracleBasicType.valueOf(dataType);
        } else if (fieldType == OracleObjectType.class) {
            type = new OracleObjectType(
                    typeName,
                    queryTypeFields(schema, typeName)
            );
        } else if (fieldType == OracleTableType.class) {
            type = queryTableType(schema, typeName);
        } else if (fieldType == OracleCursorType.class) {
            type = new OracleCursorType();
        } else {
            throw new IllegalStateException();
        }
        return type;
    }

}
