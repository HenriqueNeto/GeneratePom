package ar.com.cpqi.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
* Author         : CPQi Argentina, Henrique Neto, L0665398
* Purpose        : <Purpose>
* Input files    : N/A
* Log File       : N/A
* Output file    : N/A
*
 * Copyright 2017 Banco Galicia
 * </pre>
 */
public class PomGenerator {

    private static final String	GENERATE_POM	    = "generate_pom";
    private static final String	COMMON_FILES	    = "common_files";
    private static final String	EXCLUSIVE_FILES	    = "exclusive_files";
    private static final String	GENERATE_POM_PARENT = "generate_pom_parent";
    private static final String	GENERATE_MVN	    = "generate_mvn";

    public static void main(String[] args) throws Exception {
	String action = args[0];
	switch (action) {
	    case GENERATE_POM:
		generatePom(args);
		break;
	    case COMMON_FILES:
		commonFiles(args);
		break;
	    case EXCLUSIVE_FILES:
		exclusiveFiles(args);
		break;
	    case GENERATE_POM_PARENT:
		generatePomParent(args);
		break;
	    case GENERATE_MVN:
		generateMVN(args);
		break;
	    default:
		break;
	}
	
    }
    
    /**
     * @param args
     * @throws Exception
     */
    private static void generateMVN(String[] args) throws Exception {
	String pathOrigJars = args[1];
	List<String> jarsOrig = getJarsNames(pathOrigJars);

	FileWriter writer = new FileWriter(pathOrigJars + File.separator + "UploadAll.bat");
	PrintWriter f0 = new PrintWriter(writer);
	f0.println("@echo on");
	f0.println("setlocal");
	f0.println("set M2_HOME=C:\\Personal\\Programas\\apache-maven-3.5.0-bin\\apache-maven-3.5.0");
	f0.println("set PATH=%PATH%;%M2_HOME%\\bin");

	for (String jarName : jarsOrig) {
	    f0.println("call mvn deploy:deploy-file -Dfile=.\\" + jarName + "\\" + jarName + ".jar  -DgroupId=ar.com.bgba -DartifactId=" + jarName
	            + " -Dversion=15.1.0.19 -Dpackaging=jar -DrepositoryId=calypso-source -Durl=http://djenkiapp01:8081/nexus/content/repositories/calypso/ -DpomFile=.\\"
	            + jarName + "\\pom.xml");
	}

	writer.close();
	f0.close();

    }

    /**
     * @param args
     */
    private static void generatePomParent(String[] args) {
	String pathOrigJars = args[1];
	String pathDestinyJars = args[2];
	List<String> jarsOrig = getJarsNames(pathOrigJars);
	List<String> jarsDestiny = getJarsNames(pathDestinyJars);
	String currentJarName = null;
	String jarNameWithoutVersion = null;
	
	for (String jarName : jarsOrig) {
	    if (jarsDestiny.contains(jarName)) {
		currentJarName = jarName.substring(0, jarName.lastIndexOf("."));
		jarNameWithoutVersion = currentJarName.substring(0, currentJarName.lastIndexOf("-"));
		System.out.println("<dependency>");
		System.out.println("<groupId>ar.com.bgba</groupId>");
		System.out.println("<artifactId>" + jarNameWithoutVersion + "</artifactId>");
		System.out.println("<version>${calypso.version}</version>");
		System.out.println("</dependency>");
	    }
	}
	
    }

    /**
     * @param args
     */
    private static void exclusiveFiles(String[] args) {
	String pathOrigJars = args[1];
	String pathDestinyJars = args[2];
	List<String> jarsOrig = getJarsNames(pathOrigJars);
	List<String> jarsDestiny = getJarsNames(pathDestinyJars);
	
	int contador = 0;
	System.out.println("### Jars que estao na pasta " + pathOrigJars + " mas nao estao na pasta " + pathDestinyJars);
	for (String jarName : jarsOrig) {
	    if (!jarsDestiny.contains(jarName)) {
		contador++;
		System.out.println(jarName);
	    }
	}
	System.out.println(contador + " Arquivos");
    }

    /**
     * @param args
     */
    private static void commonFiles(String[] args) {
	String pathOrigJars = args[1];
	String pathDestinyJars = args[2];
	List<String> jarsOrig = getJarsNames(pathOrigJars);
	List<String> jarsDestiny = getJarsNames(pathDestinyJars);
	
	int contador = 0;
	System.out.println("### Jars que estao na pasta " + pathOrigJars + " e também na pasta " + pathDestinyJars);
	for (String jarName : jarsOrig) {
	    if (jarsDestiny.contains(jarName)) {
		contador++;
		System.out.println(jarName);
	    }
	}
	System.out.println(contador + " Arquivos");
    }
    
    public static void generatePom(String args[]) throws Exception {
	String pathOrigJars = args[1];
	String pathDestinyJars = args[2];
	
	File[] jars = getJars(pathOrigJars);
	String jarName = null;
	String jarNameWithoutVersion = null;
	String jarVersion = null;
	for (File jar : jars) {
	    jarName = jar.getName();
	    jarName = jarName.substring(0, jarName.lastIndexOf("."));
	    jarNameWithoutVersion = jarName.substring(0, jarName.lastIndexOf("-"));
	    jarVersion = jarName.substring(jarName.lastIndexOf("-") + 1);
	    generatePom(pathOrigJars, pathDestinyJars, jarName, jarNameWithoutVersion, jarVersion);

	}
	
    }
    
    /**
     * @param jarName
     * @param jarNameWithoutVersion
     * @param jarVersion
     * @throws Exception
     */
    private static void generatePom(String pathOrigJars, String pathDestinyJars, String jarName, String jarNameWithoutVersion, String jarVersion)
            throws Exception {
	Files.createDirectory(Paths.get(pathDestinyJars + File.separator + jarNameWithoutVersion));
	Files.copy(Paths.get(pathOrigJars + File.separator + jarName + ".jar"),
	        Paths.get(pathDestinyJars + File.separator + jarNameWithoutVersion + File.separator + jarNameWithoutVersion + ".jar"));
	FileWriter writer = new FileWriter(pathDestinyJars + File.separator + jarNameWithoutVersion + File.separator + "pom.xml");
	PrintWriter f0 = new PrintWriter(writer);
	f0.println(
	        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">");
	f0.println("	<modelVersion>4.0.0</modelVersion>");
	f0.println("	<groupId>ar.com.bgba</groupId>");
	f0.println("	<artifactId>" + jarNameWithoutVersion + "</artifactId>");
	f0.println("	<version>15.1.0.19</version>");
	f0.println("	<description>Versao Original: " + jarVersion + "</description>");
	f0.println("</project>");
	writer.close();
	f0.close();
    }

    /**
     * @param pathOrigJars
     * @return
     */
    private static List<String> getJarsNames(String pathOrigJars) {
	File file = new File(pathOrigJars);
	return Arrays.asList(file.list());
    }

    /**
     * @param pathOrigJars
     * @return
     */
    private static File[] getJars(String pathOrigJars) {
	File file = new File(pathOrigJars);
	return file.listFiles();
    }
}
