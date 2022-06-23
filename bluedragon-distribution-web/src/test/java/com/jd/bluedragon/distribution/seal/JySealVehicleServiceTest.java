package com.jd.bluedragon.distribution.seal;
import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendPhotoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.service.send.IJySendAttachmentService;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.unseal.IJyUnSealVehicleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    private static User user;

    private static CurrentOperate currentOperate;

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
                "    \"barCode\": \"JDVF00002011756-1-5-\",\n" +
                "    \"barCodeType\": 1,\n" +
                "    \"confirmSendDestId\": 56506,\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1655964896169,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 40240,\n" +
                "        \"siteName\": \"北京通州分拣中心\"\n" +
                "    },\n" +
                "    \"forceSubmit\": true,\n" +
                "    \"groupCode\": \"G00000018001\",\n" +
                "    \"noTaskConfirmDest\": true,\n" +
                "    \"sendForWholeBoard\": false,\n" +
                "    \"sendVehicleBizId\": \"NSST22062300000008\",\n" +
                "    \"noTaskRemark\": \"reakadgasgdsadgagdad昂大哥大使馆打得过\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 16698,\n" +
                "        \"userErp\": \"liuaihui3\",\n" +
                "        \"userName\": \"刘爱慧\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"自建1\"\n" +
                "}";

        SendScanRequest request = JsonHelper.fromJson(body, SendScanRequest.class);
        jySendVehicleService.sendScan(request);
    }

    @Test
    public void fetchSendTaskForBindingTest() {
        String json = "{\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1655274482318,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"华北\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"马驹桥\"\n" +
                "    },\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 15,\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"endSiteId\": 2860,\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 0,\n" +
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
                "    \"sendVehicleBizId\": \"SST22061600000106\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 16698,\n" +
                "        \"userErp\": \"liuaihui3\",\n" +
                "        \"userName\": \"刘爱慧\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"\"\n" +
                "}";

        SendVehicleProgressRequest request = JsonHelper.fromJson(json, SendVehicleProgressRequest.class);
        jySendVehicleService.loadProgress(request);
    }

}
