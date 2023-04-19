package com.jd.bluedragon.distribution.seal;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendPhotoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jd.bluedragon.distribution.jy.service.send.IJySendAttachmentService;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JySendProductAggsService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.service.unseal.IJyUnSealVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.vos.dto.SealCarDto;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName JySealVehicleServiceTest
 * @Description
 * @Author wyh
 * @Date 2022/3/16 14:15
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JySealVehicleServiceTest {

    @Autowired
    private IJyUnSealVehicleService jySealVehicleService;

    @Autowired
    private JySendProductAggsService jySendProductAggsService;

    private static User user;

    private static CurrentOperate currentOperate;

    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;
    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    static {
        user = new User();
        user.setUserCode(1);
        user.setUserName("徐迷根");
        user.setUserErp("xumigen");

        currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(10186);
        currentOperate.setSiteName("北京凉水河快运中心");
        currentOperate.setOrgId(6);
        currentOperate.setOrgName("总公司");
    }

    @Test
    public void fetchSealTaskTest() {
        SealVehicleTaskRequest request = new SealVehicleTaskRequest();
        request.setPageNumber(1);
        request.setPageSize(10);
        request.setUser(user);
        request.setCurrentOperate(currentOperate);
        request.setEndSiteCode(10186);
        request.setVehicleStatus(1);  // 待解封
        request.setBarCode("");
        request.setLineType(0);
        request.setFetchType(SealVehicleTaskQuery.FETCH_TYPE_REFRESH);
        request.setSealCarCode("");

        InvokeResult<SealVehicleTaskResponse> result = jySealVehicleService.fetchSealTask(request);
        System.out.println(JsonHelper.toJson(result));
    }

    @Autowired
    private IJySendVehicleService jySendVehicleService;

    @Autowired
    private IJySendAttachmentService sendAttachmentService;

    @Test
    public void savePhotoTest() {
        String body = "{\"currentOperate\":{\"operateTime\":1655193334127,\"orgId\":6,\"orgName\":\"总公司\",\"siteCode\":910,\"siteName\":\"北京马驹桥分拣中心6\"},\"imgList\":[\"http://test.storage.jd.com/dms-feedback/4f14296e-1e45-444b-9f81-287d816e84ed.jpg?Expires=1970553333&AccessKey=a7ogJNbj3Ee9YM1O&Signature=Q0XpipBwNwxkEcSKtPhEidyQHaU%3D\"],\"sendVehicleBizId\":\"SST22061300000149\",\"user\":{\"userCode\":17331,\"userErp\":\"wuyoude\",\"userName\":\"吴有德\"},\"vehicleArrived\":1}";
        SendPhotoRequest request = JsonHelper.fromJson(body, SendPhotoRequest.class);
        JySendAttachmentEntity attachment = new JySendAttachmentEntity();
        attachment.setSendVehicleBizId(request.getSendVehicleBizId());
        attachment.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        attachment.setVehicleArrived(request.getVehicleArrived());
        attachment.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(request.getImgList()));
        attachment.setOperateTime(request.getCurrentOperate().getOperateTime());
        attachment.setCreateTime(request.getCurrentOperate().getOperateTime());
        attachment.setCreateUserErp(request.getUser().getUserErp());
        attachment.setCreateUserName(request.getUser().getUserName());
        attachment.setUpdateTime(request.getCurrentOperate().getOperateTime());
        attachment.setUpdateUserErp(attachment.getCreateUserErp());
        attachment.setUpdateUserName(attachment.getCreateUserName());
        sendAttachmentService.saveAttachment(attachment);
    }

    @Test
    public void sendScanTest() {
        String body = "{\n" +
                "    \"barCode\": \"JDVF00001901110-1-5-\",\n" +
                "    \"barCodeType\": 1,\n" +
                "    \"confirmSendDestId\": 56506,\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1656071420850,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 40240,\n" +
                "        \"siteName\": \"北京通州分拣中心\"\n" +
                "    },\n" +
                "    \"forceSubmit\": true,\n" +
                "    \"groupCode\": \"G00000018001\",\n" +
                "    \"sendForWholeBoard\": false,\n" +
                "    \"sendVehicleBizId\": \"SST22062100000035\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"京MM6666\"\n" +
                "}";

        SendScanRequest request = JsonHelper.fromJson(body, SendScanRequest.class);
        jySendVehicleService.sendScan(request);
    }

    @Test
    public void fetchSendTaskForBindingTest() {
        String json = "{\n" +
                "    \"bizId\": \"NSST22070400000001\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1656916444602,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 40240,\n" +
                "        \"siteName\": \"北京通州分拣中心\"\n" +
                "    },\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 15,\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    }\n" +
                "}";
        VehicleTaskReq req = JsonHelper.fromJson(json, VehicleTaskReq.class);
        jySendVehicleService.fetchSendTaskForBinding(req);
    }

    @Test
    public void loadProgressTest() {
        String json = "{\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1655904091422,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 40240,\n" +
                "        \"siteName\": \"北京通州分拣中心\"\n" +
                "    },\n" +
                "    \"sendVehicleBizId\": \"SST22070100000012\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 16698,\n" +
                "        \"userErp\": \"liuaihui3\",\n" +
                "        \"userName\": \"刘爱慧\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"\"\n" +
                "}";

//        SendVehicleProgressRequest request = JsonHelper.fromJson(json, SendVehicleProgressRequest.class);
//        InvokeResult<SendVehicleProgress> sendVehicleProgressInvokeResult = jySendVehicleService.loadProgress(request);
//        System.out.println(JSON.toJSONString(sendVehicleProgressInvokeResult));

//        List<JySendVehicleProductType> result = jySendProductAggsService.getSendVehicleProductTypeList("TEST002");
//        System.out.println(JSON.toJSONString(result));

        //List<JyUnloadAggsEntity> result = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity("TEST003"));
//        JyUnloadAggsEntity entity = new JyUnloadAggsEntity();
//        entity.setBizId("TEST003");
//        entity.setBoardCode("TEST003");
//        List<GoodsCategoryDto> result = jyUnloadAggsService.queryGoodsCategoryStatistics(entity);
//        DimensionQueryDto dto = new DimensionQueryDto();
//        dto.setType(3);
//        dto.setBizId("TEST003");
//        dto.setBoardCode("TEST003");
//        ScanStatisticsDto result = jyBizTaskUnloadVehicleService.queryStatisticsByDiffDimension(dto);
        TaskFlowDto dto = new TaskFlowDto();
        dto.setBizId("T001");
        dto.setEndSiteId(910L);
        InvokeResult<TaskFlowComBoardDto> result = jyUnloadVehicleTysService.queryComBoarUnderTaskFlow(dto);
        System.out.println(JSON.toJSONString(result));

    }

    @Autowired
    private JyUnloadVehicleTysService jyUnloadVehicleTysService;

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Test
    public void updateTaskStatusTest() {

        JyBizTaskSendVehicleEntity taskSend = new JyBizTaskSendVehicleEntity();
        taskSend.setVehicleStatus(0);
        taskSend.setBizId("SST22061600000106");
        taskSend.setStartSiteId(40240L);
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp("bjxings");
        taskSend.setUpdateUserName("邢松");

        JyBizTaskSendVehicleDetailEntity taskSendDetail = new JyBizTaskSendVehicleDetailEntity();
        taskSendDetail.setSendVehicleBizId(taskSend.getBizId());
        taskSendDetail.setBizId("TW22061600792381-002");
        taskSendDetail.setVehicleStatus(0);
        taskSendDetail.setStartSiteId(taskSend.getStartSiteId());
        taskSendDetail.setEndSiteId(56506L);
        taskSendDetail.setSealCarTime(new Date());
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        sendVehicleTransactionManager.updateTaskStatus(taskSend, taskSendDetail, JyBizTaskSendDetailStatusEnum.SEALED);


    }

    @Test
    public void fetchSendTaskForTransferTest() {
        String json = "{\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1656928548911,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 40240,\n" +
                "        \"siteName\": \"北京通州分拣中心\"\n" +
                "    },\n" +
                "    \"packageCode\": \"JD0003360897087-1-1-\",\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 15,\n" +
                "    \"transferFlag\": 1,\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    }\n" +
                "}";
        jySendVehicleService.fetchSendTaskForTransfer(JsonHelper.fromJson(json, TransferVehicleTaskReq.class));
    }
    @Test
    public void resetSendStatusForUnsealTest() {
    	SealCarDto sealCarData = new SealCarDto();
    	sealCarData.setStartSiteId(910);
    	sealCarData.setTransWorkItemCode("TW22091400813808-001");
        sendVehicleTransactionManager.resetSendStatusToseal(sealCarData,"test1","name1",new Date().getTime());
    }

    @Test
    public void getSendVehicleProductTypeListTest(){
        List<JySendVehicleProductType> result = jySendProductAggsService.getSendVehicleProductTypeList("TEST002");
        System.out.println(result);
    }

    @Test
    public void getUnSealTaskInfoTest(){
        SealTaskInfoRequest request = new SealTaskInfoRequest();
        request.setBizId("SC23022800028276");
        request.setQueryRankOrder(true);
        request.setSealCarCode("SC23022800028276");
        request.setUser(user);
        request.setCurrentOperate(currentOperate);
        final Result<SealTaskInfo> result = jySealVehicleService.getUnSealTaskInfo(request);
        System.out.println(result);
    }
}
