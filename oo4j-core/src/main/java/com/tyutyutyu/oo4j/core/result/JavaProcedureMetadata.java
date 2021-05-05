package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.Param;
import com.tyutyutyu.oo4j.core.generator.RowMapperMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaProcedureMetadata {

    private final String packageName;
    private final Collection<String> imports;
    private final String className;
    private final String sql;
    private final Collection<Param> allParams;
    private final Collection<RowMapperMetadata> rowMappers;

    public Collection<Param> getInAndInOutParams() {
        return allParams
                .stream()
                .filter(param -> param.getOracleInOut().equals("IN") || param.getOracleInOut().equals("IN/OUT"))
                .collect(Collectors.toUnmodifiableList());
    }

    public Collection<Param> getInOutAndOutParams(){
        return allParams
                .stream()
                .filter(param -> param.getOracleInOut().equals("IN/OUT") || param.getOracleInOut().equals("OUT"))
                .collect(Collectors.toUnmodifiableList());
    }

}
