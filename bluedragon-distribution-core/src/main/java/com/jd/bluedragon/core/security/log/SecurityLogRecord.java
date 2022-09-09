package com.jd.bluedragon.core.security.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.context.InvokerClientInfoContext;
import com.jd.bluedragon.core.security.log.builder.SecurityLogHeaderBuilder;
import com.jd.bluedragon.core.security.log.domain.SecurityLogEntity;
import com.jd.bluedragon.core.security.log.enums.*;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.eclp.bbp.so.util.SoUtil;
import com.jd.security.log.util.LogAcesUtil;
import com.jd.securitylog.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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

            RespInfo respInfo = createRespInfo(securityLogEntity.getRespKeyMapping(), securityLogEntity.getBusinessResponse(), securityLogEntity.getOp(), securityLogEntity.getResultNum());
            LogAcesUtil.encryptSecEntity(respInfo);
            securityLog.setRespInfo(respInfo);

            SecurityLogUtil.log(securityLog);
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


        JSONObject businessRequestJson = JSONObject.parseObject(businessRequest instanceof String? (String) businessRequest : JSONObject.toJSONString(businessRequest));
        for (Map.Entry<SecurityLogReqInfoKeyEnums, String> keyEnumsStringEntry : keyMapping.entrySet()) {
            reqInfoJson.put(keyEnumsStringEntry.getKey().name(), JsonHelper.getObject(businessRequestJson, keyEnumsStringEntry.getValue()));
        }

        return reqInfoJson.toJavaObject(ReqInfo.class);

    }

    private static RespInfo createRespInfo(Map<SecurityLogUniqueIdentifierKeyEnums,String> keyMapping, Object businessResponse, SecurityLogOpEnums op, Integer resultNum) {

        RespInfo respInfo = new RespInfo();
        respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS.getCode());
        respInfo.setRecordCnt(resultNum);
        respInfo.setUniqueIdentifier(new ArrayList<UniqueIdentifier>());

        if (MapUtils.isEmpty(keyMapping) || businessResponse == null) {
            respInfo.setStatus(SecurityLogRespInfoStatusEnums.SUCCESS_NO_DATE.getCode());
            respInfo.setStatus(resultNum);
            return new RespInfo();
        }

        JSONObject businessResponseJson = JSONObject.parseObject(businessResponse instanceof String? (String) businessResponse : JSONObject.toJSONString(businessResponse));

        JSONObject respInfoJson = new JSONObject();
        for (Map.Entry<SecurityLogUniqueIdentifierKeyEnums, String> keyEnumsStringEntry : keyMapping.entrySet()) {
            respInfoJson.put(keyEnumsStringEntry.getKey().name(), JsonHelper.getObject(businessResponseJson, keyEnumsStringEntry.getValue()));
        }
        respInfo.getUniqueIdentifier().add(respInfoJson.toJavaObject(UniqueIdentifier.class));

        return respInfo;
    }


    public static void main(String[] args) throws UnknownHostException {
        String response = "{\n" +
                "  \"code\" : 200,\n" +
                "  \"messageCode\" : 200,\n" +
                "  \"message\" : \"200-操作成功\",\n" +
                "  \"data\" : \"{\\n  \\\"waybillCode\\\" : \\\"JD0003364934631\\\",\\n  \\\"originalDmsCode\\\" : 910,\\n  \\\"originalDmsName\\\" : \\\"北京马驹桥分拣中心6\\\",\\n  \\\"busiId\\\" : 31201,\\n  \\\"busiCode\\\" : \\\"10K21110\\\",\\n  \\\"busiName\\\" : \\\"次晨达\\\",\\n  \\\"originalCityCode\\\" : 72,\\n  \\\"originalCityName\\\" : \\\"朝阳区\\\",\\n  \\\"originalCrossType\\\" : 1,\\n  \\\"transportMode\\\" : \\\"特惠送\\\",\\n  \\\"priceProtectText\\\" : \\\"\\\",\\n  \\\"signBackText\\\" : \\\"\\\",\\n  \\\"distributTypeText\\\" : \\\"\\\",\\n  \\\"consigner\\\" : \\\"刘诗诗\\\",\\n  \\\"consignerMobile\\\" : \\\"13745456767\\\",\\n  \\\"consignerAddress\\\" : \\\"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\\\",\\n  \\\"busiOrderCode\\\" : \\\"ESL98201902251001\\\",\\n  \\\"specialMark\\\" : \\\"\\\",\\n  \\\"specialMarkNew\\\" : \\\"\\\",\\n  \\\"jZDFlag\\\" : \\\"\\\",\\n  \\\"senderCompany\\\" : \\\"北京京东北辰冬装有限公司\\\",\\n  \\\"road\\\" : \\\"0\\\",\\n  \\\"sopOrExternalFlg\\\" : false,\\n  \\\"printTime\\\" : \\\"2022-09-08 18:29:11\\\",\\n  \\\"bjCheckFlg\\\" : false,\\n  \\\"muslimSignText\\\" : \\\"\\\",\\n  \\\"templateName\\\" : \\\"dms-unite1011-new-v1\\\",\\n  \\\"templateVersion\\\" : 0,\\n  \\\"templateVersionStr\\\" : \\\"1\\\",\\n  \\\"freightText\\\" : \\\"\\\",\\n  \\\"goodsPaymentText\\\" : \\\"货到付款￥\\\",\\n  \\\"remark\\\" : \\\"订单号3202092298172257三方订单号imp：ESL4398056124588;商家订单号：ESL98201902251001\\\",\\n  \\\"specialRequirement\\\" : \\\"COD\\\",\\n  \\\"printAddress\\\" : \\\"北京朝阳区三环以内未访问\\\",\\n  \\\"customerName\\\" : \\\"赵丽颖\\\",\\n  \\\"customerContacts\\\" : \\\"13888889999\\\",\\n  \\\"mobileFirst\\\" : \\\"1388888\\\",\\n  \\\"mobileLast\\\" : \\\"9999\\\",\\n  \\\"telFirst\\\" : \\\"\\\",\\n  \\\"telLast\\\" : \\\"\\\",\\n  \\\"jdLogoImageKey\\\" : \\\"JDLogo.gif\\\",\\n  \\\"popularizeMatrixCode\\\" : \\\"https://mp.weixin.qq.com/a/~bdyFWnK5nG7Ly5w-xXbAYg~~\\\",\\n  \\\"popularizeMatrixCodeDesc\\\" : \\\"扫码寄快递\\\",\\n  \\\"waybillVasSign\\\" : \\\"\\\",\\n  \\\"examineFlag\\\" : \\\"[已验视]\\\",\\n  \\\"securityCheck\\\" : \\\"[已安检]\\\",\\n  \\\"deliveryMethod\\\" : \\\"【送】\\\",\\n  \\\"waybillCodeFirst\\\" : \\\"JD000336493\\\",\\n  \\\"waybillCodeLast\\\" : \\\"4631\\\",\\n  \\\"printSiteName\\\" : \\\"石景山营业部\\\",\\n  \\\"destinationCrossCode\\\" : \\\"2002\\\",\\n  \\\"destinationDmsName\\\" : \\\"北京马驹桥分拣中心6\\\",\\n  \\\"destinationTabletrolleyCode\\\" : \\\"106\\\",\\n  \\\"originalTabletrolleyCode\\\" : \\\"106\\\",\\n  \\\"roadCode\\\" : \\\"0\\\",\\n  \\\"templatePaperSizeCode\\\" : \\\"1011\\\",\\n  \\\"useNewTemplate\\\" : true,\\n  \\\"templateGroupCode\\\" : \\\"C\\\",\\n  \\\"customerPhoneText\\\" : \\\"13888889999\\\",\\n  \\\"specialMark1\\\" : \\\"\\\",\\n  \\\"additionalComment\\\" : \\\"http://www.jdwl.com   客服电话：950616\\\",\\n  \\\"willPrintPackageIndex\\\" : 0,\\n  \\\"prepareSiteCode\\\" : 39,\\n  \\\"prepareSiteName\\\" : \\\"石景山营业部\\\",\\n  \\\"purposefulDmsCode\\\" : 910,\\n  \\\"purposefulDmsName\\\" : \\\"北京马驹桥分拣中心6\\\",\\n  \\\"originalCrossCode\\\" : \\\"2002\\\",\\n  \\\"purposefulCrossCode\\\" : \\\"2002\\\",\\n  \\\"originalTabletrolley\\\" : \\\"106\\\",\\n  \\\"purposefulTableTrolley\\\" : \\\"106\\\",\\n  \\\"orderCode\\\" : \\\"3202092298172257\\\",\\n  \\\"timeCategory\\\" : \\\"\\\",\\n  \\\"collectionAddress\\\" : \\\"\\\",\\n  \\\"codMoneyText\\\" : \\\"代收货款：0.01￥\\\",\\n  \\\"totalChargeText\\\" : \\\"\\\",\\n  \\\"outputType\\\" : 0,\\n  \\\"type\\\" : 0,\\n  \\\"statusCode\\\" : 200,\\n  \\\"statusMessage\\\" : \\\"OK\\\",\\n  \\\"quantity\\\" : 1,\\n  \\\"popSupName\\\" : \\\"刘诗诗\\\",\\n  \\\"luxuryText\\\" : \\\"\\\",\\n  \\\"normalText\\\" : \\\"无\\\",\\n  \\\"userLevel\\\" : \\\"\\\",\\n  \\\"packagePrice\\\" : \\\"0.01\\\",\\n  \\\"distributeType\\\" : 70,\\n  \\\"sendPay\\\" : \\\"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\\\",\\n  \\\"waybillSign\\\" : \\\"10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\\\",\\n  \\\"packList\\\" : [ {\\n    \\\"packageCode\\\" : \\\"JD0003364934631-1-1-\\\",\\n    \\\"weight\\\" : 0.0,\\n    \\\"isPrintPack\\\" : false,\\n    \\\"packageIndex\\\" : \\\"1/1\\\",\\n    \\\"packageWeight\\\" : \\\"\\\",\\n    \\\"packageSuffix\\\" : \\\"-1-1-\\\",\\n    \\\"packageIndexNum\\\" : 1,\\n    \\\"printPack\\\" : false\\n  } ],\\n  \\\"hasPrintInvoice\\\" : false,\\n  \\\"featherLetterWaybill\\\" : false,\\n  \\\"needPrintFlag\\\" : true,\\n  \\\"longPack\\\" : false,\\n  \\\"isSelfService\\\" : false,\\n  \\\"storeId\\\" : 98,\\n  \\\"isAir\\\" : false,\\n  \\\"printInvoice\\\" : false\\n}\",\n" +
                "  \"status\" : 1,\n" +
                "  \"passed\" : true,\n" +
                "  \"weakPassed\" : false,\n" +
                "  \"succeed\" : true,\n" +
                "  \"failed\" : false,\n" +
                "  \"warn\" : false\n" +
                "}";

//        System.out.println(createRespInfo(new HashMap<SecurityLogUniqueIdentifierKeyEnums, String>(), response, SecurityLogOpEnums.op_1, 1));

        System.out.println(createHead("",SecurityAccountEnums.account_type_1,"", SecurityLogOpEnums.op_1));
    }






}
