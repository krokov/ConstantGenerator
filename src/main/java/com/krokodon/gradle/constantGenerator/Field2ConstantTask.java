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

public class Field2ConstantTask {
    @TaskAction
    public void generateConstants(final Project project) {
        ConstantGeneratorExtension extension = project.getExtensions().findByType(ConstantGeneratorExtension.class);

        List<String> binDirs = extension.getBinDir();
        String outputDir = extension.getOutputDir();
        String prefix = extension.getPackagePrefix();
        String className = extension.getClassName();
        String currentDirectory = System.getProperty("user.dir");
//See if can change places of outputdirectory mkdirs
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

        project.getLogger().lifecycle("Got 1 "); //dededed

        List<String> classBinNames = getClassNamesFromFilePaths(binDirs);
        Map<String, List<String>> classFieldsMap = new HashMap<>();
        project.getLogger().lifecycle("Got 1.5 ");
        project.getLogger().lifecycle(classBinNames.toString());
            for (String binClassName : classBinNames) {
                project.getLogger().lifecycle("Got 1.7 ");
                //File binFile = new File(binClassName);
                project.getLogger().lifecycle(binClassName);
                //try {

//Error in setting clazz for name method
                    Class<?> clazz = Class.forName(binClassName);
                    //Class<?> clazz = binClassName.class;
                    Field[] fields = clazz.getDeclaredFields();
                    List<String> fieldNames = new ArrayList<>();
                    project.getLogger().lifecycle("Got 1.9 ");
                    for (Field field : fields) {
                        project.getLogger().lifecycle(field.getName()); ///dellelalal
                        fieldNames.add(field.getName());
                    }
                    project.getLogger().lifecycle("Got 2 ");///dedede
                    classFieldsMap.put(binClassName, fieldNames);


                        outputFile = new FormatGenerator().generateConstantsContent(outputFile, fieldNames, prefix, className, "String");
                        project.getLogger().lifecycle("Generated constants for class named: " + binClassName);
                    project.getLogger().lifecycle("Got 3 ");//dededed

               // }
                   // catch(Exception e){
                   //     e.printStackTrace();
                   // }
                }



        }



        private static List<String> getClassNamesFromFilePaths (List < String > filePaths) {
            List<String> classNames = new ArrayList<>();
            for (String filePath : filePaths) {
                String className = getClassNameFromFilePath(filePath);
                if (className != null) {
                    classNames.add(className);
                }
            }
            return classNames;
        }



        private static String getClassNameFromFilePath (String filePath){
            File file = new File(filePath);
            if (file.isFile() && file.getName().endsWith(".java")) {
                String fileName = file.getName();
                String className = fileName.substring(0, fileName.length() - 5);
                return className;
            }
            return null; // Not a valid class file path
        }
    //public static Map<String, List<String>> extractFields (List < String > classNames) {
      //  Map<String, List<String>> classFieldsMap = new HashMap<>();

        //try {
         //   for (String className : classNames) {
            //    Class<?> clazz = Class.forName(className);
            //    Field[] fields = clazz.getDeclaredFields();
               // List<String> fieldNames = new ArrayList<>();
           //     for (Field field : fields) {
             //       fieldNames.add(field.getName());
         //       }
       //         classFieldsMap.put(className, fieldNames);
     //       }
   //     } catch (ClassNotFoundException e) {
 //           e.printStackTrace();
//        }

    //    return classFieldsMap;
    //}

    }


