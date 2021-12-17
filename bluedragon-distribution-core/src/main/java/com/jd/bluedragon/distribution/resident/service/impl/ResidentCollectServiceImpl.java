package com.jd.bluedragon.distribution.resident.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.MerchCollectManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrintSmsMsg;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.resident.domain.ResidentCollectDto;
import com.jd.bluedragon.distribution.resident.service.ResidentCollectService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jdl.express.collect.api.CommonDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectFailureWaybillInfoDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 驻厂揽收
 *
 * @author hujiping
 * @date 2021/10/20 6:37 下午
 */
@Service("residentCollectService")
public class ResidentCollectServiceImpl implements ResidentCollectService {

    private final Logger logger = LoggerFactory.getLogger(ResidentCollectServiceImpl.class);

    /**
     * 系统来源：分拣
     */
    private static final Integer SOURCE_FROM = 14;

    @Autowired
    private MerchCollectManager merchCollectManager;

    @Autowired
    @Qualifier("popPrintToSmsProducer")
    private DefaultJMQProducer popPrintToSmsProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private PopPrintService popPrintService;

    @Override
    public InvokeResult<Boolean> residentCollect(ResidentCollectDto residentCollectDto) {
        // 揽收处理
        InvokeResult<Boolean> result = collectDeal(residentCollectDto);
        // 站点装箱
        afterCollectSuc(residentCollectDto);
        return result;
    }

    private InvokeResult<Boolean> collectDeal(ResidentCollectDto residentCollectDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        NoTaskFinishCollectWaybillCommand command = new NoTaskFinishCollectWaybillCommand();
        command.setSiteId(residentCollectDto.getSiteCode());
        command.setSiteName(residentCollectDto.getSiteName());
        command.setOperatorId(residentCollectDto.getOperateUserId());
        command.setOperatorName(residentCollectDto.getOperateUserName());
        command.setOperateUserCode(residentCollectDto.getOperateUserCode());
        command.setSystemSource(SOURCE_FROM);
        List<NoTaskFinishCollectWaybillInfoDTO> list = new ArrayList<>();
        NoTaskFinishCollectWaybillInfoDTO waybillInfoDTO = new NoTaskFinishCollectWaybillInfoDTO();
        // 运单号或包裹号
        waybillInfoDTO.setRefId(residentCollectDto.getBarCode());
        String waybillCode = WaybillUtil.getWaybillCode(residentCollectDto.getBarCode());
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        int packNum = waybill == null ? Constants.NUMBER_ZERO : waybill.getGoodNumber() == null ? Constants.NUMBER_ZERO : waybill.getGoodNumber();
        waybillInfoDTO.setPackCount(packNum);
        double weight = residentCollectDto.getWeight() == null ? Constants.DOUBLE_ZERO : residentCollectDto.getWeight();
        double length = residentCollectDto.getLength() == null ? Constants.DOUBLE_ZERO : residentCollectDto.getLength();
        double width = residentCollectDto.getWidth() == null ? Constants.DOUBLE_ZERO : residentCollectDto.getWidth();
        double height = residentCollectDto.getHeight() == null ? Constants.DOUBLE_ZERO : residentCollectDto.getHeight();
        double volume = residentCollectDto.getVolume() == null ? Constants.DOUBLE_ZERO : residentCollectDto.getVolume();
        if(Objects.equals(volume, Constants.DOUBLE_ZERO)){
            volume = length * width * height;
        }
        waybillInfoDTO.setWeight(BigDecimal.valueOf(weight));
        waybillInfoDTO.setLength(BigDecimal.valueOf(length));
        waybillInfoDTO.setWidth(BigDecimal.valueOf(width));
        waybillInfoDTO.setHeight(BigDecimal.valueOf(height));
        waybillInfoDTO.setVolume(BigDecimal.valueOf(volume));
        waybillInfoDTO.setOperatorId(residentCollectDto.getOperateUserId());
        waybillInfoDTO.setOperatorName(residentCollectDto.getOperateUserName());
        waybillInfoDTO.setSiteCode(residentCollectDto.getSiteCode());
        waybillInfoDTO.setSiteName(residentCollectDto.getSiteName());
        waybillInfoDTO.setGoods(residentCollectDto.getConsignments());
        list.add(waybillInfoDTO);
        command.setItems(list);
        // 调用终端揽收接口
        CommonDTO<NoTaskFinishCollectWaybillDTO> commonDTO = merchCollectManager.noTaskFinishCollectWaybill(command);
        if(commonDTO == null || !Objects.equals(CommonDTO.CODE_SUCCESS, commonDTO.getCode())){
            logger.warn("操作单号:{}的终端揽收失败,原因:{}", residentCollectDto.getBarCode(), commonDTO == null ? InvokeResult.RESULT_NULL_MESSAGE : commonDTO.getMessage());
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "揽收失败!");
            return result;
        }
        if(commonDTO.getData() != null && CollectionUtils.isNotEmpty(commonDTO.getData().getOrderIds()) && commonDTO.getData().getOrderIds().get(0) != null){
            NoTaskFinishCollectFailureWaybillInfoDTO collectInfo = commonDTO.getData().getOrderIds().get(0);
            logger.warn("操作单号:{}的终端揽收失败,原因:{}", residentCollectDto.getBarCode(), collectInfo.getResultMsg());
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "揽收失败!" + collectInfo.getResultMsg());
            return result;
        }
        logger.info("单号:{}操作驻场打印完成无任务揽收成功!", residentCollectDto.getBarCode());
        return result;
    }

    private void afterCollectSuc(ResidentCollectDto residentCollectDto) {
        // 箱号存在则发送消息给终端，终端发送'站点装箱..'全程跟踪
        if(StringUtils.isNotEmpty(residentCollectDto.getBoxCode())){
            PopPrintSmsMsg popPrintSmsMsg = new PopPrintSmsMsg();
            popPrintSmsMsg.setWaybillCode(WaybillUtil.getWaybillCode(residentCollectDto.getBarCode()));
            popPrintSmsMsg.setCreateSiteCode(residentCollectDto.getSiteCode());
            popPrintSmsMsg.setCreateSiteName(residentCollectDto.getSiteName());
            popPrintSmsMsg.setPrintPackCode(residentCollectDto.getOperateUserId());
            popPrintSmsMsg.setPrintPackUser(residentCollectDto.getOperateUserName());
            popPrintSmsMsg.setPrintPackTime(new Date());
            popPrintSmsMsg.setBoxCode(residentCollectDto.getBoxCode());
            popPrintSmsMsg.setPackageBarcode(residentCollectDto.getBarCode());
            popPrintToSmsProducer.sendOnFailPersistent(popPrintSmsMsg.getPackageBarcode(), JsonHelper.toJson(popPrintSmsMsg));
        }
    }

    @Override
    public InvokeResult<Boolean> afterCollectFinish(PopPrintRequest popPrintRequest) {
        // 保存pop数据
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        try {
            // 验证运单号
            com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
            if (waybill == null) {
                logger.warn("保存POP打印信息savePopPrint --> 运单【{}】不存在", popPrintRequest.getWaybillCode());
                return result;
            }
            PopPrint popPrint = popPrintService.requestToPopPrint(popPrintRequest);
            // 保存pop数据
            // 推验货任务
            if (popPrintService.updateByWaybillOrPack(popPrint) <= Constants.NUMBER_ZERO) {
                // 判断是否已打印
                boolean isPrint = Objects.equals(popPrint.getOperateType(), PopPrintRequest.PRINT_PACK_TYPE);
                if (isPrint) {
                    popPrintService.pushInspection(popPrint);
                    popPrint.setPrintCount(Constants.CONSTANT_NUMBER_ONE);
                }
                popPrintService.add(popPrint);
            }
        }catch (Exception e){
            logger.error("处理运单号:{}的pop数据异常!", popPrintRequest.getWaybillCode(), e);
        }
        return result;
    }

}
