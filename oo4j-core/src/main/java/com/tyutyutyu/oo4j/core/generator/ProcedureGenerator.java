package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.ProcedureMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
class ProcedureGenerator {

    private final MetadataQuery metadataQuery;
    private final ProcedureMetadataMapper procedureMetadataMapper;
    private final SourceWriter sourceWriter;

    void generateProcedures(String schema) {
        metadataQuery.queryProcedures(schema)
                .forEach(procedure -> generateProcedure(schema, procedure));
    }

    void generateProcedure(String schema, OracleProcedure oracleProcedure) {

        log.debug("generateStoredProcedure - schema: {}, oracleProcedure: {}", schema, oracleProcedure);

        List<OracleProcedureField> oracleProcedureFields = metadataQuery.queryProcedureFields(
                schema,
                oracleProcedure.getObjectName(),
                oracleProcedure.getProcedureName()
        );

        JavaProcedureMetadata javaProcedureMetadata = procedureMetadataMapper.toJavaProcedureMetadata(
                schema,
                oracleProcedure,
                oracleProcedureFields
        );

        sourceWriter.writeProcedure(javaProcedureMetadata);
    }

}