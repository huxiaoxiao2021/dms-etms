package com.jd.bluedragon.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlHelper {
    
    private final static Logger logger = Logger.getLogger(XmlHelper.class);
    
    public static Object toObject(String request, Class<?> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(request);
            return unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            XmlHelper.logger.error("反序列化XML发生异常， 异常信息为：" + e.getMessage(), e);
        }
        
        return null;
    }
    
    public static String toXml(Object request, Class<?> clazz) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(request, doc);
            
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            
            return writer.toString();
        } catch (Exception e) {
            XmlHelper.logger.error("序列化XML发生异常， 异常信息为：" + e.getMessage(), e);
        }
        
        return null;
    }
    
    public static String objectToXml(Object request, Converter converter) {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias(request.getClass().getSimpleName(), request.getClass());
        if (null != converter) {
            xStream.registerConverter(converter);
        }
        
        return xStream.toXML(request);
    }
    
    /**
     * 将XML转为指定对象
     * 
     * @param <T>
     *            指定对象
     * @param xml
     *            需要反序列化数据，文本格式
     * @param clazz
     *            需要反序列化之后的对象
     * @param converter
     *            对象转换器
     * @return 指定对象
     */
    public static <T> T xmlToObject(String xml, Class<T> clazz, Converter converter) {
        return XmlHelper.xmlToObject(xml, clazz.getSimpleName(), clazz, converter);
    }
    
    /**
     * 将XML根据指定别名转为指定对象
     * 
     * @param <T>
     *            指定对象
     * @param xml
     *            需要反序列化数据，文本格式
     * @param aliasName
     *            指定别名
     * @param clazz
     *            需要反序列化之后的对象
     * @param converter
     *            对象转换器
     * @return 指定对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToObject(String xml, String aliasName, Class<T> clazz,
            Converter converter) {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias(aliasName, clazz);
        if (null != converter) {
            xStream.registerConverter(converter);
        }
        return (T) xStream.fromXML(xml);
    }
    
    public static Boolean isXml(String xml, Class<?> clazz, Converter converter) {
        if (StringHelper.isEmpty(xml)) {
            return Boolean.FALSE;
        }
        
        try {
            if (XmlHelper.xmlToObject(xml, clazz, converter) != null) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
        }
        
        return Boolean.FALSE;
    }
    
}
