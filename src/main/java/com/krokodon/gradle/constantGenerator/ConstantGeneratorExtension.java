package com.krokodon.gradle.constantGenerator;

import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NonNls;

import java.util.Arrays;
import java.util.List;
public class ConstantGeneratorExtension {
    /**
     * List of bin directories that will be used.
     */
    private List<String> binDir = Arrays.asList();
    /**
     * Specified output directory.
     */
    private String outputDir = "build/generated-sources";
    /**
     * Package name that will be added to the output directory where the constant file will sit.
     */
    private String packagePrefix;
    /**
     * Name of the singular class all constants would sit in.
     */
    private String className;
    public String getClassName() {
        return className;
    }

    public void setClassName(final String dir) {
        this.className = dir;
    }
    @NonNull
    public List<String> getBinDir() {
        return binDir;
    }

    public void setBinDir(final List<String> dir) {
        this.binDir = dir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(final String dir) {
        this.outputDir = dir;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }
    public void setPackagePrefix(final String prefix) {
        this.packagePrefix = prefix;
    }
}
