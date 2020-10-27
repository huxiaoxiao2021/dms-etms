package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.waybill.dao.WaybillCacheDao;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaybillCacheServiceImpl implements WaybillCacheService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCacheDao waybillCacheDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillService waybillService;

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillCacheServiceImpl.getFromCache", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillCache getFromCache(String waybillCode) {
        if (!WaybillUtil.isWaybillCode(waybillCode)) {
            this.log.warn("WaybillServiceImpl --> getByWaybillCodeCommon：传入运单号参数错误");
            return null;
        }
        WaybillCache waybillCache = null;
        try {
            waybillCache = waybillCacheDao.findByWaybillCode(waybillCode);
            boolean isInvalid = false;
            if (waybillCache != null) {
                isInvalid = BusinessHelper.isInvalidCacheWaybill(waybillCache);
            }

            if (waybillCache == null || isInvalid) {
                if (waybillCache == null) {
                    this.log.info("运单号为[" + waybillCode + "]的缓存数据为空，调用运单WSS接口");
                }
                if (isInvalid) {
                    this.log.info("运单号为[" + waybillCode + "]缓存数据不全，调用主缓存或者WSS服务查询");
                }
                // 调用运单接口
                this.log.info("运单号为： " + waybillCode + "调用WSS接口获取运单信息");

                BigWaybillDto bigWaybillDto = waybillService.getWaybill(waybillCode, false);
                waybillCache = this.convertWaybillWS(bigWaybillDto, true);
                return waybillCache;
            }

        } catch (Exception e) {
            log.error("getFromCache fail!" + waybillCode, e);
        }
        return waybillCache;
    }

    @Override
    public String getRouterByWaybillCode(String waybillCode) {
        String router = null;
        try {
            router = waybillCacheDao.getRouterByWaybillCode(waybillCode);
        } catch (Exception e) {
            log.error("获取路由信息失败，运单号{}", waybillCode, e);
        }
        return router;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillCacheServiceImpl.getNoCache", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillCache getNoCache(String waybillCode) {
        WaybillCache waybillCache = null;
        try {
            BigWaybillDto bigWaybillDto = waybillService.getWaybill(waybillCode, false);
            waybillCache = this.convertWaybillWS(bigWaybillDto, true);

        }catch(Exception e){
            log.error("getNoCache fail!" + waybillCode, e);
        }
        return waybillCache;
    }

    /**
     * 转换运单基本信息
     *
     * @param
     * @return
     */
    private WaybillCache convertWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName) {
        if (bigWaybillDto == null) {
            this.log.debug("转换运单基本信息 --> 原始运单数据集bigWaybillDto为空");
            return null;
        }
        Waybill waybillWS = bigWaybillDto.getWaybill();
        if (waybillWS == null) {
            this.log.debug("转换运单基本信息 --> 原始运单数据集waybillWS为空");
            return null;
        }
        WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
        if (manageDomain == null) {
            this.log.debug("转换运单基本信息 --> 原始运单数据集manageDomain为空");
            return null;
        }
        WaybillCache waybillCache = new WaybillCache();
        waybillCache.setWaybillCode(waybillWS.getWaybillCode());
        waybillCache.setPopSupId(waybillWS.getConsignerId());
        waybillCache.setPopSupName(waybillWS.getConsigner());

        // 设置站点
        waybillCache.setSiteCode(waybillWS.getOldSiteId());
        if (isSetName) {
            dealWaybillSiteName(waybillCache);
        }

        waybillCache.setPaymentType(waybillWS.getPayment());
        waybillCache.setQuantity(waybillWS.getGoodNumber());
        waybillCache.setWeight(waybillWS.getGoodWeight());
        waybillCache.setAddress(waybillWS.getReceiverAddress());
        waybillCache.setOrgId(waybillWS.getArriveAreaId());
        waybillCache.setSendPay(waybillWS.getSendPay());
        waybillCache.setType(waybillWS.getWaybillType());
        waybillCache.setTransferStationId(waybillWS.getTransferStationId());
        waybillCache.setCrossCode(waybillWS.getSlideCode());
        waybillCache.setDistributeStoreId(waybillWS.getDistributeStoreId());
        waybillCache.setBusiId(waybillWS.getBusiId());
        waybillCache.setDistributeType(waybillWS.getDistributeType());
        waybillCache.setWaybillSign(waybillWS.getWaybillSign());
        waybillCache.setDistributeStoreName(waybillWS.getDistributeStoreName());
        waybillCache.setBusiName(waybillWS.getBusiName());
        waybillCache.setRoadCode(waybillWS.getRoadCode());//added by wuzuxiang 2017-4-28 09:35:08
        waybillCache.setAgainWeight(waybillWS.getAgainWeight());//added by tangchunqing 2018年4月24日
        waybillCache.setSpareColumn2(waybillWS.getSpareColumn2());//added by tangchunqing 2018年4月24日
        waybillCache.setFreight(waybillWS.getFreight());//added by shipeilin 2018年12月19日
        return waybillCache;
    }

    private void dealWaybillSiteName(WaybillCache waybillCache) {
        Integer siteCode = waybillCache.getSiteCode();
        if (siteCode != null) {
            Site site = siteService.get(siteCode);
            if (site != null) {
                waybillCache.setSiteName(site.getName());
            } else {
                this.log.info("查找不到站点[" + siteCode + "]相关信息");
            }
        }
    }
}
