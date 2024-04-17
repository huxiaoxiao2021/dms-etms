package com.jd.bluedragon.distribution.jy.service.work;

import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class JyWorkGridManagerBusinessServiceImplTest {
    @Autowired
    JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;
    @Test
    public void submitData(){
        JyWorkGridManagerTaskEditRequest request = new JyWorkGridManagerTaskEditRequest();
        jyWorkGridManagerBusinessService.submitData(request);
    }
    
}
