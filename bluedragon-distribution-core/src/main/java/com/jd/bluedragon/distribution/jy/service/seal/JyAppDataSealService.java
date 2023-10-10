package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealVo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealSendCode;

import java.util.List;

/**
 * @ClassName: JyAppDataSealService
 * @Description: 作业app-封车主页面数据表--Service接口
 * @author wuyoude
 * @date 2022年09月27日 15:38:11
 *
 */
public interface JyAppDataSealService {
	/**
	 * 加载保存的数据
	 * @param sendVehicleDetailBizId
	 * @return
	 */
	JyAppDataSealVo loadSavedPageData(String sendVehicleDetailBizId);
	/**
	 * 保存页面数据
	 * @param sealVehicleReq
	 * @return
	 */
	InvokeResult<Boolean> savePageData(SealVehicleReq sealVehicleReq);

	/**
	 * 保存批次信息
	 * @param sendCodes
	 * @return
	 */
	Boolean saveSendCodeList(List<JyAppDataSealSendCode> sendCodes);

	/**
	 * 当前任务是否存在暂存批次
	 * @param sendVehicleDetailBizId
	 * @return
	 */
	Boolean checkExistSaveData(String sendVehicleDetailBizId);

}
