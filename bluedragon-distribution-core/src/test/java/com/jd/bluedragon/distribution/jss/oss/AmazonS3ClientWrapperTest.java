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
        amazonS3ClientWrapperUnderTest.setAccessKey("JDC_D7F39DBD95C716540108BD9333F4");
        amazonS3ClientWrapperUnderTest.setSecretKey("0AB20B66892C2A4374094F360F912C0A");
        amazonS3ClientWrapperUnderTest.setSigningRegion("cn-north-1");
        amazonS3ClientWrapperUnderTest.setEndpoint("http://s3-internal-office.cn-north-1.jdcloud-oss.com");
        amazonS3ClientWrapperUnderTest.setSocketTimeout(5000);
        amazonS3ClientWrapperUnderTest.setConnectionTimeout(5000);
        amazonS3ClientWrapperUnderTest.setBucketName("dmsweb");
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
    public void testPutObject() {
        // Setup
        final InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject(inputStream, "","fileName", 0L);

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
        // Setup
        // Run the test
        final URL result = amazonS3ClientWrapperUnderTest.generatePresignedUrl( 1, "test2","北京开放大学形考3.pdf");

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
}
