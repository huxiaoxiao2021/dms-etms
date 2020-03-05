package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetail;
import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetailStatusEnum;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库明细业务接口
 * @author: liuduo8
 * @create: 2019-12-19 17:00
 **/
public interface ReverseStockInDetailService extends Service<ReverseStockInDetail> {

    /**
     * 初始化入库单明细数据
     *
     * 初始化状态
     * 初始化外部单号 外部传入已外部为准
     * @param reverseStockInDetail
     * @return
     */
    boolean initReverseStockInDetail(ReverseStockInDetail reverseStockInDetail);

    /**
     * 批量初始化入库单明细数据
     *
     * 初始化状态
     * 初始化外部单号 外部传入已外部为准
     * @param reverseStockInDetail
     * @return
     */
    boolean initBatchReverseStockInDetail(List<ReverseStockInDetail> reverseStockInDetail);

    /**
     * 生产外部单号
     * @param key
     * @return
     */
    String genOneExternalCode(String key);

    /**
     * 批量生产外部单号
     * 理论上没有最大限制，请使用者自行评估
     *
     * 默认redis生成器生产，如果异常则返回UUID
     *
     * @param key 生产KEY - 此处采用外部单号类型
     * @return
     */
    List<String> genBatchExternalCode(String key, int count);


    /**
     * 更新入库明细状态
     * @param reverseStockInDetail
     * @param reverseStockInDetailStatusEnum
     * @return
     */
    boolean updateStatus(ReverseStockInDetail reverseStockInDetail, ReverseStockInDetailStatusEnum reverseStockInDetailStatusEnum);


    /**
     * 根据运单号和类型获取数据
     * 按创建时间倒序
     * 注意此接口不返回已取消数据
     * @param reverseStockInDetail
     * @return
     */
    List<ReverseStockInDetail> findByWaybillCodeAndType(ReverseStockInDetail reverseStockInDetail);

}
