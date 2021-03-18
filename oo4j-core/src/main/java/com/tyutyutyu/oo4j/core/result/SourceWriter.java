package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.JavaTableTypeModel;
import com.tyutyutyu.oo4j.core.generator.JavaType;

public interface SourceWriter {

    void writeType(JavaType javaType);

    void writeTableType(JavaTableTypeModel javaTableTypeModel);

    void writeProcedure(JavaProcedureMetadata javaProcedureMetadata);

    void writeSqlReturnTypeFactory(String packageName);

    void writeSqlTypeValueFactory(String packageName);
}
