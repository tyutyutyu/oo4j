package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.ImportCollector;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class ProcedureMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final ParamMapper paramMapper;

    public JavaProcedureMetadata toJavaProcedureMetadata(OracleProcedure oracleProcedure) {

        log.debug("toJavaProcedureMetadata - oracleProcedure.fullyQualifiedName: {}",
                oracleProcedure.getFullyQualifiedName());

        AtomicInteger rowMapperIndex = new AtomicInteger(0);
        Collection<Param> allParams = paramMapper.toParams(oracleProcedure, rowMapperIndex);
        Collection<RowMapperMetadata> rowMappers = getRowMappers(allParams);
        Collection<String> imports = getImports(allParams, !rowMappers.isEmpty());

        return new JavaProcedureMetadata(
                namingStrategy.getProcedurePackage(oracleProcedure.getSchema()),
                imports,
                namingStrategy.getProcedureClassName(oracleProcedure),
                oracleProcedure.getFullyQualifiedName(),
                allParams,
                rowMappers
        );
    }

    private List<RowMapperMetadata> getRowMappers(Collection<Param> allParams) {
        return allParams
                .stream()
                .filter(param -> param.getOracleInOut().equals("IN/OUT") || param.getOracleInOut().equals("OUT"))
                .filter(param -> param.getRowMapperType() != null)
                .map(param -> new RowMapperMetadata(param.getRowMapperType(), param.getJavaName()))
                .collect(Collectors.toUnmodifiableList());
    }

    private SortedSet<String> getImports(Collection<Param> allParams, boolean addRowMapper) {
        List<String> extraImports = new ArrayList<>();
        if (addRowMapper) {
            extraImports.add("org.springframework.jdbc.core.RowMapper");
        }
        if (allParams.stream().anyMatch(param -> param.getJdbcType().equals("BLOB"))) {
            extraImports.add(namingStrategy.getBasePackage() + ".SqlReturnTypeFactory");
        }

        return allParams
                .stream()
                .map(Param::getJavaClass)
                .collect(new ImportCollector(extraImports));
    }

}
