package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

@Value
public class AllProcedures {

    String owner;
    String objectName;
    String procedureName;
    String objectType;
    int subprogramId;
    Integer overload;

}
