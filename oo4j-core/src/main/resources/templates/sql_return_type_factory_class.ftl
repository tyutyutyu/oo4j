package ${packageName};

import lombok.AccessLevel;
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
    private static final BlobToByteArraySqlReturnType BLOB_TO_BYTE_ARRAY_SQL_RETURN_TYPE = new BlobToByteArraySqlReturnType();

    public static SqlReturnType createForArray(String componentSqlTypeName, Class<?> componentClass) {
        return ARRAY_CLASS_CACHE.computeIfAbsent(
                componentSqlTypeName,
                s -> new ArraySqlReturnType<>(componentSqlTypeName, componentClass)
        );
    }

    public static SqlReturnType createForSqlData(Class<?> clazz) {
        return SQL_DATA_CLASS_CACHE.computeIfAbsent(
                clazz,
                SqlDataSqlReturnType::new
        );
    }

    public static SqlReturnType createForBlob() {
        return BLOB_TO_BYTE_ARRAY_SQL_RETURN_TYPE;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SqlDataSqlReturnType implements SqlReturnType {

        private final Class<?> sqlTypeClass;

        @Override
        public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName) throws SQLException {
            cs.getConnection().getTypeMap().put(typeName, sqlTypeClass);
            return cs.getObject(paramIndex);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BlobToByteArraySqlReturnType implements SqlReturnType {

        @Override
        public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName) throws SQLException {
            return TypeConverter.toByteArray(cs.getBlob(paramIndex));
        }
    }

}