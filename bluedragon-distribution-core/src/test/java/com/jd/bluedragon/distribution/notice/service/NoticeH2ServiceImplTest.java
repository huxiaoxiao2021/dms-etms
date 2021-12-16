package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.api.Response;
/*import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeLastNewDto;*/
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-12 22:14:50 周日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/distribution-core-context-test.xml"})
public class NoticeH2ServiceImplTest {

    /*@Autowired
    private NoticeH5Service noticeH5Service;

    @Test
    public void getLastNewNotice(){
        NoticePdaQuery noticePdaQuery = new NoticePdaQuery();
        noticePdaQuery.setUserErp("bjxings");
        Response<NoticeLastNewDto> result = noticeH5Service.getLastNewNotice(noticePdaQuery);
        System.out.println(result);
    }*/
}
