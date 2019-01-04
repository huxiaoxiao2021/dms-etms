package com.jd.bluedragon.distribution.rma.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.PrintInfoParam;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;

import java.util.List;
import java.util.Map;


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
    boolean add(RmaHandoverWaybill rmaHandoverWaybill);

    /**
     * 更新
     *
     * @param rmaHandoverWaybill
     */
    boolean update(RmaHandoverWaybill rmaHandoverWaybill);

    /**
     * 获取分页数据集合-不带有明细数据
     *
     * @param param
     * @param pager
     * @return
     */
    Pager<List<RmaHandoverWaybill>> getListWithoutDetail(RmaHandoverQueryParam param, Pager<List<RmaHandoverWaybill>> pager);

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
    List<RmaHandoverWaybill> getList(List<Long> ids, boolean needDetail);

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
     * @param createSiteCode
     * @return
     */
    String getReceiverAddressByWaybillCode(String waybillCode, Integer createSiteCode);

    /**
     * 更新打印信息
     *
     * @param printInfoParam
     */
    void updatePrintInfo(PrintInfoParam printInfoParam);

    /**
     * 获取打印数据接口
     *
     * @param ids
     * @return
     */
    List<RmaHandoverPrint> getPrintInfo(List<Long> ids);

    /**
     * 获取打印数据接口
     *
     * @param ids
     * @return
     */
    Map<String, RmaHandoverPrint> getPrintInfoMap(List<Long> ids);

    /**
     * 构建逻辑RMA数据对象及数据存储
     *
     * @param sendDetail
     * @param waybill
     * @param goods
     * @return
     */
    boolean buildAndStorage(SendDetailMessage sendDetail, Waybill waybill, List<Goods> goods);

}
