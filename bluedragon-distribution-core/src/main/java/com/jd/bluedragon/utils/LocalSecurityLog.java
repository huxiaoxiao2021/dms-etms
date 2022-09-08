package com.jd.bluedragon.utils;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntity;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntityResponse;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResult;
import com.jd.common.web.LoginContext;
import com.jd.fastjson.JSON;
import com.jd.jsf.gd.util.RpcContext;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.security.log.util.LogAcesUtil;


import com.jd.securitylog.entity.Head;
import com.jd.securitylog.entity.ReqInfo;
import com.jd.securitylog.entity.RespInfo;
import com.jd.securitylog.entity.UniqueIdentifier;
import com.jd.securitylog.entity.SecurityLog;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 上报安全日志
 */
@Data
@Deprecated
public class LocalSecurityLog {

    private static final Logger log = LoggerFactory.getLogger("security.log");

    public static final String ACCOUNTNAME = "pin";
    private final static String SYSTEMNAME = "QLFJZXJT";
    private final static String APPNAME = "dms.etms";
    private final static String IP_KEY = "IP";
    private ReqInfo req;
    private RespInfo resp;

    public LocalSecurityLog(ReqInfo req, RespInfo resp) {
        this.req = req;
        this.resp = resp;
    }

    /**
     * 操作枚举值
     * 0：添加; 1：删除; 2：更新; 3：查询; 4：导出 必填；
     */
    public enum OpTypeEnum {
        ADD, DELETE, UPDATE, QUERY, EXPORT;
    }

    /**
     * 账户类型 ：1：erp; 2：passport; 3：selfCreated（自建）;4: JSF调用者;5: 定时任务
     */
    public enum AccountTypeEnum {
        NULL, ERP, PASSPORT, SELFCREATED, JSFCALL, SCHEDULE;
    }


    /**
     * 批量查询交接清单安全日志
     *
     * @param interfaceName
     * @param request
     * @param response
     */
    public static void writeBatchSummaryPrintSecurityLog(String interfaceName, PrintQueryCriteria request, List<SummaryPrintResult> response) {
        try {
            if (request == null) {
                return;
            }
            // 创建头部信息
            SecurityLog securityLog = new SecurityLog();
            // 新建头
            String clientIp="";
            RpcContext context = RpcContext.getContext();
            if (context != null && context.getRemoteHostName() != null) {
                clientIp = context.getRemoteHostName();
            }
            Head head = createHead(interfaceName,request.getUserCode(),clientIp);
            securityLog.setHead(head);
            // 新建请求
            ReqInfo reqInfo = createSendPrintReqInfo(request);
            //  对请求实体执行加密
            LogAcesUtil.encryptSecEntity(reqInfo);
            securityLog.setReqInfo(reqInfo);
            // 新建响应
            List<UniqueIdentifier> uniqueIdentifier = createBatchSummaryPrintUniqueIdentifier(response);
            RespInfo respInfo = createResp(uniqueIdentifier);
            securityLog.setRespInfo(respInfo);
            // 转成json，打印日志
            log.info(JSON.toJSONString(securityLog));
        } catch (Exception e) {
            log.error("上传安全日日志失败.入参:{}", JsonHelper.toJson(request), e);
        }

    }


    /**
     * 交接清单安全日志
     *
     * @param interfaceName
     * @param request
     * @param tList
     */
    public static void writeSummaryPrintSecurityLog(String interfaceName, PrintQueryCriteria request, List<BasicQueryEntity> tList) {
        try {
            if (request == null) {
                return;
            }
            // 创建头部信息
            SecurityLog securityLog = new SecurityLog();
            // 新建头
            String clientIp="";
            RpcContext context = RpcContext.getContext();
            if (context != null && context.getRemoteHostName() != null) {
                clientIp = context.getRemoteHostName();
            }
            Head head = createHead(interfaceName,request.getUserCode(),clientIp);
            securityLog.setHead(head);
            // 新建请求
            ReqInfo reqInfo = createSendPrintReqInfo(request);
            //  对请求实体执行加密
            LogAcesUtil.encryptSecEntity(reqInfo);
            securityLog.setReqInfo(reqInfo);
            // 新建响应
            List<UniqueIdentifier> uniqueIdentifier = createSummaryPrintUniqueIdentifier(tList);
            RespInfo respInfo = createResp(uniqueIdentifier);
            securityLog.setRespInfo(respInfo);
            // 转成json，打印日志
            log.info(JSON.toJSONString(securityLog));
        } catch (Exception e) {
            log.error("上传安全日日志失败.入参:{}", JsonHelper.toJson(request), e);
        }

    }


    /**
     * 交接清单安全日志
     *
     * @param interfaceName
     * @param jsonCommand
     * @param commandResult
     */
    public static void writeJsonCommandSecurityLog(String interfaceName, JdCommand<String> jsonCommand, String commandResult,String clientIp) {
        try {
            if (jsonCommand == null) {
                return;
            }
            //构建入参
            WaybillPrintRequest waybillPrintRequest = JsonHelper.fromJson(jsonCommand.getData(), WaybillPrintRequest.class);
            //构建响应结果
            JdResult jdResult = JsonHelper.fromJson(commandResult, JdResult.class);
            if (null == waybillPrintRequest || null == jdResult || StringUtils.isBlank(waybillPrintRequest.getUserName())) {
                return;
            }
            // 创建头部信息
            SecurityLog securityLog = new SecurityLog();
            // 新建头
            Head head = createHead(interfaceName,waybillPrintRequest.getUserName(),clientIp);
            securityLog.setHead(head);
            // 新建请求
            ReqInfo reqInfo = createJsonCommandReqInfo(waybillPrintRequest);
            //  对请求实体执行加密
            LogAcesUtil.encryptSecEntity(reqInfo);
            securityLog.setReqInfo(reqInfo);
            // 新建响应
            List<UniqueIdentifier> uniqueIdentifier = createJsonCommandUniqueIdentifier(commandResult);
            RespInfo respInfo = createResp(uniqueIdentifier);
            securityLog.setRespInfo(respInfo);
            // 转成json，打印日志
            log.info(JSON.toJSONString(securityLog));
        } catch (Exception e) {
            log.error("上传安全日日志失败.入参:{}", commandResult, e);
        }

    }

    /**
     * 创建请求头
     * @param interfaceName
     * @param account
     * @return
     * @throws UnknownHostException
     */
    private static Head createHead(String interfaceName,String account,String clientIp) throws UnknownHostException {
        String accountName =ACCOUNTNAME;
        LoginContext loginContext = LoginContext.getLoginContext();
        if(StringUtils.isNotBlank(account)){
            accountName =account;
        }
        if(loginContext != null && StringUtils.isNotBlank(loginContext.getPin()) && StringUtils.isBlank(account)){
            accountName = loginContext.getPin();
        }
        Head head = new Head();
        head.setUuid(UUID.randomUUID().toString());
        head.setOp(OpTypeEnum.QUERY.ordinal());
        head.setInterfaceName(interfaceName);
        head.setTime(System.currentTimeMillis() / 1000);
        head.setServerIp(InetAddress.getLocalHost().getHostAddress());
        head.setSystemName(SYSTEMNAME);
        head.setAppName(APPNAME);
        if (StringUtils.isNotBlank(clientIp)) {
            head.setClientIp(clientIp);
        } else {
            head.setClientIp("0.0.0.0");
        }
        head.setVersion("V1.0");
        head.setAccountName(accountName);
        head.setAccountType(AccountTypeEnum.SELFCREATED.ordinal());
        return head;
    }

    /**
     * 构建SendPrint ReqInfo请求信息
     *
     * @param request
     * @return
     */
    private static ReqInfo createSendPrintReqInfo(PrintQueryCriteria request) {

        ReqInfo reqInfo = new ReqInfo();
        reqInfo.setErpId(request.getUserCode());
        reqInfo.setTimeFrom(request.getRequestTime().getTime() / 1000);
        reqInfo.setTimeTo(System.currentTimeMillis() / 1000);
        reqInfo.setCarryBillId(request.getWaybillcode());
        reqInfo.setInputParam(JsonHelper.toJson(request));
        return reqInfo;
    }

    /**
     * 构建SendPrint ReqInfo请求信息
     *
     * @param request
     * @return
     */
    private static ReqInfo createJsonCommandReqInfo(WaybillPrintRequest request) {
        ReqInfo reqInfo = new ReqInfo();
        reqInfo.setErpId(String.valueOf(request.getUserCode()));
        reqInfo.setTimeFrom(request.getRequestTime().getTime() / 1000);
        reqInfo.setTimeTo(System.currentTimeMillis() / 1000);
        reqInfo.setCarryBillId(request.getBarCode());
        reqInfo.setInputParam(JSON.toJSONString(request));
        return reqInfo;
    }

    /**
     * 构建响应数据
     * @param uniqueIdentifiers
     * @return
     */
    private static RespInfo createResp(List<UniqueIdentifier> uniqueIdentifiers) {
        RespInfo respInfo = new RespInfo();
        respInfo.setStatus(0);
        respInfo.setRecordCnt(1);
        respInfo.setUniqueIdentifier(uniqueIdentifiers);
        return respInfo;
    }

    /**
     * 批量查询交接清单
     *
     * @param response
     * @return
     */
    private static List<UniqueIdentifier> createBatchSummaryPrintUniqueIdentifier(List<SummaryPrintResult> response) {
        List<UniqueIdentifier> identifiers = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(response)) {
            for (SummaryPrintResult result : response) {
                UniqueIdentifier identifier = new UniqueIdentifier();
                String senderAddress = "null";
                String receiveAddress = "null";
                if (StringUtils.isNotEmpty(result.getSendSiteName())) {
                    senderAddress = result.getSendSiteName();       //设置原值
                }
                if (StringUtils.isNotEmpty(result.getReceiveSiteName())) {
                    receiveAddress = result.getReceiveSiteName();
                }
                identifier.setSenderAddress(senderAddress);
                identifier.setReceiveAddress(receiveAddress);
                LogAcesUtil.encryptSecEntity(identifier);
                identifiers.add(identifier);
            }
        }
        return identifiers;
    }


    /**
     * 查询交接清单
     *
     * @param response
     * @return
     */
    private static List<UniqueIdentifier> createSummaryPrintUniqueIdentifier(List<BasicQueryEntity> response) {
        List<UniqueIdentifier> identifiers = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(response)) {
            for (BasicQueryEntity result : response) {
                UniqueIdentifier identifier = new UniqueIdentifier();
                String senderUser = "null";
                String receiverName = "null";
                String receiveAddress = "null";
                String receiverMobile = "null";
                String waybill = "null";


                //寄件人姓名
                if (StringUtils.isNotEmpty(result.getSendUser())) {
                    senderUser = result.getSendUser();
                }
                //收件人姓名
                if (StringUtils.isNotEmpty(result.getReceiverName())) {
                    receiverName = result.getReceiverName();
                }
                //收件人地址
                if (StringUtils.isNotEmpty(result.getReceiverAddress())) {
                    receiveAddress = result.getReceiverAddress();
                }
                //收件人电话
                if (StringUtils.isNotEmpty(result.getReceiverMobile())) {
                    receiverMobile = result.getReceiverMobile();
                }
                //订单号
                if (StringUtils.isNotEmpty(result.getWaybill())) {
                    waybill = result.getWaybill();
                }
                identifier.setCustomerName(senderUser);
                identifier.setReceiveName(receiverName);
                identifier.setReceiveAddress(receiveAddress);
                identifier.setReceivePhone(receiverMobile);
                identifier.setCarryBillId(waybill);
                LogAcesUtil.encryptSecEntity(identifier);
                identifiers.add(identifier);
            }
        }
        return identifiers;
    }

    /**
     * JsonCommand查询
     *
     * @param response
     * @return
     */
    private static List<UniqueIdentifier> createJsonCommandUniqueIdentifier(String response) {
        List<UniqueIdentifier> identifiers = new ArrayList<>();

        if (StringUtils.isNotBlank(response)) {
            String data = JSONObject.parseObject(response).getString("data");
            Map map = JsonHelper.json2MapNormal(data);
            UniqueIdentifier identifier = new UniqueIdentifier();
            String senderUser = "null";
            String senderPhone = "null";
            String sendAddress = "null";
            String receiverName = "null";
            String receiveAddress = "null";
            String receiverMobile = "null";
            String waybill = "null";

            if(map == null){
                return identifiers;
            }
            //寄件人姓名
            if (StringUtils.isNotEmpty(String.valueOf(map.get("consigner")))) {
                senderUser = String.valueOf(map.get("consigner"));
            }
            //寄件人电话
            if (StringUtils.isNotEmpty(String.valueOf(map.get("consignerMobile")))) {
                senderPhone = String.valueOf(map.get("consignerMobile"));
            }
            //寄件人地址
            if (StringUtils.isNotEmpty(String.valueOf(map.get("consignerAddress")))) {
                sendAddress = String.valueOf(map.get("consignerAddress"));
            }
            //收件人姓名
            if (StringUtils.isNotEmpty(String.valueOf(map.get("customerName")))) {
                receiverName = String.valueOf(map.get("customerName"));
            }
            //收件人电话
            if (StringUtils.isNotEmpty(String.valueOf(map.get("customerContacts")))) {
                receiverMobile = String.valueOf(map.get("customercustomerContactsName"));
            }
            //收件人地址
            if (StringUtils.isNotEmpty(String.valueOf(map.get("printAddress")))) {
                receiveAddress = String.valueOf(map.get("printAddress"));
            }
            //订单号
            if (StringUtils.isNotBlank(String.valueOf(map.get("waybillCode")))) {
                waybill = String.valueOf(map.get("waybillCode"));
            }
            identifier.setCustomerName(senderUser);
            identifier.setSenderPhone(senderPhone);
            identifier.setSenderAddress(sendAddress);
            identifier.setReceiveName(receiverName);
            identifier.setReceiveAddress(receiveAddress);
            identifier.setReceivePhone(receiverMobile);
            identifier.setCarryBillId(waybill);
            LogAcesUtil.encryptSecEntity(identifier);
            identifiers.add(identifier);
        }
        return identifiers;
    }
}
