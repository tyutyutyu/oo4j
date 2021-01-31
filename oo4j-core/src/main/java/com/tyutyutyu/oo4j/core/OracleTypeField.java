package com.tyutyutyu.oo4j.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class OracleTypeField {

    private final String name;
    private final String typeName;

}
