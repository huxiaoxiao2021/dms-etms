package com.jd.bluedragon.core.security.log.executor;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.core.context.InvokerClientInfoContext;
import com.jd.bluedragon.core.security.log.builder.SecurityLogHeaderBuilder;
import com.jd.bluedragon.core.security.log.domain.SecurityLogEntity;
import com.jd.bluedragon.core.security.log.enums.*;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.security.log.util.LogAcesUtil;
import com.jd.securitylog.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

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

            ReqInfo reqInfo = createReqInfo(securityLogEntity.getReqKeyMapping(), securityLogEntity.getBusinessRequest());
            LogAcesUtil.encryptSecEntity(reqInfo);
            securityLog.setReqInfo(reqInfo);

            if (CollectionUtils.isNotEmpty(securityLogEntity.getBusinessResponses())) {
                RespInfo respInfo = createRespInfo(securityLogEntity.getRespKeyMapping(), securityLogEntity.getBusinessResponses(), securityLogEntity.getOp(), securityLogEntity.getResultNum());
                securityLog.setRespInfo(respInfo);
            } else {
                RespInfo respInfo = createRespInfo(securityLogEntity.getRespKeyMapping(), Collections.singletonList(securityLogEntity.getBusinessResponse()), securityLogEntity.getOp(), securityLogEntity.getResultNum());
                securityLog.setRespInfo(respInfo);
            }

            SecurityLogUtil.log(securityLog);
        } catch (RuntimeException | UnknownHostException e) {
            log.error("记录安全日志异常：", e);
        }
    }

    private static Head createHead(String interfaceName, SecurityAccountEnums accountType, String accountName, SecurityLogOpEnums op) throws UnknownHostException {
        String appName = PropertiesHelper.newInstance().getValue("app_name");
        String sysName = PropertiesHelper.newInstance().getValue("sys_name");

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
                .accountName(StringHelper.isNotEmpty(InvokerClientInfoContext.get().getUser())? InvokerClientInfoContext.get().getUser() : accountName)
                .op(op.getOpCode())
                .build();
    }

    private static ReqInfo createReqInfo(Map<SecurityLogReqInfoKeyEnums, String> keyMapping, Object businessRequest) {
        JSONObject reqInfoJson = new JSONObject();

        if (MapUtils.isEmpty(keyMapping) || businessRequest == null) {
            return new ReqInfo();
        }


        JSONObject businessRequestJson = JSONObject.parseObject(businessRequest instanceof String? (String) businessRequest : JSONObject.toJSONString(businessRequest));
        for (Map.Entry<SecurityLogReqInfoKeyEnums, String> keyEnumsStringEntry : keyMapping.entrySet()) {
            reqInfoJson.put(keyEnumsStringEntry.getKey().name(), JsonHelper.getObject(businessRequestJson, keyEnumsStringEntry.getValue()));
        }

        return reqInfoJson.toJavaObject(ReqInfo.class);

    }

    private static RespInfo createRespInfo(Map<SecurityLogUniqueIdentifierKeyEnums,List<String>> keyMapping, List<?> businessResponses, SecurityLogOpEnums op, Integer resultNum) {

        RespInfo respInfo = new RespInfo();
        respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS.getCode());
        respInfo.setRecordCnt(resultNum);
        respInfo.setUniqueIdentifier(new ArrayList<UniqueIdentifier>());

        if (MapUtils.isEmpty(keyMapping) || CollectionUtils.isEmpty(businessResponses)) {
            respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS_NO_DATE.getCode());
            respInfo.setStatus(resultNum);
            return new RespInfo();
        }

        for (Object businessResponse : businessResponses) {
            JSONObject businessResponseJson = JSONObject.parseObject(businessResponse instanceof String? (String) businessResponse : JSONObject.toJSONString(businessResponse));

            JSONObject respInfoJson = new JSONObject();
            for (Map.Entry<SecurityLogUniqueIdentifierKeyEnums, List<String>> keyEnumsStringEntrys : keyMapping.entrySet()) {
                for (String keyEnumsStringEntry : keyEnumsStringEntrys.getValue()) {
                    Object value = JsonHelper.getObject(businessResponseJson, keyEnumsStringEntry);
                    if (value == null) {
                        continue;
                    }
                    if (respInfoJson.containsKey(keyEnumsStringEntrys.getKey().name())) {
                        respInfoJson.put(keyEnumsStringEntrys.getKey().name(),respInfoJson.get(keyEnumsStringEntrys.getKey().name()) + "," + value);
                    } else {
                        respInfoJson.put(keyEnumsStringEntrys.getKey().name(), value);
                    }
                }
            }
            UniqueIdentifier uniqueIdentifier = respInfoJson.toJavaObject(UniqueIdentifier.class);
            LogAcesUtil.encryptSecEntity(uniqueIdentifier);
            respInfo.getUniqueIdentifier().add(uniqueIdentifier);
        }
        return respInfo;
    }

}
