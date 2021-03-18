<#assign now = .now>
package ${packageName};

<#list imports as import>
import ${import};
</#list>


import jakarta.annotation.Generated;

import java.util.List;

@Generated(value = "com.tyutyutyu.oo4j.core.generator.Oo4jCodeGenerator", date = "${now?iso_utc}")
public interface ${className} {

    String SQL_TYPE_NAME = "${schema}.${typeName}";

    static SqlTypeValueFactory.ArraySqlTypeValue<${componentClassName}> createSqlTypeValue(List<${componentClassName}> list) {
        return SqlTypeValueFactory.createForArray(${componentClassName}.class, list);
    }

}