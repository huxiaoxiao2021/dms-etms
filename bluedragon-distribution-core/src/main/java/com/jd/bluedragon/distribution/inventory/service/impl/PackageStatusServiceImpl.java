package com.jd.bluedragon.distribution.inventory.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inventory.domain.PackStatusEnum;
import com.jd.bluedragon.distribution.inventory.domain.PackageStatus;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("packageStatusService")
public class PackageStatusServiceImpl implements PackageStatusService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SiteService siteService;

    @Autowired
    @Qualifier("dmsPackageStatusMQProducer")
    private DefaultJMQProducer dmsPackageStatusMQProducer;

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
        for (PackageStatus packageStatus : packageStatusLis) {
            if (logger.isInfoEnabled()) {
                logger.info("发送包裹状态消息-" + packageStatus.getPackageCode() + ":" + JsonHelper.toJson(packageStatus));
            }
            dmsPackageStatusMQProducer.sendOnFailPersistent(packageStatus.getPackageCode(), JsonHelper.toJson(packageStatus));
        }
    }


    /**
     * 同步运单状态对象转换为包裹状态对象
     *
     * @param parameters
     * @return
     */
    private List<PackageStatus> waybillSyncParam2PackageStatus(List<WaybillSyncParameter> parameters) {
        if (logger.isInfoEnabled()) {
            logger.info("同步运单状态对象转换为包裹状态对象.参数:" + JSON.toJSONString(parameters));
        }
        List<PackageStatus> packageStatusesList = new ArrayList<>();
        for (WaybillSyncParameter parameter : parameters) {
            if (parameter.getWaybillSyncParameterExtend() == null || parameter.getWaybillSyncParameterExtend().getOperateType() == null) {
                logger.warn("同步运单状态对象转换为包裹状态参数中扩展对象为空，无法获取操作节点，不进行处理." + JSON.toJSONString(parameter));
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
        if (logger.isInfoEnabled()) {
            logger.info("发送全称跟踪对象转换成包裹状态对象.参数:" + JSON.toJSONString(bdTraceDto));
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
        BaseStaffSiteOrgDto receiveSite = getReceiveSite(packageStatus.getWaybillCode(),createSiteCode);
        if(receiveSite != null){
            packageStatus.setReceiveSiteCode(receiveSite.getSiteCode());
            packageStatus.setReceiveSiteName(receiveSite.getSiteName());
            //todo 根据站点类型设置卡位
            packageStatus.setDirectionCode(receiveSite.getSiteCode());
            packageStatus.setDirectionName(receiveSite.getSiteName());
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
        if (createSite != null && createSite.getSiteType().equals(6420)) {
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
     * @param packageStatus
     */
    public BaseStaffSiteOrgDto getReceiveSite(String waybillCode, Integer createSiteCode) {
        Integer receiveSiteCode = null;
        //1.查send_d表
        if (createSiteCode != null && createSiteCode > 0) {
            List<SendDetail> sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, waybillCode, null);
            if (sendDetailList != null && sendDetailList.size() > 0) {
                receiveSiteCode = sendDetailList.get(0).getReceiveSiteCode();
            }
        }

        //2.查自己的表
        if (receiveSiteCode == null || receiveSiteCode.equals(0)) {
            String routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybillCode);
            if (StringUtils.isNotBlank(routerStr)) {
                String[] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLITER);
                //当前分拣中心下一网点
                for (int i = 0; i < routerNodes.length - 1; i++) {
                    int curNode = Integer.parseInt(routerNodes[i]);
                    int nexNode = Integer.parseInt(routerNodes[i + 1]);
                    if (curNode == createSiteCode) {
                        receiveSiteCode = nexNode;
                        break;
                    }
                }
            }
        }

        //3.查路由接口
        if (receiveSiteCode == null || receiveSiteCode.equals(0)) {

        }

        if (receiveSiteCode != null && receiveSiteCode > 0) {
            return siteService.getSite(receiveSiteCode);
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
        if (StringUtils.isBlank(waybillCode) || StringUtils.isBlank(packageCode)) {
            logger.warn("没有有效的运单号和包裹号." + JSON.toJSONString(parameter) + ";" + JSON.toJSONString(bdTraceDto));
            return false;
        }
        //非B网的不记录
        if (createSiteCode == null || createSiteCode <= 0) {
            logger.warn("操作站点无效." + JSON.toJSONString(parameter) + ";" + JSON.toJSONString(bdTraceDto));
            return false;
        }
        if (!isB2bSite(createSiteCode)) {
            logger.warn("非B网的分拣中心操作." + JSON.toJSONString(parameter) + ";" + JSON.toJSONString(bdTraceDto));
            return false;
        }

        if (operateType == null) {
            logger.warn("没有全称跟踪节点." + JSON.toJSONString(parameter) + ";" + JSON.toJSONString(bdTraceDto));
            return false;
        }
        //物流状态不在列表内的不记录
        PackStatusEnum statusEnum = getStatusInfo(operateType);
        if (statusEnum == null) {
            logger.warn("全称跟踪节点不在列表内." + JSON.toJSONString(parameter) + ";" + JSON.toJSONString(bdTraceDto));
            return false;
        }

        return true;
    }
}
