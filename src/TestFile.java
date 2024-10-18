package com.example.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.FailureDetails;
import com.example.service.AccessValidationService;
import com.example.service.VulnerabilityCheckService;
import com.example.service.AuditService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileDeletionService {

    @Autowired
    private AccessValidationService accessValidationService;

    @Autowired
    private VulnerabilityCheckService vulnerabilityCheckService;

    @Autowired
    private AuditService auditService;

    public void processDeletionRequest(String payload) throws AccessDeniedException {
        // Step 1: Parse the payload
        JSONObject request = new JSONObject(payload);
        String filePath = request.getString("filePath");
        JSONArray fileDetailsArray = request.getJSONArray("fileDetails");
        int busProcId = request.getInt("busProcId");
        String soeid = request.getString("soeid");

        List<String> fileDetails = new ArrayList<>();
        for (int i = 0; i < fileDetailsArray.length(); i++) {
            fileDetails.add(fileDetailsArray.getString(i));
        }

        // Step 2: Access check
        boolean hasAccess = accessValidationService.checkUserAccess(soeid, busProcId);
        if (!hasAccess) {
            auditService.logAccessDenied(payload, busProcId, soeid);
            throw new AccessDeniedException("User does not have access to busProcId: " + busProcId);
        }

        // Step 3: Vulnerability check
        List<File> validFiles = vulnerabilityCheckService.checkFilesForVulnerability(fileDetails);
        List<FailureDetails> failureFiles = new ArrayList<>();

        // Add unsafe files to failure list with appropriate error message
        fileDetails.stream()
            .filter(file -> validFiles.stream().noneMatch(vf -> vf.getName().equals(file)))
            .forEach(unsafeFile -> failureFiles.add(new FailureDetails(unsafeFile, "Vulnerability check failed")));

        // Step 4: Process file deletions
        List<String> successFiles = new ArrayList<>();
        for (File file : validFiles) {
            try {
                boolean isDeleted = deleteFile(file);
                if (isDeleted) {
                    successFiles.add(file.getName());
                } else {
                    failureFiles.add(new FailureDetails(file.getName(), "File could not be deleted"));
                }
            } catch (IOException | SecurityException e) {
                failureFiles.add(new FailureDetails(file.getName(), e.getMessage()));
            }
        }

        // Step 5: Log the results
        auditService.logDeletionAudit(payload, filePath, busProcId, soeid, successFiles, failureFiles);
    }

    private boolean deleteFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            throw new FileNotFoundException("File not found: " + file.getName());
        }
    }
}



package com.example.service;

import com.example.model.FailureDetails;
import com.example.model.FileDeletionRequest;
import com.example.model.AuditLog;
import com.example.mapper.AuditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    @Autowired
    private AuditMapper auditMapper;

    public void logAccessDenied(FileDeletionRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setSoeid(request.getSoeid());
        auditLog.setBusProcId(request.getBusProcId());
        auditLog.setAccessDenied(true);
        auditLog.setRequestPayload(request.toString());
        auditLog.setTimestamp(LocalDateTime.now());
        auditMapper.insertAuditLog(auditLog);
    }

    public void logDeletionAudit(FileDeletionRequest request, List<String> successFiles, List<FailureDetails> failureFiles) {
        String successFilesStr = String.join(",", successFiles);
        String failureFilesStr = failureFiles.stream()
            .map(f -> f.getFileName() + " (Error: " + f.getErrorMessage() + ")")
            .collect(Collectors.joining(","));

        AuditLog auditLog = new AuditLog();
        auditLog.setSoeid(request.getSoeid());
        auditLog.setFilePath(request.getFilePath());
        auditLog.setBusProcId(request.getBusProcId());
        auditLog.setSuccessFiles(successFilesStr);
        auditLog.setFailureFiles(failureFilesStr);
        auditLog.setRequestPayload(request.toString());
        auditLog.setTimestamp(LocalDateTime.now());

        auditMapper.insertAuditLog(auditLog);
    }
}


package com.example.model;

import java.util.List;

public class FileDeletionRequest {
    private String filePath;
    private List<String> fileDetails;
    private int busProcId;
    private String soeid;

    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(List<String> fileDetails) {
        this.fileDetails = fileDetails;
    }

    public int getBusProcId() {
        return busProcId;
    }

    public void setBusProcId(int busProcId) {
        this.busProcId = busProcId;
    }

    public String getSoeid() {
        return soeid;
    }

    public void setSoeid(String soeid) {
        this.soeid = soeid;
    }
}



CREATE TABLE AUDIT_LOG (
    ID              NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    SOEID           VARCHAR2(50) NOT NULL,         -- User ID (SOEID)
    FILE_PATH       VARCHAR2(500) NOT NULL,        -- Directory path
    BUS_PROC_ID     NUMBER NOT NULL,               -- Business process ID
    SUCCESS_FILES   CLOB,                          -- Concatenated list of successfully deleted files
    FAILURE_FILES   CLOB,                          -- Concatenated list of failed files with error messages
    REQUEST_PAYLOAD CLOB,                          -- Full request payload in JSON format
    ACCESS_DENIED   CHAR(1) DEFAULT 'N',           -- Flag to denote access denial (Y or N)
    TIMESTAMP       TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of when the request was processed
);

@RestController
@RequestMapping("/api/v1/files")
public class FileDeletionController {

    @Autowired
    private FileDeletionService fileDeletionService;

    @PostMapping("/delete")
    public ResponseEntity<?> deleteFiles(@RequestBody FileDeletionRequest request) {
        try {
            fileDeletionService.processDeletionRequest(request);
            return ResponseEntity.ok("Deletion request processed");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}

