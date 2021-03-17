package ${packageName};

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import oracle.jdbc.OracleConnection;
import org.springframework.jdbc.core.SqlTypeValue;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class SqlTypeValueFactory {

    private static final Map<Class<?>, ArraySqlTypeValue<?>> ARRAY_CLASS_CACHE = new ConcurrentHashMap<>();

    public static <T> ArraySqlTypeValue<T> createForArray(Class<T> clazz, List<T> list) {
        return (ArraySqlTypeValue<T>) ARRAY_CLASS_CACHE.computeIfAbsent(
                clazz,
                aClazz -> new ArraySqlTypeValue<>(aClazz, list.toArray(Object[]::new))
        );
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
