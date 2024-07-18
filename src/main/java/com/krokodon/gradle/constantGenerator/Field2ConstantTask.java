package com.krokodon.gradle.constantGenerator;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
public class Field2ConstantTask {
    @TaskAction
    public void generateConstants(final Project project) {
        try {
            Field2ConstantExtension extension = project.getExtensions().findByType(Field2ConstantExtension.class);
            List<String> binDirs = extension.getClassDirectories();
            String outputDir = extension.getOutputDir();
            String prefix = extension.getPackagePrefix();
            String className = extension.getClassName();
            String currentDirectory = System.getProperty("user.dir");
            File outputDirectory = new File(currentDirectory + "/" + outputDir + "/" + prefix);

            File outputFile = new File(outputDirectory, className + ".java");

            if (outputFile.exists()) {
                outputFile.delete();
            }

            outputDirectory.mkdirs();

            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                project.getLogger().error("Failed to generate constant file at: " + outputFile.getAbsolutePath(), e);
            }

            Map<String, List<String>> classFieldsMap = new HashMap<>();

            StringBuilder content = new StringBuilder();
            content.append("package ").append(prefix).append(";\n\n");
            content.append("@SuppressWarnings(\"all\")").append("\n");
            content.append("public final class ").append(className).append(" {\n\n");

            List<URL> ProjectOutputUrl = new ArrayList<>();
            URLClassLoader classLoader = null;

            try {
                for (String binDir : binDirs) {
                    ProjectOutputUrl.add(getUrlFromClassDirectory(currentDirectory + "/" + getClassDirectory(binDir)));
                }

                URL[] urlArray = ProjectOutputUrl.toArray(new URL[0]);
                classLoader = new URLClassLoader(urlArray, getClass().getClassLoader());
                } catch (Exception e) {
                e.printStackTrace();
            }

            for (String binDir : binDirs) {
                try {
                    String binClassName = getClassNameFromFilePath(binDir);
                    content.append("public static final class ").append(binClassName).append(" {\n");
                    Class<?> clazz = Class.forName(binClassName, false, classLoader);
                    Field[] fields = clazz.getDeclaredFields();
                    List<String> fieldNames = new ArrayList<>();
                    for (Field field : fields) {
                        fieldNames.add(field.getName());
                    }

                    classFieldsMap.put(binClassName, fieldNames);
                    content.append("//Constant for fields of: " + binClassName + ".class \n");
                    content = new FormatGenerator().generateConstantsContent(content, fieldNames, "String");
                    content.append("\nprivate ").append(binClassName).append("() {}\n\n");
                    content.append("}\n\n");
                    project.getLogger().lifecycle("Generated constants for class named: " + binClassName + ".class" + "at" + outputDir);
                } catch (ClassNotFoundException e) {
                    project.getLogger().lifecycle("Make sure the path to the .class file is correctly specified.");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            content.append("private final ").append(className).append("() {}\n\n");
            content.append("}\n");
            try (FileOutputStream fos = new FileOutputStream(outputFile,true);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(content.toString());
        } catch (IOException e) {
                     e.printStackTrace();
                 }
        }

        catch (Exception e){e.printStackTrace();}
        }

        private static String getClassNameFromFilePath (String filePath){
            File file = new File(filePath);
            if (file.isFile() && file.getName().endsWith(".class")) {
                String fileName = file.getName();
                return fileName.substring(0, fileName.lastIndexOf('.'));
            }
            return null;
        }
    public static String getClassDirectory(String classFilePath) {
        int lastSlashIndex = classFilePath.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return classFilePath.substring(0, lastSlashIndex);
        }
        return "";
    }
    public static URL getUrlFromClassDirectory(String classDir) {
        try {
            return new File(classDir).toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    }
