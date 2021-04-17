package com.tyutyutyu.oo4j.core.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tyutyutyu.oo4j.core.query.OracleProcedure.Type.*;

@RequiredArgsConstructor
@Slf4j
public class MetadataQuery {

    private static final String OWNERS_KEY = "owners";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Map<String, OracleType> queryTypes(Collection<String> schemas, Collection<String> typeExcludes) {

        log.debug("queryType - schemas: {}", schemas);

        Map<String, ?> parameters = Map.of(
                OWNERS_KEY, schemas
        );

        List<AllTypesExtended> allTypesExtendedRows = jdbcTemplate.query(
                SqlFactory.sql(SqlFactory.Sql.TYPES),
                new MapSqlParameterSource(parameters),
                (rs, rowNum) -> new AllTypesExtended(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)
                )
        );

        allTypesExtendedRows = allTypesExtendedRows
                .stream()
                .filter(row -> !typeExcludes.contains(row.getOwner() + "." + row.getTypeName()))
                .collect(Collectors.toUnmodifiableList());

        Collection<List<AllTypesExtended>> temp = allTypesExtendedRows
                .stream()
                .collect(Collectors.groupingBy(r -> r.getOwner() + "." + r.getTypeName()))
                .values()
                .stream()
                .sorted((a, b) -> b.get(0).getTypeCode().compareTo(a.get(0).getTypeCode()))
                .collect(Collectors.toUnmodifiableList());

        Map<String, OracleType> result = new HashMap<>();
        for (List<AllTypesExtended> list : temp) {
            OracleType oracleType = finisher(result).apply(list);
            if (oracleType instanceof OracleComplexType) {
                result.put(((OracleComplexType) oracleType).getFullyQualifiedName(), oracleType);
            }
        }

        return result;
    }

    private Function<List<AllTypesExtended>, OracleType> finisher(Map<String, OracleType> accumulator) {
        return typesQueryResults -> {
            AllTypesExtended first = typesQueryResults.get(0);

            return getType(first.getOwner(), first.getTypeCode(), first.getTypeName(), typesQueryResults, accumulator);
        };
    }

    private OracleType getType(
            String schema,
            String typeCode,
            String typeName,
            List<AllTypesExtended> allTypesExtendedRows,
            Map<String, OracleType> accumulator
    ) {
        if (accumulator.containsKey(schema + "." + typeName)) {
            return accumulator.get(schema + "." + typeName);
        }

        Class<? extends OracleType> fieldType = OracleType.getTypeByDataType(typeCode, typeName);
        OracleType type;
        if (fieldType == OracleBasicType.class) {
            type = OracleBasicType.valueOf(typeName);
        } else if (fieldType == OracleObjectType.class) {
            type = new OracleObjectType(
                    schema,
                    typeName,
                    getObjectTypeAttributes(schema, typeName, allTypesExtendedRows, accumulator)
            );
        } else if (fieldType == OracleTableType.class) {
            type = createTableType(schema, typeName, allTypesExtendedRows, accumulator);
        } else if (fieldType == OracleCursorType.class) {
            type = new OracleCursorType();
        } else {
            throw new IllegalStateException();
        }
        return type;
    }

    private List<OracleTypeField> getObjectTypeAttributes(
            String schema,
            String typeName,
            List<AllTypesExtended> allTypesExtendedRows,
            Map<String, OracleType> accumulator
    ) {
        return allTypesExtendedRows
                .stream()
                .filter(allTypesExtended -> allTypesExtended.getOwner().equals(schema)
                        && allTypesExtended.getTypeName().equals(typeName))
                .map(allTypesExtended -> new OracleTypeField(
                        allTypesExtended.getAttrName(),
                        getType(
                                allTypesExtended.getOwner(),
                                allTypesExtended.getTypeCode(),
                                allTypesExtended.getAttrTypeName(),
                                allTypesExtendedRows,
                                accumulator)
                ))
                .collect(Collectors.toUnmodifiableList());
    }

    private OracleTableType createTableType(String schema, String typeName, List<AllTypesExtended> allTypesExtendedRows, Map<String, OracleType> accumulator) {

        log.debug("queryTableType - schema: {}, typeName: {}", schema, typeName);

        return allTypesExtendedRows
                .stream()
                .filter(allTypesExtended -> allTypesExtended.getOwner().equals(schema)
                        && allTypesExtended.getTypeName().equals(typeName))
                .findAny()
                .map(AllTypesExtended::getElemTypeName)
                .map(elemTypeName -> new OracleTableType(
                        schema,
                        typeName,
                        getTypeX(schema, elemTypeName, accumulator)
                ))
                .orElseThrow(() ->
                        new NoPrivilegeException(
                                String.format(
                                        "The database user has no privilege to query information about %s.%s type from ALL_COLL_TYPES.",
                                        schema, typeName
                                )
                        )
                );
    }

    private static OracleType getTypeX(String schema, String typeName, Map<String, OracleType> accumulator) {
        if (OracleType.isBasicType(typeName)) {
            return OracleBasicType.valueOf(typeName);
        }

        return accumulator.get(schema + "." + typeName);
    }

    public List<OracleProcedure> queryProcedures(Collection<String> schemas, Map<String, OracleType> typesMap) {

        log.debug("queryProcedures - schemas: {}", schemas);

        Map<String, ?> parameters = Map.of(
                OWNERS_KEY, schemas
        );

        List<AllProcedures> query = jdbcTemplate.query(
                SqlFactory.sql(SqlFactory.Sql.PROCEDURES),
                new MapSqlParameterSource(parameters),
                (rs, rowNum) -> new AllProcedures(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getObject(5) == null
                                ? null
                                : rs.getInt(5)
                )
        );

        return queryProcedureFields(schemas, query, typesMap);
    }

    private List<OracleProcedure> queryProcedureFields(Collection<String> schemas, Collection<AllProcedures> allProcedures, Map<String, OracleType> typesMap) {

        log.debug("queryProcedureFields - allProcedures.size: {}", allProcedures.size());

        Map<String, Object> parameters = Map.of(OWNERS_KEY, schemas);

        List<AllArguments> allArgumentsRows = jdbcTemplate.query(
                SqlFactory.sql(SqlFactory.Sql.ARGUMENTS),
                new MapSqlParameterSource(parameters),
                (rs, rowNum) -> new AllArguments(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getObject(8) == null ? null : rs.getInt(8)
                )
        )
                .stream()
                .collect(Collectors.toUnmodifiableList());

        return allProcedures
                .stream()
                .map(allProcedureRow -> new OracleProcedure(
                                allProcedureRow.getOwner(),
                                allProcedureRow.getObjectName(),
                                allProcedureRow.getProcedureName(),
                                allProcedureRow.getObjectType().equals("PACKAGE") ? IN_PACKAGE : STANDALONE,
                                allProcedureRow.getOverload(),
                                mapProcedureFields(allProcedureRow, allArgumentsRows, typesMap)
                        )
                )
                .collect(Collectors.toUnmodifiableList());
    }

    private List<OracleProcedureField> mapProcedureFields(AllProcedures allProcedureRow, List<AllArguments> queryResult, Map<String, OracleType> typesMap) {
        return queryResult
                .stream()
                .filter(allArgumentsRow -> "PROCEDURE".equals(allProcedureRow.getObjectType())
                        ? allArgumentsRow.getPackageName() == null
                        && allArgumentsRow.getObjectName().equals(allProcedureRow.getObjectName())
                        : allArgumentsRow.getPackageName() != null
                        && allArgumentsRow.getPackageName().equals(allProcedureRow.getObjectName())
                        && allArgumentsRow.getObjectName().equals(allProcedureRow.getProcedureName())
                )
                .filter(allArgumentsRow -> allArgumentsRow.getOverload() == null || allArgumentsRow.getOverload().equals(allProcedureRow.getOverload()))
                .map(mapProcedureField(typesMap))
                .collect(Collectors.toUnmodifiableList());
    }

    private Function<AllArguments, OracleProcedureField> mapProcedureField(Map<String, OracleType> typesMap) {
        return allArgumentsRow -> {
            OracleType type;
            if (allArgumentsRow.getTypeName() == null && OracleType.isBasicType(allArgumentsRow.getDataType())) {
                type = OracleBasicType.valueOf(allArgumentsRow.getDataType());
            } else if ("REF CURSOR".equals(allArgumentsRow.getDataType())) {
                type = new OracleCursorType();
            } else {
                type = typesMap.get(allArgumentsRow.getOwner() + "." + allArgumentsRow.getTypeName());
            }

            return new OracleProcedureField(
                    allArgumentsRow.getArgumentName(),
                    allArgumentsRow.getInOut(),
                    type
            );
        };
    }

}
