import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePermissionService {
    private static final Logger logger = LoggerFactory.getLogger(FilePermissionService.class);

    /**
     * Main method to ensure file permissions are adjusted as needed.
     * First attempts to change POSIX permissions, falls back to basic permissions if POSIX fails.
     */
    public void adjustFilePermissions(File structure, List<String> logRemarksList) {
        try {
            // Attempt POSIX permissions
            setPosixPermissions(structure, logRemarksList);
        } catch (UnsupportedOperationException | IOException e) {
            logger.warn("POSIX permission change failed, falling back to basic permissions.", e);
            logRemarksList.add("POSIX permission change failed, falling back to basic permissions.");

            try {
                // Attempt basic permissions as fallback
                setBasicPermissions(structure, logRemarksList);
            } catch (Exception fallbackException) {
                logger.error("Fallback basic permission change failed.", fallbackException);
                logRemarksList.add("Fallback basic permission change failed: " + fallbackException.getMessage());
            }
        }
    }

    /**
     * Attempts to set POSIX permissions for the file.
     */
    private void setPosixPermissions(File structure, List<String> logRemarksList) throws IOException {
        logger.info(">>>>>> Inside setPosixPermissions <<<<<<");

        // Define permissions: Read and write for owner, group, and others
        Set<PosixFilePermission> permissions = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_WRITE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_WRITE
        );

        // Set POSIX permissions
        Files.setPosixFilePermissions(structure.toPath(), permissions);
        logRemarksList.add("File permissions updated using POSIX.");
        logger.info("POSIX permissions updated successfully for file: {}", structure.getPath());
    }

    /**
     * Fallback method to set basic permissions using java.io.File methods.
     */
    private void setBasicPermissions(File structure, List<String> logRemarksList) throws Exception {
        logger.info(">>>>>> Inside setBasicPermissions <<<<<<");

        boolean success;

        // Ensure file exists before proceeding
        if (!structure.exists()) {
            throw new IOException("File not found: " + structure.getPath());
        }

        // Set the file readable for everyone
        success = structure.setReadable(true, false);
        if (!success) {
            throw new Exception("Failed to set file as readable for everyone.");
        }
        logRemarksList.add("File set to readable for everyone.");

        // Set the file writable for everyone
        success = structure.setWritable(true, false);
        if (!success) {
            throw new Exception("Failed to set file as writable for everyone.");
        }
        logRemarksList.add("File set to writable for everyone.");

        logger.info("Basic permissions updated successfully for file: {}", structure.getPath());
    }
}
