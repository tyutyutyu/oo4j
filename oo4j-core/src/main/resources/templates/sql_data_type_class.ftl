<#assign now = .now>
package ${packageName};

<#list imports as import>
import ${import};
</#list>

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

@Generated(value = "TODO", date = "${now?iso_utc}")
@Getter
@NoArgsConstructor
public class ${className} implements SQLData {

    public static final String SQL_TYPE_NAME = "${schema}.${typeName}";

    <#list fields as field>
    private ${field.javaSimpleType} ${field.javaName};
    </#list>

    public ${className}(
        <#list fields as field>
        ${field.javaSimpleType} ${field.javaName}<#if field?has_next>,</#if>
        </#list>
    ) {
        <#list fields as field>
        this.${field.javaName} = ${field.javaName};
        </#list>
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return SQL_TYPE_NAME;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) {
        return new ${className}(
            <#list fields as field>
            ${field.javaName}<#if field?has_next>,</#if>
            </#list>
        );
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        <#list fields as field>
        stream.write${field.javaSimpleType}(${field.javaName});
        </#list>
    }

}
