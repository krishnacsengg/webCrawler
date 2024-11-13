<select id="findAuditFileManagerRequests" resultType="com.example.demo.model.AuditFileManagerRequest">
    SELECT *
    FROM AUDIT_FILEMANAGER_REQUEST
    WHERE PROC_ID = #{busProcId}
      AND SUCCESS_FILES != '[]'
      <if test="fileName != null">
          AND SUCCESS_FILES LIKE '%' || #{fileName} || '%'
      </if>
      <if test="actionType != null">
          AND ACTION_TYPE = #{actionType}
      </if>
      <if test="actionOwner != null">
          AND USERID = #{actionOwner}
      </if>
    ORDER BY TIMESTAMP DESC
    <if test="page != null and size != null">
        OFFSET (#{page} - 1) * #{size} ROWS FETCH NEXT #{size} ROWS ONLY
    </if>
</select>
