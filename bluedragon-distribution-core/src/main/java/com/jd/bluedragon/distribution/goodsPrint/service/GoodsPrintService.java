package com.jd.bluedragon.distribution.goodsPrint.service;

import com.jd.ql.dms.report.domain.GoodsPrintDto;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月16日 16时:22分
 */
public interface GoodsPrintService {
    List<GoodsPrintDto> query(GoodsPrintDto goodsPrintDto);
    List<List<Object>> export(GoodsPrintDto goodsPrintDto);
}
