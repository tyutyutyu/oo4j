ALTER SESSION SET CONTAINER=XEPDB1;

CREATE TYPE OO4J.T_SIMPLE_TYPE AS OBJECT (
    test_varchar2      VARCHAR2(5)
);
/

CREATE TYPE OO4J.T_TEST_TYPE AS OBJECT (
    test_varchar2      VARCHAR2(5),
    test_char          CHAR(5),
    test_clob          CLOB,
    test_number        NUMBER(5),
    test_float         FLOAT(5),
    test_date          DATE,
    test_timestamp     TIMESTAMP,
    test_blob          BLOB,
    test_simple_type   OO4J.T_SIMPLE_TYPE
);
/

CREATE TYPE OO4J.T_TEST_TYPE_TABLE AS TABLE OF OO4J.T_TEST_TYPE;
/
