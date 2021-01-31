package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.JavaTypeMetadata;

public interface SourceWriter {

    void writeType(JavaTypeMetadata javaTypeMetadata);

    void writeProcedure(JavaProcedureMetadata javaProcedureMetadata);

}
