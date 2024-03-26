package com.krokodon.gradle.constantGenerator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomLoader extends ClassLoader{
public CustomLoader(){}

    public Class<?> ref() throws Exception{

        File directory = new File("C:/Users/adeba/test4plug/src/main/java");

        URL url = directory.toURI().toURL();

        ClassLoader classLoader = new URLClassLoader(new URL[]{url});

        Class<?> clazz = classLoader.loadClass("Bomba");

        return clazz;
    }
}
