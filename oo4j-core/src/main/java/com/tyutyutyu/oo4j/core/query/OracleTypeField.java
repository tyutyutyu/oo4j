package com.tyutyutyu.oo4j.core.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class OracleTypeField {

    private final String name;
    private final OracleType type;

}
