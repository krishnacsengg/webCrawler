import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditFileManagerController {

    @Autowired
    private AuditFileManagerService service;

    @GetMapping("/file-manager-requests")
    public ResponseEntity<List<Map<String, Object>>> getAuditFileManagerRequests(
            @RequestParam("bus_proc_id") int busProcId,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "actionType", required = false) String actionType,
            @RequestParam(value = "actionOwner", required = false) String actionOwner) {

        String requestPayload = String.format("bus_proc_id=%d&fileName=%s&page=%s&size=%s",
                busProcId, fileName, page, size);

        try {
            List<Map<String, Object>> result = service.getAuditRecords(busProcId, fileName, page, size, actionType, actionOwner);
            service.logAuditEntry(busProcId, "FetchAuditDetails", requestPayload, 200, result.size());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            service.logAuditEntry(busProcId, "FetchAuditDetails", requestPayload, 500, 0);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditFileManagerService {

    @Autowired
    private AuditFileManagerRequestMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    public void logAuditEntry(int busProcId, String actionType, String requestPayload, int status, int numberOfRecords) {
        String responseStatus = String.format("{\"ResponseStatus\": %d, \"NumberOfRecord\": %d}", status, numberOfRecords);
        mapper.insertAuditRequest(busProcId, actionType, requestPayload, responseStatus);
    }

    public List<Map<String, Object>> getAuditRecords(int busProcId, String fileName, Integer page, Integer size, String actionType, String actionOwner) {
        List<AuditFileManagerRequest> records = mapper.findAuditFileManagerRequests(busProcId, fileName, page, size, actionType, actionOwner);

        return records.stream()
                .flatMap(record -> parseAndFilterFiles(record, fileName).stream())
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> parseAndFilterFiles(AuditFileManagerRequest record, String fileName) {
        List<Map<String, Object>> fileRecords = new ArrayList<>();
        
        try {
            String[] files = objectMapper.readValue(record.getSuccessFiles(), String[].class);
            
            for (String file : files) {
                if (fileName == null || file.contains(fileName)) {
                    fileRecords.add(createFileRecord(file.trim(), record));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fileRecords;
    }

    private Map<String, Object> createFileRecord(String fileName, AuditFileManagerRequest record) {
        return Map.of(
                "FileName", fileName,
                "ActionOwner", record.getUserId(),
                "ActionTime", record.getTimestamp(),
                "ActionType", record.getActionType()
        );
    }
}



@Mapper
public interface AuditFileManagerRequestMapper {

    List<AuditFileManagerRequest> findAuditFileManagerRequests(
            @Param("busProcId") int busProcId,
            @Param("fileName") String fileName,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("actionType") String actionType,
            @Param("actionOwner") String actionOwner
    );

    void insertAuditRequest(@Param("busProcId") int busProcId,
                            @Param("actionType") String actionType,
                            @Param("requestPayload") String requestPayload,
                            @Param("responseStatus") String responseStatus);
}


<insert id="insertAuditRequest">
    INSERT INTO AUDIT_FILEMANAGER_REQUEST (BUS_PROC_ID, ACTION_TYPE, REQUEST_PAYLOAD, RESPONSE_STATUS)
    VALUES (#{busProcId}, #{actionType}, #{requestPayload}, #{responseStatus})
</insert>


    <select id="findAuditFileManagerRequests" resultType="com.example.demo.model.AuditFileManagerRequest">
        SELECT *
        FROM AUDIT_FILEMANAGER_REQUEST
        WHERE PROC_ID = #{busProcId}
          AND SUCCESS_FILES != '[]'
          <if test="fileName != null">
              AND SUCCESS_FILES LIKE CONCAT('%', #{fileName}, '%')
          </if>
          <if test="actionType != null">
              AND ACTION_TYPE = #{actionType}
          </if>
          <if test="actionOwner != null">
              AND USERID = #{actionOwner}
          </if>
        ORDER BY TIMESTAMP DESC
        <if test="page != null and size != null">
            LIMIT #{size} OFFSET #{page} * #{size}
        </if>
    </select>

    
