package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.JavaTableTypeModel;
import com.tyutyutyu.oo4j.core.generator.JavaTypeModel;

public interface SourceWriter {

    void writeType(JavaTypeModel javaTypeModel);

    void writeTableType(JavaTableTypeModel javaTableTypeModel);

    void writeProcedure(JavaProcedureMetadata javaProcedureMetadata);

    void writeSqlReturnTypeFactory(String typePackageName);

    void writeSqlTypeValueFactory(String typePackageName);
}
