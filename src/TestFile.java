TO_CHAR(created_at, 'MM-DD-YYYY HH:MI:SS AM') AS created_at_12hr


  <insert id="insertUserActions" parameterType="map">
        INSERT INTO user_actions (user_id, action_type, file_name)
        VALUES
        <foreach collection="fileNames" item="fileName" separator=",">
            (#{userID}, #{actionType}, #{fileName})
        </foreach>
    </insert>
