package com.jd.bluedragon.distribution.box.jsf;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxBindingDto;
import com.jd.bluedragon.distribution.box.domain.BoxRelationQ;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * @ClassName BoxRelationJsfService
 * @Description
 * @Author wyh
 * @Date 2020/12/23 17:51
 **/
public interface BoxRelationJsfService {

    /**
     * 分页查询箱号绑定记录
     * @param query
     * @return
     */
    InvokeResult<PagerResult<BoxBindingDto>> queryBindingData(BoxRelationQ query);

    /**
     * 查询总数
     * @param query
     * @return
     */
    InvokeResult<Integer> queryCount(BoxRelationQ query);

}
