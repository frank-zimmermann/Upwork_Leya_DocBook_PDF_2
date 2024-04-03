package law.leya.File;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.trans.XmlCatalogResolver;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;


public class Xml extends File {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private DocumentBuilderFactory factory = null;
    private DocumentBuilder builder = null;
    private Document doc = null;
    private XPathFactory xPathfactory = null;
    private XPath xPath = null;
    private XPathExpression expr = null;
    private XMLReader reader = null;


    private NodeList nl;


    public Xml(String xmlfile) {
        super(xmlfile);

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            xPathfactory = XPathFactory.newInstance();
            xPath = xPathfactory.newXPath();

        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Transformation of the Xml file with a Xsl-Stylesheet.
     *
     * @param xsl    The xsl file.
     * @param xmlout The result xml file
     */
    public void transform(String xsl, String xmlout) {
        transform(xsl, xmlout, null);
    }

    /**
     * Transformation of the Xml file with a Xsl-Stylesheet.
     * @param xsl    The xsl file.
     * @param xmlout The result xml file
     * @param catalog The catalog file.
     */
    public void transform(String xsl, String xmlout, String catalog) {


        File xmlFile = new File(this.getAbsolutePath());
        File xslFile = new File(xsl);
        File outputFile = new File(xmlout);


        Source xmlSource = new StreamSource(xmlFile);
        Source xslSource = new StreamSource(xslFile);
        Result outputResult = new StreamResult(outputFile);


        Configuration conf = new Configuration();

        conf.setValidationWarnings(true);
        conf.setXIncludeAware(true);
        conf.setXMLVersion(2);
        conf.setValidation(catalog != null);


        Transformer transformer = null;

        if (catalog != null) {
            try {
                XmlCatalogResolver.setCatalog(catalog, conf, false);
                XmlCatalogResolver r = new XmlCatalogResolver();

            } catch (XPathException e) {
                LOGGER.fatal(e.getMessage());
            }
        }

        TransformerFactory tf = new TransformerFactoryImpl(conf);

        try {
            transformer = tf.newTransformer(xslSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            LOGGER.fatal(e.getMessage());

        }

        try {
            transformer.transform(xmlSource, outputResult);
        } catch (TransformerException e) {
            LOGGER.fatal(e.getMessage());
        }

    }
}
