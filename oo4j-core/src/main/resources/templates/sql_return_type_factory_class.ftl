package ${packageName};

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.SqlReturnType;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class SqlReturnTypeFactory {

    private static final Map<Class<?>, SqlDataSqlReturnType> SQL_DATA_CLASS_CACHE = new ConcurrentHashMap<>();

    public static <T> SqlDataSqlReturnType createForSqlData(Class<T> clazz) {
        return SQL_DATA_CLASS_CACHE.computeIfAbsent(
                clazz,
                SqlDataSqlReturnType::new
        );
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
