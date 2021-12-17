package com.jd.bluedragon.core.base;


import com.jd.tms.jdi.dto.*;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 1:55 下午
 */
public interface JdiSelectWSManager {

    /**
     * 根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新
     *
     * @param simpleCode
     * @param transportCode
     * @return
     */
    CommonDto<String> checkTransportCode(String simpleCode, String transportCode);

    /**
     * 根据预约提货时间等条件获取委托书列表接口
     *
     * @param transBookBillQueryDto
     * @param pageDto
     * @return
     */
    CommonDto<PageDto<TransBookBillResultDto>> getTransBookBill(TransBookBillQueryDto transBookBillQueryDto, PageDto<TransBookBillQueryDto> pageDto);

    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     *
     * @param transWorkItemWsDto
     * @return
     */
    CommonDto<TransWorkItemWsDto> getVehicleNumberOrItemCodeByParam(TransWorkItemWsDto transWorkItemWsDto);

}
