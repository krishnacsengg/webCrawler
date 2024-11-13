long totalRecords = auditFileManagerService.countRequests(bus_proc_id, fileName);
    int totalPages = (int) Math.ceil((double) totalRecords / size);


<select id="countAuditFileManagerRequests" resultType="long">
    SELECT COUNT(*)
    FROM AUDIT_FILEMANAGER_REQUEST
    WHERE BUS_PROC_ID = #{busProcId}
    <if test="fileName != null">
        AND SUCCESS_FILES LIKE CONCAT('%', #{fileName}, '%')
    </if>
    <if test="actionType != null">
        AND ACTION_TYPE = #{actionType}
    </if>
    <if test="actionOwner != null">
        AND USER_ID = #{actionOwner}
    </if>
</select>
