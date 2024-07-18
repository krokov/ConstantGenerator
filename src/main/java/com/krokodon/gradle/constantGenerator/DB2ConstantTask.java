package com.krokodon.gradle.constantGenerator;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

public class DB2ConstantTask extends DefaultTask {
    @TaskAction
    public void generateConstants() {
        Project project = getProject();
        DB2ConstantExtension extension = project.getExtensions().findByType(DB2ConstantExtension.class);

        if (extension != null) {
            String dbUrl = extension.getDbUrl();
            String dbUser = extension.getDbUser();
            String dbPassword = extension.getDbPassword();
            String outputDir = extension.getOutputDir();
            String packagePrefix = extension.getPackagePrefix();

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    generateTableConstants(metaData, tableName, outputDir, packagePrefix);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateTableConstants(DatabaseMetaData metaData, String tableName, String outputDir, String packagePrefix) throws SQLException {
        String className = toClassName(tableName);
        File outputFile = new File(outputDir, className + ".java");

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            StringBuilder content = new StringBuilder();
            content.append("package ").append(packagePrefix).append(";\n\n");
            content.append("public final class ").append(className).append(" {\n");
            content.append("    public static final String TBL_").append(tableName.toUpperCase()).append(" = \"").append(tableName).append("\";\n");

            ResultSet columns = metaData.getColumns(null, null, tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                boolean isNullable = "YES".equals(columns.getString("IS_NULLABLE"));
                int columnSize = columns.getInt("COLUMN_SIZE");

                content.append("    public static final String COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append(" = \"").append(columnName).append("\";\n");
                content.append("    public static final boolean COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append("_NULLABLE = ").append(isNullable).append(";\n");

                if (columnSize > 0) {
                    content.append("    public static final int COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append("_SIZE = ").append(columnSize).append(";\n");
                }
            }

            content.append("}\n");
            osw.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toClassName(String tableName) {
        StringBuilder className = new StringBuilder();
        boolean capitalize = true;

        for (char c : tableName.toCharArray()) {
            if (c == '_') {
                capitalize = true;
            } else {
                className.append(capitalize ? Character.toUpperCase(c) : c);
                capitalize = false;
            }
        }

        return className.toString();
    }
}
