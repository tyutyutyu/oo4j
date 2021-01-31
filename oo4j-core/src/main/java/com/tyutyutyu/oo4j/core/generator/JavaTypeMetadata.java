package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.JavaTypeField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaTypeMetadata {

    private final String packageName;
    private final List<String> imports;
    private final String className;
    private final String schema;
    private final String typeName;
    private final List<JavaTypeField> fields;

}
