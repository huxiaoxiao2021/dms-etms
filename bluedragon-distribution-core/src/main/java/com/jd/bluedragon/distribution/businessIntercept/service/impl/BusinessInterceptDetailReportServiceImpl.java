package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOperateDimensionTypeEnum;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptDetailReportService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 分拣拦截报表明细提交服务实现
 *
 * @author fanggang7
 * @time 2020-12-11 17:36:32 周五
 */
@Data
@Service("businessInterceptDetailReportService")
public class BusinessInterceptDetailReportServiceImpl implements IBusinessInterceptDetailReportService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("businessOperateInterceptSendProducer")
    @Autowired
    private DefaultJMQProducer businessOperateInterceptSendProducer;

    @Qualifier("disposeAfterInterceptSendProducer")
    @Autowired
    private DefaultJMQProducer disposeAfterInterceptSendProducer;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    /**
     * 发送拦截消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:38:50 周五
     */
    @Override
    public Response<Boolean> sendInterceptMsg(SaveInterceptMsgDto msgDto) {
        log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }

            String barCode = msgDto.getBarCode();
            int operateDimensionType = this.getOperateDimensionTypeByBarCode(barCode);
            // 1. 如果是包裹
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.PACKAGE.getCode()) {
                SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
                BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
                saveInterceptMsgDto.setPackageCode(barCode);
                saveInterceptMsgDto.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
                log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(msgDto));
                businessOperateInterceptSendProducer.send(barCode, JSON.toJSONString(saveInterceptMsgDto));
            }
            // 2. 如果是运单
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.WAYBILL.getCode()) {
                String waybillCode = WaybillUtil.getWaybillCode(barCode);
                Waybill waybillAndPack = waybillCommonService.findWaybillAndPack(waybillCode, true, false, true, true);
                if(waybillAndPack != null && CollectionUtils.isNotEmpty(waybillAndPack.getPackList())){
                    List<Pack> packList = waybillAndPack.getPackList();
                    for (Pack pack : packList) {
                        SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
                        BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
                        saveInterceptMsgDto.setPackageCode(pack.getPackageCode());
                        saveInterceptMsgDto.setWaybillCode(waybillCode);
                        log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(saveInterceptMsgDto));
                        businessOperateInterceptSendProducer.send(barCode, JSON.toJSONString(saveInterceptMsgDto));
                    }
                }
            }
            // 3. 如果是箱号
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.BOX.getCode()) {
                // 查询箱号下所有包裹号 fixme 可能存在箱号中包裹数量过大问题
                Sorting sortingParam = new Sorting();
                sortingParam.setCreateSiteCode(msgDto.getSiteCode());
                sortingParam.setBoxCode(barCode);
                List<Sorting> sortingList = sortingService.findByBoxCode(sortingParam);
                if (sortingList.size() > 0) {
                    for (Sorting sorting : sortingList) {
                        SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
                        BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
                        saveInterceptMsgDto.setPackageCode(sorting.getPackageCode());
                        saveInterceptMsgDto.setWaybillCode(sorting.getWaybillCode());
                        log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(saveInterceptMsgDto));
                        businessOperateInterceptSendProducer.send(barCode, JSON.toJSONString(saveInterceptMsgDto));
                    }
                }
            }
            log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg full param: {}", JSON.toJSONString(msgDto));
        } catch (Exception e) {
            log.error("BusinessInterceptDetailReportServiceImpl sendInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截消息异常");
        }
        return result;
    }

    /**
     * 根据操作单据号得到操作维度
     * @param barCode 单据号
     * @return 操作维度类型
     * @author fanggang7
     * @time 2020-12-20 20:48:12 周日
     */
    private int getOperateDimensionTypeByBarCode(String barCode) {
        int operateDimensionType = 1;
        if(WaybillUtil.isPackageCode(barCode)){
            operateDimensionType = BusinessInterceptOperateDimensionTypeEnum.PACKAGE.getCode();
        }
        if(WaybillUtil.isWaybillCode(barCode)){
            operateDimensionType = BusinessInterceptOperateDimensionTypeEnum.WAYBILL.getCode();
        }
        if(BusinessUtil.isBoxcode(barCode)){
            operateDimensionType = BusinessInterceptOperateDimensionTypeEnum.BOX.getCode();
        }
        return operateDimensionType;
    }

    /**
     * 发送拦截后处理消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:39:15 周五
     */
    @Override
    public Response<Boolean> sendDisposeAfterInterceptMsg(SaveDisposeAfterInterceptMsgDto msgDto) {
        log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }
            String barCode = msgDto.getBarCode();
            int operateDimensionType = this.getOperateDimensionTypeByBarCode(barCode);
            // 1. 如果是包裹
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.PACKAGE.getCode()) {
                SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
                BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
                saveDisposeAfterInterceptMsgDto.setPackageCode(barCode);
                saveDisposeAfterInterceptMsgDto.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
                log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                disposeAfterInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
            }
            // 2. 如果是运单
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.WAYBILL.getCode()) {
                String waybillCode = WaybillUtil.getWaybillCode(barCode);
                Waybill waybillAndPack = waybillCommonService.findWaybillAndPack(waybillCode, true, false, true, true);
                if(waybillAndPack != null && CollectionUtils.isNotEmpty(waybillAndPack.getPackList())){
                    List<Pack> packList = waybillAndPack.getPackList();
                    for (Pack pack : packList) {
                        SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
                        BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
                        saveDisposeAfterInterceptMsgDto.setPackageCode(pack.getPackageCode());
                        saveDisposeAfterInterceptMsgDto.setWaybillCode(waybillCode);
                        log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                        disposeAfterInterceptSendProducer.send(barCode, JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                    }
                }
            }
            // 3. 如果是箱号
            if (operateDimensionType == BusinessInterceptOperateDimensionTypeEnum.BOX.getCode()) {
                // 查询箱号下所有包裹号 fixme 可能存在箱号中包裹数量过大问题
                Sorting sortinParam = new Sorting();
                sortinParam.setCreateSiteCode(msgDto.getSiteCode());
                sortinParam.setBoxCode(barCode);
                List<Sorting> sortingList = sortingService.findByBoxCode(sortinParam);
                if (sortingList.size() > 0) {
                    for (Sorting sorting : sortingList) {
                        SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
                        BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
                        saveDisposeAfterInterceptMsgDto.setPackageCode(sorting.getPackageCode());
                        saveDisposeAfterInterceptMsgDto.setWaybillCode(sorting.getWaybillCode());
                        log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                        disposeAfterInterceptSendProducer.send(barCode, JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                    }
                }
            }
            log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg full param: {}", JSON.toJSONString(msgDto));
        } catch (Exception e) {
            log.error("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截消息异常");
        }
        return result;
    }
}
