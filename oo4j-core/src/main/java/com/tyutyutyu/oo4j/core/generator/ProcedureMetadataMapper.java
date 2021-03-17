package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.ToImportListFunction;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class ProcedureMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;
    private final ParamMapper paramMapper;

    public JavaProcedureMetadata toJavaProcedureMetadata(OracleProcedure oracleProcedure) {

        log.debug("toJavaProcedureMetadata - oracleProcedure.fullyQualifiedName: {}",
                oracleProcedure.getFullyQualifiedName());

        AtomicInteger rowMapperIndex = new AtomicInteger(0);
        List<Param> inParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "IN");
        List<Param> inOutParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "IN/OUT");
        List<Param> outParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "OUT");
        List<RowMapperMetadata> rowMappers = getRowMappers(inOutParams, outParams);
        List<String> imports = getImports(oracleProcedure, !rowMappers.isEmpty());

        return new JavaProcedureMetadata(
                namingStrategy.getProcedurePackage(oracleProcedure.getSchema()),
                imports,
                namingStrategy.getProcedureClassName(oracleProcedure),
                oracleProcedure.getFullyQualifiedName(),
                inParams,
                inOutParams,
                outParams,
                rowMappers
        );
    }

    private List<RowMapperMetadata> getRowMappers(List<Param> inOutParams, List<Param> outParams) {
        return Stream.concat(inOutParams.stream(), outParams.stream())
                .filter(param -> param.getRowMapperType() != null)
                .map(param -> new RowMapperMetadata(param.getRowMapperType(), param.getJavaName()))
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getImports(OracleProcedure oracleProcedure, boolean addRowMapper) {
        List<JavaClass> javaClasses = oracleProcedure
                .getFields()
                .stream()
                .map(oracleProcedureField -> oracleDataTypeMapper.oracleDataTypeToJavaClass(
                        oracleProcedureField.getType()
                ))
                .collect(Collectors.toUnmodifiableList());

        List<String> imports = new ToImportListFunction().apply(javaClasses);

        if (addRowMapper) {
            imports.add("org.springframework.jdbc.core.RowMapper");
        }

        return imports;
    }

}
