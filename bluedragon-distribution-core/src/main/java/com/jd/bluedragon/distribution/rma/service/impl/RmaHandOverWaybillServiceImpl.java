package com.jd.bluedragon.distribution.rma.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.dao.RmaHandOverWaybillDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.PrintInfoParam;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverDetailService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SkuSn;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
@Service
public class RmaHandOverWaybillServiceImpl implements RmaHandOverWaybillService {

    private final static Logger logger = LoggerFactory.getLogger(RmaHandOverWaybillServiceImpl.class);

    @Autowired
    private RmaHandOverWaybillDao rmaHandOverWaybillDao;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private RmaHandOverDetailService rmaHandOverDetailService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    /**
     * 缓存redis的key
     */
    private final static String REDIS_CACHE_KEY = "RMA-HANDOVER-TOKEN-";

    /**
     * 分隔符号
     */
    private final static String SEPARATOR = "-";

    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean add(RmaHandoverWaybill rmaHandoverWaybill) {
        if (rmaHandoverWaybill != null) {
            if (rmaHandOverWaybillDao.add(rmaHandoverWaybill) > 0) {
                List<RmaHandoverDetail> details = rmaHandoverWaybill.getRmaHandoverDetail();
                if (details != null && !details.isEmpty()) {
                    for (RmaHandoverDetail detail : details) {
                        detail.setHandoverWaybillId(rmaHandoverWaybill.getId());
                    }
                    rmaHandOverDetailService.batchAdd(details);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(RmaHandoverWaybill rmaHandoverWaybill) {
        if (rmaHandoverWaybill != null) {
            if (rmaHandOverWaybillDao.update(rmaHandoverWaybill) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Pager<List<RmaHandoverWaybill>> getListWithoutDetail(RmaHandoverQueryParam param, Pager<List<RmaHandoverWaybill>> pager) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("sendDateStart", param.getSendDateStart());
        parameter.put("sendDateEnd", param.getSendDateEnd());
        parameter.put("createSiteCode", param.getCreateSiteCode());
        parameter.put("waybillCode", param.getWaybillCode());
        Integer printStatus = param.getPrintStatus();
        if (printStatus != null) {
            parameter.put("printStatus", printStatus);
        }
        String receiverAddress = param.getReceiverAddress();
        if (StringHelper.isNotEmpty(receiverAddress)) {
            parameter.put("receiverAddress", receiverAddress);
        }

        int count = rmaHandOverWaybillDao.getCountByParams(parameter);
        if (count > 0) {
            pager.setTotalSize(count);
            pager.init();
            parameter.put("startIndex", pager.getStartIndex());
            parameter.put("pageSize", pager.getPageSize());
            //查询符合条件的记录
            List<RmaHandoverWaybill> list = rmaHandOverWaybillDao.getListByParams(parameter);
            pager.setData(list);
        } else {
            pager.setData(Collections.EMPTY_LIST);
        }
        return pager;
    }

    @Override
    public RmaHandoverWaybill get(Long id, boolean needDetail) {
        if (id != null && id > 0) {
            RmaHandoverWaybill rmaHandoverWaybill = rmaHandOverWaybillDao.getById(id);
            if (needDetail) {
                rmaHandoverWaybill.setRmaHandoverDetail(rmaHandOverDetailService.getListByHandoverWaybillId(rmaHandoverWaybill.getId()));
            }
            return rmaHandoverWaybill;
        }
        return null;
    }

    @Override
    public List<RmaHandoverWaybill> getList(List<Long> ids, boolean needDetail) {
        if (ids != null && !ids.isEmpty()) {
            List<RmaHandoverWaybill> rmaHandoverWaybills = rmaHandOverWaybillDao.getListByIds(ids);
            if (needDetail && !rmaHandoverWaybills.isEmpty()) {
                for (RmaHandoverWaybill rmaHandoverWaybill : rmaHandoverWaybills) {
                    rmaHandoverWaybill.setRmaHandoverDetail(rmaHandOverDetailService.getListByHandoverWaybillId(rmaHandoverWaybill.getId()));
                }
            }
            return rmaHandoverWaybills;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public RmaHandoverWaybill getByParams(String waybillCode, Integer createSiteCode, boolean needDetail) {
        Map<String, Object> parameter = new HashMap<String, Object>(2);
        parameter.put("waybillCode", waybillCode);
        parameter.put("createSiteCode", createSiteCode);
        List<RmaHandoverWaybill> rmaHandoverWaybillList = rmaHandOverWaybillDao.getListByParams(parameter);
        if (!rmaHandoverWaybillList.isEmpty()) {
            RmaHandoverWaybill rmaHandoverWaybill = rmaHandoverWaybillList.get(0);
            if (needDetail) {
                rmaHandoverWaybill.setRmaHandoverDetail(rmaHandOverDetailService.getListByHandoverWaybillId(rmaHandoverWaybill.getId()));
            }
            return rmaHandoverWaybill;
        }
        return null;
    }

    @Override
    public String getReceiverAddressByWaybillCode(String waybillCode, Integer createSiteCode) {
        RmaHandoverWaybill rmaHandoverWaybill = this.getByParams(waybillCode, createSiteCode, false);
        if (rmaHandoverWaybill != null) {
            return rmaHandoverWaybill.getReceiverAddress();
        }
        return null;
    }

    @Override
    public void updatePrintInfo(PrintInfoParam printInfoParam) {
        List<Long> ids = printInfoParam.getIds();
        PrintStatusEnum printStatus = printInfoParam.getPrintStatus();
        if (ids != null && ids.size() > 0 && printStatus != null) {
            Map<String, Object> parameter = new HashMap<String, Object>(5);
            parameter.put("ids", printInfoParam.getIds());
            parameter.put("printTime", printInfoParam.getPrintTime());
            parameter.put("printUserName", printInfoParam.getPrintUserName());
            parameter.put("printUserCode", printInfoParam.getPrintUserCode());
            parameter.put("printStatus", printInfoParam.getPrintStatus());
            rmaHandOverWaybillDao.updatePrintInfo(parameter);
        }
    }

    @Override
    public List<Integer> getTokenGroupByKey(List<Long> ids) {
        List<RmaHandoverWaybill> rmaHandoverWaybills = this.getList(ids, false);
        if (rmaHandoverWaybills.size() > 0) {
            Set<String> keySetSet = new HashSet<String>();
            Set<Integer> hashCodeKeySet = new HashSet<Integer>();
            for (RmaHandoverWaybill handoverWaybill : rmaHandoverWaybills) {
                // 获取判断是否属于同一RMA接货单的判断依据key值
                String key = this.getRmaHandoverKey(handoverWaybill);
                // 获取token
                Integer token = this.getHashCodeToken(keySetSet, hashCodeKeySet, key);
                redisClientCache.sAdd(REDIS_CACHE_KEY + token, String.valueOf(handoverWaybill.getId()));
            }
            if (hashCodeKeySet.size() > 0) {
                List<Integer> tokens = new ArrayList<Integer>(hashCodeKeySet);
                for (Integer token : tokens) {
                    redisClientCache.expire(REDIS_CACHE_KEY + token, 60, TimeUnit.SECONDS);
                }
                return tokens;
            }
        }
        return Collections.emptyList();
    }

    /**
     * 获取哈希Token
     *
     * @param hashCodeKey
     * @param key
     * @return
     */
    private Integer getHashCodeToken(Set<String> keySetSet, Set<Integer> hashCodeKey, String key) {
        if (keySetSet.add(key)) {
            while (!hashCodeKey.add(key.hashCode())) {
                //出现哈希碰撞 再哈希
                key = String.valueOf(key.hashCode());
            }
        }
        return key.hashCode();
    }

    @Override
    public RmaHandoverPrint getPrintObject(Integer token) {
        Set<String> idSet = redisClientCache.sMembers(REDIS_CACHE_KEY + token);
        redisClientCache.del(REDIS_CACHE_KEY + token);
        if (idSet != null && idSet.size() > 0) {
            List<String> idStrList = new ArrayList<String>(idSet);
            List<Long> ids = new ArrayList<Long>(idStrList.size());
            for (String id : idStrList) {
                ids.add(Long.valueOf(id));
            }
            List<RmaHandoverWaybill> rmaHandoverWaybills = this.getList(ids, true);
            RmaHandoverPrint printResult = this.buildPrintInfo(rmaHandoverWaybills.get(0));
            for (int i = 1, len = rmaHandoverWaybills.size(); i < len; i++) {
                printResult.getIds().add(rmaHandoverWaybills.get(i).getId());
                // 已存在则将明细添加
                List<RmaHandoverDetail> details = rmaHandoverWaybills.get(i).getRmaHandoverDetail();
                printResult.getHandoverDetails().addAll(details);
                printResult.setWaybillCount(printResult.getWaybillCount() + 1);
                printResult.setPackageCount(printResult.getPackageCount() + 1);
                printResult.setSpareCount(printResult.getSpareCount() + details.size());
            }
            return printResult;
        }
        return null;
    }

    /**
     * 获取判断是否属于同一RMA接货单的判断依据key值
     * 格式：收货人 + 分隔符 + 收货地址
     *
     * @param handoverWaybill
     * @return
     */
    private String getRmaHandoverKey(RmaHandoverWaybill handoverWaybill) {
        return handoverWaybill.getReceiver() + "-" + handoverWaybill.getReceiverAddress();
    }

    @JProfiler(jKey = "DMSCORE.RmaHandOverWaybillServiceImpl.buildAndStorage", mState = {JProEnum.TP})
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean buildAndStorage(SendDetailMessage sendDetail, Waybill waybill, List<Goods> goods) {
        boolean result = true;
        // 查询该运单号在该站点是否已经发货
        RmaHandoverWaybill history = this.getByParams(waybill.getWaybillCode(), sendDetail.getCreateSiteCode(), false);
        if (history == null) {
            result = this.add(this.buildHandoverObject(waybill, goods, sendDetail, true));
        } else {
            Date sendDateHistory = history.getSendDate();
            if (sendDateHistory != null && sendDateHistory.before(new Date(sendDetail.getOperateTime()))) {
                RmaHandoverWaybill current = this.buildHandoverObject(waybill, goods, sendDetail, false);
                current.setId(history.getId());
                result = this.update(current);
            }
        }
        return result;
    }

    /**
     * 构建RMA交接信息对象
     *
     * @param waybill
     * @param goodsList
     * @param sendDetail
     * @return
     */
    private RmaHandoverWaybill buildHandoverObject(Waybill waybill, List<Goods> goodsList, SendDetailMessage sendDetail, boolean needDetail) {
        RmaHandoverWaybill rmaHandOverWaybill = new RmaHandoverWaybill();
        this.buildSendDetailAttribute(rmaHandOverWaybill, sendDetail);
        this.buildWaybillAttribute(rmaHandOverWaybill, waybill);
        if (needDetail && goodsList != null && !goodsList.isEmpty()) {
            this.buildGoodsDetailAttribute(rmaHandOverWaybill, goodsList);
        }
        return rmaHandOverWaybill;
    }

    /**
     * 构建RMA交接信息发货明细属性
     *
     * @param rmaHandOverWaybill
     * @param sendDetail
     */
    private void buildSendDetailAttribute(RmaHandoverWaybill rmaHandOverWaybill, SendDetailMessage sendDetail) {
        /** 发货人编号 */
        rmaHandOverWaybill.setSendUserCode(sendDetail.getCreateUserCode());
        /** 发货人姓名 */
        rmaHandOverWaybill.setSendUserName(sendDetail.getCreateUser());
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByStaffIdNoCache(sendDetail.getCreateUserCode());
        if (userDto != null) {
            /** 发货人手机号 */
            rmaHandOverWaybill.setSendUserMobile(userDto.getPhone());
        }
        /** 操作站点编号 */
        rmaHandOverWaybill.setCreateSiteCode(sendDetail.getCreateSiteCode());
        BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
        if (siteDto != null) {
            /** 操作站点名称 */
            rmaHandOverWaybill.setCreateSiteName(siteDto.getSiteName());
            /** 操作站点所属省ID */
            rmaHandOverWaybill.setSendProvinceId(siteDto.getProvinceId());
            /** 操作站点所属省 */
            rmaHandOverWaybill.setSendProvinceName(siteDto.getProvinceName());
            /** 操作站点所属城市ID */
            rmaHandOverWaybill.setSendCityId(siteDto.getCityId());
            /** 操作站点所属城市 */
            rmaHandOverWaybill.setSendCityName(siteDto.getCityName());
        }
        /** 发货时间 */
        if (sendDetail.getOperateTime() != null) {
            rmaHandOverWaybill.setSendDate(new Date(sendDetail.getOperateTime()));
        }
    }

    /**
     * 构建RMA交接信息运单属性
     *
     * @param rmaHandOverWaybill
     * @param waybill
     */
    private void buildWaybillAttribute(RmaHandoverWaybill rmaHandOverWaybill, Waybill waybill) {
        /** 运单号 */
        rmaHandOverWaybill.setWaybillCode(waybill.getWaybillCode());
        /** 包裹数量 */
        rmaHandOverWaybill.setPackageCount(waybill.getGoodNumber());

        Integer provinceId = waybill.getProvinceId();
        /** 目的省ID */
        rmaHandOverWaybill.setTargetProvinceId(provinceId);
        /** 目的省 */
        rmaHandOverWaybill.setTargetProvinceName(this.getLocationName(waybill.getProvinceName(), provinceId));

        Integer cityId = waybill.getCityId();
        /** 目的城市ID */
        rmaHandOverWaybill.setTargetCityId(cityId);
        /** 目的城市名称 */
        rmaHandOverWaybill.setTargetCityName(this.getLocationName(waybill.getCityName(), cityId));
        /** 商家ID */
        rmaHandOverWaybill.setBusiId(waybill.getBusiId());
        /** 商家名称 */
        rmaHandOverWaybill.setBusiName(waybill.getBusiName());
        /** 收货人 */
        rmaHandOverWaybill.setReceiver(waybill.getReceiverName());
        /** 收货人ID */
        rmaHandOverWaybill.setReceiverId(waybill.getReceiverId());
        /** 收货人电话号 */
        rmaHandOverWaybill.setReceiverMobile(waybill.getReceiverMobile());
        /** 收货人地址 */
        rmaHandOverWaybill.setReceiverAddress(waybill.getReceiverAddress());
    }

    /**
     * 获取位置名称，若名称不为空则直接返回，为空则根据位置id调用基础资料获取名称返回
     *
     * @param name
     * @param id
     * @return
     */
    private String getLocationName(String name, Integer id) {
        if (StringUtils.isEmpty(name) && id != null) {
            Assort assort = baseService.getOneAssortById(id);
            if (assort != null) {
                return assort.getAssDis();
            }
        }
        return name;
    }

    /**
     * 构建商品明细属性信息
     *
     * @param rmaHandOverWaybill
     * @param goodsList
     */
    private void buildGoodsDetailAttribute(RmaHandoverWaybill rmaHandOverWaybill, List<Goods> goodsList) {
        Map<String, String> skuSnMap = null;
        BaseEntity<List<SkuSn>> baseEntity = waybillQueryManager.getSkuSnListByOrderId(rmaHandOverWaybill.getWaybillCode());
        if (baseEntity.getData() != null && !baseEntity.getData().isEmpty()) {
            skuSnMap = this.convertMap(baseEntity.getData());
        }
        List<RmaHandoverDetail> detailList = new ArrayList<RmaHandoverDetail>(goodsList.size());
        for (Goods goods : goodsList) {
            RmaHandoverDetail detail = new RmaHandoverDetail();
            /** 运单号 */
            detail.setWaybillCode(goods.getWaybillCode());
            /** 商品数量 */
            detail.setGoodCount(goods.getGoodCount());
            /** 商品名称 */
            detail.setGoodName(goods.getGoodName());
            /** 获取备件条码 */
            detail.setSpareCode(goods.getSku());
            if (skuSnMap != null) {
                /** 商品编码 69码*/
                detail.setSkuCode(skuSnMap.get("2" + SEPARATOR + goods.getSku()));
                /** 获取备件库出库单号 */
                detail.setOutboundOrderCode(skuSnMap.get("3" + SEPARATOR + goods.getSku()));
            }
            detailList.add(detail);
        }
        rmaHandOverWaybill.setRmaHandoverDetail(detailList);
    }

    private Map<String, String> convertMap(List<SkuSn> skuSnList) {
        Map<String, String> resultMap = new HashMap<String, String>(skuSnList.size());
        for (SkuSn skuSn : skuSnList) {
            // codeType为3 SnCode是备件库出库单号，codeType为2 SnCode是商品编号 69码
            resultMap.put(skuSn.getCodeType() + SEPARATOR + skuSn.getSkuCode(), skuSn.getSnCode());
        }
        return resultMap;
    }

    private RmaHandoverPrint buildPrintInfo(RmaHandoverWaybill handoverWaybill) {
        RmaHandoverPrint printInfo = new RmaHandoverPrint();
        List ids = new ArrayList<Long>();
        ids.add(handoverWaybill.getId());
        printInfo.setIds(ids);
        /** 发货城市 */
        printInfo.setSendCityName(handoverWaybill.getSendCityName());
        /** 发货场地 */
        printInfo.setCreateSiteName(handoverWaybill.getCreateSiteName());
        /** 目的城市，一级 + 二级 */
        printInfo.setTargetCityName(handoverWaybill.getTargetProvinceName() + handoverWaybill.getTargetCityName());
        /** 发货联系人 */
        printInfo.setSendUserName(handoverWaybill.getSendUserName());
        /** 发货联系电话 */
        printInfo.setSendUserMobile(handoverWaybill.getSendUserMobile());
        /** 商家名称 */
        printInfo.setBusiName(handoverWaybill.getBusiName());
        /** 收件人名称 */
        printInfo.setReceiver(handoverWaybill.getReceiver());
        /** 收件人电话 */
        printInfo.setReceiverMobile(handoverWaybill.getReceiverMobile());
        /** 收件人地址 */
        printInfo.setReceiverAddress(handoverWaybill.getReceiverAddress());
        /** 运单数量 */
        printInfo.setWaybillCount(1);
        /** 包裹数量 */
        printInfo.setPackageCount(1);
        /** 备件数量 */
        printInfo.setSpareCount(handoverWaybill.getRmaHandoverDetail().size());
        /** 交接明细 */
        printInfo.setHandoverDetails(handoverWaybill.getRmaHandoverDetail());
        /** 打印时间 */
        printInfo.setPrintDate(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDD));
        return printInfo;
    }

}
