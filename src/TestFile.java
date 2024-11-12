import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class FileNameConverter {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a List<String> to a JSON string.
     *
     * @param fileNames List of file names to convert
     * @return JSON string representation of the list
     * @throws JsonProcessingException if there is an error during conversion
     */
    public String listToJson(List<String> fileNames) throws JsonProcessingException {
        return objectMapper.writeValueAsString(fileNames);
    }

    /**
     * Converts a JSON string back to a List<String>.
     *
     * @param json JSON string representation of the list
     * @return List of file names
     * @throws IOException if there is an error during conversion
     */
    public List<String> jsonToList(String json) throws IOException {
        return objectMapper.readValue(json, List.class);
    }

    // For quick testing
    public static void main(String[] args) {
        FileNameConverter converter = new FileNameConverter();
        
        // Sample file names list with special characters
        List<String> fileNames = List.of("file1@.txt", "another-file#.txt", "special&char$.txt");

        try {
            // Convert List to JSON
            String json = converter.listToJson(fileNames);
            System.out.println("JSON representation: " + json);

            // Convert JSON back to List
            List<String> convertedList = converter.jsonToList(json);
            System.out.println("Converted back to List: " + convertedList);

        } catch (JsonProcessingException e) {
            System.err.println("Error during JSON serialization: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during JSON deserialization: " + e.getMessage());
        }
    }
}
