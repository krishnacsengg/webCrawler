String filePath = "/data/files";
        List<String> fileNames = Arrays.asList("file1.txt", "file2.txt", "file3.txt");

        // Call the batch update method
        service.updateFileStatusToDeletedBatch(filePath, fileNames);


<update id="updateFileStatusToDeletedBatch" parameterType="map">
  UPDATE AUDIT_RUNID_ARCHIVAL
  SET STATUS = 'Deleted'
  WHERE FILE_PATH = #{filePath} 
  AND FILE_NAME IN 
  <foreach collection="fileNames" item="fileName" open="(" separator="," close=")">
    #{fileName}
  </foreach>
</update>










