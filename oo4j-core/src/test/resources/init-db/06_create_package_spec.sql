ALTER SESSION SET CONTAINER=XEPDB1;

CREATE OR REPLACE PACKAGE OO4J.TEST_PACKAGE AS

    PROCEDURE TEST_PROCEDURE2(
        p_test_varchar2   IN  VARCHAR2,
        test_varchar2     OUT VARCHAR2
    );

   PROCEDURE TEST_PROCEDURE2(
        p_test_varchar2   IN  VARCHAR2
    );

END TEST_PACKAGE;
/