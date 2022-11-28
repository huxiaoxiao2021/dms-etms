package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JyComboardAggsServiceTest {

    @Autowired
    JyComboardAggsService jyComboardAggsService;

    @Test
    public void testQueryComboardAggs() throws Exception {
        JyComboardAggsEntity jyComboardAggsEntity = jyComboardAggsService.queryComboardAggs(910, 39);
        System.out.println(JsonHelper.toJson(jyComboardAggsEntity));
    }
}
