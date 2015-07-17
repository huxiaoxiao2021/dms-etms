package com.jd.postal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetBillNoBySysResponseReturn_QNAME = new QName("http://printService.webservice.bigaccount.hollycrm.com", "return");
    private final static QName _GetBillNoBySysXmlStr_QNAME = new QName("http://printService.webservice.bigaccount.hollycrm.com", "xmlStr");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetBillNoByDsResponse }
     * 
     */
    public GetBillNoByDsResponse createGetBillNoByDsResponse() {
        return new GetBillNoByDsResponse();
    }

    /**
     * Create an instance of {@link UpdatePrintEMSDatasResponse }
     * 
     */
    public UpdatePrintEMSDatasResponse createUpdatePrintEMSDatasResponse() {
        return new UpdatePrintEMSDatasResponse();
    }

    /**
     * Create an instance of {@link QryEMSDataByIdResponse }
     * 
     */
    public QryEMSDataByIdResponse createQryEMSDataByIdResponse() {
        return new QryEMSDataByIdResponse();
    }

    /**
     * Create an instance of {@link PrintEMSDatasResponse }
     * 
     */
    public PrintEMSDatasResponse createPrintEMSDatasResponse() {
        return new PrintEMSDatasResponse();
    }

    /**
     * Create an instance of {@link UpdInterPrintDatasResponse }
     * 
     */
    public UpdInterPrintDatasResponse createUpdInterPrintDatasResponse() {
        return new UpdInterPrintDatasResponse();
    }

    /**
     * Create an instance of {@link QryEMSDataById }
     * 
     */
    public QryEMSDataById createQryEMSDataById() {
        return new QryEMSDataById();
    }

    /**
     * Create an instance of {@link PrintEMSDatas }
     * 
     */
    public PrintEMSDatas createPrintEMSDatas() {
        return new PrintEMSDatas();
    }

    /**
     * Create an instance of {@link GetBillNo }
     * 
     */
    public GetBillNo createGetBillNo() {
        return new GetBillNo();
    }

    /**
     * Create an instance of {@link UpdInterPrintDatas }
     * 
     */
    public UpdInterPrintDatas createUpdInterPrintDatas() {
        return new UpdInterPrintDatas();
    }

    /**
     * Create an instance of {@link UpdatePrintEMSDatas }
     * 
     */
    public UpdatePrintEMSDatas createUpdatePrintEMSDatas() {
        return new UpdatePrintEMSDatas();
    }

    /**
     * Create an instance of {@link QryEMSDatasByConResponse }
     * 
     */
    public QryEMSDatasByConResponse createQryEMSDatasByConResponse() {
        return new QryEMSDatasByConResponse();
    }

    /**
     * Create an instance of {@link GetBillNoByDs }
     * 
     */
    public GetBillNoByDs createGetBillNoByDs() {
        return new GetBillNoByDs();
    }

    /**
     * Create an instance of {@link GetBillNoBySysResponse }
     * 
     */
    public GetBillNoBySysResponse createGetBillNoBySysResponse() {
        return new GetBillNoBySysResponse();
    }

    /**
     * Create an instance of {@link UpdatePrintEMSWeigthResponse }
     * 
     */
    public UpdatePrintEMSWeigthResponse createUpdatePrintEMSWeigthResponse() {
        return new UpdatePrintEMSWeigthResponse();
    }

    /**
     * Create an instance of {@link GetBillNoResponse }
     * 
     */
    public GetBillNoResponse createGetBillNoResponse() {
        return new GetBillNoResponse();
    }

    /**
     * Create an instance of {@link TestWsOkResponse }
     * 
     */
    public TestWsOkResponse createTestWsOkResponse() {
        return new TestWsOkResponse();
    }

    /**
     * Create an instance of {@link TestWsOk }
     * 
     */
    public TestWsOk createTestWsOk() {
        return new TestWsOk();
    }

    /**
     * Create an instance of {@link GetBillNoBySys }
     * 
     */
    public GetBillNoBySys createGetBillNoBySys() {
        return new GetBillNoBySys();
    }

    /**
     * Create an instance of {@link UpdatePrintEMSWeigth }
     * 
     */
    public UpdatePrintEMSWeigth createUpdatePrintEMSWeigth() {
        return new UpdatePrintEMSWeigth();
    }

    /**
     * Create an instance of {@link QryEMSDatasByCon }
     * 
     */
    public QryEMSDatasByCon createQryEMSDatasByCon() {
        return new QryEMSDatasByCon();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = GetBillNoBySysResponse.class)
    public JAXBElement<String> createGetBillNoBySysResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, GetBillNoBySysResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = PrintEMSDatasResponse.class)
    public JAXBElement<String> createPrintEMSDatasResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, PrintEMSDatasResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = GetBillNoByDsResponse.class)
    public JAXBElement<String> createGetBillNoByDsResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, GetBillNoByDsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = GetBillNoBySys.class)
    public JAXBElement<String> createGetBillNoBySysXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, GetBillNoBySys.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = QryEMSDatasByCon.class)
    public JAXBElement<String> createQryEMSDatasByConXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, QryEMSDatasByCon.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = GetBillNo.class)
    public JAXBElement<String> createGetBillNoXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, GetBillNo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = UpdInterPrintDatas.class)
    public JAXBElement<String> createUpdInterPrintDatasXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, UpdInterPrintDatas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = GetBillNoByDs.class)
    public JAXBElement<String> createGetBillNoByDsXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, GetBillNoByDs.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = TestWsOk.class)
    public JAXBElement<String> createTestWsOkXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, TestWsOk.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = QryEMSDataById.class)
    public JAXBElement<String> createQryEMSDataByIdXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, QryEMSDataById.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = PrintEMSDatas.class)
    public JAXBElement<String> createPrintEMSDatasXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, PrintEMSDatas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = TestWsOkResponse.class)
    public JAXBElement<String> createTestWsOkResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, TestWsOkResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = UpdatePrintEMSDatasResponse.class)
    public JAXBElement<String> createUpdatePrintEMSDatasResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, UpdatePrintEMSDatasResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = UpdatePrintEMSWeigth.class)
    public JAXBElement<String> createUpdatePrintEMSWeigthXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, UpdatePrintEMSWeigth.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = UpdatePrintEMSWeigthResponse.class)
    public JAXBElement<String> createUpdatePrintEMSWeigthResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, UpdatePrintEMSWeigthResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = QryEMSDataByIdResponse.class)
    public JAXBElement<String> createQryEMSDataByIdResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, QryEMSDataByIdResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = UpdInterPrintDatasResponse.class)
    public JAXBElement<String> createUpdInterPrintDatasResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, UpdInterPrintDatasResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "xmlStr", scope = UpdatePrintEMSDatas.class)
    public JAXBElement<String> createUpdatePrintEMSDatasXmlStr(String value) {
        return new JAXBElement<String>(_GetBillNoBySysXmlStr_QNAME, String.class, UpdatePrintEMSDatas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = GetBillNoResponse.class)
    public JAXBElement<String> createGetBillNoResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, GetBillNoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://printService.webservice.bigaccount.hollycrm.com", name = "return", scope = QryEMSDatasByConResponse.class)
    public JAXBElement<String> createQryEMSDatasByConResponseReturn(String value) {
        return new JAXBElement<String>(_GetBillNoBySysResponseReturn_QNAME, String.class, QryEMSDatasByConResponse.class, value);
    }

}
