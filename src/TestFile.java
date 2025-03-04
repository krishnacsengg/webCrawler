import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DatasetService {
    private final DatasetDAO datasetDAO;

    @Autowired
    private Map<String, String> basePathMap;  // Injected Map with actual basePath values

    public DatasetService(DatasetDAO datasetDAO) {
        this.datasetDAO = datasetDAO;
    }

    public List<String> getPaths(String someValue) {
        List<String> allPaths = new ArrayList<>();
        List<String> datasetJsonList = datasetDAO.getDatasetJsonList(someValue);

        ObjectMapper objectMapper = new ObjectMapper();
        for (String jsonInput : datasetJsonList) {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonInput);
                String basePathKey = rootNode.get("basePath").asText();  // Key to lookup in basePathMap
                String actualBasePath = basePathMap.getOrDefault(basePathKey, basePathKey);  // Replace basePath

                String relativePath = rootNode.get("relativePath").asText();
                String staging = rootNode.get("staging").asText();
                String input = rootNode.get("input").asText();

                // Use Paths.get() for safe path concatenation
                Path stagingPath = Paths.get(actualBasePath, relativePath, staging);
                Path inputPath = Paths.get(actualBasePath, relativePath, input);

                allPaths.add(stagingPath.toString());
                allPaths.add(inputPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allPaths;
    }
}



<select id="getDatasetJsonList" resultType="String">
        SELECT d.dataset_add_prop 
        FROM dataset_def d
        JOIN bus_def_table b ON d.busId = b.busId
        WHERE b.some_condition = #{someValue}
    </select>


            session.selectList







