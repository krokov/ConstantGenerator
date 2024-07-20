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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String className = extension.getclassName();

            if (isNullOrEmpty(dbUrl)) {
                throw new IllegalArgumentException("Database URL, and possible user/password must be specified.");
            }

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

                Map<String, List<ColumnInfo>> tableColumnsMap = new HashMap<>();

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    List<ColumnInfo> columnInfoList = getColumnInfos(metaData, tableName);
                    tableColumnsMap.put(tableName, columnInfoList);
                }

                generateDbConstantsFile(tableColumnsMap, outputDir, packagePrefix, className);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ColumnInfo> getColumnInfos(DatabaseMetaData metaData, String tableName) throws SQLException {
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ResultSet columns = metaData.getColumns(null, null, tableName, "%");

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            boolean isNullable = "YES".equals(columns.getString("IS_NULLABLE"));
            int columnSize = columns.getInt("COLUMN_SIZE");

            columnInfoList.add(new ColumnInfo(columnName, isNullable, columnSize));
        }

        return columnInfoList;
    }

    private void generateDbConstantsFile(Map<String, List<ColumnInfo>> tableColumnsMap, String outputDir, String packagePrefix, String className) {
        File outputFile = new File(outputDir,  className + ".java");

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            StringBuilder content = new StringBuilder();
            content.append("package ").append(packagePrefix).append(";\n\n");
            content.append("public final class DBConstants {\n");

            for (Map.Entry<String, List<ColumnInfo>> entry : tableColumnsMap.entrySet()) {
                String tableName = entry.getKey();
                List<ColumnInfo> columns = entry.getValue();
                String tblClassName = toClassName(tableName);

                content.append("    public static final String TBL_").append(tableName.toUpperCase()).append(" = \"").append(tableName).append("\";\n");
                content.append("    public static final class ").append(tblClassName).append(" {\n");

                for (ColumnInfo columnInfo : columns) {
                    String columnName = columnInfo.getName();
                    boolean isNullable = columnInfo.isNullable();
                    int columnSize = columnInfo.getSize();

                    content.append("        public static final String COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append(" = \"").append(columnName).append("\";\n");
                    content.append("        public static final boolean COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append("_NULLABLE = ").append(isNullable).append(";\n");

                    if (columnSize > 0) {
                        content.append("        public static final int COL_").append(tableName.toUpperCase()).append("__").append(columnName.toUpperCase()).append("_SIZE = ").append(columnSize).append(";\n");
                    }
                }

                content.append("    }\n");
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

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static class ColumnInfo {
        private final String name;
        private final boolean nullable;
        private final int size;

        public ColumnInfo(String name, boolean nullable, int size) {
            this.name = name;
            this.nullable = nullable;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public boolean isNullable() {
            return nullable;
        }

        public int getSize() {
            return size;
        }
    }
}
