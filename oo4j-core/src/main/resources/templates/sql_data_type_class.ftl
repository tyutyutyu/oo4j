<#assign now = .now>
package ${packageName};

<#list imports as import>
import ${import};
</#list>

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.jdbc.core.SqlReturnType;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

@AllArgsConstructor
@Builder
@Generated(value = "com.tyutyutyu.oo4j.core.generator.Oo4jCodeGenerator", date = "${now?iso_utc}")
@Getter
@NoArgsConstructor
@ToString
public class ${className} implements SQLData {

    public static final String SQL_TYPE_NAME = "${schema}.${typeName}";
    public static final SqlReturnType SQL_RETURN_TYPE = SqlReturnTypeFactory.createForSqlData(${className}.class);

    <#list fields as field>
    private ${field.javaClass.className} ${field.name};
    </#list>

    @Override
    public String getSQLTypeName() {
        return SQL_TYPE_NAME;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        <#list fields as field>
        <#if field.custom>
        this.${field.name} = stream.read${field.javaClass.jdbcAdaptedType}(${field.javaClass.className}.class);
        <#elseif field.javaClass.jdbcAdaptedType == 'Bytes'>
        this.${field.name} = TypeConverter.toByteArray(stream.readBlob());
        <#else>
        this.${field.name} = stream.read${field.javaClass.jdbcAdaptedType}();
        </#if>
        </#list>
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        <#list fields as field>
        <#if field.javaClass.jdbcAdaptedType == 'Bytes'>
        stream.writeBlob(TypeConverter.toBlob(stream, ${field.name}));
        <#else>
        stream.write${field.javaClass.jdbcAdaptedType}(${field.name});
        </#if>
        </#list>
    }

}
