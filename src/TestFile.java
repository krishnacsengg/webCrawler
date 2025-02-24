public void copyFile(String filename, String inputPath, String parentPath, String runIDPath) throws IOException {
        // Build the source file path from the inputPath and filename.
        Path sourceFile = Path.of(inputPath, filename);
        if (!Files.exists(sourceFile)) {
            throw new IOException("Source file does not exist: " + sourceFile);
        }

        // Compute the relative path from the parentPath to the source file.
        // This gives the folder structure (e.g., Input/data/Setting/config.xml)
        Path relativePath = Path.of(parentPath).relativize(sourceFile);

        // Build the destination file path by appending the relative path to the runIDPath.
        Path destinationFile = Path.of(runIDPath, relativePath.toString());

        // Create any missing directories in the destination path.
        Files.createDirectories(destinationFile.getParent());

        // Copy the source file to the destination, replacing any existing file.
        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("File copied successfully from " + sourceFile + " to " + destinationFile);
    }
