package law.leya;

import law.leya.File.Xml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Logger;

public class Converter {

    public static void main(String[] args) {
        System.out.println("-------------------");
        System.out.println("Leya PDF Generator");
        System.out.println("-------------------");

        ArgumentParser parser = new ArgumentParser(args);
        String xmlFolder = parser.getXmlFolder();

        // Delete the "pdf" subfolder if it exists
        String pdfFolder = xmlFolder + "/pdf";
        File pdfFolderFile = new File(pdfFolder);
        if (pdfFolderFile.exists()) {
            try {
                deleteDirectory(pdfFolderFile);
            } catch (IOException e) {
                System.out.println("Failed to delete the 'pdf' subfolder: " + e.getMessage());
                return; // Stop the script execution
            }
        }

        long startTime = System.currentTimeMillis(); // Capture start time

        // Process XML files in the specified folder and its subfolders
        processFolder(new File(xmlFolder));

        long endTime = System.currentTimeMillis(); // Capture end time
        long elapsedTime = endTime - startTime; // Calculate elapsed time in milliseconds

        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;

        System.out.println("Processing completed in " + minutes + " minutes and " + seconds + " seconds.");
    }

    private static void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("Failed to delete directory: " + directory.getAbsolutePath());
        }
    }

    private static void processFolder(File folder) {
        File[] files = Objects.requireNonNull(folder.listFiles());

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively process subfolders
                processFolder(file);
            } else if (file.getName().toLowerCase().endsWith(".xml") && !file.getName().toLowerCase().endsWith("_clean.xml")) {
                // Process XML files excluding those ending with "_clean.xml"
                processXmlFile(file);
            }
        }
    }

    static void processXmlFile(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();
        System.out.println("Processing " + xmlPath + " ...");

        String rootNamespace = getRootNamespace(xmlPath);
        if (rootNamespace.equals("http://docbook.org/ns/docbook")) {
            // DocBook XML
            processDocbookXML(xmlFile);
        } else {
            // Timehouse XML
            processTimehouseXML(xmlFile);
        }
    }

    private static void processDocbookXML(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();

        // Create the "pdf" subfolder if it doesn't exist
        String pdfFolder = xmlFile.getParent() + "/pdf";
        new File(pdfFolder).mkdirs();

        // Transform XML using Docbook_cleanUp.xsl
        String cleanUpXslPath = FileUtils.getAbsolutePath("xslt/Docbook_cleanUp.xsl");
        String cleanedXmlPath = pdfFolder + "/" + xmlFile.getName().replace(".xml", "_clean.xml");
        Xml xml = new Xml(xmlPath);
        xml.transform(cleanUpXslPath, cleanedXmlPath);

        // Search and replace in the cleaned XML file
        searchAndReplaceInXml(cleanedXmlPath);

        // Transform cleaned XML to FO
        String foXslPath = FileUtils.getAbsolutePath("xslt/docbook-xsl/fo/docbook_custom.xsl");
        String foPath = cleanedXmlPath.replace("_clean.xml", ".fo");
        xml = new Xml(cleanedXmlPath);
        xml.transform(foXslPath, foPath);

        // Generate PDF from FO
        String pdfPath = foPath.replace(".fo", ".pdf");
        String fopXconfPath = FileUtils.getAbsolutePath("xslt/fop.xconf.xml");
        PdfGenerator pdf = new PdfGenerator(foPath, pdfPath, fopXconfPath);
        pdf.transform();

        // Delete the _clean.xml and .fo files
        new File(cleanedXmlPath).delete();
        new File(foPath).delete();
    }

    private static void processTimehouseXML(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();

        // Create the "pdf" subfolder if it doesn't exist
        String pdfFolder = xmlFile.getParent() + "/pdf";
        new File(pdfFolder).mkdirs();

        // Copy the XML file to the "pdf" subfolder
        String copiedXmlPath = pdfFolder + "/" + xmlFile.getName();
        Path sourcePath = Paths.get(xmlPath);
        Path targetPath = Paths.get(copiedXmlPath);
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Search and replace in the copied XML file
        searchAndReplaceInXml(copiedXmlPath);

        // Determine the XSLT file based on the XML structure
        String timehouseXslPath = determineTransformationXslt(copiedXmlPath);

        // Transform copied XML to FO
        String foPath = copiedXmlPath.replace(".xml", ".fo");
        Xml xml = new Xml(copiedXmlPath);
        xml.transform(timehouseXslPath, foPath);

        // Generate PDF from FO
        String pdfPath = foPath.replace(".fo", ".pdf");
        String fopXconfPath = FileUtils.getAbsolutePath("xslt/fop.xconf.xml");
        PdfGenerator pdf = new PdfGenerator(foPath, pdfPath, fopXconfPath);
        pdf.transform();

        // Delete the copied XML file and the .fo file
        //new File(copiedXmlPath).delete();
        //new File(foPath).delete();
    }

    private static String determineTransformationXslt(String cleanedXmlPath) {
        // Get the parent folder path of the original XML file
        String parentFolderPath = new File(cleanedXmlPath).getParentFile().getParent();

        // Check if the parent folder is named "Regulation"
        if (new File(parentFolderPath).getName().equals("Regulation")) {
            return FileUtils.getAbsolutePath("xslt/Timehouse_Regulation.xsl");
        }
        else if (new File(parentFolderPath).getName().equals("EU")) {
            return FileUtils.getAbsolutePath("xslt/Timehouse_EU.xsl");
        }

        // Default XSLT file if no match found
        return FileUtils.getAbsolutePath("xslt/Timehouse_FO.xsl");
    }

    private static void searchAndReplaceInXml(String xmlPath) {
        try {
            File xmlFile = new File(xmlPath);
            BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
            StringBuilder xmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line).append("\n");
            }
            reader.close();

            // Perform search and replace using regular expressions
            String updatedXmlContent = xmlContent.toString().replaceAll("\\s*xmlns\\s*=\\s*\".*?\"", "");

            // Write updated content back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));
            writer.write(updatedXmlContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRootNamespace(String xmlPath) {
        try {
            File xmlFile = new File(xmlPath);
            BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<")) {
                    int nsStartIndex = line.indexOf("xmlns=\"");
                    if (nsStartIndex != -1) {
                        nsStartIndex += "xmlns=\"".length();
                        int nsEndIndex = line.indexOf("\"", nsStartIndex);
                        if (nsEndIndex != -1) {
                            String namespace = line.substring(nsStartIndex, nsEndIndex);
                            reader.close();
                            return namespace;
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
