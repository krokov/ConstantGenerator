package com.krokodon.gradle.constantGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.io.File;
public class FormatGenerator {
    public File generateConstantsContent(final File file, final List<String> lines, final String prefix, final String className, final String type) {
        StringBuilder content = new StringBuilder();
        content.append("package ").append(prefix).append(";\n\n");
        content.append("public final class ").append(className).append(" {\n");
        content.append("private ").append(className).append("() {};\n");
        lines.stream()
                //.filter(line -> !line.trim().startsWith("#") && line.contains("="))
                //.map(line -> line.split("=")[0].trim())
                .distinct()
                .forEach(line -> {
                    String constantName = line.toUpperCase().replaceAll("[^\\p{L}\\p{N}_]+", "_");
                    content.append("\tpublic static final " + type + " ").append(constantName).append(" = \"").append(line).append("\";\n");
                });
        content.append("}\n");

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
