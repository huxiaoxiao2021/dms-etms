package com.jd.bluedragon.distribution.test.utils;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonUtilTest {

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
    private static String jsonArray="";
    @Test
    public void testGetInstance() throws Exception {
        Assert.assertNotNull(JsonUtil.getInstance());
    }

    @Test
    public void testObject2Json() throws Exception {
        JdResponse response=new JdResponse();
        response.setCode(200);
        response.setMessage("OK");
        String result= JsonUtil.getInstance().object2Json(response);
        response=(JdResponse)JsonUtil.getInstance().json2Object(result,JdResponse.class);
        Assert.assertEquals(Integer.valueOf(200),response.getCode());
        Assert.assertEquals("OK",response.getMessage());
    }

    @Test
    public void testJson2Object() throws Exception {
        JdResponse response=(JdResponse)JsonUtil.getInstance().json2Object(jsonText,JdResponse.class);
        Assert.assertEquals(Integer.valueOf(200),response.getCode());
        Assert.assertEquals("OK",response.getMessage());
    }

    @Test
    public void testJson2List() throws Exception {

    }

    @Test
    public void testJson2List1() throws Exception {

    }

    @Test
    public void testList2Json() throws Exception {

    }
}