
package com.jd.wms.packExchange;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.wms.packExchange package. 
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

    private final static QName _ProcessWs_QNAME = new QName("http://wms3.360buy.com", "processWs");
    private final static QName _QueryWsResponse_QNAME = new QName("http://wms3.360buy.com", "queryWsResponse");
    private final static QName _QueryWs_QNAME = new QName("http://wms3.360buy.com", "queryWs");
    private final static QName _ProcessWsResponse_QNAME = new QName("http://wms3.360buy.com", "processWsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.wms.packExchange
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessWsResponse }
     * 
     */
    public ProcessWsResponse createProcessWsResponse() {
        return new ProcessWsResponse();
    }

    /**
     * Create an instance of {@link QueryWs }
     * 
     */
    public QueryWs createQueryWs() {
        return new QueryWs();
    }

    /**
     * Create an instance of {@link QueryWsResponse }
     * 
     */
    public QueryWsResponse createQueryWsResponse() {
        return new QueryWsResponse();
    }

    /**
     * Create an instance of {@link ProcessWs }
     * 
     */
    public ProcessWs createProcessWs() {
        return new ProcessWs();
    }

    /**
     * Create an instance of {@link Result }
     * 
     */
    public Result createResult() {
        return new Result();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessWs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wms3.360buy.com", name = "processWs")
    public JAXBElement<ProcessWs> createProcessWs(ProcessWs value) {
        return new JAXBElement<ProcessWs>(_ProcessWs_QNAME, ProcessWs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryWsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wms3.360buy.com", name = "queryWsResponse")
    public JAXBElement<QueryWsResponse> createQueryWsResponse(QueryWsResponse value) {
        return new JAXBElement<QueryWsResponse>(_QueryWsResponse_QNAME, QueryWsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryWs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wms3.360buy.com", name = "queryWs")
    public JAXBElement<QueryWs> createQueryWs(QueryWs value) {
        return new JAXBElement<QueryWs>(_QueryWs_QNAME, QueryWs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessWsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wms3.360buy.com", name = "processWsResponse")
    public JAXBElement<ProcessWsResponse> createProcessWsResponse(ProcessWsResponse value) {
        return new JAXBElement<ProcessWsResponse>(_ProcessWsResponse_QNAME, ProcessWsResponse.class, null, value);
    }

}
