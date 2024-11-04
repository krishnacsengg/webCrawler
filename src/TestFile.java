SET SERVEROUTPUT ON;

DECLARE
    v_search_word VARCHAR2(100) := ''; -- Replace with the word you want to search for
    v_sql         CLOB;
    v_count       INTEGER;
BEGIN
    FOR t IN (SELECT table_name, column_name
              FROM all_tab_columns
              WHERE owner = 'PBIRS' -- Replace with your schema name
              AND data_type = 'VARCHAR2') 
    LOOP
        BEGIN
            -- Build the query to count occurrences
            v_sql := 'SELECT COUNT(*) FROM ' || t.table_name ||
                     ' WHERE ' || t.column_name || ' LIKE ''%' || v_search_word || '%''';
            
            -- Execute the query and store the count result in v_count
            EXECUTE IMMEDIATE v_sql INTO v_count;
            
            -- Print the result if occurrences are found
            IF v_count > 0 THEN
                DBMS_OUTPUT.PUT_LINE('Table: ' || t.table_name || 
                                     ', Column: ' || t.column_name || 
                                     ', Occurrences: ' || v_count);
            END IF;
        EXCEPTION
            -- Catch any errors, such as ORA-00942, and continue with the next iteration
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Skipping table: ' || t.table_name || 
                                     ' due to error: ' || SQLERRM);
        END;
    END LOOP;
END;
/
