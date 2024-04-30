package com.jd.bluedragon.core.base;

import com.jdl.print.dto.data.PrintDataResult;
import com.jdl.print.dto.data.QueryPrintDataDto;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description: 云打印服务接口包装
 */
public interface CloudPrintDataServiceManager {

    /**
     * 获取打印数据
     * @param queryPrintDataDto
     * @return 打印数据
     */
    PrintDataResult innerPlainText(QueryPrintDataDto queryPrintDataDto);
}
