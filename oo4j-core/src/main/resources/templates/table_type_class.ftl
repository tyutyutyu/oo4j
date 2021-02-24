<#assign now = .now>
package ${packageName};

import jakarta.annotation.Generated;

import java.util.List;

@Generated(value = "TODO", date = "${now?iso_utc}")
public interface ${className} {

    String SQL_TYPE_NAME = "${schema}.${typeName}";

    static SqlTypeValueFactory.ArraySqlTypeValue<${componentClassName}> createSqlTypeValue(List<${componentClassName}> list) {
        return SqlTypeValueFactory.createForArray(${componentClassName}.class, list);
    }

}