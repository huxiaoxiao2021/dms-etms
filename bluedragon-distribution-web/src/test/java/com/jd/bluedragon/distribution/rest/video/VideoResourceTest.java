package com.jd.bluedragon.distribution.rest.video;

import com.jd.bluedragon.distribution.video.domain.VideoRequest;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class VideoResourceTest {
    String urlRoot ="http://localhost:8083/services";

    @Test
    public void testGetUserAndTime(){
        VideoRequest videoRequest = new VideoRequest();
        videoRequest.setCreateSiteCode(910);
        videoRequest.setOperateType(1200);
        videoRequest.setPackageCode("VA66626428278-2-2-");
        String url = this.urlRoot + "/video/getUserAndOperateTime";
        RestTemplate template = new RestTemplate();
        try {
            JdResponse response = template.postForObject(url, videoRequest, JdResponse.class);
            System.out.println(JsonHelper.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
