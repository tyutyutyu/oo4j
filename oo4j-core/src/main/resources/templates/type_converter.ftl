<#assign now = .now>
package ${packageName};

import jakarta.annotation.Generated;
import lombok.experimental.UtilityClass;
import oracle.sql.OracleSQLOutput;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLOutput;

@Generated(value = "com.tyutyutyu.oo4j.core.generator.Oo4jCodeGenerator", date = "${now?iso_utc}")
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
