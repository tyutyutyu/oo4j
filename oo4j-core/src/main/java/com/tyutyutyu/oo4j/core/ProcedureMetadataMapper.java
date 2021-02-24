package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.JavaClassUtils;
import com.tyutyutyu.oo4j.core.query.*;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class ProcedureMetadataMapper {

    private static final char[] GENERIC_TYPES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaProcedureMetadata toJavaProcedureMetadata(
            String schema,
            OracleProcedure oracleProcedure,
            List<OracleProcedureField> oracleProcedureFields
    ) {

        log.debug("toJavaProcedureMetadata - schema: {}, oracleProcedure: {}, oracleProcedureFields: {}",
                schema, oracleProcedure, oracleProcedureFields);

        String packageName = namingStrategy.getProcedurePackage(schema);
        String className = namingStrategy.getProcedureClassName(oracleProcedure);

        String sql = createSql(oracleProcedure);

        AtomicInteger rowMapperIndex = new AtomicInteger(0);
        List<Param> inParams = toParams(schema, oracleProcedureFields, rowMapperIndex, "IN");
        List<Param> inOutParams = toParams(schema, oracleProcedureFields, rowMapperIndex, "IN/OUT");
        List<Param> outParams = toParams(schema, oracleProcedureFields, rowMapperIndex, "OUT");
        List<RowMapperMetadata> rowMappers = getRowMappers(inOutParams, outParams);
        List<String> imports = getImports(schema, oracleProcedureFields, !rowMappers.isEmpty());

        return new JavaProcedureMetadata(
                packageName,
                imports,
                className,
                sql,
                inParams,
                inOutParams,
                outParams,
                rowMappers
        );
    }

    @Nonnull
    private List<Param> toParams(String schema, List<OracleProcedureField> oracleProcedureFields, AtomicInteger rowMapperIndex, String inOut) {
        return oracleProcedureFields
                .stream()
                .filter(oracleProcedureField -> inOut.equals(oracleProcedureField.getInOut()))
                .map(oracleProcedureField -> getParam(schema, oracleProcedureField, rowMapperIndex))
                .collect(Collectors.toUnmodifiableList());
    }

    private List<RowMapperMetadata> getRowMappers(List<Param> inOutParams, List<Param> outParams) {

        List<Param> temp = Stream.concat(inOutParams.stream(), outParams.stream())
                .filter(param -> param.getRowMapperType() != null)
                .collect(Collectors.toUnmodifiableList());

        List<RowMapperMetadata> rowMappers = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            rowMappers.add(new RowMapperMetadata(temp.get(i).getRowMapperType(), temp.get(i).getJavaName()));
        }
        return rowMappers;
    }

    private List<String> getImports(String schema, List<OracleProcedureField> oracleProcedureFields, boolean addRowMapper) {
        List<String> imports = JavaClassUtils.toImportList(oracleProcedureFields
                .stream()
                .map(oracleProcedureField -> oracleDataTypeMapper.oracleDataTypeToJavaClass(
                        oracleProcedureField.getType(),
                        schema
                )));
        if (addRowMapper) {
            imports.add("org.springframework.jdbc.core.RowMapper");
        }

        return imports;
    }

    private static String createSql(OracleProcedure oracleProcedure) {
        String procedureName = oracleProcedure.getObjectName() +
                (
                        oracleProcedure.getProcedureName() == null
                                ? ""
                                : "." + oracleProcedure.getProcedureName()
                );
        return String.format("%s.%s", oracleProcedure.getSchema(), procedureName);
    }

    private Param getParam(String schema, OracleProcedureField oracleProcedureField, AtomicInteger rowMapperIndex) {

        log.debug("getParam - schema: {}, oracleProcedureField: {}", schema, oracleProcedureField);

        JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(
                oracleProcedureField.getType(),
                schema
        );

        return new Param(
                javaClass,
                namingStrategy.oracleAttributeNameToJavaVariableName(oracleProcedureField.getName()),
                oracleProcedureField.getName(),
                oracleProcedureField.getType().getJdbcType().name(),
                oracleProcedureField.getType() instanceof OracleObjectType,
                oracleProcedureField.getType() instanceof OracleCursorType
                        ? String.valueOf(GENERIC_TYPES[rowMapperIndex.getAndIncrement()])
                        : null,
                schema + "." + oracleProcedureField.getType().getName(),
                oracleProcedureField.getType() instanceof OracleTableType
                        ? namingStrategy.oracleTypeNameToJavaClassName(
                        ((OracleTableType) oracleProcedureField.getType()).getComponentTypeName()
                )
                        : null
        );
    }


}
