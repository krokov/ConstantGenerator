package com.krokodon.gradle.constantGenerator;

import java.util.List;

public class FormatGenerator {
    public StringBuilder generateConstantsContent(StringBuilder content, final List<String> lines, final String type) {
        lines.stream()
                .distinct()
                .forEach(line -> {
                    String constantName = toConstantName(line);
                    content.append("\tpublic static final " + type + " ").append(constantName).append(" = \"").append(line).append("\";\n");
                });

        return content;
    }
    public static String toConstantName(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

}
