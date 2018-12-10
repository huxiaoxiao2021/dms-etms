package com.jd.bluedragon.core.base;

import com.jd.ql.dms.report.domain.GoodsPrintDto;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月19日 18时:41分
 */
public interface GoodsPrintEsManager {
    boolean insertOrUpdate(GoodsPrintDto goodsPrintDto);

    GoodsPrintDto findGoodsPrintBySendCodeAndWaybillCode(String sendCode, String waybillCode);

    List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatus(String sendCode);

    List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatusOfPage(String sendCode, int page, int pageSize);

    Long findGoodsPrintBySendCodeAndStatusCount(String sendCode);
}
