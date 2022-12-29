package com.jd.bluedragon.core.security.log;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.security.log.builder.SecurityLogUniqueIdentifierKeyMappingBuilder;
import com.jd.bluedragon.core.security.log.domain.SecurityLogEntity;
import com.jd.bluedragon.core.security.log.enums.SecurityAccountEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogOpEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogReqInfoKeyEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogUniqueIdentifierKeyEnums;
import com.jd.bluedragon.core.security.log.executor.SecurityLogRecord;
import com.jd.bluedragon.distribution.api.response.OrderResponse;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntityResponse;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.spotcheck.SpotCheckReportQueryCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.converter
 * @ClassName: SecurityLogWriter
 * @Description: 业务代码中的安全日志写入类
 * @Author： wuzuxiang
 * @CreateDate 2022/9/9 15:37
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Slf4j
public class SecurityLogWriter {

    /**
     * 打印数据查询安全日志写入
     * 切入接口：
     * @see com.jd.bluedragon.distribution.command.JdCommandService#execute(java.lang.String)
     * @param jsonCommand
     */
    public static void jsonCommandExecuteWrite(JdCommand<String> jsonCommand, String jsonResponse){
        CallerInfo info = Profiler.registerInfo("DMSWEB.SecurityLogWriter.jsonCommandExecuteWrite", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try{
            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "data.barCode");
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "data");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId, "data.waybillCode")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"data.customerName")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"data.jsonData.customerName")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"data.printAddress")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"data.jsonData.printAddress")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"data.customerContacts")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"data.jsonData.customerContacts")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"data.customerPhoneText")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"data.jsonData.customerPhoneText")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderPhone,"data.consignerTelText")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderPhone,"data.jsonData.consignerTelText")

                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderAddress,"data.consignerAddress")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderAddress,"data.jsonData.consignerAddress")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.command.JsonCommandServiceImpl#execute")
                            .accountName(String.valueOf(JsonHelper.getObject(JSONObject.parseObject(JSONObject.toJSONString(jsonCommand)),"data.userCode")))
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_8)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(jsonCommand)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(jsonResponse)
                            .resultNum(1)
                            .build()
            );
        }catch (Exception ex){
            log.error("构建安全日日志失败.jsonCommand#execute:{}",jsonCommand,ex);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 发货交接清单查询安全日志写入
     * 切入接口：
     * @see com.jd.bluedragon.distribution.sendprint.service.impl.SendPrintServiceImpl#basicPrintQuery(com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria)
     * @param criteria
     */
    public static void sendPrintBasicPrintQueryWrite(PrintQueryCriteria criteria, BasicQueryEntityResponse response, SummaryPrintResultResponse summaryPrintResultResponse) {
        try {
            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.accountId, "userCode");
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.sendprint.service.impl.SendPrintServiceImpl#basicPrintQuery")
                            .accountName(String.valueOf(JsonHelper.getObject(JSONObject.parseObject(JSONObject.toJSONString(criteria)),"data.userCode")))
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_3)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(criteria)
                            .respKeyMapping(new HashMap<SecurityLogUniqueIdentifierKeyEnums, List<String>>())
                            .businessResponse(response == null? summaryPrintResultResponse :response)
                            .resultNum(response != null && CollectionUtils.isNotEmpty(response.getData())? response.getData().size() :
                                    summaryPrintResultResponse != null && CollectionUtils.isNotEmpty(summaryPrintResultResponse.getData())? summaryPrintResultResponse.getData().size() : 0)
                            .build()
            );
        }catch (Exception e){
            log.error("打印交接清单上传安全日日志失败.入参:{}",JsonHelper.toJson(criteria),e);
        }
    }

    /**
     * 发货交接清单查询安全日志写入
     * 切入接口：
     * @see com.jd.bluedragon.distribution.sendprint.service.impl.SendPrintServiceImpl#basicPrintQuery(com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria)
     * @param startDmsCode
     * @param waybillCodeOrPackage
     * @param packOpeFlowFlg
     * @param waybill
     */
    public static void waybillResourceGetWaybillPackWrite(Integer startDmsCode, String waybillCodeOrPackage, Integer packOpeFlowFlg, Waybill waybill) {
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("startDmsCode", startDmsCode);
            params.put("waybillCodeOrPackage", waybillCodeOrPackage);
            params.put("packOpeFlowFlg", packOpeFlowFlg);

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "waybillCodeOrPackage");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"waybillCode")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"address")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"receiverName")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"receiverMobile")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.rest.waybill.WaybillResource#getWaybillPack")
                            .accountName("")
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_3)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(params)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(waybill)
                            .resultNum(1)
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败waybillResource#getWaybillPack:",ex);
        }
    }

    /**
     * 外单逆向换单安全日志接入
     * 切入接口：
     *
     * @param request
     * @param responseDTO
     */
    public static void waybillResourceGetOldOrderMessageNewWrite(ExchangeWaybillDto request, DmsWaybillReverseResponseDTO responseDTO) {
        try{

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "waybillCode");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"waybillCode")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"receiveAddress")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"receiveName")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"receiveMobile")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderPhone,"senderMobile")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.senderAddress,"senderAddress")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.rest.waybill.WaybillResource#getOldOrderMessageNew")
                            .accountName("")
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(request)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(responseDTO)
                            .resultNum(1)
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败waybillResource#getOldOrderMessageNew:",ex);
        }

    }

    /**
     * 现场预分拣查询
     * 切入接口：
     * com.jd.bluedragon.distribution.rest.order.OrderResource#getOrderResponse
     *
     * @param packageCode
     * @param response
     */
    public static void orderResourceGetOrderResponseWrite(String packageCode, OrderResponse response) {
        try{
            Map<String,String> param = new HashMap<>();
            param.put("packageCode", packageCode);

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "packageCode");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"waybillCode")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"address")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"mobile")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.rest.order.OrderResource#getOrderResponse")
                            .accountName("")
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(param)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(response)
                            .resultNum(1)
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败OrderResource#getOrderResponse:",ex);
        }
    }

    /**
     * RMA查询订单详细地址信息
     * @param id
     * @param rmaHandoverWaybill
     */
    public static void showDetailAddressWrite(Integer id, RmaHandoverWaybill rmaHandoverWaybill, String userCode) {
        try{
            Map<String,String> param = new HashMap<>();
            param.put("id", String.valueOf(id));

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"waybillCode")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"receiverAddress")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController#showDetailAddress")
                            .accountName(userCode)
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(param)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(rmaHandoverWaybill)
                            .resultNum(1)
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败RmaHandOverController#showDetailAddress:",ex);
        }
    }

    /**
     * RMA查询订单详细地址信息
     * @param waybillCode
     * @param address
     * @param userCode
     */
    public static void getReceiverAddressQueryWrite(String waybillCode, String address, String userCode) {
        try{
            Map<String,String> param = new HashMap<>();
            param.put("waybillCode", waybillCode);

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "waybillCode");
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            Map<String,String> paramRes = new HashMap<>();
            paramRes.put("address", address);

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"address")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController#getReceiverAddressQuery")
                            .accountName(userCode)
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(param)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponse(paramRes)
                            .resultNum(1)
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败RmaHandOverController#getReceiverAddressQuery:",ex);
        }
    }

    /**
     * RMA打印订单详细地址信息
     * @param idList
     * @param rmaHandoverPrintList
     * @param userCode
     */
    public static void printWaybillRmaWrite(String idList, List<RmaHandoverPrint> rmaHandoverPrintList, String userCode) {
        try{
            Map<String,String> param = new HashMap<>();
            param.put("ids", idList);

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"receiverAddress")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"receiver")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"receiverMobile")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController#printWaybillRma")
                            .accountName(userCode)
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(param)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponses(rmaHandoverPrintList)
                            .resultNum(rmaHandoverPrintList.size())
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败RmaHandOverController#printWaybillRma:",ex);
        }
    }

    /**
     * RMA打印订单详细地址信息
     * @param idList
     * @param rmaHandoverPrintList
     * @param userCode
     */
    public static void doExportWrite(String idList, List<RmaHandoverPrint> rmaHandoverPrintList, String userCode) {
        try{
            Map<String,String> param = new HashMap<>();
            param.put("ids", idList);

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"receiverAddress")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receiveName,"receiver")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"receiverMobile")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController#doExport")
                            .accountName(userCode)
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(param)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponses(rmaHandoverPrintList)
                            .resultNum(rmaHandoverPrintList.size())
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败RmaHandOverController#doExport:",ex);
        }
    }

    /**
     * 抽检查看包裹图片记录安全日志
     * @param condition
     * @param weightVolumePictureDtos
     * @param userCode
     */
    public static void searchPictureWrite(SpotCheckReportQueryCondition condition, List<WeightVolumePictureDto> weightVolumePictureDtos, String userCode) {
        try{

            Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "waybillCode");
            reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "");

            Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new SecurityLogUniqueIdentifierKeyMappingBuilder()
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"packageCode")
                    .addKey(SecurityLogUniqueIdentifierKeyEnums.fileUrl,"url")
                    .builder();

            SecurityLogRecord.log(
                    SecurityLogEntity.builder()
                            .interfaceName("com.jd.bluedragon.distribution.spotcheck.SpotCheckReportController#searchPicture")
                            .accountName(userCode)
                            .accountType(SecurityAccountEnums.account_type_1)
                            .op(SecurityLogOpEnums.op_5)
                            .reqKeyMapping(reqInfoKeyEnumsStringMap)
                            .businessRequest(condition)
                            .respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
                            .businessResponses(weightVolumePictureDtos)
                            .resultNum(weightVolumePictureDtos.size())
                            .build()
            );
        } catch (RuntimeException ex){
            log.error("构建安全日日志失败SpotCheckReportController#searchPicture:",ex);
        }
    }

}
