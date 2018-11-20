package com.jd.bluedragon.distribution.goodsPrint.service;

import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月16日 16时:22分
 */
public interface GoodsPrintService {
    JdResponse<List<GoodsPrintDto>> query(GoodsPrintDto goodsPrintDto);
    List<List<Object>> export(GoodsPrintDto goodsPrintDto);

    /**
     * 如果redis中读到了 key 说明已经往es写过了  后面就不重复写了
     *
     * @param key
     * @return
     */
    public boolean getWaybillFromEsOperator(String key);

    /**
     * 写缓存 如果往es里写过，就不重复写了 同一批发货，同一批运单可能很多包裹，所以减少重复操作
     *
     * @param key
     * @return
     */
    public boolean setWaybillFromEsOperator(String key);
}
