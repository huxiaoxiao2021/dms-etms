package com.jd.bluedragon.distribution.inventory.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inventory.domain.PackStatusEnum;
import com.jd.bluedragon.distribution.inventory.domain.PackageStatus;
import com.jd.bluedragon.distribution.inventory.domain.SiteWithDirection;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.utils.StringHelper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("packageStatusService")
public class PackageStatusServiceImpl implements PackageStatusService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SiteService siteService;

    @Autowired
    @Qualifier("dmsPackageStatusMQProducer")
    private DefaultJMQProducer dmsPackageStatusMQProducer;

    @Autowired
    private WaybillCacheService waybillCacheService;
    
    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;

    @Autowired
    private RouterService routerService;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLITER = "\\|";

    private static List<Integer> PACK_STATUS_RECEIVE_LIST = new ArrayList<>();
    private static List<Integer> PACK_STATUS_INSPECTION_LIST = new ArrayList<>();
    private static List<Integer> PACK_STATUS_SORTING_LIST = new ArrayList<>();
    private static List<Integer> PACK_STATUS_SEND_LIST = new ArrayList<>();

    static {
        PACK_STATUS_RECEIVE_LIST.add(WaybillStatus.WAYBILL_TRACK_SH);//正向收货
        PACK_STATUS_RECEIVE_LIST.add(WaybillStatus.WAYBILL_TRACK_REVERSE_SH);//逆向收货
        PACK_STATUS_RECEIVE_LIST.add(WaybillStatus.WAYBILL_TRACK_UP_DELIVERY);//配送员上门收货

        PACK_STATUS_INSPECTION_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION);//正向验货
        PACK_STATUS_INSPECTION_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_INSPECTION);//逆向验货
        PACK_STATUS_INSPECTION_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_POP_InFactory);//驻场验货

        PACK_STATUS_SORTING_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_SORTING); //正向分拣
        PACK_STATUS_SORTING_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING); //逆向分拣

        PACK_STATUS_SEND_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY); //正向发货
        PACK_STATUS_SEND_LIST.add(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_DELIVERY); //逆向发货
    }

    /**
     * 包裹物流状态信息发MQ
     *
     * @param parameters
     * @param bdTraceDto
     */
    public void recordPackageStatus(List<WaybillSyncParameter> parameters, BdTraceDto bdTraceDto) {
        List<PackageStatus> packageStatusLis = new ArrayList<>();
        //将回传运单状态/发送全称跟踪对象转换为包裹状态实体
        if (parameters != null) {
            packageStatusLis = waybillSyncParam2PackageStatus(parameters);
        } else if (bdTraceDto != null) {
            packageStatusLis = bdTraceDto2PackageStatus(bdTraceDto);
        }

        //包裹状态发送JMQ消息
        List<Message> messageList = new ArrayList<>();
        for (PackageStatus packageStatus : packageStatusLis) {
            if (log.isDebugEnabled()) {
                log.debug("发送包裹状态消息-{}：{}",packageStatus.getPackageCode(), JsonHelper.toJson(packageStatus));
            }
            messageList.add(new Message(dmsPackageStatusMQProducer.getTopic(),JSON.toJSONString(packageStatus),packageStatus.getPackageCode()));

        }
        dmsPackageStatusMQProducer.batchSendOnFailPersistent(messageList);

    }


    /**
     * 同步运单状态对象转换为包裹状态对象
     *
     * @param parameters
     * @return
     */
    private List<PackageStatus> waybillSyncParam2PackageStatus(List<WaybillSyncParameter> parameters) {
        if (log.isDebugEnabled()) {
            log.debug("同步运单状态对象转换为包裹状态对象.参数:{}" , JSON.toJSONString(parameters));
        }
        List<PackageStatus> packageStatusesList = new ArrayList<>();
        for (WaybillSyncParameter parameter : parameters) {
            if (parameter.getWaybillSyncParameterExtend() == null || parameter.getWaybillSyncParameterExtend().getOperateType() == null) {
                log.warn("同步运单状态对象转换为包裹状态参数中扩展对象为空，无法获取操作节点，不进行处理:{}", JSON.toJSONString(parameter));
                continue;
            }

            //校验是否合法，是否需要发MQ
            if (!isValidPackageStatus(parameter, null)) {
                continue;
            }

            //设置包裹状态对象的基础信息
            String barCode = parameter.getOperatorCode();
            PackageStatus basicPackageStatus = new PackageStatus();
            basicPackageStatus.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
            basicPackageStatus.setBoxCode(parameter.getWaybillSyncParameterExtend().getBoxId());
            basicPackageStatus.setSendCode(parameter.getWaybillSyncParameterExtend().getBatchId());

            basicPackageStatus.setCreateSiteCode(parameter.getZdId());
            basicPackageStatus.setReceiveSiteCode(parameter.getWaybillSyncParameterExtend().getArriveZdId());
            basicPackageStatus.setOperatorCode(parameter.getOperatorId());
            basicPackageStatus.setOperatorName(parameter.getOperatorName());
            basicPackageStatus.setOperateTypeNode(parameter.getWaybillSyncParameterExtend().getOperateType());
            basicPackageStatus.setOperateTime(parameter.getOperateTime());

            //完善包裹包裹状态对象的始发、目的站点信息；物流状态信息
            fullPackageStatus(basicPackageStatus);

            //如果是按照运单号同步运单状态，需要转换成包裹维度
            List<String> packageCodeList = new ArrayList<>();
            if (WaybillUtil.isWaybillCode(barCode)) {
                packageCodeList.addAll(getPackageCodeListByWaybillCode(barCode));
            } else if (WaybillUtil.isPackageCode(barCode)) {
                packageCodeList.add(barCode);
            }

            //循环设置包裹号
            for (String packageCode : packageCodeList) {
                PackageStatus packageStatus = new PackageStatus();
                BeanUtils.copyProperties(basicPackageStatus, packageStatus);

                packageStatus.setPackageCode(packageCode);

                packageStatusesList.add(packageStatus);
            }
        }

        return packageStatusesList;
    }

    /**
     * 发送全称跟踪对象转换成包裹状态对象
     *
     * @param bdTraceDto
     * @return
     */
    private List<PackageStatus> bdTraceDto2PackageStatus(BdTraceDto bdTraceDto) {
        if (log.isDebugEnabled()) {
            log.debug("发送全称跟踪对象转换成包裹状态对象.参数:{}",JSON.toJSONString(bdTraceDto));
        }

        List<PackageStatus> packageStatusesList = new ArrayList<>();

        if (isValidPackageStatus(null, bdTraceDto)) {
            //设置包裹状态对象的基础信息
            PackageStatus basicPackageStatus = new PackageStatus();

            basicPackageStatus.setWaybillCode(StringUtils.isNotBlank(bdTraceDto.getWaybillCode()) ? bdTraceDto.getWaybillCode() : WaybillUtil.getWaybillCode(bdTraceDto.getPackageBarCode()));
            basicPackageStatus.setCreateSiteCode(bdTraceDto.getOperatorSiteId());
            basicPackageStatus.setOperatorCode(bdTraceDto.getOperatorUserId());
            basicPackageStatus.setOperatorName(bdTraceDto.getOperatorUserName());
            basicPackageStatus.setOperateTime(bdTraceDto.getOperatorTime());
            basicPackageStatus.setOperateTypeNode(bdTraceDto.getOperateType());

            //完善包裹包裹状态对象的始发、目的站点信息；物流状态信息
            fullPackageStatus(basicPackageStatus);

            String waybillCode = bdTraceDto.getWaybillCode();
            String packageCode = bdTraceDto.getPackageBarCode();

            List<String> packageCodeList = new ArrayList<>();

            //如果是按照运单号同步运单状态，需要转换成包裹维度
            if (StringUtils.isNotBlank(packageCode)) {
                packageCodeList.add(packageCode);
            } else if (StringUtils.isNotBlank(waybillCode)) {
                packageCodeList.addAll(getPackageCodeListByWaybillCode(waybillCode));
            }

            //循环设置包裹号
            for (String packageBarCode : packageCodeList) {
                PackageStatus packageStatus = new PackageStatus();
                BeanUtils.copyProperties(basicPackageStatus, packageStatus);

                packageStatus.setPackageCode(packageBarCode);

                packageStatusesList.add(packageStatus);
            }
        }

        return packageStatusesList;
    }

    /**
     * 1.补充目的地信息
     * 2.补充始发和目的站点类型
     * 3.补充物流状态（收货、验货、分拣、发货、补打、取消分拣、取消发货）
     *
     * @param packageStatus
     */
    private void fullPackageStatus(PackageStatus packageStatus) {
        //1.补充始发地信息
        Integer createSiteCode = packageStatus.getCreateSiteCode();
        BaseStaffSiteOrgDto createSite = siteService.getSite(createSiteCode);
        if (createSite != null) {
            packageStatus.setCreateSiteName(createSite.getSiteName());
            packageStatus.setCreateSiteType(createSite.getSiteType());
            packageStatus.setCreateSiteSubType(createSite.getSubType());
        }

        //2.补充目的地信息
        SiteWithDirection receiveSite = null;
        if(packageStatus.getReceiveSiteCode() != null ){
            receiveSite = getDirectionBySiteCode(packageStatus.getReceiveSiteCode());
        }
        if(receiveSite == null){
            receiveSite = getReceiveSiteByWaybillCode(packageStatus.getWaybillCode(),createSiteCode);
        }
        if(receiveSite != null){
            packageStatus.setReceiveSiteCode(receiveSite.getSiteCode());
            packageStatus.setReceiveSiteName(receiveSite.getSiteName());
            packageStatus.setDirectionCode(receiveSite.getDirectionCode());
            packageStatus.setDirectionName(receiveSite.getDirectionName());
        }

        //3.补充物流状态信息
        PackStatusEnum statusEnum = getStatusInfo(packageStatus.getOperateTypeNode());
        if (statusEnum != null) {
            packageStatus.setStatusCode(statusEnum.getCode());
            packageStatus.setStatusDesc(statusEnum.getDesc());
        }
    }


    /**
     * 判断是否是B网站点
     *
     * @param createSiteCode
     * @return
     */
    private boolean isB2bSite(Integer createSiteCode) {
        BaseStaffSiteOrgDto createSite = siteService.getSite(createSiteCode);
        if (createSite != null && createSite.getSubType() != null && createSite.getSubType().equals(Constants.B2B_SITE_TYPE)) {
            return true;
        }
        return false;
    }

    /**
     * 根据运单号获取包裹号列表
     *
     * @param waybillCode
     * @return
     */
    private List<String> getPackageCodeListByWaybillCode(String waybillCode) {
        List<String> packageCodeList = new ArrayList<>();

        //调用运单接口获取所有包裹号
        BaseEntity<BigWaybillDto> waybill = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if (waybill != null && waybill.getData() != null) {
            List<DeliveryPackageD> packages = waybill.getData().getPackageList();
            for (DeliveryPackageD packageD : packages) {
                packageCodeList.add(packageD.getPackageBarcode());
            }
        }
        return packageCodeList;
    }

    /**
     * 根据运单号和始发获取目的分拣
     *
     * @param
     */
    public SiteWithDirection getReceiveSiteByWaybillCode(String waybillCode, Integer createSiteCode) {
        Integer receiveSiteCode = null;
        //1.查send_d表
        if (createSiteCode != null && createSiteCode > 0) {
            SendDetail sendDetaiFirst = sendDetailService.findOneByWaybillCode(createSiteCode, waybillCode);
            if (sendDetaiFirst != null) {
                receiveSiteCode = sendDetaiFirst.getReceiveSiteCode();
            }
        }

        //2.查自己的表
        if (receiveSiteCode == null || receiveSiteCode.equals(0)) {
            RouteNextDto routeNextDto = routerService.matchRouterNextNode(createSiteCode, waybillCode);
            receiveSiteCode = routeNextDto == null? null : routeNextDto.getFirstNextSiteId();
        }

        if (receiveSiteCode != null && receiveSiteCode > 0) {
            return getDirectionBySiteCode(receiveSiteCode);
        }
        return null;
    }

    /**
     * 获取物流状态
     *
     * @param operateType
     */
    private PackStatusEnum getStatusInfo(Integer operateType) {
        if (PACK_STATUS_RECEIVE_LIST.contains(operateType)) {
            return PackStatusEnum.RECEIVE;
        } else if (PACK_STATUS_INSPECTION_LIST.contains(operateType)) {
            return PackStatusEnum.INSPECTION;
        } else if (PACK_STATUS_SORTING_LIST.contains(operateType)) {
            return PackStatusEnum.SORTING;
        } else if (PACK_STATUS_SEND_LIST.contains(operateType)) {
            return PackStatusEnum.SEND;
        } else if (WaybillStatus.WAYBILL_TRACK_MSGTYPE_PACK_REPRINT.equals(operateType)) {
            return PackStatusEnum.REPRINT;
        } else if (WaybillStatus.WAYBILL_TRACK_SORTING_CANCEL.equals(operateType)) {
            return PackStatusEnum.SORTING_CANCEL;
        } else if (WaybillStatus.WAYBILL_TRACK_SEND_CANCEL.equals(operateType)) {
            return PackStatusEnum.SEND_CANCEL;
        } else if(WaybillStatus.WAYBILL_TRACK_QC.equals(operateType)){
            return PackStatusEnum.EXCEPTION;
        }

        return null;
    }

    /**
     * 校验是否是需要记录的包裹状态信息
     *
     * @param parameter
     * @param bdTraceDto
     * @return
     */
    private boolean isValidPackageStatus(WaybillSyncParameter parameter, BdTraceDto bdTraceDto) {
        String waybillCode = null;
        String packageCode = null;
        Integer createSiteCode = null;
        Integer operateType = null;

        if (parameter != null) {
            String barCode = parameter.getOperatorCode();
            if (WaybillUtil.isPackageCode(barCode)) {
                packageCode = barCode;
            }
            waybillCode = WaybillUtil.getWaybillCode(barCode);
            createSiteCode = parameter.getZdId();
            operateType = parameter.getWaybillSyncParameterExtend() != null ? parameter.getWaybillSyncParameterExtend().getOperateType() : null;
        } else if (bdTraceDto != null) {
            waybillCode = bdTraceDto.getWaybillCode();
            packageCode = bdTraceDto.getPackageBarCode();
            createSiteCode = bdTraceDto.getOperatorSiteId();
            operateType = bdTraceDto.getOperateType();
        }

        //运单号包裹号至少有一个
        if (StringUtils.isBlank(waybillCode) && StringUtils.isBlank(packageCode)) {
            log.warn("没有有效的运单号和包裹号:{}={}",JSON.toJSONString(parameter), JSON.toJSONString(bdTraceDto));
            return false;
        }
        if (createSiteCode == null || createSiteCode <= 0) {
            log.warn("操作站点无效:{}-{}",JSON.toJSONString(parameter), JSON.toJSONString(bdTraceDto));
            return false;
        }
        if (!isB2bSite(createSiteCode)) {
            log.warn("非B网的分拣中心操作:{}-{}",JSON.toJSONString(parameter), JSON.toJSONString(bdTraceDto));
            return false;
        }

        if (operateType == null) {
            log.warn("没有全称跟踪节点:{}-{}",JSON.toJSONString(parameter), JSON.toJSONString(bdTraceDto));
            return false;
        }
        //物流状态不在列表内的不记录
        PackStatusEnum statusEnum = getStatusInfo(operateType);
        if (statusEnum == null) {
            log.warn("全称跟踪节点不在列表内:{}-{}",JSON.toJSONString(parameter), JSON.toJSONString(bdTraceDto));
            return false;
        }

        return true;
    }

    /**
     * 根据站点编码获取流向信息
     * @param siteCode
     * @return
     */
    private SiteWithDirection getDirectionBySiteCode(Integer siteCode){
        BaseStaffSiteOrgDto dto = siteService.getSite(siteCode);
        if(dto!=null){
            SiteWithDirection siteWithDirection = new SiteWithDirection();
            siteWithDirection.setSiteCode(dto.getSiteCode());
            siteWithDirection.setSiteName(dto.getSiteName());
            siteWithDirection.setSiteType(dto.getSiteType());
            siteWithDirection.setSiteSubType(dto.getSubType());
            siteWithDirection.setDirectionCode(dto.getSiteCode());
            siteWithDirection.setDirectionName(dto.getSiteName());
            log.info("PackageStatusServiceImpl.getDirectionBySiteCode----parameter=【{}】,result=【{}】", siteCode, JsonHelper.toJson(dto));
            if(BusinessUtil.isTerminalSite(dto.getSiteType())) {
                siteWithDirection.setDirectionCode(SiteWithDirection.DIRECTION_CODE_TERMINAL_SITE);
                siteWithDirection.setDirectionName(SiteWithDirection.DIRECTION_NAME_TERMINAL_SITE);
            }else if(BusinessUtil.isConvey(dto.getSiteType())){
                siteWithDirection.setDirectionCode(SiteWithDirection.DIRECTION_CODE_CONVEY);
                siteWithDirection.setDirectionName(SiteWithDirection.DIRECTION_NAME_CONVEY);
            }
            return siteWithDirection;
        }
        return null;
    }

    /**
     * 过滤验货
     */
	@Override
	public void filterAndSendDmsHasnoPresiteWaybillMq(List<WaybillSyncParameter> parameters, BdTraceDto bdTraceDto) {
        //将回传运单状态/发送全称跟踪对象转换为消息对象
        if (parameters != null) {
        	List<DmsHasnoPresiteWaybillMq> messageList = toDmsHasnoPresiteWaybillMqList(parameters);
        	if(messageList == null
        			|| messageList.isEmpty()) {
        		return;
        	}
            for (DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq : messageList) {
            	waybillHasnoPresiteRecordService.sendDataChangeMq(dmsHasnoPresiteWaybillMq);
            }
        } else if (bdTraceDto != null) {
        	DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = toDmsHasnoPresiteWaybillMq(bdTraceDto);
        	if(dmsHasnoPresiteWaybillMq == null) {
        		return;
        	}
            waybillHasnoPresiteRecordService.sendDataChangeMq(dmsHasnoPresiteWaybillMq);
        }
	}
	/**
	 * 全程跟踪转成mq对象
	 * @param bdTraceDto
	 * @return
	 */
    private DmsHasnoPresiteWaybillMq toDmsHasnoPresiteWaybillMq(BdTraceDto bdTraceDto) {
    	DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = new DmsHasnoPresiteWaybillMq();
        //判断是否验货
        PackStatusEnum statusEnum = getStatusInfo(bdTraceDto.getOperateType());
        if(!PackStatusEnum.INSPECTION.equals(statusEnum)) {
        	return null;
        }
        dmsHasnoPresiteWaybillMq.setWaybillCode(StringUtils.isNotBlank(bdTraceDto.getWaybillCode()) ? bdTraceDto.getWaybillCode() : WaybillUtil.getWaybillCode(bdTraceDto.getPackageBarCode()));
        dmsHasnoPresiteWaybillMq.setOperateSiteCode(bdTraceDto.getOperatorSiteId());
        dmsHasnoPresiteWaybillMq.setOperateUserErp(""+bdTraceDto.getOperatorUserId());
        dmsHasnoPresiteWaybillMq.setOperateUserName(bdTraceDto.getOperatorUserName());
        dmsHasnoPresiteWaybillMq.setOperateTime(bdTraceDto.getOperatorTime());
    	dmsHasnoPresiteWaybillMq.setOperateCode(DmsHasnoPresiteWaybillMqOperateEnum.CHECK.getCode());
    	return dmsHasnoPresiteWaybillMq;
    }
    /**
     * 全程跟踪list转成mq对象
     * @param parameters
     * @return
     */
    private List<DmsHasnoPresiteWaybillMq> toDmsHasnoPresiteWaybillMqList(List<WaybillSyncParameter> parameters) {
    	if(parameters == null
    			|| parameters.isEmpty()) {
    		return null;
    	}
    	//运单维度防重
    	Set<String> waybillCodes = new HashSet<String>();
    	List<DmsHasnoPresiteWaybillMq> messageList = new ArrayList<>();
    	for(WaybillSyncParameter parameter : parameters) {
        	//无法获取状态码
        	if(parameter.getWaybillSyncParameterExtend() == null) {
        		continue;
        	}
            //判断是否验货
            PackStatusEnum statusEnum = getStatusInfo(parameter.getWaybillSyncParameterExtend().getOperateType());
            if(!PackStatusEnum.INSPECTION.equals(statusEnum)) {
            	continue;
            }
            DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = new DmsHasnoPresiteWaybillMq();
            String waybillCode = WaybillUtil.getWaybillCode(parameter.getOperatorCode());
            if(StringHelper.isEmpty(waybillCode)||
            		waybillCodes.contains(waybillCode)) {
            	continue;
            }
            dmsHasnoPresiteWaybillMq.setWaybillCode(waybillCode);
            dmsHasnoPresiteWaybillMq.setOperateSiteCode(parameter.getZdId());
            if(parameter.getOperatorId() != null) {
            	dmsHasnoPresiteWaybillMq.setOperateUserErp(parameter.getOperatorId().toString());
            }
            dmsHasnoPresiteWaybillMq.setOperateUserName(parameter.getOperatorName());
            dmsHasnoPresiteWaybillMq.setOperateTime(parameter.getOperateTime());
        	dmsHasnoPresiteWaybillMq.setOperateCode(DmsHasnoPresiteWaybillMqOperateEnum.CHECK.getCode());
        	messageList.add(dmsHasnoPresiteWaybillMq);
        	waybillCodes.add(waybillCode);
    	}
    	return messageList;
    }
}
