SET LONG 10000;
SET PAGESIZE 500;
SET LINESIZE 200;

-- Generate DDL for all tables
SELECT DBMS_METADATA.GET_DDL('TABLE', table_name, 'ADMIN_USER') AS ddl_statement
FROM all_tables
WHERE owner = 'ADMIN_USER';
