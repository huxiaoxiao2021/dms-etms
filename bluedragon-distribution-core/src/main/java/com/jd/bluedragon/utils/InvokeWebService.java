package com.jd.bluedragon.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientCallback;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;

import com.jd.bluedragon.Constants;

public class InvokeWebService {
    
    private static final String JD_TOKEN = "JD_TOKEN";
    
    private static Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();
    private static final DynamicClientFactory dynamicClientFactory = DynamicClientFactory
            .newInstance();
    
    private static Client getClient(String service) {
        Client client = clientMap.get(service);
        if (client != null) {
            return client;
        }
        
        String webService = PropertiesHelper.newInstance().getValue(service);
        String[] webServiceArray = webService.split(Constants.SEPARATOR_COMMA);
        
        synchronized (InvokeWebService.class) {
            if (clientMap.get(service) == null) {
                String jqToken = PropertiesHelper.newInstance().getValue(JD_TOKEN);
                
                client = dynamicClientFactory.createClient(webServiceArray[0].trim());
                AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor();
                authInterceptor.setToken(jqToken);
                client.getOutInterceptors().add(authInterceptor);
                ((HTTPConduit) client.getConduit()).getClient().setAllowChunking(false);
                clientMap.put(service, client);
            } else {
                client = clientMap.get(service);
            }
        }
        
        return client;
    }
    
    /**
     * 异步调用webService
     * 
     * @param service
     * @param params
     * @throws Exception
     */
    public static void asyncInvoke(String service, Object[] params) throws Exception {
        String webService = PropertiesHelper.newInstance().getValue(service);
        if (StringHelper.isNotEmpty(webService)) {
            String[] webServiceArray = webService.split(Constants.SEPARATOR_COMMA);
            ClientCallback callback = new ClientCallback();
            getClient(service).invoke(callback, webServiceArray[1].trim(), params);
        }
    }
    
    /**
     * 同步调用WebService
     * 
     * @param service
     * @param params
     * @return
     * @throws Exception
     */
    public static Object[] syncInvoke(String service, Object[] params) throws Exception {
        String webService = PropertiesHelper.newInstance().getValue(service);
        if (StringHelper.isNotEmpty(webService)) {
            String[] webServiceArray = webService.split(Constants.SEPARATOR_COMMA);
            return getClient(service).invoke(webServiceArray[1].trim(), params);
        }
        
        return null;
    }
    
}
