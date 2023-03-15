package ld;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;
import com.jd.bluedragon.distribution.jy.dto.collect.UnloadScanCollectDealDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageRespDto;
import com.jd.bluedragon.distribution.jy.enums.ScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadVehicleCheckTysService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/12
 * @Description:
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyTysUnloadTest {

    @Autowired
    private JyUnloadVehicleCheckTysService jyUnloadVehicleCheckTysService;

    @Test
    public void test(){
        while (true) {
            System.out.println(System.currentTimeMillis());
        }
    }

    @Test
    public void collectDeal(){
        while(true) {

            try{
                String bizId = "XCZJ23031500000017";
                String scanCode = "JD0003419465569-1-20-";

                User user = new User();
                user.setUserErp("xumigen");

                CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(10186);

                InvokeResult<ScanPackageRespDto> invokeResult = new InvokeResult<>();
                UnloadScanCollectDealDto collectDealDto = new UnloadScanCollectDealDto();
                collectDealDto.setScanCode(scanCode);
                collectDealDto.setUser(user);
                collectDealDto.setCurrentOperate(currentOperate);
                collectDealDto.setBizId(bizId);
                collectDealDto.setScanCodeType(ScanCodeTypeEnum.SCAN_PACKAGE.getCode());
//            collectDealDto.setScanCodeType(ScanCodeTypeEnum.SCAN_WAYBILL.getCode());
                collectDealDto.setManualCreateTaskFlag(true);
//            collectDealDto.setManualCreateTaskFlag(false);

                jyUnloadVehicleCheckTysService.collectDeal(collectDealDto, invokeResult);
                log.info(JsonHelper.toJson(invokeResult));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
