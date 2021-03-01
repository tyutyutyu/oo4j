package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.ProcedureMetadataMapper;
import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleType;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Oo4jCodeGenerator {

    private final String basePackage;
    private final MetadataQuery metadataQuery;
    private final TypeGenerator typeGenerator;
    private final ProcedureMetadataMapper procedureMetadataMapper;
    private final SourceWriter sourceWriter;

    public Oo4jCodeGenerator(
            String basePackage,
            DataSource dataSource,
            NamingStrategy namingStrategy,
            SourceWriter sourceWriter
    ) {
        this.basePackage = basePackage;
        OracleDataTypeMapper oracleDataTypeMapper = new OracleDataTypeMapper(namingStrategy);
        this.procedureMetadataMapper = new ProcedureMetadataMapper(namingStrategy, oracleDataTypeMapper);
        this.sourceWriter = sourceWriter;
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        metadataQuery = new MetadataQuery(jdbcTemplate);
        typeGenerator = new TypeGenerator(new TypeMetadataMapper(namingStrategy, oracleDataTypeMapper));
    }

    public void generate(Collection<String> schemas, Collection<String> typeExcludes, Collection<String> procedureExcludes) {

        Map<String, OracleType> typesMap = metadataQuery.queryTypes(schemas);
        typesMap = typesMap
                .entrySet()
                .stream()
                .filter(e -> !typeExcludes.contains(e.getKey()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        List<JavaTypeModel> typeModels = typeGenerator.generateObjectTypes(typesMap.values());
        typeModels.forEach(sourceWriter::writeType);

        List<JavaTableTypeModel> tableTypeModels = typeGenerator.generateTableTypes(typesMap.values());
        tableTypeModels.forEach(sourceWriter::writeTableType);

        metadataQuery.queryProcedures(schemas, typesMap)
                .stream()
                .filter(oracleProcedure -> !procedureExcludes.contains(oracleProcedure.getFullyQualifiedName()))
                .map(procedureMetadataMapper::toJavaProcedureMetadata)
                .forEach(sourceWriter::writeProcedure);

        generateHelperClasses();
    }

    private void generateHelperClasses() {
        sourceWriter.writeSqlReturnTypeFactory(basePackage);
        sourceWriter.writeSqlTypeValueFactory(basePackage);
    }

}
