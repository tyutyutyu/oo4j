package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.JavaTableTypeModel;
import com.tyutyutyu.oo4j.core.generator.JavaType;
import com.tyutyutyu.oo4j.core.template.FreemarkerApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class FileSourceWriter implements SourceWriter {

    private final FreemarkerApi freemarkerApi;
    private final Path baseDir;
    private final boolean createDir;

    @SneakyThrows
    @Override
    public void writeType(JavaType javaType) {

        String classSource = freemarkerApi.generate("sql_data_type_class", javaType);

        write(
                javaType.getPackageName(),
                javaType.getClassName(),
                classSource
        );
    }

    @Override
    public void writeTableType(JavaTableTypeModel javaTableTypeModel) {
        String classSource = freemarkerApi.generate("table_type_class", javaTableTypeModel);

        write(
                javaTableTypeModel.getPackageName(),
                javaTableTypeModel.getClassName(),
                classSource
        );
    }

    @Override
    public void writeProcedure(JavaProcedureMetadata javaProcedureMetadata) {

        String classSource = freemarkerApi.generate("procedure_class", javaProcedureMetadata);

        write(
                javaProcedureMetadata.getPackageName(),
                javaProcedureMetadata.getClassName(),
                classSource
        );
    }


    @Override
    public void writeSqlReturnTypeFactory(String packageName) {

        String classSource = freemarkerApi.generate(
                "sql_return_type_factory_class",
                Map.of("packageName", packageName)
        );

        write(
                packageName,
                "SqlReturnTypeFactory",
                classSource
        );
    }

    @Override
    public void writeSqlTypeValueFactory(String packageName) {

        String classSource = freemarkerApi.generate(
                "sql_type_value_factory_class",
                Map.of("packageName", packageName)
        );

        write(
                packageName,
                "SqlTypeValueFactory",
                classSource
        );
    }

    @SneakyThrows
    private void write(String packageName, String className, String classSource) {

        if (createDir && !Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        String dir = packageName.replace(".", "/");
        Path targetDir = baseDir.resolve(dir);
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(className + ".java");
        Files.write(targetFile, classSource.getBytes(StandardCharsets.UTF_8));
    }
}
