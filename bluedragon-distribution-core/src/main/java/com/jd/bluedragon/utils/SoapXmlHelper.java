package com.jd.bluedragon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * <p>
 *     简单的SOAP的通讯报文的构建器
 *     一般的soap包含Envelope header body fault元素
 *     这里就偷个懒了，后续开发者按需添加相应标签
 *
 * @author wuzuxiang
 * @since 2019/12/23
 **/
public class SoapXmlHelper {

    private static Logger logger = LoggerFactory.getLogger(SoapXmlHelper.class);

    /**
     * 构建简单的soap的报文
     * @param methodName 请求的方法名
     * @param nameSpaceUIR 命名空间
     * @param parameterName 请求参数名
     * @param body 请求内容
     * @return 返回 soap报文
     * @throws RuntimeException 当发生SOAPException 和 IOException
     * @see SOAPException
     * @see IOException
     */
    public static String createSoapXml(String methodName, String nameSpaceUIR, String parameterName, String body) throws RuntimeException {
        try {
            SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.DEFAULT_SOAP_PROTOCOL).createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapMessage.getSOAPBody();

            soapEnvelope.createName("Envelope","soap", SOAPConstants.URI_NS_SOAP_ENVELOPE);

            SOAPFactory factory = SOAPFactory.newInstance();
            SOAPElement soapElement = factory.createElement(methodName,"",nameSpaceUIR);
            SOAPElement content = factory.createElement(QName.valueOf(parameterName));
            content.addTextNode(body);
            soapElement.addChildElement(content);
            soapBody.addChildElement(soapElement);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (SOAPException | IOException e) {
            String errorMsg = MessageFormat.format("构建简单的SOAP报文发生异常。【method:{}】【nameSpaceUIR:{}】【parameter:{}】",
                    methodName, nameSpaceUIR, parameterName);
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg,e);
        }
    }



}
