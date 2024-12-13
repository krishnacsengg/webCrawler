SELECT 
    SID,
    SERIAL#,
    USERNAME,
    SCHEMANAME,
    STATUS,
    PROGRAM,
    MACHINE,
    MODULE,
    LOGON_TIME
FROM 
    V$SESSION
WHERE 
    STATUS = 'ACTIVE';


@PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
public String validateInput(@RequestBody String jsonRequest) {
    System.out.println("Received JSON Input: " + jsonRequest);

    // Hard-coded response
    return """
        {
            "status": "success",
            "message": "Validation completed",
            "code": 200
        }
    """;
}
