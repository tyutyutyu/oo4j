package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

@Value
public class OracleProcedureField {

    String name;
    String inOut;
    OracleType type;

}
