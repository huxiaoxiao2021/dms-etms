package com.jd.bluedragon.distribution.station.gateway;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanForLoginRequest;
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
	 * 按条件分页查询-按操作人
	 * @param query
	 * @return
	 */
	JdCResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query);
	/**
	 * 查询用户最近一次签到数据
	 * @param query
	 * @return
	 */
	JdCResponse<UserSignRecordData> queryLastUserSignRecordData(UserSignQueryRequest query);
	/**
	 * 查询上岗码相关信息
	 * 	校验上岗码是否关联作业app功能码
	 * @param positionCode
	 * @return
	 */
	JdCResponse<PositionData> queryPositionData(String positionCode);

	/**
	 * 查询上岗码信息
	 * @param positionCode
	 * @return
	 */
	JdCResponse<PositionData> queryPositionInfo(String positionCode);
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
    /**
     * 按岗位签到-执行添加组员操作
     * @param signInRequest
     * @return
     */
    JdCResponse<UserSignRecordData> signInWithGroup(UserSignRequest signInRequest);
    /**
     * 按岗位签退-执行移除组员操作
     * @param signOutRequest
     * @return
     */
	JdCResponse<UserSignRecordData> signOutWithGroup(UserSignRequest signOutRequest);
    /**
     * 自动签到、签退-执行添加、移除组员操作
     * @param signInRequest
     * @return
     */
	JdCResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest);
	/**
	 * 作废签到记录
	 * @param userSignRequest
	 * @return
	 */
	JdCResponse<UserSignRecordData> deleteUserSignRecord(UserSignRequest userSignRequest);
	/**
	 * 查询最近一次-已签未退记录
	 * @param query
	 * @return
	 */
	JdCResponse<UserSignRecordData> queryLastUnSignOutRecordData(UserSignQueryRequest query);	
	/**
	 * 签到前校验
	 * @param userSignRequest
	 * @return
	 */
	JdCResponse<UserSignRecordData> checkBeforeSignIn(UserSignRequest userSignRequest);
	/**
	 * 登录-查询扫描用户信息
	 * @param scanRequest
	 * @return
	 */
	JdCResponse<ScanUserData> queryUserDataForLogin(ScanForLoginRequest scanRequest);
	/**
	 * 登录-查询扫岗位信息
	 * @param scanRequest
	 * @return
	 */
	JdCResponse<PositionData> queryPositionDataForLogin(ScanForLoginRequest scanRequest);	
}
