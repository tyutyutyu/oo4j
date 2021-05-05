package com.tyutyutyu.oo4j.core.javalang;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class JavaClass {

    public enum ContainerType {
        ARRAY, LIST
    }

    public static final JavaClass BYTE_ARRAY = new JavaClass(
            byte[].class.getPackageName(),
            byte[].class.getSimpleName(),
            false,
            new JavaClass(
                    byte.class.getPackageName(),
                    byte.class.getSimpleName(),
                    true,
                    null,
                    false,
                    null,
                    "Byte"
            ),
            true,
            ContainerType.ARRAY,
            "Bytes"
    );

    public static final JavaClass DOUBLE = new JavaClass(
            Double.class.getPackageName(),
            Double.class.getSimpleName(),
            false,
            null,
            false,
            null,
            "Double"
    );
    public static final JavaClass BIG_DECIMAL = new JavaClass(
            BigDecimal.class.getPackageName(),
            BigDecimal.class.getSimpleName(),
            false,
            null,
            false,
            null,
            "BigDecimal"
    );

    public static final JavaClass STRING = new JavaClass(
            String.class.getPackageName(),
            String.class.getSimpleName(),
            false,
            null,
            false,
            null,
            "String"
    );

    public static final JavaClass TIMESTAMP = new JavaClass(
            Timestamp.class.getPackageName(),
            Timestamp.class.getSimpleName(),
            false,
            null,
            false,
            null,
            "Timestamp"
    );

    private final String packageName;
    private final String className;
    private final boolean primitive;

    private final JavaClass componentClass;
    private final boolean container;
    private final ContainerType containerType;
    /**
     * Type for SQLInput readX, SQLOutput writeX
     */
    private final String jdbcAdaptedType;

    public static JavaClass listOf(JavaClass componentClass) {
        return new JavaClass(
                List.class.getPackageName(),
                List.class.getSimpleName(),
                false,
                componentClass,
                true,
                ContainerType.LIST,
                null
        );
    }

    public String getCanonicalName() {
        return packageName + "." + className;
    }

}
