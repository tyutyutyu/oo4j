package com.tyutyutyu.oo4j.core.result;

import com.tyutyutyu.oo4j.core.generator.JavaTypeMetadata;
import com.tyutyutyu.oo4j.core.template.FreemarkerApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileSourceWriter implements SourceWriter {

    private final FreemarkerApi freemarkerApi;
    private final Path baseDir;
    private final boolean createDir;

    @SneakyThrows
    @Override
    public void writeType(JavaTypeMetadata javaTypeMetadata) {

        String classSource = freemarkerApi.generate("sql_data_type_class", javaTypeMetadata);

        write(
                javaTypeMetadata.getPackageName(),
                javaTypeMetadata.getClassName(),
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
