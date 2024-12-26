public static String extractRelevantPath(String filePath, List<String> basePaths) {
        // Find the matching base path
        for (String basePath : basePaths) {
            if (filePath.startsWith(basePath)) {
                // Extract the base directory name
                String baseDirectory = basePath.substring(basePath.lastIndexOf('/') + 1);

                // Add any trailing path info from the input filePath
                String trailingPath = filePath.substring(basePath.length());

                // Combine the base directory and trailing path
                return baseDirectory + trailingPath;
            }
        }
        return null; // No match found
    }
