package com.jd.bluedragon.utils;


import com.jd.ldop.business.api.dto.Page;

import java.util.List;

/**
 * liuchunhe1
 * 用于读取数据
 */
public interface ExcelHelper<T> {

    List selectList(T t,Integer offSet);
}
