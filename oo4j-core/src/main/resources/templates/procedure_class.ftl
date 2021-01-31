<#assign now = .now>
package ${package};

<#list imports as import>
import ${import};
</#list>

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Generated(value = "TODO", date = "${now?iso_utc}")
public class ${className} {

    private static final String SQL = "${sql}";

    private final DataSource dataSource;
    private final Boolean autoCommit;
    private final String sql;

    public ${className}(DataSource dataSource) {
        this(dataSource, null, null);
    }

    public ${className}(DataSource dataSource) {
        this(dataSource, null);
    }

    public ${className}(DataSource dataSource, Boolean autoCommit) {
        this(dataSource, autoCommit);
    }

    public ${className}(DataSource dataSource, Boolean autoCommit) {

        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    public Out call(
            <#list inParams as param>
            ${param.javaSimpleType} ${param.javaName}<#if param?has_next>,</#if>
            </#list>
    ) throws SQLException {

        try (Connection connection = getConnection();
             CallableStatement stmt = connection.prepareCall(SQL)) {

            <#list inParams as param>
            stmt.set${param.setterJavaSimpleType}("${param.name}", ${param.javaName});
            </#list>

            <#list outParams as param>
            stmt.registerOutParameter("${param.name}", Types.${param.sqlType});
            </#list>

            stmt.execute();

            return new Out(
                    <#list outParams as param>
                    <#if param.custom>
                    stmt.get${param.setterJavaSimpleType}("${param.name}", ${param.javaSimpleType}.class)<#if param?has_next>,</#if>
                    <#else>
                    stmt.get${param.setterJavaSimpleType}("${param.name}")<#if param?has_next>,</#if>
                    </#if>
                    </#list>
            );
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        if (autoCommit != null) {
            connection.setAutoCommit(autoCommit);
        }
        <#list addToTypeMap as type>
        connection.getTypeMap().put(${type}.SQL_TYPE_NAME, ${type}.class);
        </#list>
        return connection;
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Out {
        <#list outParams as param>
        private final ${param.javaSimpleType} ${param.javaName};
        </#list>
    }

}
