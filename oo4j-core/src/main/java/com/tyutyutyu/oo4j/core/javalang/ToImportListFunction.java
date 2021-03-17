package com.tyutyutyu.oo4j.core.javalang;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToImportListFunction implements Function<List<JavaClass>, List<String>> {

    @Override
    public List<String> apply(List<JavaClass> javaClasses) {
        return javaClasses
                .stream()
                .flatMap(this::affectedClasses)
                .filter(this::needToImport)
                .map(JavaClass::getCanonicalName)
                .distinct()
                .collect(Collectors.toList());
    }

    private Stream<? extends JavaClass> affectedClasses(JavaClass javaClass) {
        if (javaClass.isContainer()) {
            if (javaClass.getContainerType() == JavaClass.ContainerType.LIST) {
                if (javaClass.getComponentClass() == null) {
                    return Stream.of(javaClass, JavaClass.listOf(null));
                } else {
                    return Stream.of(javaClass, javaClass.getComponentClass(), JavaClass.listOf(null));
                }
            } else {
                return Stream.of(javaClass, javaClass.getComponentClass());
            }
        } else {
            return Stream.of(javaClass);
        }
    }

    private boolean needToImport(JavaClass javaClass) {
        return !javaClass.getPackageName().startsWith("java.lang")
                && !javaClass.isPrimitive();
    }

}
