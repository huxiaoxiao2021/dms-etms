package com.jd.bluedragon.distribution.rma.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.RmaConsumerException;
import com.jd.bluedragon.distribution.rma.dao.RmaHandOverWaybillDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.PrintInfoParam;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverDetailService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SkuSn;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
        return null;
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
        return rmaHandoverWaybill.getReceiverAddress();
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
    public List<RmaHandoverPrint> getPrintInfo(List<Long> ids) {
        List<RmaHandoverWaybill> rmaHandoverWaybills = this.getList(ids, true);
        Map<String, RmaHandoverPrint> result = new HashMap<String, RmaHandoverPrint>();
        for (RmaHandoverWaybill handoverWaybill : rmaHandoverWaybills) {
            // 根据商家编号 + 收货地址作为判断是否属于同一RMA接货单的判断依据
            String key = handoverWaybill.getBusiId() + "-" + handoverWaybill.getReceiverAddress();
            RmaHandoverPrint printInfo = result.get(key);
            if (printInfo != null) {
                printInfo.getIds().add(handoverWaybill.getId());
                // 已存在则将明细添加
                List<RmaHandoverDetail> details = handoverWaybill.getRmaHandoverDetail();
                printInfo.getHandoverDetails().addAll(details);
                printInfo.setWaybillCount(printInfo.getWaybillCount() + 1);
                printInfo.setPackageCount(printInfo.getPackageCount() + 1);
                printInfo.setSpareCount(printInfo.getSpareCount() + details.size());
            } else {
                printInfo = this.buildPrintInfo(handoverWaybill);
                result.put(key, printInfo);
            }
        }
        if (result.size() > 0) {
            return new ArrayList<RmaHandoverPrint>(result.values());
        }
        return Collections.emptyList();
    }

    @Override
    public void addConsumer(SendDetail sendDetail) {
        String waybillCode = sendDetail.getWaybillCode();
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryWaybillE(true);
        choice.setQueryWaybillM(true);
        choice.setQueryGoodList(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
        if (baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
            Waybill waybill = baseEntity.getData().getWaybill();
            if (BusinessHelper.isRMA(waybill.getWaybillSign())) {
                RmaHandoverWaybill rmaHandoverWaybill = this.buildHandoverObject(waybill, baseEntity.getData().getGoodsList(), sendDetail);
                if (!this.add(rmaHandoverWaybill)) {
                    throw new RmaConsumerException("RMA消费发货明细MQ消息，执行新增入库时失败");
                }
            }
        } else {
            throw new RmaConsumerException("RMA消费发货明细MQ消息，根据运单号获取运单信息为空");
        }

    }

    private RmaHandoverWaybill buildHandoverObject(Waybill waybill, List<Goods> goodsList, SendDetail sendDetail) {
        RmaHandoverWaybill rmaHandOverWaybill = new RmaHandoverWaybill();
        this.buildSendDetailAttribute(rmaHandOverWaybill, sendDetail);
        this.buildWaybillAttribute(rmaHandOverWaybill, waybill);
        if (goodsList != null && !goodsList.isEmpty()) {
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
    private void buildSendDetailAttribute(RmaHandoverWaybill rmaHandOverWaybill, SendDetail sendDetail) {
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
        rmaHandOverWaybill.setSendDate(sendDetail.getOperateTime());
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
        /** 目的省ID */
        rmaHandOverWaybill.setTargetProvinceId(waybill.getProvinceId());
        /** 目的省 */
        rmaHandOverWaybill.setTargetProvinceName(waybill.getProvinceName());
        /** 目的城市ID */
        rmaHandOverWaybill.setTargetCityId(waybill.getCityId());
        /** 目的城市名称 */
        rmaHandOverWaybill.setTargetCityName(waybill.getCityName());
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

    private void buildGoodsDetailAttribute(RmaHandoverWaybill rmaHandOverWaybill, List<Goods> goodsList) {
        BaseEntity<List<SkuSn>> baseEntity = waybillQueryManager.getSkuSnListByOrderId(rmaHandOverWaybill.getWaybillCode());
        if (baseEntity.getData() != null && !baseEntity.getData().isEmpty()) {
            Map<String, String> skuSnMap = this.convertMap(baseEntity.getData());
            List<RmaHandoverDetail> detailList = new ArrayList<RmaHandoverDetail>(goodsList.size());
            for (Goods goods : goodsList) {
                RmaHandoverDetail detail = new RmaHandoverDetail();
                detail.setWaybillCode(goods.getWaybillCode());
                detail.setGoodCount(goods.getGoodCount());
                detail.setGoodName(goods.getGoodName());
                detail.setSkuCode(goods.getSku());
                /** 获取备件条码 CodeType为2 */
                detail.setSpareCode(skuSnMap.get(goods.getSku() + "-2"));
                /** 获取出库单号 CodeType为3 */
                detail.setOutboundOrderCode(skuSnMap.get(goods.getSku() + "-3"));
                detail.setExceptionRemark(goods.getRemark());
                detailList.add(detail);
            }
            rmaHandOverWaybill.setRmaHandoverDetail(detailList);
        }
    }

    private Map<String, String> convertMap(List<SkuSn> skuSnList) {
        Map<String, String> resultMap = new HashMap<String, String>(skuSnList.size());
        for (SkuSn skuSn : skuSnList) {
            resultMap.put(skuSn.getSkuCode() + "-" + skuSn.getCodeType(), skuSn.getSnCode());
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
