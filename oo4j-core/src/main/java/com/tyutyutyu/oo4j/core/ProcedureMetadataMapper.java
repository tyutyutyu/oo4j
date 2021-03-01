package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.JavaClassUtils;
import com.tyutyutyu.oo4j.core.query.*;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class ProcedureMetadataMapper {

    private static final char[] GENERIC_TYPES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaProcedureMetadata toJavaProcedureMetadata(OracleProcedure oracleProcedure) {

        log.debug("toJavaProcedureMetadata - oracleProcedure.fullyQualifiedName: {}",
                oracleProcedure.getFullyQualifiedName());

        String packageName = namingStrategy.getProcedurePackage(oracleProcedure.getSchema());
        String className = namingStrategy.getProcedureClassName(oracleProcedure);

        String sql = createSql(oracleProcedure);

        AtomicInteger rowMapperIndex = new AtomicInteger(0);
        List<Param> inParams = toParams(
                oracleProcedure.getSchema(),
                oracleProcedure.getFields(),
                rowMapperIndex,
                "IN"
        );
        List<Param> inOutParams = toParams(
                oracleProcedure.getSchema(),
                oracleProcedure.getFields(),
                rowMapperIndex,
                "IN/OUT"
        );
        List<Param> outParams = toParams(
                oracleProcedure.getSchema(),
                oracleProcedure.getFields(),
                rowMapperIndex,
                "OUT"
        );
        List<RowMapperMetadata> rowMappers = getRowMappers(inOutParams, outParams);
        List<String> imports = getImports(
                oracleProcedure.getSchema(),
                oracleProcedure.getFields(),
                !rowMappers.isEmpty()
        );

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
        return Stream.concat(inOutParams.stream(), outParams.stream())
                .filter(param -> param.getRowMapperType() != null)
                .map(param -> new RowMapperMetadata(param.getRowMapperType(), param.getJavaName()))
                .collect(Collectors.toUnmodifiableList());
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
