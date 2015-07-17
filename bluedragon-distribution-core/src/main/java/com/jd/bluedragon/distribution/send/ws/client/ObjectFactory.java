
package com.jd.bluedragon.distribution.send.ws.client;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.bluedragon.distribution.send.ws.client package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.bluedragon.distribution.send.ws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PutMessage }
     * 
     */
    public PutMessage createPutMessage() {
        return new PutMessage();
    }

    /**
     * Create an instance of {@link PutMessageResponse }
     * 
     */
    public PutMessageResponse createPutMessageResponse() {
        return new PutMessageResponse();
    }

    /**
     * Create an instance of {@link PutMessageByNewStoreIDResponse }
     * 
     */
    public PutMessageByNewStoreIDResponse createPutMessageByNewStoreIDResponse() {
        return new PutMessageByNewStoreIDResponse();
    }

    /**
     * Create an instance of {@link ReturnMessage }
     * 
     */
    public ReturnMessage createReturnMessage() {
        return new ReturnMessage();
    }

    /**
     * Create an instance of {@link PutMessageByNewStoreID }
     * 
     */
    public PutMessageByNewStoreID createPutMessageByNewStoreID() {
        return new PutMessageByNewStoreID();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

}
