package com.jd.bluedragon.utils;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AuthenticationInterceptor extends AbstractSoapInterceptor {
	
	private static String namespaceURI = "http://receive.service.distribution.jd.com/";
	
	private String token;
	
	public AuthenticationInterceptor() {
		super(Phase.WRITE);
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		QName qname = new QName("AuthenticationHeader");
		Document document = DOMUtils.createDocument();
		
		Element element = document.createElement("token");
		element.setTextContent(this.token);
		
		Element root = document.createElementNS(namespaceURI, "AuthenticationHeader");
		root.appendChild(element);
		
		List<Header> headers = message.getHeaders();
		SoapHeader header = new SoapHeader(qname, root);
		headers.add(header);
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
}
