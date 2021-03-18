package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class Param {

    private final JavaClass javaClass;
    private final String javaName;
    private final String name;
    private final String jdbcType;
    private final boolean custom;
    private final String rowMapperType;
    private final String genericType;

    public String getDeclarationType() {
        if (javaClass.getContainerType() == JavaClass.ContainerType.LIST) {
            if (rowMapperType != null) {
                return "List<" + rowMapperType + ">";
            } else {
                return "List<" + genericType + ">";
            }
        } else {
            return javaClass.getClassName();
        }
    }

    public boolean isListType() {
        return javaClass.isContainer() && javaClass.getContainerType() == JavaClass.ContainerType.LIST;
    }

}
