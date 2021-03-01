package com.tyutyutyu.oo4j.core.query;

import lombok.*;

import java.util.List;

@Value
public class OracleProcedure {

    String schema;
    String objectName;
    String procedureName;
    String objectType;
    int subprogramId;
    Integer overload;
    List<OracleProcedureField> fields;

    public String getName() {
        return "PACKAGE".equals(objectType)
                ? objectName + "." + procedureName
                : objectName;
    }

    public String getFullyQualifiedName() {
        return schema + "." + getName();
    }

}
