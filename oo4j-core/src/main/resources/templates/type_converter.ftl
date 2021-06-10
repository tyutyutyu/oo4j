package ${packageName};

import lombok.experimental.UtilityClass;
import oracle.sql.OracleSQLOutput;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLOutput;

@UtilityClass
public class TypeConverter {

    public static byte[] toByteArray(Blob blob) throws SQLException {
        if (blob == null) {
            return null;
        }

        try {
            return blob.getBytes(1, (int) blob.length());
        } finally {
            blob.free();
        }
    }

    public static Blob toBlob(SQLOutput stream, byte[] bytes) throws SQLException {
        Blob blob = ((OracleSQLOutput) stream).getSTRUCT().getOracleConnection().createBlob();
        blob.setBytes(1, bytes);
        return blob;
    }

}
