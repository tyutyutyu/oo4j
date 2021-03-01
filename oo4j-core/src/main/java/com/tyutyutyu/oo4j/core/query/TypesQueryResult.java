package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

@Value
public class TypesQueryResult {

    String owner;
    String typeName;
    String typeCode;
    String attrName;
    String attrTypeName;
    String elemTypeName;

}
