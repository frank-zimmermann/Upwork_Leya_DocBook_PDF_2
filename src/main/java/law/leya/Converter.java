package law.leya;

import law.leya.File.Xml;

import java.io.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class Converter {
    public static void main(String[] args) {

        System.out.println("-------------------");
        System.out.println("Leya PDF Generator");
        System.out.println("-------------------");

        ArgumentParser parser = new ArgumentParser(args);
        String xmlFolder = parser.getXmlFolder();

        // Remove *_clean.xml files before processing
        removeCleanXmlFiles(new File(xmlFolder));

        // Process XML files in the specified folder and its subfolders
        processFolder(new File(xmlFolder));
    }

    private static void removeCleanXmlFiles(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                // Recursively process subfolders
                removeCleanXmlFiles(file);
            } else if (file.getName().toLowerCase().endsWith("_clean.xml")) {
                // Delete *_clean.xml files
                file.delete();
            }
        }
    }

    private static void processFolder(File folder) {
        File[] files = Objects.requireNonNull(folder.listFiles());
        boolean hasXmlFiles = false;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively process subfolders
                processFolder(file);
            } else if (file.getName().toLowerCase().endsWith(".xml") && !file.getName().toLowerCase().endsWith("_clean.xml")) {
                hasXmlFiles = true;
            }
        }

        if (hasXmlFiles) {
            System.out.println("Processing folder " + folder.getAbsolutePath() + " ...");

            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".xml") && !file.getName().toLowerCase().endsWith("_clean.xml")) {
                    processXmlFile(file);
                }
            }
        }
    }

    private static void processXmlFile(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();
        System.out.println("Processing " + xmlPath + " ...");

        String rootNamespace = getRootNamespace(xmlPath);
        if (rootNamespace.equals("http://docbook.org/ns/docbook")) {
            // DocBook XML
            processDocbookXML(xmlFile);
        } else if (rootNamespace.equals("http://www.timehouse.fi/schemas/HtmlLike")) {
            // Timehouse XML
            processTimehouseXML(xmlFile);
        } else {
            System.err.println("Unsupported namespace '" + rootNamespace + "' for file: " + xmlPath + ". Skipping transformation.");
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

        // Transform XML using Timehouse_cleanup.xsl
        String cleanUpXslPath = FileUtils.getAbsolutePath("xslt/Timehouse_cleanup.xsl");
        String cleanedXmlPath = pdfFolder + "/" + xmlFile.getName().replace(".xml", "_clean.xml");
        Xml xml = new Xml(xmlPath);
        xml.transform(cleanUpXslPath, cleanedXmlPath);

        // Transform cleaned XML to FO
        String timehouseXslPath = FileUtils.getAbsolutePath("xslt/Timehouse_FO.xsl");
        String foPath = cleanedXmlPath.replace("_clean.xml", ".fo");
        xml = new Xml(cleanedXmlPath);
        xml.transform(timehouseXslPath, foPath);

        // Generate PDF from FO
        String pdfPath = foPath.replace(".fo", ".pdf");
        String fopXconfPath = FileUtils.getAbsolutePath("xslt/fop.xconf.xml");
        PdfGenerator pdf = new PdfGenerator(foPath, pdfPath, fopXconfPath);
        pdf.transform();

        // Delete the _clean.xml and .fo files
        new File(cleanedXmlPath).delete();
        new File(foPath).delete();
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
            String updatedXmlContent = xmlContent.toString().replaceAll(Pattern.quote("xmlns=\"\""), "");

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
