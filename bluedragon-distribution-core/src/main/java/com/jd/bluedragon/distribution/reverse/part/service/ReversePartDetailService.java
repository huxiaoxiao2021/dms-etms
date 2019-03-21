package com.jd.bluedragon.distribution.reverse.part.service;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: ReversePartDetailService
 * @Description: 半退明细表--Service接口
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
public interface ReversePartDetailService extends Service<ReversePartDetail> {

    /**
     * 获取累计发货记录
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    List<ReversePartDetail> queryAllSendPack(String waybillCode, String createSiteCode);

    /**
     * 获取未发货记录
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    List<String> queryNoSendPack(String waybillCode, String createSiteCode);

    int queryByParamCount(ReversePartDetailCondition reversePartDetailCondition);

    /**
     *
     * 组装查询条件
     *
     * 1.如果有批次号根据批次号获取已发货的运单号
     * @param reversePartDetailCondition
     */
    boolean makeQuery(ReversePartDetailCondition reversePartDetailCondition);


    /**
     * 批量插入半退明细
     * @param list
     * @return
     */
    boolean batchInsert(List<ReversePartDetail> list);

    /**
     * 获取导出数据
     * @param reversePartDetailCondition
     * @return
     */
    List<List<Object>> getExportData(ReversePartDetailCondition reversePartDetailCondition);

    /**
     * 仓储收货 更新收货时间
     * @param reversePartDetail
     * @return
     */
    int updateReceiveTime(ReversePartDetail reversePartDetail);


    /**
     * 取消发货
     * 同步发货数据
     * @param tSendM
     */
    boolean cancelPartSend(SendM tSendM);

}
