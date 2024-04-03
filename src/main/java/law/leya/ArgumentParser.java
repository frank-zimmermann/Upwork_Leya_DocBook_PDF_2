package law.leya;

import org.apache.commons.cli.*;

public class ArgumentParser {

    private final String[] arguments;
    private String xmlFolder;

    public ArgumentParser(String[] args) {
        this.arguments = args;
        parseCommandLineOptions();
    }

    public String getXmlFolder() {
        return xmlFolder;
    }

    private void parseCommandLineOptions() {
        Options options = new Options();

        Option xmlFolderOption = Option.builder("f")
                .argName("folder")
                .hasArg()
                .longOpt("xmlFolder")
                .desc("Folder containing XML files")
                .build();

        options.addOption(xmlFolderOption);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, arguments);

            if (cmd.hasOption("f")) {
                xmlFolder = cmd.getOptionValue("f");
                if (!isValidFolder(xmlFolder)) {
                    throw new ParseException("Invalid folder path specified.");
                }
            } else {
                throw new ParseException("Missing required parameter: xmlFolder");
            }
        } catch (ParseException e) {
            System.err.println("Error parsing command line: " + e.getMessage());
            printHelp(options);
            System.exit(1);
        }
    }

    private boolean isValidFolder(String folderPath) {
        return new java.io.File(folderPath).isDirectory();
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar LeyaXMLtoPDF", options, true);
    }
}
