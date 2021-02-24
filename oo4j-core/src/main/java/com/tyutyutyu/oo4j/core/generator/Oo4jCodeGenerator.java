package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.ProcedureMetadataMapper;
import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

public class Oo4jCodeGenerator {

    private final NamingStrategy namingStrategy;
    private final TypeGenerator typeGenerator;
    private final TableTypeGenerator tableTypeGenerator;
    private final ProcedureGenerator procedureGenerator;
    private final SourceWriter sourceWriter;

    public Oo4jCodeGenerator(
            DataSource dataSource,
            NamingStrategy namingStrategy,
            SourceWriter sourceWriter
    ) {
        this.namingStrategy = namingStrategy;
        this.sourceWriter = sourceWriter;
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MetadataQuery metadataQuery = new MetadataQuery(jdbcTemplate);
        typeGenerator = new TypeGenerator(
                metadataQuery,
                new TypeMetadataMapper(namingStrategy, new OracleDataTypeMapper(namingStrategy))
        );
        tableTypeGenerator = new TableTypeGenerator(
                metadataQuery,
                new TableTypeMetadataMapper(namingStrategy),
                sourceWriter
        );
        procedureGenerator = new ProcedureGenerator(
                metadataQuery,
                new ProcedureMetadataMapper(namingStrategy, new OracleDataTypeMapper(namingStrategy)),
                sourceWriter
        );
    }

    public void generate(Collection<String> schemaList, Collection<String> typeExcludes, Collection<String> procedureExcludes) {
        schemaList.forEach(schema -> {
            List<JavaTypeModel> types = generateTypes(schema, typeExcludes);
            types.forEach(sourceWriter::writeType);
            generateTableTypes(schema, typeExcludes);
            generateProcedures(schema, procedureExcludes);
            generateHelperClasses(schema);
        });
    }

    public List<JavaTypeModel> generateTypes(String schema, Collection<String> typeExcludes) {
        return typeGenerator.generateTypes(schema, typeExcludes);
    }

    public void generateTableTypes(String schema, Collection<String> typeExcludes) {
        tableTypeGenerator.generateTableTypes(schema, typeExcludes);
    }

    public void generateProcedures(String schema, Collection<String> procedureExcludes) {
        procedureGenerator.generateProcedures(schema, procedureExcludes);
    }

    private void generateHelperClasses(String schema) {
        sourceWriter.writeSqlReturnTypeFactory(namingStrategy.getTypePackage(schema));
        sourceWriter.writeSqlTypeValueFactory(namingStrategy.getTypePackage(schema));
    }

}
