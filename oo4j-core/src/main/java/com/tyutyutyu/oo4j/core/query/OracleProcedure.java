package com.tyutyutyu.oo4j.core.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class OracleProcedure {

    private final String schema;
    private final String objectName;
    private final String procedureName;
    private final String objectType;

}
