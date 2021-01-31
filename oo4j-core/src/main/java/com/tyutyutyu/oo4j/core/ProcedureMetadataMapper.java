package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import com.tyutyutyu.oo4j.core.result.JavaProcedureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
public class ProcedureMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaProcedureMetadata toJavaProcedureMetadata(
            String schema,
            OracleProcedure oracleProcedure,
            List<OracleProcedureField> oracleProcedureFields
    ) {

        String packageName = namingStrategy.getProcedurePackage(schema);
        String className = namingStrategy.getProcedureClassName(oracleProcedure);

        String sql = createSql(oracleProcedure, oracleProcedureFields);

        List<Param> inParams = oracleProcedureFields
                .stream()
                .filter(oracleProcedureField -> "IN".equals(oracleProcedureField.getInOut()))
                .map(oracleProcedureField -> getParam(schema, oracleProcedureField))
                .collect(Collectors.toUnmodifiableList());
        // TODO: in/out handling
        List<Param> outParams = oracleProcedureFields
                .stream()
                .filter(oracleProcedureField -> "OUT".equals(oracleProcedureField.getInOut()))
                .map(oracleProcedureField -> getParam(schema, oracleProcedureField))
                .collect(Collectors.toUnmodifiableList());

        List<String> imports = oracleProcedureFields
                .stream()
                .map(oracleProcedureField -> oracleDataTypeMapper.oracleDataTypeToJavaType(
                        schema,
                        oracleProcedureField.getDataType(),
                        oracleProcedureField.getTypeName()
                ))
                .filter(javaType -> !javaType.startsWith("java.lang"))
                .distinct()
                .collect(Collectors.toUnmodifiableList());
        List<String> addToTypeMap = imports
                .stream()
                .filter(javaType -> !javaType.startsWith("java."))
                .map(ClassUtils::getShortName)
                .collect(Collectors.toUnmodifiableList());

        return new JavaProcedureMetadata(
                packageName,
                imports,
                className,
                sql,
                addToTypeMap,
                inParams,
                outParams
        );
    }

    private static String createSql(OracleProcedure oracleProcedure, List<OracleProcedureField> oracleProcedureFieldMetadata) {
        String procedureName = oracleProcedure.getObjectName() +
                (
                        oracleProcedure.getProcedureName() == null
                                ? ""
                                : "." + oracleProcedure.getProcedureName()
                );
        String args = IntStream
                .range(0, oracleProcedureFieldMetadata.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(","));

        return String.format("BEGIN %s.%s(%s)", oracleProcedure.getSchema(), procedureName, args);
    }

    private Param getParam(String schema, OracleProcedureField oracleProcedureField) {
        String javaCanonicalName = oracleDataTypeMapper.oracleDataTypeToJavaType(
                schema,
                oracleProcedureField.getDataType(),
                oracleProcedureField.getTypeName()
        );
        String javaSimpleName = ClassUtils.getShortName(javaCanonicalName);
        String setterJavaSimpleType = oracleProcedureField.getTypeName() == null ? javaSimpleName : "Object";

        return new Param(
                javaCanonicalName,
                javaSimpleName,
                setterJavaSimpleType,
                namingStrategy.oracleAttributeNameToJavaVariableName(oracleProcedureField.getName()),
                oracleProcedureField.getName(),
                oracleDataTypeMapper.toJdbcType(oracleProcedureField.getDataType()),
                "OBJECT".equals(oracleProcedureField.getDataType())
        );
    }


}
