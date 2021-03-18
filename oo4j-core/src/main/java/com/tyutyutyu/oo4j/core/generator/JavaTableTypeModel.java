package com.tyutyutyu.oo4j.core.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaTableTypeModel {

    private final String packageName;
    private final Collection<String> imports;
    private final String className;
    private final String componentClassName;
    private final String schema;
    private final String typeName;

}
