package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.NoPrivilegeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class MetadataQuery {

    private static final String OWNERS_KEY = "owners";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Map<String, OracleType> queryTypes(Collection<String> schemas) {

        log.debug("queryType - schemas: {}", schemas);

        Map<String, ?> parameters = Map.of(
                OWNERS_KEY, schemas
        );

        List<TypesQueryResult> queryResults = jdbcTemplate.query(
                SqlFactory.sql(SqlFactory.Sql.TYPES),
                new MapSqlParameterSource(parameters),
                (rs, rowNum) -> new TypesQueryResult(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)
                )
        );

        Collection<List<TypesQueryResult>> temp = queryResults
                .stream()
                .collect(Collectors.groupingBy(r -> r.getOwner() + "." + r.getTypeName()))
                .values();

        Map<String, OracleType> result = new HashMap<>();
        for (List<TypesQueryResult> list : temp) {
            OracleType oracleType = finisher(result).apply(list);
            result.put(oracleType.getFullyQualifiedName(), oracleType);
        }

        return result;
    }

    private Function<List<TypesQueryResult>, OracleType> finisher(Map<String, OracleType> accumulator) {
        return typesQueryResults -> {
            TypesQueryResult first = typesQueryResults.get(0);

            return getType(first.getOwner(), first.getTypeCode(), first.getTypeName(), typesQueryResults, accumulator);
        };
    }

    private OracleType getType(
            String schema,
            String typeCode,
            String typeName,
            List<TypesQueryResult> typesQueryResults,
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
                    getObjectTypeAttributes(schema, typeName, typesQueryResults, accumulator)
            );
        } else if (fieldType == OracleTableType.class) {
            type = queryTableType(schema, typeName, typesQueryResults);
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
            List<TypesQueryResult> typesQueryResults,
            Map<String, OracleType> accumulator
    ) {
        return typesQueryResults
                .stream()
                .filter(typesQueryResult -> typesQueryResult.getOwner().equals(schema)
                        && typesQueryResult.getTypeName().equals(typeName))
                .map(typesQueryResult -> new OracleTypeField(
                        typesQueryResult.getAttrName(),
                        getType(
                                typesQueryResult.getOwner(),
                                typesQueryResult.getTypeCode(),
                                typesQueryResult.getAttrTypeName(),
                                typesQueryResults,
                                accumulator)
                ))
                .collect(Collectors.toUnmodifiableList());
    }

    private OracleTableType queryTableType(String schema, String typeName, List<TypesQueryResult> typesQueryResults) {

        log.debug("queryTableType - schema: {}, typeName: {}", schema, typeName);

        return typesQueryResults
                .stream()
                .filter(typesQueryResult -> typesQueryResult.getOwner().equals(schema)
                        && typesQueryResult.getTypeName().equals(typeName))
                .findAny()
                .map(TypesQueryResult::getElemTypeName)
                .map(elemTypeName -> new OracleTableType(schema, typeName, elemTypeName))
                .orElseThrow(() ->
                        new NoPrivilegeException(
                                String.format(
                                        "The database user has no privilege to query information about %s.%s from ALL_COLL_TYPES.",
                                        schema, typeName
                                )
                        )
                );
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
                        rs.getInt(5),
                        rs.getObject(6) == null
                                ? null
                                : rs.getInt(6)
                )
        );

        return queryProcedureFields(schemas, query, typesMap);
    }

    private List<OracleProcedure> queryProcedureFields(Collection<String> schemas, Collection<AllProcedures> allProcedures, Map<String, OracleType> typesMap) {

        log.debug("queryProcedureFields - allProcedures.size: {}", allProcedures.size());

        Map<String, Object> parameters = Map.of(OWNERS_KEY, schemas);

        List<AllArguments> queryResult = jdbcTemplate.query(
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
                                allProcedureRow.getObjectType(),
                                allProcedureRow.getSubprogramId(),
                                allProcedureRow.getOverload(),
                                map4(allProcedureRow, queryResult, typesMap)
                        )
                )
                .collect(Collectors.toUnmodifiableList());
    }

    private List<OracleProcedureField> map4(AllProcedures allProcedureRow, List<AllArguments> queryResult, Map<String, OracleType> typesMap) {
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
            OracleType type = typesMap.get(allArgumentsRow.getOwner() + "." + allArgumentsRow.getTypeName());

            return new OracleProcedureField(
                    allArgumentsRow.getArgumentName(),
                    allArgumentsRow.getInOut(),
                    type
            );
        };
    }

}
