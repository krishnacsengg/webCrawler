import java.time.LocalDateTime;

public class AuditFileManagerRequest {

    private Long serialNumber; // Primary key for each audit entry
    private int busProcId;     // Business process ID, required for querying records
    private String actionType; // Action taken (e.g., "FetchAuditDetails")
    private String requestPayload; // Query parameters received in the API request
    private String successFiles; // JSON array string representing the list of successful files
    private String responseStatus; // JSON-formatted response status
    private String userId; // ID of the user who performed the action
    private LocalDateTime timestamp; // Date and time the action occurred
