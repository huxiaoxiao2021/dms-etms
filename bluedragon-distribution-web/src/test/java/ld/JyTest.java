package ld;

import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/12
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyTest {

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Test
    public void testFindByConditionOfPage(){

        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        JyBizTaskUnloadOrderTypeEnum typeEnum = JyBizTaskUnloadOrderTypeEnum.UNLOAD_PROGRESS;
        Integer pageNum = 1;
        Integer pageSize = 20;
        condition.setEndSiteId(910L);
        condition.setVehicleStatus(JyBizTaskUnloadStatusEnum.ON_WAY.getCode());
        jyBizTaskUnloadVehicleService.findByConditionOfPage(condition,typeEnum,pageNum,pageSize);

    }

    @Test
    public void testSaveOrUpdateOfBusinessInfo(){
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();

        entity.setBizId("SC22041300014927");
        entity.setUnloadProgress(BigDecimal.valueOf(10.2));

        jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(entity);
    }
}
