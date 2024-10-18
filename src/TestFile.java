package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.FailureDetails;
import com.example.model.FileDeletionRequest;
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

    public void processDeletionRequest(FileDeletionRequest request) throws AccessDeniedException {
        // Step 1: Access check
        boolean hasAccess = accessValidationService.checkUserAccess(request.getSoeid(), request.getBusProcId());
        if (!hasAccess) {
            auditService.logAccessDenied(request);
            throw new AccessDeniedException("User does not have access to busProcId: " + request.getBusProcId());
        }

        // Step 2: Vulnerability check
        List<File> validFiles = vulnerabilityCheckService.checkFilesForVulnerability(request.getFileDetails());
        List<FailureDetails> failureFiles = new ArrayList<>();

        // Add unsafe files to failure list with appropriate error message
        request.getFileDetails().stream()
            .filter(file -> validFiles.stream().noneMatch(vf -> vf.getName().equals(file)))
            .forEach(unsafeFile -> failureFiles.add(new FailureDetails(unsafeFile, "Vulnerability check failed")));

        // Step 3: Process file deletions
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

        // Step 4: Log the results
        auditService.logDeletionAudit(request, successFiles, failureFiles);
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
