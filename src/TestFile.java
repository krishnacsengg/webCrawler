public class TarFileReader {
    public static File extractTarFile(File tarFile, String outputDir) throws IOException {
        File extractedFile = null;
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(tarFile))) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                extractedFile = new File(outputDir, entry.getName());
                try (OutputStream out = new FileOutputStream(extractedFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = tarIn.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }
                }
            }
        }
        return extractedFile;
    }
}

 ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

try {
            File extractedFile = TzFileExtractor.extractTzFile(file.getResource().getFile(), "outputDir");
            List<ManagedGeography> geographies = JsonParser.parseJsonFile(extractedFile);
            service.saveAll(geographies);
            return "File processed and data saved successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing file: " + e.getMessage();
        }



    public static List<ManagedGeography> parseJsonFile(File file) throws IOException {
        return objectMapper.readValue(file,
            objectMapper.getTypeFactory().constructCollectionType(List.class, ManagedGeography.class));
    }


public class ManagedGeography {
    @JsonProperty("SetID")
    private String setId;

    @JsonProperty("ParentNode")
    private String parentNode;

    @JsonProperty("ManagedGeographyID")
    private String managedGeographyId;

    @JsonProperty("EffectiveDate")
    private ZonedDateTime effectiveDate;

    @JsonProperty("Status")
    private String status;

        @Mapper
public interface ManagedGeographyMapper {
    @Insert({
        "<script>",
        "INSERT INTO ManagedGeography (SetID, ParentNode, ManagedGeographyID, EffectiveDate, Status) VALUES",
        "<foreach collection='geographies' item='geo' separator=','>",
        "(#{geo.setId}, #{geo.parentNode}, #{geo.managedGeographyId}, #{geo.effectiveDate}, #{geo.status})",
        "</foreach>",
        "</script>"
    })
    void batchInsert(@Param("geographies") List<ManagedGeography> geographies);
    
    // If using Option A (batch executor), you might also have:
    @Insert("INSERT INTO ManagedGeography (SetID, ParentNode, ManagedGeographyID, EffectiveDate, Status) " +
            "VALUES (#{setId}, #{parentNode}, #{managedGeographyId}, #{effectiveDate}, #{status})")
    void insert(ManagedGeography managedGeography);
}
        
           public void saveAll(List<ManagedGeography> geographies) {
        if (geographies == null || geographies.isEmpty()) {
            return;
        }

        int total = geographies.size();
        int batches = (total / batchSize) + (total % batchSize == 0 ? 0 : 1);

        for (int i = 0; i < batches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, total);
            List<ManagedGeography> batchList = geographies.subList(start, end);
            mapper.batchInsert(batchList);
            // Optionally, log progress
            // log.info("Inserted batch {}: {} records", i + 1, batchList.size());
        }
    }

public void saveAll(List<ManagedGeography> geographies) {
        if (geographies == null || geographies.isEmpty()) {
            return; // No data to process
        }

        // Divide the list into smaller batches
        int totalSize = geographies.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            List<ManagedGeography> batch = geographies.subList(i, Math.min(i + batchSize, totalSize));
            mapper.batchInsert(batch);
        }
    }

public class JsonFileProcessor {
    public static List<Result> processJsonFile(File file) throws IOException {
        List<Result> results = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                JsonData jsonData = mapper.readValue(line, JsonData.class);
                results.addAll(jsonData.getResults());
            }
        }
        return results;
    }
}

File extractedFile = TarFileReader.extractTarFile(tarFile, "output");
        List<Result> results = JsonFileProcessor.processJsonFile(extractedFile);
        resultService.saveResults(results);


@Mapper
public interface ResultMapper {
    @Insert({
        "<script>",
        "INSERT INTO RESULTS (SET_VALUE1, DATE_VALUE1, EFFECTIVE_DATE, STATUS) VALUES ",
        "<foreach collection='results' item='result' separator=','>",
        "(#{result.setValue1}, #{result.dateValue1}, #{result.effectiveDate}, #{result.status})",
        "</foreach>",
        "</script>"
    })
    void insertResults(@Param("results") List<Result> results);
}

@Service
public class ResultService {
    @Autowired
    private ResultMapper resultMapper;

    public void saveResults(List<Result> results) {
        if (results != null && !results.isEmpty()) {
            resultMapper.insertResults(results);
        }
    }
}

public class ResultService {
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private ResultMapper resultMapper;

    public void saveResults(List<Result> results) {
        if (results != null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i += BATCH_SIZE) {
                List<Result> batch = results.subList(i, Math.min(i + BATCH_SIZE, results.size()));
                resultMapper.insertResults(batch);
            }
        }
    }
}



