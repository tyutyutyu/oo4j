package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.ImportCollector;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        Collection<Param> inParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "IN");
        Collection<Param> inOutParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "IN/OUT");
        Collection<Param> outParams = paramMapper.toParams(oracleProcedure, rowMapperIndex, "OUT");
        Collection<RowMapperMetadata> rowMappers = getRowMappers(inOutParams, outParams);
        Collection<String> imports = getImports(inParams, inOutParams, outParams, !rowMappers.isEmpty());

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

    private List<RowMapperMetadata> getRowMappers(Collection<Param> inOutParams, Collection<Param> outParams) {
        return Stream.concat(inOutParams.stream(), outParams.stream())
                .filter(param -> param.getRowMapperType() != null)
                .map(param -> new RowMapperMetadata(param.getRowMapperType(), param.getJavaName()))
                .collect(Collectors.toUnmodifiableList());
    }

    private SortedSet<String> getImports(Collection<Param> inParams, Collection<Param> inOutParams, Collection<Param> outParams, boolean addRowMapper) {
        List<String> extraImports = addRowMapper ? List.of("org.springframework.jdbc.core.RowMapper") : List.of();
        return Stream.of(inParams, inOutParams, outParams)
                .flatMap(Collection::stream)
                .map(Param::getJavaClass)
                .collect(new ImportCollector(extraImports));
    }

}
