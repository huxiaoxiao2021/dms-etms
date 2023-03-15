package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.vos.dto.SealCarDto;


public interface JySealVehicleService {

    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

    /**
     * 查询流向任务的相关封车数据
     * @param sealVehicleInfoReq
     * @return
     */
    InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);


    /**
     * 提交封车-干支封车
     *
     */
    InvokeResult sealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 传站封车
     *
     */
    InvokeResult czSealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 根据任务简码 获取任务详情
     * @return
     */
    InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);
    /**
     * 保存封车数据
     *
     */
	InvokeResult<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq);

    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    InvokeResult<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq request);


    /**
     * 校验运力编码的目的地是否一致
     * @return
     */
    InvokeResult<TransportResp> checkTransCode(CheckTransportReq reqcuest);

    /**
     * 取消封车 更新批次任务和封签列表
     */
    InvokeResult<Boolean> updateBoardStatusAndSealCode(SealCarDto sealCarCodeOfTms, String batchCode, String operateUserCode, String operateUserName);

    /**
     * 根据transWorkItemCode删除封签明细
     */
    InvokeResult<Boolean> deleteBySendVehicleBizId(String transWorkItemCode, String operateUserCode, String operateUserName);

    /**
     * 根据包裹|箱号查询板详情信息
     */
    InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request);

    /**将 带归属区号的车牌号 转换成 汉字开头的车牌号
     *  例：010A68665 -> 京A68665
     * @param carLicense
     * @return
     */
    String transformLicensePrefixToChinese(String carLicense);
}
