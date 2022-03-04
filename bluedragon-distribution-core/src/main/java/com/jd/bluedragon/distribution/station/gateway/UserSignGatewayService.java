package com.jd.bluedragon.distribution.station.gateway;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 人员签到表--Service接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignGatewayService {
	/**
	 * 签到
	 * @param signInRequest
	 * @return
	 */
	JdCResponse<Boolean> signInWithPosition(UserSignRequest signInRequest);
	/**
	 * 签退
	 * @param signOutRequest
	 * @return
	 */
	JdCResponse<Boolean> signOutWithPosition(UserSignRequest signOutRequest);
	/**
	 * 自动签到、签退
	 * @param userSignRequest
	 * @return
	 */
	JdCResponse<Boolean> signAuto(UserSignRequest userSignRequest);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<PageDto<UserSignRecordData>> querySignListWithPosition(UserSignQueryRequest query);
	/**
	 * 查询上岗码相关信息
	 * @param positionCode
	 * @return
	 */
	JdCResponse<PositionData> queryPositionData(String positionCode);
}
