package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.Param;
import com.tyutyutyu.oo4j.core.RowMapperMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaProcedureMetadata {

    private final String packageName;
    private final List<String> imports;
    private final String className;
    private final String sql;
    private final List<Param> inParams;
    private final List<Param> inOutParams;
    private final List<Param> outParams;
    private final List<RowMapperMetadata> rowMappers;

}
