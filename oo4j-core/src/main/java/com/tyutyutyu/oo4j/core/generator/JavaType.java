package com.tyutyutyu.oo4j.core.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaType {

    private final String packageName;
    private final Collection<String> imports;
    private final String className;
    private final String schema;
    private final String typeName;
    private final Collection<JavaTypeField> fields;

}
