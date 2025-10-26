package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.BackupFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseBackupService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    private static final String BACKUP_DIR = "backups";

    /**
     * Generate a complete SQL backup of the database
     * @return File path of the generated backup
     */
    public String generateBackup() throws SQLException, IOException {
        // Create backup directory if it doesn't exist
        File backupDirectory = new File(BACKUP_DIR);
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();
        }

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_" + timestamp + ".sql";
        String filePath = BACKUP_DIR + File.separator + fileName;

        try (Connection connection = dataSource.getConnection();
             BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // Extract database name from URL
            String databaseName = extractDatabaseName(databaseUrl);

            // Write SQL header
            writer.write("-- Database Backup\n");
            writer.write("-- Generated: " + LocalDateTime.now() + "\n");
            writer.write("-- Database: " + databaseName + "\n\n");
            writer.write("SET FOREIGN_KEY_CHECKS=0;\n\n");

            // Get all tables
            List<String> tables = getAllTables(connection, databaseName);

            // Backup each table
            for (String table : tables) {
                backupTable(connection, writer, table);
            }

            writer.write("\nSET FOREIGN_KEY_CHECKS=1;\n");
        }

        return filePath;
    }

    /**
     * Get all table names from the database
     */
    private List<String> getAllTables(Connection connection, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }

        return tables;
    }

    /**
     * Backup a single table (structure + data)
     */
    private void backupTable(Connection connection, BufferedWriter writer, String tableName)
            throws SQLException, IOException {

        writer.write("-- Table: " + tableName + "\n");

        // Drop and create table statement
        writer.write("DROP TABLE IF EXISTS `" + tableName + "`;\n");

        // Get CREATE TABLE statement
        String createTableSQL = getCreateTableStatement(connection, tableName);
        writer.write(createTableSQL + ";\n\n");

        // Get all data from table
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (!rs.isBeforeFirst()) {
                // No data in table
                writer.write("-- No data in table " + tableName + "\n\n");
                return;
            }

            // Write INSERT statements
            writer.write("-- Data for table " + tableName + "\n");

            while (rs.next()) {
                StringBuilder insertSQL = new StringBuilder();
                insertSQL.append("INSERT INTO `").append(tableName).append("` VALUES (");

                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);

                    if (value == null) {
                        insertSQL.append("NULL");
                    } else if (value instanceof String || value instanceof Date || value instanceof Timestamp) {
                        insertSQL.append("'").append(escapeSQL(value.toString())).append("'");
                    } else {
                        insertSQL.append(value);
                    }

                    if (i < columnCount) {
                        insertSQL.append(", ");
                    }
                }

                insertSQL.append(");\n");
                writer.write(insertSQL.toString());
            }

            writer.write("\n");
        }
    }

    /**
     * Get CREATE TABLE statement for a table
     */
    private String getCreateTableStatement(Connection connection, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {

            if (rs.next()) {
                return rs.getString(2);
            }
        }
        return "";
    }

    /**
     * Escape special characters in SQL strings
     */
    private String escapeSQL(String str) {
        return str.replace("'", "''")
                  .replace("\\", "\\\\");
    }

    /**
     * Extract database name from JDBC URL
     */
    private String extractDatabaseName(String url) {
        // Format: jdbc:mysql://localhost:3306/database_name
        String[] parts = url.split("/");
        String dbNameWithParams = parts[parts.length - 1];
        // Remove any parameters (e.g., ?useSSL=false)
        return dbNameWithParams.split("\\?")[0];
    }

    /**
     * Get list of all backup files
     */
    public List<BackupFile> getAllBackupFiles() {
        File backupDirectory = new File(BACKUP_DIR);
        List<BackupFile> backupFiles = new ArrayList<>();

        if (backupDirectory.exists() && backupDirectory.isDirectory()) {
            File[] files = backupDirectory.listFiles((dir, name) -> name.endsWith(".sql"));
            if (files != null) {
                for (File file : files) {
                    backupFiles.add(new BackupFile(
                        file.getName(),
                        file.length(),
                        file.getAbsolutePath()
                    ));
                }
            }
        }

        return backupFiles;
    }

    /**
     * Get a specific backup file
     */
    public BackupFile getBackupFile(String fileName) {
        File file = new File(BACKUP_DIR + File.separator + fileName);
        if (file.exists() && file.isFile()) {
            return new BackupFile(file.getName(), file.length(), file.getAbsolutePath());
        }
        return null;
    }

    /**
     * Delete a backup file
     */
    public boolean deleteBackupFile(String fileName) {
        File file = new File(BACKUP_DIR + File.separator + fileName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
}
