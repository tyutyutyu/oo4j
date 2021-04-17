package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.Param;
import com.tyutyutyu.oo4j.core.generator.RowMapperMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaProcedureMetadata {

    private final String packageName;
    private final Collection<String> imports;
    private final String className;
    private final String sql;
    private final Collection<Param> paramsForDeclaration;
    private final Collection<Param> inParams;
    private final Collection<Param> inOutParams;
    private final Collection<Param> outParams;
    private final Collection<RowMapperMetadata> rowMappers;

}
