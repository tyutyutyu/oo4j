package com.tyutyutyu.oo4j.core.generator;

import lombok.Value;

import java.util.Collection;

@Value
public class JavaTableTypeModel {

    String packageName;
    Collection<String> imports;
    String className;
    String componentClassName;
    boolean customComponentType;
    String schema;
    String typeName;

}
