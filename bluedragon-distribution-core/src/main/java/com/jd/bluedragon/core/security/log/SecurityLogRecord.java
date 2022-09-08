package com.jd.bluedragon.core.security.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.context.InvokerClientInfoContext;
import com.jd.bluedragon.core.security.log.builder.SecurityLogHeaderBuilder;
import com.jd.bluedragon.core.security.log.domain.SecurityLogEntity;
import com.jd.bluedragon.core.security.log.enums.*;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.security.log.util.LogAcesUtil;
import com.jd.securitylog.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.security.log
 * @ClassName: SecurityLog
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/1 17:48
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Slf4j
public class SecurityLogRecord {

    public static void log(SecurityLogEntity securityLogEntity) {
        try {
            SecurityLog securityLog = new SecurityLog();
            securityLog.setHead(createHead(securityLogEntity.getInterfaceName(), securityLogEntity.getAccountType(), securityLogEntity.getAccountName(), securityLogEntity.getOp()));
            securityLog.setReqInfo(createReqInfo(securityLogEntity.getReqKeyMapping(), securityLogEntity.getBusinessRequest()));
            securityLog.setRespInfo(createRespInfo(securityLogEntity.getRespKeyMapping(), securityLogEntity.getBusinessResponseList(), securityLogEntity.getOp(), securityLogEntity.getResultNum()));
            LogAcesUtil.encryptSecEntity(securityLog);
            log.info(JSONObject.toJSONString(securityLog));
        } catch (RuntimeException | UnknownHostException e) {
            log.error("记录安全日志异常：", e);
        }
    }

    private static Head createHead(String interfaceName, SecurityAccountEnums accountType, String accountName, SecurityLogOpEnums op) throws UnknownHostException {
        String appName = StringUtils.isNotEmpty(System.getProperty("deploy.app.name"))? System.getProperty("deploy.app.name") : System.getProperty("def_app_name");
        String sysName = StringUtils.isNotEmpty(System.getProperty("SYSTEM_NAME"))? System.getProperty("SYSTEM_NAME") : System.getProperty("JONE_SYSTEM_NAME");

        return new SecurityLogHeaderBuilder()
                .version("V1.0")
                .systemName(sysName)
                .appName(appName)
                .interfaceName(interfaceName)
                .uuid(UUID.randomUUID().toString())
                .time(System.currentTimeMillis() / 1000)
                .serverIp(InetAddress.getLocalHost().getHostAddress())
                .clientIp(InvokerClientInfoContext.get().getClientIp())
                .accountType(accountType.getType())
                .accountName(accountName)
                .op(op.getOpCode())
                .build();
    }

    private static ReqInfo createReqInfo(Map<SecurityLogReqInfoKeyEnums, String> keyMapping, Object businessRequest) {
        JSONObject reqInfoJson = new JSONObject();

        if (MapUtils.isEmpty(keyMapping) || businessRequest == null) {
            return new ReqInfo();
        }

        JSONObject businessRequestJson = JSONObject.parseObject(JSONObject.toJSONString(businessRequest));
        for (Map.Entry<SecurityLogReqInfoKeyEnums, String> keyEnumsStringEntry : keyMapping.entrySet()) {
            reqInfoJson.put(keyEnumsStringEntry.getKey().name(), JsonHelper.getObject(businessRequestJson, keyEnumsStringEntry.getValue()));
        }

        return reqInfoJson.toJavaObject(ReqInfo.class);

    }

    private static RespInfo createRespInfo(Map<SecurityLogUniqueIdentifierKeyEnums,String> keyMapping, List<Object> businessResponseList, SecurityLogOpEnums op, Integer resultNum) {

        RespInfo respInfo = new RespInfo();
        respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS.getCode());
        respInfo.setRecordCnt(resultNum);
        respInfo.setUniqueIdentifier(new ArrayList<UniqueIdentifier>());

        if (MapUtils.isEmpty(keyMapping) || CollectionUtils.isEmpty(businessResponseList)) {
            respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS_NO_DATE.getCode());
            respInfo.setStatus(resultNum);
            return new RespInfo();
        }
        for (Object businessResponse : businessResponseList) {
            JSONObject businessResponseJson = JSONObject.parseObject(JSONObject.toJSONString(businessResponse));

            JSONObject respInfoJson = new JSONObject();
            for (Map.Entry<SecurityLogUniqueIdentifierKeyEnums, String> keyEnumsStringEntry : keyMapping.entrySet()) {
                respInfoJson.put(keyEnumsStringEntry.getKey().name(), JsonHelper.getObject(businessResponseJson, keyEnumsStringEntry.getValue()));
            }
            respInfo.getUniqueIdentifier().add(respInfoJson.toJavaObject(UniqueIdentifier.class));
        }

        return respInfo;
    }






}
