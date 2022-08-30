package com.jd.bluedragon.core.base;


import com.jd.tms.jdi.dto.*;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 1:55 下午
 */
public interface JdiQueryWSManager {

    /**
     * 根据派车任务明细简码获取派车任务明细
     *
     * @param simpleCode
     * @return
     */
    CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode);

    /**
     * 根据派车明细编码获取派车任务明细
     * @param var1
     * @return
     */
    TransWorkItemDto getTransWorkItemsDtoByItemCode(String var1);

    /**
     * 根据条件查派车单
     * @param transWorkCode
     * @param option
     * @return
     * @see <see>https://cf.jd.com/pages/viewpage.action?pageId=309509770</see>
     */
    BigTransWorkDto queryTransWorkByChoice(String transWorkCode, BigQueryOption option);

    /**
     * 根据条件查派车单
     * @param transWorkCode
     * @return
     */
    TransWorkBillDto queryTransWork(String transWorkCode);

    /**
     * 根据车牌号后四位 检索派车单号
     * @param param
     * @return
     */
    List<String> getCarNoByVehicleFuzzy(TransWorkFuzzyQueryParam  param);
}
