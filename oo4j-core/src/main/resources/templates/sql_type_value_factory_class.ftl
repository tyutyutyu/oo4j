package ${packageName};

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import oracle.jdbc.OracleConnection;
import org.springframework.jdbc.core.SqlTypeValue;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@UtilityClass
public class SqlTypeValueFactory {

    public static <T> ArraySqlTypeValue<T> createForArray(Class<T> clazz, List<T> list) {
        return new ArraySqlTypeValue<>(clazz, list.toArray((T[]) java.lang.reflect.Array.newInstance(clazz, 0)));
    }

    @RequiredArgsConstructor
    public static class ArraySqlTypeValue<T> implements SqlTypeValue {

        private final Class<?> sqlTypeClass;
        private final T[] javaArray;

        @Override
        public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName) throws SQLException {
            OracleConnection oracleConnection = ps.getConnection().unwrap(OracleConnection.class);
            oracleConnection.getTypeMap().put(typeName, sqlTypeClass);
            Array array = oracleConnection.createOracleArray(typeName, javaArray);
            ps.setArray(paramIndex, array);
        }
    }

}
