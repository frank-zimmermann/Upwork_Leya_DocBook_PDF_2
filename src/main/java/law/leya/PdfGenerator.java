package law.leya;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

public class PdfGenerator {


    private static final Logger LOGGER = LogManager.getRootLogger();

    private String fofile = null;
    private String pdffile = null;
    private String fopconfig = null;
    private Fop fop = null;
    private final TransformerFactory factory = TransformerFactory.newInstance();

    public PdfGenerator(String foFile, String pdfFile, String fopConfig) {
        this.fofile = foFile;
        this.pdffile = pdfFile;
        this.fopconfig = fopConfig;
    }


    public void transform() {
        try {

            URI baseDir = new File(fofile).toURI();

            FopFactory fopFactory = FopFactory.newInstance(new File(fopconfig));

            Source inputSource = new StreamSource(new File(fofile));
            try (OutputStream outputStram = new BufferedOutputStream(Files.newOutputStream(new File(pdffile).toPath()))) {
                fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStram);
                Transformer transformer = factory.newTransformer(); // identity transformer

                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(inputSource, result);
            }

        } catch (SAXException | IOException | TransformerException e) {
            LOGGER.fatal(e.getLocalizedMessage());
        }
    }


}
