package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.ProcedureMetadataMapper;
import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class Oo4jCodeGenerator {

    private final TypeGenerator typeGenerator;
    private final ProcedureGenerator procedureGenerator;

    public Oo4jCodeGenerator(
            DataSource dataSource,
            NamingStrategy namingStrategy,
            SourceWriter sourceWriter
    ) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MetadataQuery metadataQuery = new MetadataQuery(jdbcTemplate);
        typeGenerator = new TypeGenerator(
                metadataQuery,
                new TypeMetadataMapper(namingStrategy, new OracleDataTypeMapper(namingStrategy)),
                sourceWriter
        );
        procedureGenerator = new ProcedureGenerator(
                metadataQuery,
                new ProcedureMetadataMapper(namingStrategy, new OracleDataTypeMapper(namingStrategy)),
                sourceWriter
        );
    }

    public void generate(List<String> schemaList) {
        schemaList.forEach(schema -> {
            generateTypes(schema);
            // TODO: arrays
            generateProcedures(schema);
        });
    }

    public void generateTypes(List<String> schemaList) {
        schemaList.forEach(this::generateTypes);
    }

    public void generateTypes(String schema) {
        typeGenerator.generateTypes(schema);
    }

    public void generateType(String schema, String typeName) {
        typeGenerator.generateType(schema, typeName);
    }

    public void generateProcedures(List<String> schemaList) {
        schemaList.forEach(this::generateProcedures);
    }

    public void generateProcedures(String schema) {
        procedureGenerator.generateProcedures(schema);
    }

    public void generateProcedures(String schema, OracleProcedure oracleProcedure) {
        procedureGenerator.generateProcedure(schema, oracleProcedure);
    }

}
