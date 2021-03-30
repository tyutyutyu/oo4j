package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.query.*;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Oo4jCodeGenerator {

    private final String basePackage;
    private final MetadataQuery metadataQuery;
    private final ProcedureMetadataMapper procedureMetadataMapper;
    private final SourceWriter sourceWriter;
    private final TypeMetadataMapper typeMetadataMapper;

    public Oo4jCodeGenerator(
            String basePackage,
            DataSource dataSource,
            NamingStrategy namingStrategy,
            SourceWriter sourceWriter
    ) {
        this.basePackage = basePackage;
        OracleDataTypeMapper oracleDataTypeMapper = new OracleDataTypeMapper(namingStrategy);
        this.procedureMetadataMapper = new ProcedureMetadataMapper(
                namingStrategy,
                new ParamMapper(namingStrategy, oracleDataTypeMapper)
        );
        this.sourceWriter = sourceWriter;
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        metadataQuery = new MetadataQuery(jdbcTemplate);
        typeMetadataMapper = new TypeMetadataMapper(namingStrategy, oracleDataTypeMapper);
    }

    public void generate(Collection<String> schemas, Collection<String> typeExcludes, Collection<String> procedureExcludes) {

        log.debug("generate - schemas: {}, typeExcludes: {}, procedureExcludes: {}", schemas, typeExcludes, procedureExcludes);

        Map<String, OracleType> typesMap = metadataQuery.queryTypes(schemas, typeExcludes);
        typesMap = typesMap
                .entrySet()
                .stream()
                .filter(e -> !typeExcludes.contains(e.getKey()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        List<JavaType> typeModels = generateObjectTypes(typesMap.values());
        typeModels.forEach(sourceWriter::writeType);

        List<JavaTableTypeModel> tableTypeModels = generateTableTypes(typesMap.values());
        tableTypeModels.forEach(sourceWriter::writeTableType);

        metadataQuery.queryProcedures(schemas, typesMap)
                .stream()
                .filter(oracleProcedure -> !procedureExcludes.contains(oracleProcedure.getFullyQualifiedName()))
                .map(procedureMetadataMapper::toJavaProcedureMetadata)
                .forEach(sourceWriter::writeProcedure);

        generateCommonClasses();
    }

    private void generateCommonClasses() {
        sourceWriter.writeCommonClasses(basePackage);
    }

    private List<JavaType> generateObjectTypes(Collection<OracleType> types) {
        return types
                .stream()
                .filter(OracleObjectType.class::isInstance)
                .map(OracleObjectType.class::cast)
                .map(typeMetadataMapper::toJavaTypeMetadata)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<JavaTableTypeModel> generateTableTypes(Collection<OracleType> types) {
        return types
                .stream()
                .filter(OracleTableType.class::isInstance)
                .map(OracleTableType.class::cast)
                .map(typeMetadataMapper::toJavaTableTypeMetadata)
                .collect(Collectors.toUnmodifiableList());
    }

}
