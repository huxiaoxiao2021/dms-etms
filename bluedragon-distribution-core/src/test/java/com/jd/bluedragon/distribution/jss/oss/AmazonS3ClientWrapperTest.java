package com.jd.bluedragon.distribution.jss.oss;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.input.BrokenInputStream;
import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AmazonS3ClientWrapperTest {

    private AmazonS3ClientWrapper amazonS3ClientWrapperUnderTest;

    @Before
    public void setUp() throws Exception {
        amazonS3ClientWrapperUnderTest = new AmazonS3ClientWrapper();
    }

    @Test
    public void testPutObjectAndContentType() {
        // Setup
        final InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        // Run the test
        amazonS3ClientWrapperUnderTest.putObjectAndContentType("bucketName", inputStream, "fileName", "contentType",
                0L);

        // Verify the results
    }

    @Test
    public void testPutObjectAndContentType_EmptyInputStream() {
        // Setup
        final InputStream inputStream = new NullInputStream(1);

        // Run the test
        amazonS3ClientWrapperUnderTest.putObjectAndContentType("bucketName", inputStream, "fileName", "contentType",
                0L);

        // Verify the results
    }

    @Test
    public void testPutObjectAndContentType_BrokenInputStream() {
        // Setup
        final InputStream inputStream = new BrokenInputStream();

        // Run the test
        amazonS3ClientWrapperUnderTest.putObjectAndContentType("bucketName", inputStream, "fileName", "contentType",
                0L);

        // Verify the results
    }

    @Test
    public void testPutObjectThenGetUrl() {
        // Setup
        final InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl("bucketName", inputStream, "fileName",
                0L);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    public void testPutObjectThenGetUrl_EmptyInputStream() {
        // Setup
        final InputStream inputStream = new NullInputStream(1);

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl("bucketName", inputStream, "fileName",
                0L);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    public void testPutObjectThenGetUrl_BrokenInputStream() {
        // Setup
        final InputStream inputStream = new BrokenInputStream();

        // Run the test
        final String result = amazonS3ClientWrapperUnderTest.putObjectThenGetUrl("bucketName", inputStream, "fileName",
                0L);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    public void testPutObject() {
        // Setup
        final InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject("bucketName", inputStream, "fileName", 0L);

        // Verify the results
    }

    @Test
    public void testPutObject_EmptyInputStream() {
        // Setup
        final InputStream inputStream = new NullInputStream(1);

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject("bucketName", inputStream, "fileName", 0L);

        // Verify the results
    }

    @Test
    public void testPutObject_BrokenInputStream() {
        // Setup
        final InputStream inputStream = new BrokenInputStream();

        // Run the test
        amazonS3ClientWrapperUnderTest.putObject("bucketName", inputStream, "fileName", 0L);

        // Verify the results
    }

    @Test
    public void testDeleteObject() {
        // Setup
        // Run the test
        amazonS3ClientWrapperUnderTest.deleteObject("bucketName", "key");

        // Verify the results
    }

    @Test
    public void testGeneratePresignedUrl() throws Exception {
        // Setup
        // Run the test
        final URL result = amazonS3ClientWrapperUnderTest.generatePresignedUrl("bucketName", 1, "fileName");

        // Verify the results
        assertEquals(new URL("https://example.com/"), result);
    }

    @Test
    public void testGetUrl() throws Exception {
        // Setup
        // Run the test
        final URL result = amazonS3ClientWrapperUnderTest.getUrl("bucketName", "fileName");

        // Verify the results
        assertEquals(new URL("https://example.com/"), result);
    }

    @Test
    public void testIsExists() {
        // Setup
        // Run the test
        final boolean result = amazonS3ClientWrapperUnderTest.isExists("bucketName", "fileName");

        // Verify the results
        assertFalse(result);
    }

    @Test
    public void testListObjects() {
        // Setup
        // Run the test
        final List<String> result = amazonS3ClientWrapperUnderTest.listObjects("bucketName", "fileNamePrefix", 0,
                "marker");

        // Verify the results
        assertEquals(Arrays.asList("value"), result);
    }

    @Test
    public void testListObjectListing() {
        // Setup
        // Run the test
        final ObjectListing result = amazonS3ClientWrapperUnderTest.listObjectListing("bucketName", "fileNamePrefix", 0,
                "marker");

        // Verify the results
    }

    @Test
    public void testGetObject() {
        // Setup
        // Run the test
        final S3Object result = amazonS3ClientWrapperUnderTest.getObject("bucketName", "fileName");

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
