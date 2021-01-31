CREATE TYPE OO4J.T_TEST_TYPE AS OBJECT (
    test_varchar2      VARCHAR2(5),
    test_char          CHAR(5),
    test_clob          CLOB,
    test_number        NUMBER(5),
    test_float         FLOAT(5),
    test_date          DATE,
    test_timestamp     TIMESTAMP,
    test_raw           RAW(5),
    test_blob          BLOB
);