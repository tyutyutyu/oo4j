<#assign now = .now>
package ${packageName};

<#list imports as import>
import ${import};
</#list>

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Generated(value = "TODO", date = "${now?iso_utc}")
public class ${className}<#if rowMappers?size != 0><<#list rowMappers as rowMapper>${rowMapper.type}<#if rowMapper?has_next>, </#if></#list>></#if> {

    private static final String SQL = "${sql}";

    private final StoredProcedure storedProcedure = new GenericStoredProcedure();

    public ${className}(
        DataSource dataSource<#list rowMappers as rowMapper>,
        RowMapper<${rowMapper.type}> ${rowMapper.name}RowMapper<#if rowMapper?has_next><#rt></#if></#list>
    ) {
        storedProcedure.setDataSource(dataSource);
        storedProcedure.setSql(SQL);

        <#list inParams as param>
        <#if param.custom>
        storedProcedure.declareParameter(new SqlParameter("${param.name}", Types.${param.jdbcType}, ${param.javaClass.className}.SQL_TYPE_NAME));
        <#elseif param.javaClass.container>
        storedProcedure.declareParameter(new SqlParameter("${param.name}", Types.${param.jdbcType}, ${param.javaClass.className}.SQL_TYPE_NAME));
        <#else>
        storedProcedure.declareParameter(new SqlParameter("${param.name}", Types.${param.jdbcType}));
        </#if>
        </#list>

        <#list inOutParams as param>
        <#if param.custom>
        storedProcedure.declareParameter(new SqlInOutParameter("${param.name}", Types.${param.jdbcType}, ${param.javaClass.className}.SQL_TYPE_NAME, ${param.javaClass.className}.SQL_RETURN_TYPE));
        <#elseif param.rowMapperType??>
        storedProcedure.declareParameter(new SqlOutParameter("${param.name}", Types.${param.jdbcType}, ${param.javaName}RowMapper));
        <#elseif param.javaClass.container>
        storedProcedure.declareParameter(new SqlInOutParameter("${param.name}", Types.${param.jdbcType}, ${param.javaClass.className}.SQL_TYPE_NAME));
        <#else>
        storedProcedure.declareParameter(new SqlInOutParameter("${param.name}", Types.${param.jdbcType}));
        </#if>
        </#list>

        <#list outParams as param>
        <#if param.custom>
        storedProcedure.declareParameter(new SqlOutParameter("${param.name}", Types.${param.jdbcType}, ${param.javaClass.className}.SQL_TYPE_NAME, ${param.javaClass.className}.SQL_RETURN_TYPE));
        <#elseif param.rowMapperType??>
        storedProcedure.declareParameter(new SqlOutParameter("${param.name}", Types.${param.jdbcType}, ${param.javaName}RowMapper));
        <#else>
        storedProcedure.declareParameter(new SqlOutParameter("${param.name}", Types.${param.jdbcType}));
        </#if>
        </#list>

        storedProcedure.compile();
    }

    public Out call(
            <#list inParams as param>
            ${param.declarationType} ${param.javaName}<#if param?has_next || inOutParams?size != 0>,</#if>
            </#list>
            <#list inOutParams as param>
            ${param.declarationType} ${param.javaName}<#if param?has_next>,</#if>
            </#list>
    ) {

        Map<String, Object> results = storedProcedure.execute(
            <#list inParams as param>
            <#if param.javaClass.container>
            ${param.javaClass.className}.createSqlTypeValue(${param.javaName})<#rt>
            <#else>
            ${param.javaName}<#rt>
            </#if>
            <#if param?has_next || inOutParams?size != 0><#lt>,</#if>
            </#list>
            <#list inOutParams as param>
            <#if param.javaClass.container>
            ${param.javaClass.className}.createSqlTypeValue(${param.javaName})<#rt>
            <#else>
            ${param.javaName}<#rt>
            </#if>
            <#if param?has_next><#lt>,</#if>
            </#list>
        );

        return new Out(
            <#list inOutParams as param>
            (${param.declarationType}) results.get("${param.name}")<#if param?has_next || outParams?size != 0>,</#if>
            </#list>
            <#list outParams as param>
            (${param.declarationType}) results.get("${param.name}")<#if param?has_next>,</#if>
            </#list>
        );
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public class Out {
        <#list inOutParams as param>
        private final ${param.declarationType} ${param.javaName};
        </#list>
        <#list outParams as param>
        private final ${param.declarationType} ${param.javaName};
        </#list>
    }

}
