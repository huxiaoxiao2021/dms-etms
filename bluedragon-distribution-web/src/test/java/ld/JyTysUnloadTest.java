package ld;

import com.alibaba.fastjson.JSONObject;
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

                String json = "{\n" +
                        "    \"bizId\": \"XCZJ23031600000013\",\n" +
                        "    \"currentOperate\": {\n" +
                        "        \"groupCode\": \"G00000052005\",\n" +
                        "        \"operateTime\": 1678959622178,\n" +
                        "        \"positionCode\": \"GW00108005\",\n" +
                        "        \"siteCode\": 10186,\n" +
                        "        \"siteName\": \"北京凉水河快运中心\"\n" +
                        "    },\n" +
                        "    \"goodNumber\": 22,\n" +
                        "    \"manualCreateTaskFlag\": true,\n" +
                        "    \"scanCode\": \"JD0003419474704-1-22-\",\n" +
                        "    \"scanCodeType\": 101,\n" +
                        "    \"user\": {\n" +
                        "        \"userCode\": 18225,\n" +
                        "        \"userErp\": \"xumigen\",\n" +
                        "        \"userName\": \"徐迷根\"\n" +
                        "    }\n" +
                        "}";

                UnloadScanCollectDealDto param1 = JSONObject.parseObject(json, UnloadScanCollectDealDto.class);
                String json2 = "{\n" +
                        "        \"barCode\": \"JD0003419474704-1-22-\",\n" +
                        "        \"bizId\": \"XCZJ23031600000013\",\n" +
                        "        \"collectDemoteSwitch\": false,\n" +
                        "        \"confirmMsg\": {},\n" +
                        "        \"firstScan\": false,\n" +
                        "        \"prevSiteId\": 10186,\n" +
                        "        \"prevSiteName\": \"北京凉水河快运中心\",\n" +
                        "        \"stageFirstScan\": false,\n" +
                        "        \"supplementary\": false,\n" +
                        "        \"unloadCollectDto\": {\n" +
                        "            \"collectType\": 102\n" +
                        "        },\n" +
                        "        \"warnMsg\": {}\n" +
                        "    }";
                ScanPackageRespDto scanPackageRespDto = JSONObject.parseObject(json, ScanPackageRespDto.class);
                InvokeResult<ScanPackageRespDto> param2 = new InvokeResult<>();
                param2.success();
                param2.setData(scanPackageRespDto);
                jyUnloadVehicleCheckTysService.collectDeal(param1, param2);
                log.info(JsonHelper.toJson(invokeResult));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
