package com.jd.bluedragon.distribution.rma.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.ql.dms.common.domain.PrintStatusEnum;

import java.util.List;


/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public interface RmaHandOverWaybillService {

    /**
     * 新增
     *
     * @param rmaHandoverWaybill
     */
    void add(RmaHandoverWaybill rmaHandoverWaybill);

    /**
     * 更新
     *
     * @param rmaHandoverWaybill
     */
    void update(RmaHandoverWaybill rmaHandoverWaybill);

    /**
     * 获取分页数据集合-不带有明细数据
     *
     * @param param
     * @param pager
     * @return
     */
    Pager<List<RmaHandoverWaybill>> getListWithoutDetail(RmaHandoverQueryParam param, Pager pager);

    /**
     * 根据Id查询
     *
     * @param id
     * @param needDetail
     * @return
     */
    RmaHandoverWaybill get(Long id, boolean needDetail);

    /**
     * 根据ID列表批量获取
     *
     * @param ids
     * @param needDetail
     * @return
     */
    Pager<List<RmaHandoverWaybill>> getList(List<Long> ids, boolean needDetail);

    /**
     * 根据参数获取最新操作数据
     *
     * @param waybillCode
     * @param createSiteCode
     * @param needDetail
     * @return
     */
    RmaHandoverWaybill getByParams(String waybillCode, Integer createSiteCode, boolean needDetail);

    /**
     * 根据运单号获取收货人地址
     *
     * @param waybillCode
     * @return
     */
    String getReceiverAddressByWaybillCode(String waybillCode);

    /**
     * 更新打印状态
     *
     * @param id
     * @param status
     */
    void updatePrintStatus(Long id, PrintStatusEnum status);

    /**
     * dmsWorkSendDetail报文消费增加
     *
     * @param sendDetail
     */
    void addConsumer(SendDetail sendDetail);
}
