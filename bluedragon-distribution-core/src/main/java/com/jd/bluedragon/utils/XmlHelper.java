package com.jd.bluedragon.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

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
        XStream xStream = toSafeXStream();
        xStream.alias(request.getClass().getSimpleName(), request.getClass());
        if (null != converter) {
            xStream.registerConverter(converter);
        }

        return xStream.toXML(request);
    }

    /**
     * 将XML转为指定对象
     *
     * @param <T>       指定对象
     * @param xml       需要反序列化数据，文本格式
     * @param clazz     需要反序列化之后的对象
     * @param converter 对象转换器
     * @return 指定对象
     */
    public static <T> T xmlToObject(String xml, Class<T> clazz, Converter converter) {
        return XmlHelper.xmlToObject(xml, clazz.getSimpleName(), clazz, converter);
    }

    /**
     * 将XML根据指定别名转为指定对象
     *
     * @param <T>       指定对象
     * @param xml       需要反序列化数据，文本格式
     * @param aliasName 指定别名
     * @param clazz     需要反序列化之后的对象
     * @param converter 对象转换器
     * @return 指定对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToObject(String xml, String aliasName, Class<T> clazz,
                                    Converter converter) {
        XStream xStream = toSafeXStream();
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
            XmlHelper.logger.warn("序列化XML发生异常， 异常信息为：" + e.getMessage(), e);
        }

        return Boolean.FALSE;
    }

    /**
     * 架构部安全处理
     */
    public static XStream toSafeXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.denyTypes(new String[]{"org.apache.commons.configuration.ConfigurationMap", "org.apache.commons.logging.impl.NoOpLog", "org.apache.commons.configuration.Configuration", "org.apache.commons.configuration.JNDIConfiguration", "java.util.ServiceLoader$LazyIterator", "com.sun.jndi.rmi.registry.BindingEnumeration", "org.apache.commons.beanutils.BeanComparator", "jdk.nashorn.internal.objects.NativeString", "com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data", "sun.misc.Service$LazyIterator", "com.sun.jndi.rmi.registry.ReferenceWrapper", "com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl", "org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator$PartiallyComparableAdvisorHolder", "org.springframework.beans.factory.BeanFactory", "org.springframework.jndi.support.SimpleJndiBeanFactory",

                "org.springframework.beans.factory.support.RootBeanDefinition", "org.springframework.beans.factory.support.DefaultListableBeanFactory", "org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor", "org.springframework.aop.aspectj.annotation.BeanFactoryAspectInstanceFactory", "org.springframework.aop.aspectj.AspectJPointcutAdvisor", "org.springframework.aop.aspectj.AspectJAroundAdvice", "org.springframework.aop.aspectj.AspectInstanceFactory", "org.springframework.aop.aspectj.AbstractAspectJAdvice", "javax.script.ScriptEngineFactory", "com.sun.rowset.JdbcRowSetImpl", "com.rometools.rome.feed.impl.ToStringBean", "com.rometools.rome.feed.impl.EqualsBean", "java.beans.EventHandler", "javax.imageio.ImageIO$ContainsFilter", "java.util.Collections$EmptyIterator",

                "javax.imageio.spi.FilterIterator", "java.lang.ProcessBuilder", "org.codehaus.groovy.runtime.MethodClosure", "groovy.util.Expando", "com.sun.xml.internal.ws.encoding.xml.XMLMessage$XmlDataSource"});
        return xStream;
    }

    /**
     * 过滤xml无效字符
     *
     * @param xmlStr
     * @return
     */
    public static String invalidXmlStrFilter(String xmlStr) {
        StringBuilder sb = new StringBuilder();
        for (char c : xmlStr.toCharArray()) {
            if (!(0x00 < c && c < 0x08 || 0x0b < c && c < 0x0c || 0x0e < c && c < 0x1f)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
