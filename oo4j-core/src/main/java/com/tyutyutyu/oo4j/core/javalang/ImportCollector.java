package com.tyutyutyu.oo4j.core.javalang;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ImportCollector implements Collector<JavaClass, List<String>, SortedSet<String>> {

    private final List<String> extraImports;

    @Override
    public Supplier<List<String>> supplier() {
        return () -> new ArrayList<>(extraImports);
    }

    @Override
    public BiConsumer<List<String>, JavaClass> accumulator() {
        return (strings, javaClass) -> strings.addAll(
                affectedClasses(javaClass)
                        .stream()
                        .filter(ImportCollector::needToImport)
                        .map(JavaClass::getCanonicalName)
                        .distinct()
                        .collect(Collectors.toList())
        );
    }

    @Override
    public BinaryOperator<List<String>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }

    @Override
    public Function<List<String>, SortedSet<String>> finisher() {
        return TreeSet::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    private static List<JavaClass> affectedClasses(JavaClass javaClass) {
        if (javaClass.isContainer()) {
            if (javaClass.getContainerType() == JavaClass.ContainerType.LIST) {
                if (javaClass.getComponentClass() == null) {
                    return List.of(javaClass, JavaClass.listOf(null));
                } else {
                    return List.of(javaClass, javaClass.getComponentClass(), JavaClass.listOf(null));
                }
            } else {
                return List.of(javaClass, javaClass.getComponentClass());
            }
        } else {
            return List.of(javaClass);
        }
    }

    private static boolean needToImport(JavaClass javaClass) {
        return !javaClass.getPackageName().startsWith("java.lang")
                && !javaClass.isPrimitive();
    }

}
