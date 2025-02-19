import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchivalService {
    private static final Logger logger = LoggerFactory.getLogger(ArchivalService.class);
    private static final String BASE_PATH = "/base/directory/path";
    private static final String ARCHIVE_PATH = BASE_PATH + "/ArchivedRuns";

    public static void processRun(String useCase, int runId) {
        String runPath = BASE_PATH + "/" + useCase + "/RunId_" + runId;
        Path runDir = Paths.get(runPath);

        try {
            if (!Files.exists(runDir)) {
                createRunDirectories(runPath);
                copyDirectory(Paths.get(BASE_PATH, useCase, "Input"), Paths.get(runPath, "Input"), useCase, runId, "Uploaded");
            } else {
                moveFile(useCase, runId, "data.csv");
            }
            copyDirectory(Paths.get(BASE_PATH, useCase, "Output"), Paths.get(runPath, "Output"), useCase, runId, "Uploaded");
            archiveOldRuns(useCase);
        } catch (IOException | SQLException e) {
            logger.error("Error processing run {}: {}", runId, e.getMessage(), e);
        }
    }

    private static void createRunDirectories(String runPath) throws IOException {
        Files.createDirectories(Paths.get(runPath, "Input"));
        Files.createDirectories(Paths.get(runPath, "Output"));
        Files.createDirectories(Paths.get(runPath, "Log"));
    }

    private static void copyDirectory(Path source, Path target, String useCase, int runId, String status) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    updateOrInsertAuditEntry(useCase, runId, sourcePath.getFileName().toString(), status);
                }
            } catch (IOException | SQLException e) {
                logger.error("Error copying file: {}", sourcePath, e);
            }
        });
    }

    private static void moveFile(String useCase, int runId, String fileName) throws IOException, SQLException {
        Path sourcePath = Paths.get(BASE_PATH, useCase, "Staging", fileName);
        Path targetPath = Paths.get(BASE_PATH, useCase, "RunId_" + runId, "Input", fileName);
        if (Files.exists(sourcePath)) {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            updateOrInsertAuditEntry(useCase, runId, fileName, "Moved");
        }
    }

    private static void updateOrInsertAuditEntry(String useCase, int runId, String fileName, String status) throws SQLException, IOException {
        String query = "INSERT INTO audit_log (usecase_path, file_name, file_size, status, run_id, last_modified) VALUES (?, ?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE status = ?, last_modified = NOW()";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "user", "password");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, useCase);
            pstmt.setString(2, fileName);
            pstmt.setDouble(3, Files.size(Paths.get(BASE_PATH, useCase, "RunId_" + runId, "Input", fileName)) / (1024.0 * 1024.0)); // Size in MB
            pstmt.setString(4, status);
            pstmt.setInt(5, runId);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
        }
    }

    private static void archiveOldRuns(String useCase) throws IOException, SQLException {
        List<Integer> runIds = getRunIdsSortedByLastModified(useCase);
        if (runIds.size() > 7) {
            Files.createDirectories(Paths.get(ARCHIVE_PATH));
            for (int i = 6; i < runIds.size(); i++) {
                Path runFolder = Paths.get(BASE_PATH, useCase, "RunId_" + runIds.get(i));
                Path zipPath = Paths.get(ARCHIVE_PATH, "RunId_" + runIds.get(i) + ".zip");
                long totalSizeMB = zipFolder(runFolder, zipPath);
                deleteDirectory(runFolder);
                logger.info("Archived and removed {} -> Compressed file: {}, Size: {} MB", runFolder, zipPath, totalSizeMB);
            }
        }
    }

    private static List<Integer> getRunIdsSortedByLastModified(String useCase) throws SQLException {
        String query = "SELECT run_id, MAX(last_modified) as last_modified FROM audit_log WHERE usecase_path = ? GROUP BY run_id ORDER BY last_modified DESC";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "user", "password");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, useCase);
            ResultSet rs = pstmt.executeQuery();
            List<Integer> runIds = new ArrayList<>();
            while (rs.next()) {
                runIds.add(rs.getInt("run_id"));
            }
            return runIds;
        }
    }

    private static long zipFolder(Path sourceFolder, Path zipFilePath) throws IOException {
        long totalSize = 0;
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            List<Path> files = Files.walk(sourceFolder).filter(Files::isRegularFile).collect(Collectors.toList());
            for (Path file : files) {
                ZipEntry zipEntry = new ZipEntry(sourceFolder.relativize(file).toString());
                zos.putNextEntry(zipEntry);
                Files.copy(file, zos);
                totalSize += Files.size(file);
                zos.closeEntry();
            }
        }
        return totalSize / (1024 * 1024); // Convert to MB
    }

    private static void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}





/**
 * ArchivalService handles the creation and management of run-specific folders
 * for different use cases, copying required input and output files, auditing file operations,
 * and archiving old runs by compressing them into zip files.
 *
 * Key Responsibilities:
 * 1. Create run-specific folders (Input, Output, Log) for each use case and run ID.
 * 2. Copy input and output files while logging audit entries in the database.
 * 3. Move specified files from staging to the run-specific Input folder with proper auditing.
 * 4. Archive old runs, keeping only the latest 6 and compressing older ones into an ArchivedRuns folder.
 *
 * This service ensures proper logging, exception handling, and efficient file management.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchivalService {
    private static final Logger logger = LoggerFactory.getLogger(ArchivalService.class);
    private static final String BASE_PATH = "/base/directory/path";
    private static final String ARCHIVE_PATH = BASE_PATH + "/ArchivedRuns";

    // ThreadPool for controlled asynchronous compression
    private static final ExecutorService compressionExecutor = new ThreadPoolExecutor(
            2, 2, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10), // Queue size limit
            new ThreadPoolExecutor.CallerRunsPolicy()); // Ensures task runs in the caller thread if queue is full

    public static void processRun(String useCase, int runId) {
        String runPath = BASE_PATH + "/" + useCase + "/RunId_" + runId;
        Path runDir = Paths.get(runPath);

        try {
            if (!Files.exists(runDir)) {
                createRunDirectories(runPath);
                copyDirectory(Paths.get(BASE_PATH, useCase, "Input"), Paths.get(runPath, "Input"), useCase, runId, "Uploaded");
            } else {
                moveFile(useCase, runId, "data.csv");
            }
            copyDirectory(Paths.get(BASE_PATH, useCase, "Output"), Paths.get(runPath, "Output"), useCase, runId, "Uploaded");
            archiveOldRuns(useCase);
        } catch (IOException | SQLException e) {
            logger.error("Error processing run {}: {}", runId, e.getMessage(), e);
        }
    }

    private static void createRunDirectories(String runPath) throws IOException {
        Files.createDirectories(Paths.get(runPath, "Input"));
        Files.createDirectories(Paths.get(runPath, "Output"));
        Files.createDirectories(Paths.get(runPath, "Log"));
    }

    private static void copyDirectory(Path source, Path target, String useCase, int runId, String status) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    updateOrInsertAuditEntry(useCase, runId, sourcePath.getFileName().toString(), status);
                }
            } catch (IOException | SQLException e) {
                logger.error("Error copying file: {}", sourcePath, e);
            }
        });
    }

    private static void moveFile(String useCase, int runId, String fileName) throws IOException, SQLException {
        Path sourcePath = Paths.get(BASE_PATH, useCase, "Staging", fileName);
        Path targetPath = Paths.get(BASE_PATH, useCase, "RunId_" + runId, "Input", fileName);
        if (Files.exists(sourcePath)) {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            updateOrInsertAuditEntry(useCase, runId, fileName, "Moved");
        }
    }

    private static void updateOrInsertAuditEntry(String useCase, int runId, String fileName, String status) throws SQLException, IOException {
        String query = "INSERT INTO audit_log (usecase_path, file_name, file_size, status, run_id, last_modified) VALUES (?, ?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE status = ?, last_modified = NOW()";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "user", "password");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, useCase);
            pstmt.setString(2, fileName);
            pstmt.setDouble(3, Files.size(Paths.get(BASE_PATH, useCase, "RunId_" + runId, "Input", fileName)) / (1024.0 * 1024.0)); // Size in MB
            pstmt.setString(4, status);
            pstmt.setInt(5, runId);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
        }
    }

    private static void archiveOldRuns(String useCase) throws IOException, SQLException {
        List<Integer> runIds = getRunIdsSortedByLastModified(useCase);
        if (runIds.size() > 7) {
            Files.createDirectories(Paths.get(ARCHIVE_PATH));
            for (int i = 6; i < runIds.size(); i++) {
                int runId = runIds.get(i);
                Path runFolder = Paths.get(BASE_PATH, useCase, "RunId_" + runId);
                Path zipPath = Paths.get(ARCHIVE_PATH, "RunId_" + runId + ".zip");
                compressionExecutor.submit(() -> {
                    try {
                        long totalSizeMB = zipFolder(runFolder, zipPath);
                        deleteDirectory(runFolder);
                        logger.info("Archived and removed {} -> Compressed file: {}, Size: {} MB", runFolder, zipPath, totalSizeMB);
                    } catch (IOException e) {
                        logger.error("Failed to archive folder: {}", runFolder, e);
                    }
                });
            }
        }
    }

    private static List<Integer> getRunIdsSortedByLastModified(String useCase) throws SQLException {
        String query = "SELECT run_id, MAX(last_modified) as last_modified FROM audit_log WHERE usecase_path = ? GROUP BY run_id ORDER BY last_modified DESC";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "user", "password");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, useCase);
            ResultSet rs = pstmt.executeQuery();
            List<Integer> runIds = new ArrayList<>();
            while (rs.next()) {
                runIds.add(rs.getInt("run_id"));
            }
            return runIds;
        }
    }

    private static long zipFolder(Path sourceFolder, Path zipFilePath) throws IOException {
        long totalSize = 0;
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            List<Path> files = Files.walk(sourceFolder).filter(Files::isRegularFile).collect(Collectors.toList());
            byte[] buffer = new byte[64 * 1024]; // 64KB buffer

            for (Path file : files) {
                ZipEntry zipEntry = new ZipEntry(sourceFolder.relativize(file).toString());
                zos.putNextEntry(zipEntry);
                try (InputStream is = Files.newInputStream(file)) {
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                totalSize += Files.size(file);
                zos.closeEntry();
            }
        }
        return totalSize / (1024 * 1024); // Convert to MB
    }

    private static void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static void shutdownExecutor() {
        compressionExecutor.shutdown();
        try {
            if (!compressionExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                compressionExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            compressionExecutor.shutdownNow();
        }
    }
}

