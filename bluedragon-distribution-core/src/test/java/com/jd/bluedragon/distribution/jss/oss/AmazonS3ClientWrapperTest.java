package com.jd.bluedragon.distribution.jss.oss;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.input.BrokenInputStream;
import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AmazonS3ClientWrapperTest {

    private AmazonS3ClientWrapper amazonS3ClientWrapperUnderTest;

    @Before
    public void setUp() throws Exception {
        amazonS3ClientWrapperUnderTest = new AmazonS3ClientWrapper();
        amazonS3ClientWrapperUnderTest.setAccessKey("JDC_F1852E17FA45BCDD76ECA7E28DF8");
        amazonS3ClientWrapperUnderTest.setSecretKey("0939322DE96A77695E2F2110891EF23E");
        amazonS3ClientWrapperUnderTest.setSigningRegion("cn-north-1");
        amazonS3ClientWrapperUnderTest.setEndpoint("http://s3-internal-office.cn-north-1.jdcloud-oss.com");
        amazonS3ClientWrapperUnderTest.setSocketTimeout(5000);
        amazonS3ClientWrapperUnderTest.setConnectionTimeout(5000);
        amazonS3ClientWrapperUnderTest.setBucketName("workbench");
        amazonS3ClientWrapperUnderTest.setOuterNetEndpoint("http://s3.cn-north-1.jdcloud-oss.com");
        amazonS3ClientWrapperUnderTest.afterPropertiesSet();

    }

    @Test
    public void testPutObjectAndContentType() throws Exception{
        // Setup
        File file = new File("/Users/xumigen/Downloads/北京开放大学形考3.pdf");

        // Run the test
        amazonS3ClientWrapperUnderTest.putObjectAndContentType( new FileInputStream(file), "test2",file.getName(), null,
                0L);

        // Verify the results
    }



    @Test
    public void testPutObjectThenGetUrl() throws Exception{
        // Setup
        File file = new File("/Users/xumigen/Downloads/北京开放大学形考3.pdf");

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl( new FileInputStream(file), "test3","北京开放大学形考3.pdf",
                0L,0);

        // Verify the results
        assertNotNull( result);
    }

    @Test
    public void testPutObjectThenGetUrl_EmptyInputStream() {
        // Setup
        final InputStream inputStream = new NullInputStream(1);

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl(inputStream, "","fileName",
                0L,0);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    public void testPutObjectThenGetUrl_BrokenInputStream() {
        // Setup
        final InputStream inputStream = new BrokenInputStream();

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl(inputStream, "","fileName",
                0L,0);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    public void testPutObject() throws Exception{
        // Setup
        FileInputStream fileInputStream = new FileInputStream("/Users/xumigen/Downloads/1-PDA通用安装包.rar");

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject(fileInputStream, "test","anzhaung/tt/1-PDA通用安装包.rar", 0L);

        // Verify the results
    }

    @Test
    public void testPutObject_EmptyInputStream() {
        // Setup
        final InputStream inputStream = new NullInputStream(1);

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject( inputStream, "","fileName", 0L);

        // Verify the results
    }

    @Test
    public void testPutObject_BrokenInputStream() {
        // Setup
        final InputStream inputStream = new BrokenInputStream();

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject( inputStream, "","fileName", 0L);

        // Verify the results
    }

    @Test
    public void testDeleteObject() {
        // Setup
        // Run the test
        amazonS3ClientWrapperUnderTest.deleteObject( "","key");

        // Verify the results
    }

    @Test
    public void testGeneratePresignedUrl() throws Exception {
        // Setup /wb-public-bucket/DMS_WB_SUP_CHECK_19176410596706883C24E9A85D77A6C087B603F6913B9C154.mp4
        // Run the test
        final URL result = amazonS3ClientWrapperUnderTest.generatePresignedUrl( 10, "wb-public-bucket","DMS_WB_SUP_CHECK_19176410596706883C24E9A85D77A6C087B603F6913B9C154.mp4");
        System.out.println(result);
        // Verify the results
        assertNotNull(result);
    }


    @Test
    public void testgeneratePresignedOuterNetUrl() throws Exception {
        // Setup
        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.generatePresignedOuterNetUrl( 1, "*","00000cd3-2b79-4945-91bd-88b3e869624c.jpg");
        System.out.println(result);
        // Verify the results
        assertNotNull(result);
    }

    @Test
    public void testGetUrl() throws Exception {
        // Setup
        // Run the test
        final URL result = amazonS3ClientWrapperUnderTest.getUrl( "test2","北京开放大学形考3.pdf");

        // Verify the results
        assertNotNull(result);
    }

    @Test
    public void testIsExists() {
        // Setup
        // Run the test
        final boolean result = amazonS3ClientWrapperUnderTest.isExists( "","fileName");

        // Verify the results
        assertFalse(result);
    }

    @Test
    public void testListObjects() {
        // Setup
        // Run the test
        final List<String> result = amazonS3ClientWrapperUnderTest.listObjects( "","fileNamePrefix", 0,
                "marker");

        // Verify the results
        assertEquals(Arrays.asList("value"), result);
    }

    @Test
    public void testGetObject() {
        // Setup
        // Run the test
        final S3Object result = amazonS3ClientWrapperUnderTest.getObject( "","fileName");

        // Verify the results
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        // Setup
        // Run the test
        amazonS3ClientWrapperUnderTest.afterPropertiesSet();

        // Verify the results
    }

    @Test(expected = Exception.class)
    public void testAfterPropertiesSet_ThrowsException() throws Exception {
        // Setup
        // Run the test
        amazonS3ClientWrapperUnderTest.afterPropertiesSet();
    }

    @Test
    public void testPutObjectThenGetOutNetUrl() throws Exception{
        FileInputStream fileInputStream = new FileInputStream("/Users/xumigen/Downloads/BlueDragonPrintService.zip");
        amazonS3ClientWrapperUnderTest.putObjectThenGetOutNetUrl(fileInputStream,"test","BlueDragonPrintService.zip",0,365);
    }
}
