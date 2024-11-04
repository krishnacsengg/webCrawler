DECLARE
    v_search_word VARCHAR2(100) := 'London'; -- Word we’re searching for
    v_sql         CLOB;
BEGIN
    FOR t IN (SELECT table_name, column_name
              FROM all_tab_columns
              WHERE owner = 'HR_SCHEMA' -- Replace with your schema name
              AND data_type = 'VARCHAR2') 
    LOOP
        v_sql := 'SELECT ''' || t.table_name || ''' AS table_name, ''' ||
                 t.column_name || ''' AS column_name, COUNT(*) AS occurrences ' ||
                 'FROM ' || t.table_name ||
                 ' WHERE ' || t.column_name || ' LIKE ''%' || v_search_word || '%''';
        
        EXECUTE IMMEDIATE v_sql;
    END LOOP;
END;
