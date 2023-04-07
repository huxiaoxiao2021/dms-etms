package com.jd.bluedragon.distribution.rest.filesupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInputImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.api.request.FileRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class LabelPrintFileResourceTest extends TestCase {

    @InjectMocks
    private LabelPrintFileResource labelPrintFileResource;

    @Mock
    private AmazonS3ClientWrapper labelprintAmazonS3ClientWrapper;

    private String secretKey = "dms-print-test";
    private String dmsWebSecretKey = "46BF9403EC39C597A04E4E505224D063";
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        labelPrintFileResource.fileModifySecretKey = secretKey;
        Mockito.doNothing().when(labelprintAmazonS3ClientWrapper).putObjectWithFlow(any(),any(),any(),any());
        when(labelprintAmazonS3ClientWrapper.getObject(any(),any())).thenReturn(null);
//        when(labelprintAmazonS3ClientWrapper.listObjects("","",100,null)).thenReturn(any());
        Mockito.doNothing().when(labelprintAmazonS3ClientWrapper).deleteObject(any(),any());
    }

    @Test
    public void testUploadfile() {
        MultipartFormDataInput formDataInputs = new MultipartFormDataInputImpl(null,new ResteasyProviderFactory());
        labelPrintFileResource.uploadfile(formDataInputs);
    }

    @Test
    public void testDownloadFile() {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSourceSysName("dmsWeb");
        fileRequest.setSecretKey(dmsWebSecretKey);
        Response result= labelPrintFileResource.downloadFile(fileRequest);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void testListFiles() {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSourceSysName("dmsWeb");
        fileRequest.setSecretKey(dmsWebSecretKey);
        labelPrintFileResource.listFiles(fileRequest);
    }

    @Test
    public void testDeleteFile() {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSourceSysName("dmsWeb");
        fileRequest.setSecretKey(dmsWebSecretKey);
        InvokeResult<Boolean> result = labelPrintFileResource.deleteFile(fileRequest);
        System.out.println(JsonHelper.toJson(result));
    }
    @Test
    public void testGetSecretKey() throws Exception {
    	Thread.sleep(3000);
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSourceSysName("test");
        String[] secretKeys = {"dms-print-test","dms-print-prod"};
        //
        String[] systems = {
        		"test",
        		"dmsClient40","dmsClient42","dmsClient43",
        		"dmsWeb",
        		"sms",
        		};
        for(String secretKey:secretKeys) {
            fileRequest.setSecretKey(secretKey);
            for(String system:systems) {
            	fileRequest.setSourceSysName(system);
                InvokeResult<String> result = labelPrintFileResource.getSecretKey(fileRequest);
                System.out.println(system+":"+result.getData());
                System.out.println(system+":"+result.getData());
            }
        }
        System.out.println("测试环境秘钥：");
        fileRequest.setSecretKey(secretKeys[0]);
        labelPrintFileResource.fileModifySecretKey = secretKeys[0];
        for(String system:systems) {
        	fileRequest.setSourceSysName(system);
            InvokeResult<String> result = labelPrintFileResource.getSecretKey(fileRequest);
            System.out.println(system+":"+result.getData());
        }
        System.out.println("正式环境秘钥：");
        fileRequest.setSecretKey(secretKeys[1]);
        labelPrintFileResource.fileModifySecretKey = secretKeys[1];
        for(String system:systems) {
        	fileRequest.setSourceSysName(system);
            InvokeResult<String> result = labelPrintFileResource.getSecretKey(fileRequest);
            System.out.println(system+":"+result.getData());
        }
    }
    
}