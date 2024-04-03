package law.leya;

import law.leya.File.Xml;

import java.io.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class Converter {
    public static void main(String[] args) {
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
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                // Recursively process subfolders
                processFolder(file);
            } else if (file.getName().toLowerCase().endsWith(".xml")) {
                // Process XML files excluding those ending with "_clean.xml"
                if (!file.getName().toLowerCase().endsWith("_clean.xml")) {
                    processXmlFile(file);
                }
            }
        }
    }

    private static void processXmlFile(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();
        System.out.println("Processing " + xmlPath + " ...");

        // Transform XML using cleanUp.xsl
        String cleanUpXslPath = FileUtils.getAbsolutePath("xslt/cleanUp.xsl");
        String cleanedXmlPath = xmlPath.replace(".xml", "_clean.xml");
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
        String pdfPath = cleanedXmlPath.replace("_clean.xml", ".pdf");
        String fopXconfPath = FileUtils.getAbsolutePath("xslt/fop.xconf.xml");
        PdfGenerator pdf = new PdfGenerator(foPath, pdfPath, fopXconfPath);
        pdf.transform();
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
}
