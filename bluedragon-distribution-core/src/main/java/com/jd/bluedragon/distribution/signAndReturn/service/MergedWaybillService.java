package com.jd.bluedragon.distribution.signAndReturn.service;

import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName: 123
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/29 18:18
 */
public interface MergedWaybillService {

    /**
     * 批量增加
     * @param mergedWaybillList
     * @return
     */
    int bathAdd(List<MergedWaybill> mergedWaybillList);

    /**
     * 通过主键id获取旧运单号集合
     * @param id
     * @return
     */
    List<MergedWaybill> getListBySignReturnPrintMId(Long id);

    /**
     * 通过运单号获取旧运单号集合
     * */
    List<MergedWaybill> getListByWaybillCode(SignReturnCondition condition);

    /**
     * 通过旧单号获得返单信息
     * */
    PagerResult<SignReturnPrintM> getSignReturnByConditon(SignReturnCondition condition);
}
