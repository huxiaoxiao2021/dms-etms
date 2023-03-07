package com.jd.bluedragon.distribution.rest.filesupport;

import com.jd.bluedragon.distribution.api.request.FileRequest;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import junit.framework.TestCase;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LabelPrintFileResourceTest extends TestCase {

    @InjectMocks
    private LabelPrintFileResource labelPrintFileResource;

    @Mock
    private AmazonS3ClientWrapper labelprintAmazonS3ClientWrapper;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
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
        fileRequest.setSecretKey("dms");


        labelPrintFileResource.downloadFile(fileRequest);
    }

    @Test
    public void testListFiles() {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSecretKey("dms");
        labelPrintFileResource.listFiles(fileRequest);
    }

    @Test
    public void testDeleteFile() {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileNamePrefix("A");
        fileRequest.setFileName("B");
        fileRequest.setFolder("C");
        fileRequest.setSecretKey("dms");
        labelPrintFileResource.deleteFile(fileRequest);
    }

}