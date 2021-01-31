package com.tyutyutyu.oo4j.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TypeAttr {

    private final String owner;
    private final String typeName;
    private final String attrName;
    private final String attrTypeMod;
    private final String attrTypeOwner;
    private final String attrTypeName;
    private final String length;
    private final String precision;
    private final String scale;
    private final String characterSetName;
    private final String attrNo;
    private final String inherited;

}
