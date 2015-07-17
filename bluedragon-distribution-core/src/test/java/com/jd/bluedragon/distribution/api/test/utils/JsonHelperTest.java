package com.jd.bluedragon.distribution.api.test.utils;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonHelperTest {

    private String jsonText="{\n" +
            "\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"OK\",\n" +
            "    \"siteCode\": 54106,\n" +
            "    \"siteName\": \"泉州水头站\",\n" +
            "    \"siteType\": 4,\n" +
            "    \"dmsCode\": \"595Y007\"\n" +
            "\n" +
            "}";
    @BeforeClass
    public static void init(){

    }

    @Test
    public void testFromJson() throws Exception {
        JdResponse response= JsonHelper.fromJson(jsonText,JdResponse.class);

        Assert.assertNotNull(response);
    }

    @Test
    public void testJsonToArray() throws Exception {
        JdResponse response= JsonHelper.fromJson(jsonText,JdResponse.class);

        Assert.assertNotNull(response);
    }

    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testToJson1() throws Exception {

    }

    @Test
    public void testJson2Map() throws Exception {

    }
}