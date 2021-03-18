package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

@Value
public class AllArguments {

    String owner;
    String argumentName;
    String dataType;
    String inOut;
    String typeName;
    String packageName;
    String objectName;
    Integer overload;

}
