package com.krokodon.gradle.constantGenerator;

public class DB2ConstantExtension {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String outputDir;
    private String packagePrefix;

    // Getters and Setters
    public String getDbUrl() { return dbUrl; }
    public void setDbUrl(final String dbUrl) { this.dbUrl = dbUrl; }

    public String getDbUser() { return dbUser; }
    public void setDbUser(final String dbUser) { this.dbUser = dbUser; }

    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(final String dbPassword) { this.dbPassword = dbPassword; }

    public String getOutputDir() { return outputDir; }
    public void setOutputDir(final String outputDir) { this.outputDir = outputDir; }

    public String getPackagePrefix() { return packagePrefix; }
    public void setPackagePrefix(final String packagePrefix) { this.packagePrefix = packagePrefix; }
}

