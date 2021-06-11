<#assign now = .now>
package ${packageName};

import jakarta.annotation.Generated;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import oracle.jdbc.OracleConnection;
import org.springframework.jdbc.core.SqlTypeValue;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Generated(value = "com.tyutyutyu.oo4j.core.generator.Oo4jCodeGenerator", date = "${now?iso_utc}")
@UtilityClass
public class SqlTypeValueFactory {

    public static <T> ArraySqlTypeValue<T> createForArray(Class<T> clazz, List<T> list) {
        return new ArraySqlTypeValue<>(clazz, list);
    }

    @RequiredArgsConstructor
    public static class ArraySqlTypeValue<T> implements SqlTypeValue {

        private final Class<?> sqlTypeClass;
        private final List<T> list;

        @Override
        public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName) throws SQLException {
            if (list == null) {
                ps.setNull(paramIndex, Types.ARRAY);
            } else {
                OracleConnection oracleConnection = ps.getConnection().unwrap(OracleConnection.class);
                oracleConnection.getTypeMap().put(typeName, sqlTypeClass);
                Array array = oracleConnection.createOracleArray(
                        typeName,
                        list.toArray((T[]) java.lang.reflect.Array.newInstance(sqlTypeClass, 0))
                );
                ps.setArray(paramIndex, array);
            }
        }
    }

}
