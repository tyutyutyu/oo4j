package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

import java.util.List;

@Value
public class OracleProcedure {

    public enum Type {
        IN_PACKAGE, STANDALONE
    }

    String schema;
    String objectName;
    String procedureName;
    Type type;
    Integer overload;
    List<OracleProcedureField> fields;

    public String getName() {
        return type == Type.IN_PACKAGE
                ? objectName + "." + procedureName
                : objectName;
    }

    public String getFullyQualifiedName() {
        return schema + "." + getName();
    }

}
