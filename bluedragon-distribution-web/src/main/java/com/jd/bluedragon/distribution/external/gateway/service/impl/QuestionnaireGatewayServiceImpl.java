package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.questionnaire.*;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.external.gateway.service.QuestionnaireGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.Constants.PDA_QUESTIONNAIRE_FUNC_CODE;
import static com.jd.bluedragon.Constants.PDA_QUESTIONNAIRE_ID;

@Service
@Slf4j
public class QuestionnaireGatewayServiceImpl implements QuestionnaireGatewayService {

    @Autowired
    private SysConfigService sysConfigService;

    private static final String REST_CONTENT_TYPE = "application/json; charset=UTF-8";

    private static final String HTTP_REQUEST_PREFIX = "http://dongjian.jd.local";

    private static final String HTTP_REQUEST_HEADER_APP = "dms-etms";

    private static final String SAVE_ANSWER_HTTP_URL = "http://dongjian.jd.local/wj/saveAnswer";

    // 调查问卷
    public static final int NOT_FOUNT_QUESTIONNAIRE_ID = 3068;

    @Autowired
    private PositionManager positionManager;

    @Value("${questionnaire.appSecret}")
    private String appSecret;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.QuestionnaireGatewayServiceImpl.getQuestionnaire",mState={JProEnum.TP,JProEnum.FunctionError})
    public JdCResponse<String> getQuestionnaire(QuestionnaireReq req) {
        JdCResponse<String> response = new JdCResponse<>();
        if (!checkQuestionnaireReq(req)) {
            return response;
        }

        // 获取调查问卷id
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(PDA_QUESTIONNAIRE_ID);
        if (sysConfig == null || StringUtils.isEmpty(sysConfig.getConfigContent())) {
            log.info("未获取到调查问卷id");
            response.setCode(NOT_FOUNT_QUESTIONNAIRE_ID);
            return response;
        }
        String questionnaireId = sysConfig.getConfigContent();

        if (StringUtils.isNotEmpty(req.getPositionCode()) && checkPositionCode(req)) {
            response.setCode(NOT_FOUNT_QUESTIONNAIRE_ID);
            return response;
        }


        // 判断当前用户是否已经作答
        if (checkUserHasAnswered(questionnaireId,req.getUserErp())) {
            log.info("用户已经作答:{}", req.getUserErp());
            response.setCode(NOT_FOUNT_QUESTIONNAIRE_ID);
            return response;
        }
        // 获取调查问卷信息
        String body = exeHttpGetMethod(HTTP_REQUEST_PREFIX + "/wj/getQuestionnaire/" + questionnaireId);
        return JsonHelper.fromJson(body, JdCResponse.class);
    }

    public static void main(String[] args) {
        String funcConfig = "COMBOARD_SEND_POSITION,PATROL_MANAGER_POSITION";
        String[] funcs = funcConfig.split(",");
        List<String> funcList = Arrays.asList(funcs);
        System.out.println(JsonHelper.toJson(funcList));
    }

    private boolean checkPositionCode(QuestionnaireReq req) {
        // 校验岗位是否需要弹窗
        SysConfig funcConfig = sysConfigService.findConfigContentByConfigName(PDA_QUESTIONNAIRE_FUNC_CODE);
        if (StringUtils.isEmpty(funcConfig.getConfigContent())) {
            return true;
        }
        String[] funcs = funcConfig.getConfigContent().split(",");
        List<String> funcList = Arrays.asList(funcs);
        Result<PositionData> dataResult = positionManager.queryPositionWithIsMatchAppFunc(req.getPositionCode());
        if(dataResult == null){
            return true;
        }
        if(!dataResult.isSuccess() || dataResult.getData() == null){
            return true;
        }
        PositionData positionData = dataResult.getData();
        if (StringUtils.isEmpty(positionData.getDefaultMenuCode())) {
            return true;
        }

        if (funcList.contains(positionData.getDefaultMenuCode())) {
            return false;
        }
        return true;
    }

    private String exeHttpGetMethod(String url) {
        try {
            HttpClient httpClient = new HttpClient();
            GetMethod method = new GetMethod(url);
            method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
            method.addRequestHeader("Accept", REST_CONTENT_TYPE);
            method.addRequestHeader("app", HTTP_REQUEST_HEADER_APP);
            long timestamp = new Date().getTime();
            method.addRequestHeader("timestamp", Long.toString(timestamp));
            String sign = Md5Helper.getMd5(HTTP_REQUEST_HEADER_APP + appSecret + timestamp);
            method.addRequestHeader("sign", sign);
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.OK.value()) {
                return null;
            }
            return method.getResponseBodyAsString();
        }catch (Exception e) {
            log.error("调用调查问卷接口失败：{}", url, e);
            return null;
        }
    }

    /**
     * 检查用户是否已回答问卷
     *
     * @param questionnaireId 问卷ID
     * @param userErp 用户ERP
     * @return 返回用户是否已回答问卷的状态
     */
    private boolean checkUserHasAnswered(String questionnaireId, String userErp) {
        String body = exeHttpGetMethod(HTTP_REQUEST_PREFIX + "/wj/checkUserHasAnswered"
                + "?pin=" + userErp + "&questionnaireId=" + questionnaireId);
        log.info("校验用户问卷状态返回值:{}", body);
        if (!StringUtils.isEmpty(body)) {
            JdCResponse response = JsonHelper.fromJson(body, JdCResponse.class);
            if (response != null && response.getData() != null) {
                return (boolean) response.getData();
            }
        }
        return true;
    }

    private boolean checkQuestionnaireReq(QuestionnaireReq req) {
        if (req == null || StringUtils.isEmpty(req.getUserErp())) {
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.QuestionnaireGatewayServiceImpl.answerQuestionnaire",mState={JProEnum.TP,JProEnum.FunctionError})
    public JdCResponse<Void> answerQuestionnaire(AnswerQuestionnaireReq req) {
        JdCResponse<Void> response = new JdCResponse<>();
        if (req == null
                || req.getAnswerQuestionnaire() == null
                ||StringUtils.isEmpty(req.getAnswerQuestionnaire().getQuestionnaireId())
                || CollectionUtils.isEmpty(req.getAnswerQuestionnaire().getAnswers())) {
            response.toFail("回答问题后再提交！");
            return response;
        }
        if (StringUtils.isEmpty(req.getUserErp())) {
            response.toFail("未获取到用户信息！");
            return response;
        }
        exeHttpPostMethod(req);
        response.toSucceed("提交成功");
        return response;
    }

    private String exeHttpPostMethod(AnswerQuestionnaireReq req) {
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod method = new PostMethod(SAVE_ANSWER_HTTP_URL);
            method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
            method.addRequestHeader("Accept", REST_CONTENT_TYPE);
            method.addRequestHeader("app", HTTP_REQUEST_HEADER_APP);
            long timestamp = new Date().getTime();
            method.addRequestHeader("timestamp", Long.toString(timestamp));
            String sign = Md5Helper.getMd5(HTTP_REQUEST_HEADER_APP + appSecret + timestamp);
            method.addRequestHeader("sign", sign);
            method.addRequestHeader("Cookie", "pin=" + req.getUserErp());
            method.setRequestEntity(new StringRequestEntity(JsonHelper.toJson(req.getAnswerQuestionnaire()),
                    REST_CONTENT_TYPE,
                    StandardCharsets.UTF_8.name()));
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.OK.value()) {
                return null;
            }
            return method.getResponseBodyAsString();
        }catch (Exception e) {
            log.error("调用调查问卷接口失败：{}", SAVE_ANSWER_HTTP_URL, e);
            return null;
        }
    }
}
