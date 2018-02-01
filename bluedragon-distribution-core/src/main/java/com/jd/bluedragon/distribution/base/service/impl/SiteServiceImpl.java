package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.request.ThirdPartyOverrunRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.ThirdPartyOverrunResponse;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.proxy.VtsQueryWSProxy;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.BaseSiteGoods;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("siteService")
public class SiteServiceImpl implements SiteService {
    private static final int SITE_PAGE_SIZE = 1000;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private VtsQueryWS vtsQueryWS;

    @Autowired
    private VtsQueryWSProxy vtsQueryWSProxy;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BasicSecondaryWS basicSecondaryWS;

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * 批次号正则
     */
    private static final Pattern RULE_SEND_REGEX = Pattern.compile("^[Y|y]?(\\d+)-(\\d+)-([0-9]{14,})$");

    public BaseStaffSiteOrgDto getSite(Integer siteCode) {
        return this.baseMajorManager.getBaseSiteBySiteId(siteCode);
    }

    public BasicTraderInfoDTO getTrader(Integer siteCode) {
        return this.baseMinorManager.getBaseTraderById(siteCode);
    }

    //车辆管理系统获取运力编码
    @Override
    public RouteTypeResponse getCapacityCodeInfo(String capacityCode) {
        CommonDto<VtsTransportResourceDto> vtsDto = vtsQueryWS.getTransportResourceByTransCode(capacityCode);
        RouteTypeResponse base = new RouteTypeResponse();
        if (vtsDto == null) {    //JSF接口返回空
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage("查询运力信息结果为空:" + capacityCode);
            return base;
        }
        if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
            VtsTransportResourceDto vtrd = vtsDto.getData();
            if (vtrd != null) {
                base.setSiteCode(vtrd.getEndNodeId());
                base.setSendUserType(vtrd.getTransType());
                base.setDriverId(vtrd.getCarrierId());
                base.setRouteType(vtrd.getRouteType()); // 增加运输类型返回值
                base.setDriver(vtrd.getCarrierName());
                base.setTransWay(vtrd.getTransMode());
                base.setCarrierType(vtrd.getTransType());
                base.setCode(JdResponse.CODE_OK);
                base.setMessage(JdResponse.MESSAGE_OK);
            } else {
                base.setCode(JdResponse.CODE_SERVICE_ERROR);
                base.setMessage("查询运力信息结果为空:" + capacityCode);
            }
        } else if (Constants.RESULT_WARN == vtsDto.getCode()) {    //查询运力信息接口返回警告，给出前台提示
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage(vtsDto.getMessage());
        } else { //服务出错或者出异常，打日志
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage("查询运力信息出错！");
            logger.error("查询运力信息出错,出错原因:" + vtsDto.getMessage());
            logger.error("查询运力信息出错,运力编码:" + capacityCode);
        }
        return base;
    }

    @Override
    public CapacityCodeResponse queryCapacityCodeInfo(
            CapacityCodeRequest request) {
        CapacityCodeResponse response = new CapacityCodeResponse();
        VtsTransportResourceDto dtorequest = new VtsTransportResourceDto();

        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        try {
            // 始发区域
            if (request.getSorgid() != null)
                dtorequest.setStartOrgId(request.getSorgid());
            // 始发站
            if (request.getScode() != null)
                dtorequest.setStartNodeId(request.getScode());
            // 目的区域
            if (request.getRorgid() != null)
                dtorequest.setEndOrgId(request.getRorgid());
            // 目的站
            if (request.getRcode() != null)
                dtorequest.setEndNodeId(request.getRcode());
            // 线路类型
            if (request.getRouteType() != null)
                dtorequest.setRouteType(request.getRouteType());
            // 运力类型
            if (request.getTranType() != null)
                dtorequest.setTransType(request.getTranType());
            // 运输方式
            if (request.getTranMode() != null)
                dtorequest.setTransMode(request.getTranMode());
            // 运力编码
            if (request.getTranCode() != null)
                dtorequest.setTransCode(request.getTranCode());
            // 承运人信息
            if (request.getCarrierId() != null) {
                //承运商ID
                if (NumberHelper.isNumber(request.getCarrierId()) || request.getCarrierId().equals(Constants.JDZY))
                    dtorequest.setCarrierId(Integer.parseInt(request.getCarrierId()));
                    //承运商名称
                else dtorequest.setCarrierName(request.getCarrierId());
            }

            List<VtsTransportResourceDto> result = vtsQueryWSProxy.getVtsTransportResourceDtoAll(dtorequest);

            if (result != null && !result.isEmpty()) {
                List<CapacityDomain> domainList = new ArrayList<CapacityDomain>();
                for (VtsTransportResourceDto dto : result) {
                    // 返回客户端所有信息
                    CapacityDomain domain = new CapacityDomain();

                    // 到车时间
                    domain.setArriveTime(String.valueOf(dto.getArriveCarTime()));
                    // 承运商
                    domain.setCarrierName(String.valueOf(dto.getCarrierName()));
                    // 目的站
                    domain.setRcode(String.valueOf(dto.getEndNodeId()));
                    domain.setRname(dto.getEndNodeName());
                    // 目的区域
                    domain.setRorgid(String.valueOf(dto.getEndOrgId()));
                    domain.setRorgName(dto.getEndOrgName());
                    // 线路类型
                    domain.setRouteType(String.valueOf(dto.getRouteType()));
                    // 始发站
                    domain.setScode(String.valueOf(dto.getStartNodeId()));
                    domain.setSname(dto.getStartNodeName());
                    // 发车时间
                    domain.setSendTime(String.valueOf(dto.getSendCarTime()));
                    // 始发区域
                    domain.setSorgid(String.valueOf(dto.getStartOrgId()));
                    domain.setSorgName(dto.getStartOrgName());
                    // 运力编码
                    domain.setTranCode(String.valueOf(dto.getTransCode()));
                    // 运输方式
                    domain.setTranMode(String.valueOf(dto.getTransMode()));
                    // 运力类型
                    domain.setTranType(String.valueOf(dto.getTransType()));
                    // 在途时长
                    domain.setTravelTime(String.valueOf(dto.getTravelTime()));
                    // 承运商名称
                    domain.setCarrierName(dto.getCarrierName());
                    // 承运商ID
                    domain.setCarrierId(String.valueOf(dto.getCarrierId()));
                    //航空班次
                    domain.setAirShiftName(dto.getAirShiftName());

                    domainList.add(domain);
                }
                if (domainList != null && !domainList.isEmpty()) {
                    response.setData(domainList);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @Cache(key = "SiteServiceImpl.getSitesByPage@args0-@args1", memoryEnable = false, redisExpiredTime = 1000 * 60 * 5, redisEnable = true)
    @Override
    public Pager<List<SiteWareHouseMerchant>> getSitesByPage(int category, int pageNo) {
        switch (category) {
            case 1:
                return baseMajorManager.getBaseSiteByPage(pageNo);
            case 2:
                return baseMajorManager.getBaseStoreInfoByPage(pageNo);
            case 3:
                return baseMajorManager.getTraderListByPage(pageNo);
            default:
                return baseMajorManager.getBaseSiteByPage(pageNo);
        }

    }

    /**
     * 根据批次号的正则匹配始发分拣中心id和目的分拣中心id
     *
     * @param sendCode 批次号
     * @return
     */
    @Override
    public Integer[] getSiteCodeBySendCode(String sendCode) {
        Integer[] sites = new Integer[]{-1, -1};
        if (StringHelper.isNotEmpty(sendCode)) {
            Matcher matcher = RULE_SEND_REGEX.matcher(sendCode.trim());
            if (matcher.matches()) {
                sites[0] = Integer.valueOf(matcher.group(1));
                sites[1] = Integer.valueOf(matcher.group(2));
            }
        }
        return sites;
    }

    /**
     * 验证三方承运商商品是否超限
     *
     * @param request
     * @return
     */
    @Override
    public ThirdPartyOverrunResponse thirdPartyIsOverrun(ThirdPartyOverrunRequest request) {
        ThirdPartyOverrunResponse response = new ThirdPartyOverrunResponse();
        try {
            this.rebuildRequestParam(request);
            BaseResult<BaseSiteGoods> baseResult = basicSecondaryWS.getGoodsVolumeLimitBySiteCode(request.getSiteCode());
//            BaseResult<BaseSiteGoods> baseResult = new BaseResult<BaseSiteGoods>();
//            baseResult.setResultCode(BaseResult.RESULT_SUCCESS);
//            BaseSiteGoods baseSiteGoods1 = new BaseSiteGoods();
//            baseSiteGoods1.setGoodsWeight(10.0f);
//            baseSiteGoods1.setGoodsVolume(10.0);
//            baseSiteGoods1.setGoodsLength(10.0f);
//            baseSiteGoods1.setGoodsWidth(10.0f);
//            baseSiteGoods1.setGoodsHeight(10.0f);
//            baseResult.setData(baseSiteGoods1);
            if (baseResult != null && baseResult.getResultCode() == BaseResult.RESULT_SUCCESS) {
                BaseSiteGoods baseSiteGoods = baseResult.getData();
                if (baseSiteGoods != null) {
                    if (request.getWeight() > baseSiteGoods.getGoodsWeight()) {
                        response.set(ThirdPartyOverrunResponse.CODE_WEIGHT_OVERRUN, ThirdPartyOverrunResponse.MESSAGE_WEIGHT_OVERRUN);
                        return response;
                    }
                    if (request.getVolume() > baseSiteGoods.getGoodsVolume()) {
                        response.set(ThirdPartyOverrunResponse.CODE_VOLUME_OVERRUN, ThirdPartyOverrunResponse.MESSAGE_VOLUME_OVERRUN);
                        return response;
                    }
                    if (request.getLength() > baseSiteGoods.getGoodsLength()) {
                        response.set(ThirdPartyOverrunResponse.CODE_LENGTH_OVERRUN, ThirdPartyOverrunResponse.MESSAGE_LENGTH_OVERRUN);
                        return response;
                    }
                    if (request.getWidth() > baseSiteGoods.getGoodsWidth()) {
                        response.set(ThirdPartyOverrunResponse.CODE_WIDTH_OVERRUN, ThirdPartyOverrunResponse.MESSAGE_WIDTH_OVERRUN);
                        return response;
                    }
                    if (request.getHeight() > baseSiteGoods.getGoodsHeight()) {
                        response.set(ThirdPartyOverrunResponse.CODE_HEIGHT_OVERRUN, ThirdPartyOverrunResponse.MESSAGE_HEIGHT_OVERRUN);
                        return response;
                    }
                } else { //获取标准值为空 视为未维护标准值 则默认为无标准，不超限
                    response.set(ThirdPartyOverrunResponse.CODE_OK, ThirdPartyOverrunResponse.MESSAGE_OK);
                }
            } else {
                response.set(ThirdPartyOverrunResponse.CODE_SERVICE_ERROR, "调用基础资料接口获取标准信息失败");
            }
        } catch (Exception e) {
            response.set(ThirdPartyOverrunResponse.CODE_SERVICE_ERROR, "[验证三方承运商商品是否超限]调用基础资料接口获取三方站点限制信息时发生异常");
            logger.error("[验证三方承运商商品是否超限]调用基础资料接口获取三方站点限制信息时发生异常", e);
        }
        return response;
    }

    /**
     * 重构请求参数
     *
     * @param request
     */
    private void rebuildRequestParam(ThirdPartyOverrunRequest request) {
        this.resetParams(request);
        boolean isLoad = false;
        ThirdPartyOverrunRequest packageSizeInfo = new ThirdPartyOverrunRequest();
        packageSizeInfo.setPackageCode(request.getPackageCode());
        // 重构重量参数
        isLoad = this.rebuildWeightParam(request, packageSizeInfo, isLoad);
        // 重构体积参数
        isLoad = this.rebuildVolumeParam(request, packageSizeInfo, isLoad);
        // 重构长 宽 高 参数
        this.rebuildSizeParam(request, packageSizeInfo, isLoad);
    }

    /**
     * 设置体积 判断体积是否有值 为Null或0则设置根据长*宽*高取值 重排长宽高
     *
     * @param request
     */
    private void resetParams(ThirdPartyOverrunRequest request) {
        Double[] sizeArray = new Double[3];
        sizeArray[0] = request.getLength() == null ? 0.0 : request.getLength();
        sizeArray[1] = request.getWidth() == null ? 0.0 : request.getWidth();
        sizeArray[2] = request.getHeight() == null ? 0.0 : request.getHeight();
        Arrays.sort(sizeArray);
        request.setHeight(sizeArray[0]);
        request.setWidth(sizeArray[1]);
        request.setLength(sizeArray[2]);
        if (request.getVolume() == null || request.getVolume() == 0.0) {
            request.setVolume(request.getLength() * request.getWidth() * request.getHeight());
        }
    }

    /**
     * 重构重量参数
     *
     * @param request
     * @param packageSizeInfo
     * @param isLoad
     * @return
     */
    private boolean rebuildWeightParam(ThirdPartyOverrunRequest request, ThirdPartyOverrunRequest packageSizeInfo, boolean isLoad) {
        if (request.getWeight() == null || request.getWeight() == 0.0) {
            if (!isLoad) {
                this.loadWaybillPackageSizeInfo(packageSizeInfo);
                isLoad = true;
            }
            request.setWeight(packageSizeInfo.getWeight());
        }
        return isLoad;
    }

    /**
     * 重构体积参数
     *
     * @param request
     * @param packageSizeInfo
     * @param isLoad
     * @return
     */
    private boolean rebuildVolumeParam(ThirdPartyOverrunRequest request, ThirdPartyOverrunRequest packageSizeInfo, boolean isLoad) {
        if (request.getVolume() == null || request.getVolume() == 0.0) {
            if (!isLoad) {
                this.loadWaybillPackageSizeInfo(packageSizeInfo);
                isLoad = true;
            }
            request.setVolume(packageSizeInfo.getVolume());
        }
        return isLoad;
    }

    /**
     * 重构长度参数
     *
     * @param request
     * @param packageSizeInfo
     * @param isLoad
     * @return
     */
    private void rebuildSizeParam(ThirdPartyOverrunRequest request, ThirdPartyOverrunRequest packageSizeInfo, boolean isLoad) {
        if (request.getLength() == 0.0 && request.getWidth() == 0.0 && request.getHeight() == 0.0) {
            if (!isLoad) {
                this.loadWaybillPackageSizeInfo(packageSizeInfo);
            }
            request.setLength(packageSizeInfo.getLength());
            request.setWidth(packageSizeInfo.getWidth());
            request.setHeight(packageSizeInfo.getHeight());
        }
    }

    /**
     * 加载运单中包裹的各项指标信息
     *
     * @param packageSizeInfo
     */
    private boolean loadWaybillPackageSizeInfo(ThirdPartyOverrunRequest packageSizeInfo) {
        String packageCode = packageSizeInfo.getPackageCode();
        BigWaybillDto waybillDto = waybillQueryManager.getReturnWaybillByOldWaybillCode(BusinessHelper.getWaybillCode(packageCode), true, true, true, true);
        if (waybillDto != null) {
            Goods goods = getGoods(packageCode, waybillDto.getGoodsList());
            Waybill waybill = waybillDto.getWaybill();
            Double againWeight = this.getAgainWeight(packageCode, waybillDto.getPackageList());
            if (againWeight == null || againWeight == 0.0) {
                if (goods != null) {
                    packageSizeInfo.setWeight(goods.getGoodWeight());
                }
            } else {
                packageSizeInfo.setWeight(againWeight);
            }

            if (goods != null) {
                packageSizeInfo.setVolume(goods.getGoodVolume());
            }
            String formula = waybill.getVolumeFormula();
            if (StringUtils.isNotEmpty(formula)) {
                String[] sizeInfo = formula.split("/*");
                if (sizeInfo.length == 3) {
                    Double[] sizeInfoOrder = getOrderArray(sizeInfo);
                    packageSizeInfo.setHeight(sizeInfoOrder[0]);
                    packageSizeInfo.setWidth(sizeInfoOrder[1]);
                    packageSizeInfo.setLength(sizeInfoOrder[2]);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 获取尺寸信息进行排序 按照由小到大 分别对应高 宽 长
     *
     * @param sizeInfo
     * @return
     */
    private Double[] getOrderArray(Object[] sizeInfo) {
        if (sizeInfo instanceof Double[]) {
            Arrays.sort(sizeInfo);
            return (Double[]) sizeInfo;
        } else {
            Double[] orderArray = new Double[sizeInfo.length];
            for (int i = 0, len = orderArray.length; i < len; i++) {
                if (sizeInfo[i] != null && NumberUtils.isNumber(sizeInfo[i].toString())) {
                    orderArray[i] = Double.valueOf(sizeInfo[i].toString());
                } else {
                    orderArray[i] = 0.0;
                }
            }
            Arrays.sort(orderArray);
            return orderArray;
        }
    }

    private Double getAgainWeight(String packageCode, List<DeliveryPackageD> packageDList) {
        if (packageDList != null && packageDList.size() > 0) {
            for (DeliveryPackageD deliveryPackageD : packageDList) {
                if (deliveryPackageD.getPackageBarcode().equals(packageCode)) {
                    return deliveryPackageD.getAgainWeight();
                }
            }
        }
        return null;
    }

    private Goods getGoods(String packageCode, List<Goods> goods) {
        if (goods != null && goods.size() > 0) {
            for (Goods goodsInfo : goods) {
                if (goodsInfo.getPackBarcode().equals(packageCode)) {
                    return goodsInfo;
                }
            }
        }
        return null;
    }

}
