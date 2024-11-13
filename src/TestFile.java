TO_CHAR(created_at, 'MM-DD-YYYY HH:MI:SS AM') AS created_at_12hr


  <insert id="insertUserActions" parameterType="map">
        INSERT INTO user_actions (user_id, action_type, file_name)
        VALUES
        <foreach collection="fileNames" item="fileName" separator=",">
            (#{userID}, #{actionType}, #{fileName})
        </foreach>
    </insert>
public List<Map<String, Object>> getAuditRecords(int busProcId, String fileName, Integer page, Integer size, String actionType, String actionOwner) {
    List<AuditFileManagerRequest> records = mapper.findAuditFileManagerRequests(busProcId, fileName, page, size, actionType, actionOwner);

    return records.stream()
            .map(record -> {
                Map<String, Object> fileRecord = new HashMap<>();
                fileRecord.put("FileName", record.getFileName());
                fileRecord.put("ActionOwner", record.getUserId());
                fileRecord.put("ActionTime", record.getTimestamp());
                fileRecord.put("ActionType", record.getActionType());
                return fileRecord;
            })
            .collect(Collectors.toList());
}
