package law.leya;

import java.io.File;

class FileProcessor implements Runnable {
    private final File file;

    public FileProcessor(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        Converter.processXmlFile(file);
    }
}