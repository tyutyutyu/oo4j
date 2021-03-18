CREATE OR REPLACE PROCEDURE OO4J.TEST_PROCEDURE(
    p_test_varchar2   IN  VARCHAR2,
    p_test_char       IN  CHAR,
    p_test_clob       IN  CLOB,
    p_test_number     IN  NUMBER,
    p_test_float      IN  FLOAT,
    p_test_date       IN  DATE,
    p_test_timestamp  IN  TIMESTAMP,
    p_test_blob       IN  BLOB,
    p_test_type       IN  OO4J.T_TEST_TYPE,
    p_test_table_type IN  OO4J.T_TEST_TYPE_TABLE,
    test_varchar2     OUT VARCHAR2,
    test_char         OUT CHAR,
    test_clob         OUT CLOB,
    test_number       OUT NUMBER,
    test_float        OUT FLOAT,
    test_date         OUT DATE,
    test_timestamp    OUT TIMESTAMP,
    test_blob         OUT BLOB,
    test_type         OUT OO4J.T_TEST_TYPE,
    test_table_type   OUT OO4J.T_TEST_TYPE_TABLE
) AS
BEGIN
  NULL;
END TEST_PROCEDURE;
