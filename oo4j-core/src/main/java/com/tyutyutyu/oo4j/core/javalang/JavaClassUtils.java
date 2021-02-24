package com.tyutyutyu.oo4j.core.javalang;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class JavaClassUtils {

    public static class ImportPredicate implements Predicate<JavaClass> {

        @Override
        public boolean test(JavaClass javaClass) {
            return !javaClass.getPackageName().startsWith("java.lang")
                    && !javaClass.isPrimitive();
        }
    }

    public static List<String> toImportList(Stream<JavaClass> javaClassStream) {
        return javaClassStream
                .flatMap(javaClass -> javaClass.getComponentClass() != null
                        ? Stream.of(javaClass, javaClass.getComponentClass())
                        : Stream.of(javaClass)
                )
                .filter(new ImportPredicate())
                .flatMap(javaClass -> javaClass.isContainer() && javaClass.getContainerType() == JavaClass.ContainerType.LIST
                        ? Stream.of(javaClass, JavaClass.listOf(null))
                        : Stream.of(javaClass))
                .map(JavaClass::getCanonicalName)
                .distinct()
                .collect(Collectors.toList());
    }

}
