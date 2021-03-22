package ${packageName};

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.SqlReturnType;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@UtilityClass
public class SqlReturnTypeFactory {

    private static final Map<String, ArraySqlReturnType<?>> ARRAY_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, SqlDataSqlReturnType> SQL_DATA_CLASS_CACHE = new ConcurrentHashMap<>();

    public static SqlReturnType createForArray(String componentSqlTypeName, Class<?> componentClass) {
        return ARRAY_CLASS_CACHE.computeIfAbsent(
                componentSqlTypeName,
                s -> new ArraySqlReturnType<>(componentSqlTypeName, componentClass)
        );
    }

    public static SqlDataSqlReturnType createForSqlData(Class<?> clazz) {
        return SQL_DATA_CLASS_CACHE.computeIfAbsent(
                clazz,
                SqlDataSqlReturnType::new
        );
    }

    @RequiredArgsConstructor
    public static class ArraySqlReturnType<T> implements SqlReturnType {

        private final String componentSqlTypeName;
        private final Class<T> componentClass;

        @Override
        public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName) throws SQLException {

            cs.getConnection().getTypeMap().put(componentSqlTypeName, componentClass);

            Array array = cs.getArray(paramIndex);
            if (array == null) {
                return null;
            }

            return Arrays
                    .stream((Object[]) array.getArray())
                    .map(s -> (T) s)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    @RequiredArgsConstructor
    public static class SqlDataSqlReturnType implements SqlReturnType {

        private final Class<?> sqlTypeClass;

        @Override
        public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName) throws SQLException {
            cs.getConnection().getTypeMap().put(typeName, sqlTypeClass);
            return cs.getObject(paramIndex);
        }
    }

}
