package org.planit.utils.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.misc.Pair;

/**
 * Utilities for creating and using basic Java xml writers
 * 
 * @author markr
 *
 */
public class PlanitXmlWriterUtils {
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(PlanitXmlWriterUtils.class.getCanonicalName());
  
  /** utf-8 string */
  public static final String UTF8 = "UTF-8";
  
  /** xml versin 1.0 string */
  public static final String XML_V1= "1.0";

  /**
   * create an xml stream writer for the given path
   * @param xmlFilePath to create the writer for
   * @return created xml stream writer and writer instances
   * @throws PlanItException thrown if error
   */
  public static Pair<XMLStreamWriter, Writer> createXMLWriter(Path xmlFilePath) throws PlanItException {
    Path absoluteXmlPath = xmlFilePath.toAbsolutePath();
        
    /* create dir if not present */
    File directory = absoluteXmlPath.getParent().toFile();
    if(!directory.exists()) {      
      if(!directory.mkdirs()) {
        throw new PlanItException(String.format("Unable to create Xml writer output directory %s",directory.toString()));
      }      
    }
    
    /* create writer */
    Writer theWriter = null;
    try {    
      theWriter = new OutputStreamWriter(new FileOutputStream(absoluteXmlPath.toFile()), "UTF-8");
      XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
      return Pair.of(xmlOutputFactory.createXMLStreamWriter(theWriter),theWriter);
    } catch (XMLStreamException | IOException e) {
      try {
        theWriter.flush();
        theWriter.close();
      } catch (Exception ex) {
      }       
      LOGGER.severe(e.getMessage());
      throw new PlanItException("Could not instantiate XML writer",e);
    }finally {     
    }  
  }
  
  /** write a new line to the stream, e.g. "\n"
   * @param xmlWriter to use
   * @throws XMLStreamException thrown if error 
   */
  public static void writeNewLine(XMLStreamWriter xmlWriter) throws XMLStreamException {
    xmlWriter.writeCharacters("\n");
  } 
  
  /** add indentation to stream at current indentation level via tabs
   * 
   * @param xmlWriter to use
   * @param indentationLevel to use
   * @throws XMLStreamException thrown if error
   */
  public static void writeIndentation(XMLStreamWriter xmlWriter, int indentationLevel) throws XMLStreamException {
    for(int index=0;index<indentationLevel;++index) {
      xmlWriter.writeCharacters("\t");
    }
  }  
  
  /**
   * write an empty element (with indentation), e.g. {@code <xmlElementName/>}
   * 
   * @param xmlWriter to use
   * @param xmlElementName element to start tag, e.g. <xmlElementName>\n
   * @param indentationLevel to use
   * @throws XMLStreamException thrown if error
   */
  public static void writeEmptyElement(XMLStreamWriter xmlWriter, String xmlElementName, int indentationLevel) throws XMLStreamException {
    writeIndentation(xmlWriter, indentationLevel);
    xmlWriter.writeEmptyElement(xmlElementName);
  }  
  
  /**
   * write a start element (with indentation) and add newline afterwards
   * 
   * @param xmlWriter to use
   * @param xmlElementName element to start tag, e.g. {@code <xmlElementName>}
   * @param indentationLevel to use
   * @throws XMLStreamException thrown if error
   */
  public static  void writeStartElementNewLine(XMLStreamWriter xmlWriter, String xmlElementName, int indentationLevel) throws XMLStreamException {
    writeIndentation(xmlWriter,indentationLevel);
    xmlWriter.writeStartElement(xmlElementName);
    writeNewLine(xmlWriter);
  }
   
  
  /**
   * write an end element and add newline afterwards
   * 
   * @param xmlWriter to use
   * @param indentationLevel to use
   * @throws XMLStreamException thrown if error
   */  
  public static  void writeEndElementNewLine(XMLStreamWriter xmlWriter, int indentationLevel) throws XMLStreamException {
    writeIndentation(xmlWriter, indentationLevel);    
    xmlWriter.writeEndElement();
    writeNewLine(xmlWriter);
  }  
  
  /** start xml document
   * @param xmlFileWriter the writer
   * @param docType to reference
   * @throws XMLStreamException thrown if exception
   */
  public static void startXmlDocument(XMLStreamWriter xmlWriter, final String docType) throws XMLStreamException {
    
    /* <xml tag> */
    xmlWriter.writeStartDocument(UTF8, XML_V1);
    
    /* less ugly by adding new lines */
    writeNewLine(xmlWriter);
    
    /* DOCTYPE reference (MATSIM is based on dtd rather than schema)*/
    xmlWriter.writeDTD(docType);
    writeNewLine(xmlWriter);
  } 
  
  /** end the xml document and close the writers, streams etc.
   * 
   * @param xmlFileWriterPair writer pair
   * @throws XMLStreamException thrown if error
   * @throws IOException thrown if error
   */
  public static void endXmlDocument(Pair<XMLStreamWriter, Writer> xmlFileWriterPair) throws XMLStreamException, IOException {
    XMLStreamWriter xmlWriter = xmlFileWriterPair.first();
    Writer writer = xmlFileWriterPair.second();
    xmlWriter.writeEndDocument();
   
    xmlWriter.flush();
    xmlWriter.close();

    writer.flush();
    writer.close();
  }  
}
