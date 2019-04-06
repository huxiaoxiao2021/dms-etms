package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 17:54
 */
public interface ReceiveWeightCheckService {

    /**
     * 新增一条记录
     * */
    public int insert(ReceiveWeightCheckResult receiveWeightCheckResult);

    /**
     * 按条件查询
     * */
    public PagerResult<ReceiveWeightCheckResult> queryByCondition(ReceiveWeightCheckCondition condition);

    /**
     * 根据条件查询数据条数
     * */
    public Integer queryNumByCondition(ReceiveWeightCheckCondition condition);

    /**
     * 整理导出数据
     * */
    public List<List<Object>> getExportData(ReceiveWeightCheckCondition condition);

    /**
     * 判断换单新单运单是否存
     * @param oldWaybillCode 老运单号
     * @param newWaybillCode 新运单号
     * @return
     */
    public InvokeResult<Integer> waybillExchangeCheckWeightAndVolume( String oldWaybillCode, String newWaybillCode);
}
