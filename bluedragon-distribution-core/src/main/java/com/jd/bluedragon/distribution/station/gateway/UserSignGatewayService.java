package com.jd.bluedragon.distribution.station.gateway;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanUserData;
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
	JdCResponse<UserSignRecordData> signInWithPosition(UserSignRequest signInRequest);
	/**
	 * 签退
	 * @param signOutRequest
	 * @return
	 */
	JdCResponse<UserSignRecordData> signOutWithPosition(UserSignRequest signOutRequest);
	/**
	 * 自动签到、签退
	 * @param userSignRequest
	 * @return
	 */
	JdCResponse<UserSignRecordData> signAuto(UserSignRequest userSignRequest);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<PageDto<UserSignRecordData>> querySignListWithPosition(UserSignQueryRequest query);
	/**
	 * 查询用户最近一次签到数据
	 * @param query
	 * @return
	 */
	JdCResponse<UserSignRecordData> queryLastUserSignRecordData(UserSignQueryRequest query);
	/**
	 * 查询上岗码相关信息
	 * @param positionCode
	 * @return
	 */
	JdCResponse<PositionData> queryPositionData(String positionCode);
	/**
	 * 查询扫描用户信息
	 * @param scanUserCode
	 * @return
	 */
	JdCResponse<ScanUserData> queryScanUserData(String scanUserCode);
	/**
	 * 登录-查询扫描用户信息
	 * @param scanUserCode
	 * @return
	 */
	JdCResponse<ScanUserData> queryScanUserDataForLogin(String scanUserCode);
}
