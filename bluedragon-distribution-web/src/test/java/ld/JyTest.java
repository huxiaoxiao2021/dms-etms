package ld;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadNoTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadNoTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        jyBizTaskUnloadVehicleService.findByConditionOfPage(condition,typeEnum,pageNum,pageSize, null);

    }

    @Test
    public void testSaveOrUpdateOfBusinessInfo(){
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();

        entity.setBizId("SC22041300014927");
        entity.setUnloadProgress(BigDecimal.valueOf(10.2));

        jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(entity);
    }

    @Autowired
    private JyUnloadVehicleGatewayService jyUnloadVehicleGatewayService;

    Logger logger = LoggerFactory.getLogger(JyTest.class);

    @Test
    public void testNoTaskScan(){

        //第一步创建无任务模式卸车任务
        //第二步进入扫描界面
        //第三步扫描包裹开始卸车
        //第四步返回重新进入扫描界面

        while (true){
            try{

                String bizId = "";
                String taskId = "";
                String vehicleNumber = "津A22337";

                String json1 = "{\"operateSiteId\":910,\"operateSiteName\":\"北京马驹桥分拣中心6\",\"vehicleNumber\":\""+vehicleNumber+"\"}";
                UnloadNoTaskRequest request1 = JsonHelper.fromJson(json1, UnloadNoTaskRequest.class);

                JdCResponse<UnloadNoTaskResponse> noTaskUnloadTask = jyUnloadVehicleGatewayService.createNoTaskUnloadTask(request1);
                taskId = noTaskUnloadTask.getData().getTaskId();
                bizId = noTaskUnloadTask.getData().getBizId();



                String json2 = "{\"bizId\":\""+bizId+"\",\"currentOperate\":{\"operateTime\":1649938766880,\"orgId\":10,\"orgName\":\"广州分公司\",\"siteCode\":910,\"siteName\":\"北京马驹桥分拣中心6\"},\"sealCarCode\":\"\",\"user\":{\"userCode\":17849,\"userErp\":\"liuduo8\",\"userName\":\"刘铎\"},\"vehicleNumber\":\""+vehicleNumber+"\"}";
                UnloadCommonRequest request2 = JsonHelper.fromJson(json2,UnloadCommonRequest.class);

                JdCResponse<UnloadScanDetail> unloadScanDetailJdCResponse1 = jyUnloadVehicleGatewayService.unloadDetail(request2);

                String barCode1 = "JDVF00001747218-1-5-";
                String json3 = "{\"barCode\":\""+barCode1+"\",\"bizId\":\""+bizId+"\",\"currentOperate\":{\"operateTime\":1649938834584,\"orgId\":10,\"orgName\":\"广州分公司\",\"siteCode\":910,\"siteName\":\"北京马驹桥分拣中心6\"},\"forceSubmit\":false,\"groupCode\":\"G00000010006\",\"sealCarCode\":\"\",\"taskId\":\""+taskId+"\",\"user\":{\"userCode\":17849,\"userErp\":\"liuduo8\",\"userName\":\"刘铎\"}}";
                UnloadScanRequest request3 = JsonHelper.fromJson(json3,UnloadScanRequest.class);

                JdVerifyResponse<Integer> integerJdVerifyResponse = jyUnloadVehicleGatewayService.unloadScan(request3);


                String json4 = "{\"bizId\":\""+bizId+"\",\"currentOperate\":{\"operateTime\":1649938839116,\"orgId\":10,\"orgName\":\"广州分公司\",\"siteCode\":910,\"siteName\":\"北京马驹桥分拣中心6\"},\"sealCarCode\":\"\",\"user\":{\"userCode\":17849,\"userErp\":\"liuduo8\",\"userName\":\"刘铎\"},\"vehicleNumber\":\""+vehicleNumber+"\"}";
                UnloadCommonRequest request4 = JsonHelper.fromJson(json4,UnloadCommonRequest.class);

                JdCResponse<UnloadScanDetail> unloadScanDetailJdCResponse = jyUnloadVehicleGatewayService.unloadDetail(request4);


            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }

        }


    }
}
