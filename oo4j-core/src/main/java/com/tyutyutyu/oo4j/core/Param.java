package com.tyutyutyu.oo4j.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class Param {

    private final String javaType;
    private final String javaSimpleType;
    private final String setterJavaSimpleType;
    private final String javaName;
    private final String name;
    private final String sqlType;
    private final boolean custom;

}
