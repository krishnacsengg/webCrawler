-- Tables present in ADMIN_USER but missing in APP_USER
SELECT table_name
FROM all_tables
WHERE owner = 'ADMIN_USER'
MINUS
SELECT table_name
FROM all_tables
WHERE owner = 'APP_USER';

-- Tables present in APP_USER but missing in ADMIN_USER
SELECT table_name
FROM all_tables
WHERE owner = 'APP_USER'
MINUS
SELECT table_name
FROM all_tables
WHERE owner = 'ADMIN_USER';


SELECT 'ADMIN_USER -> APP_USER' AS DIFF_TYPE, a.table_name, a.column_name, a.data_type, a.data_length, a.data_precision, a.data_scale
FROM all_tab_columns a
LEFT JOIN all_tab_columns b
    ON a.table_name = b.table_name
    AND a.column_name = b.column_name
    AND a.data_type = b.data_type
    AND a.data_length = b.data_length
    AND a.data_precision = b.data_precision
    AND a.data_scale = b.data_scale
    AND b.owner = 'APP_USER'
WHERE a.owner = 'ADMIN_USER'
  AND a.table_name = 'EMPLOYEES'
  AND b.column_name IS NULL

UNION ALL

-- Columns in APP_USER.EMPLOYEES but not in ADMIN_USER.EMPLOYEES
SELECT 'APP_USER -> ADMIN_USER' AS DIFF_TYPE, b.table_name, b.column_name, b.data_type, b.data_length, b.data_precision, b.data_scale
FROM all_tab_columns a
RIGHT JOIN all_tab_columns b
    ON a.table_name = b.table_name
    AND a.column_name = b.column_name
    AND a.data_type = b.data_type
    AND a.data_length = b.data_length
    AND a.data_precision = b.data_precision
    AND a.data_scale = b.data_scale
    AND a.owner = 'ADMIN_USER'
WHERE b.owner = 'APP_USER'
  AND b.table_name = 'EMPLOYEES'
  AND a.column_name IS NULL
ORDER BY table_name, column_name;
