package com.jd.bluedragon.distribution.rma.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.dao.RmaHandOverWaybillDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.PrintInfoParam;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverDetailService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.utils.DateHelper;
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
    private RmaHandOverDetailService rmaHandOverDetailService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean add(RmaHandoverWaybill rmaHandoverWaybill) {
        if (rmaHandoverWaybill != null) {
            if (rmaHandOverWaybillDao.add(rmaHandoverWaybill) > 0) {
                List<RmaHandoverDetail> details = rmaHandoverWaybill.getRmaHandoverDetail();
                if (details != null && !details.isEmpty()) {
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
        Integer printStatus = param.getPrintStatus();
        if (printStatus != null) {
            parameter.put("isPrinted", PrintStatusEnum.getEnum(printStatus));
        }
        parameter.put("createSiteCode", param.getCreateSiteCode());
        parameter.put("waybillCode", param.getWaybillCode());
        parameter.put("receiverAddress", param.getReceiverAddress());
        int count = rmaHandOverWaybillDao.getCountByParams(parameter);
        if (count > 0) {
            pager.setTotalSize(count);
            pager.init();
            parameter.put("startIndex", pager.getStartIndex());
            parameter.put("pageSize", pager.getPageSize());
            //查询符合条件的记录
            List<RmaHandoverWaybill> list = rmaHandOverWaybillDao.getListByParams(parameter);
            pager.setData(list);
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
            Map<String, Object> parameter = new HashMap<String, Object>();
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

    private RmaHandoverPrint buildPrintInfo(RmaHandoverWaybill handoverWaybill) {
        RmaHandoverPrint printInfo = new RmaHandoverPrint();
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
