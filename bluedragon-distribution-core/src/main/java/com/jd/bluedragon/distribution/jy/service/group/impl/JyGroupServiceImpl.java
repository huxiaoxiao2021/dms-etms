package com.jd.bluedragon.distribution.jy.service.group.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.group.JyGroupDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupQuery;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;

/**
 * @ClassName: WorkStationServiceImpl
 * @Description: 工作小组--Service接口实现
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
@Service("jyGroupService")
public class JyGroupServiceImpl implements JyGroupService {

	@Autowired
	@Qualifier("jyGroupDao")
	private JyGroupDao jyGroupDao;
	
	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;
	
	@Autowired
	private IGenerateObjectId genObjectId;

	/**
	 * 插入一条数据
	 * @param data
	 * @return
	 */
	@Override
	public Result<Boolean> addGroupData(JyGroupEntity data) {
		Result<Boolean> result = checkAndFillBeforeAdd(data);
		if(!result.isSuccess()) {
			return result;
		}
		generateAndSetGroupCode(data);
		result.setData(jyGroupDao.insert(data) == 1);
		return result;
	}
	@Override
	public Result<JyGroupEntity> queryGroupByPosition(JyGroupQuery query) {
		Result<JyGroupEntity> result = new Result<JyGroupEntity>();
		result.toSuccess();
		result.setData(jyGroupDao.queryGroupByPosition(query));
		return result;
	}	
	private Result<Boolean> checkAndFillBeforeAdd(JyGroupEntity data) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		if(StringHelper.isEmpty(data.getPositionCode())) {
			result.toFail("岗位码不能为空！");
		}
		return result;
	}

	/**
	 * 生成并设置groupCode
	 * @param data
	 */
	private void generateAndSetGroupCode(JyGroupEntity data) {
		if(data != null) {
			data.setGroupCode(DmsConstants.CODE_PREFIX_JY_GROUP.concat(StringHelper.padZero(this.genObjectId.getObjectId(JyGroupEntity.class.getName()),11)));
		}
	}
	@Override
	public JdCResponse<GroupMemberData> queryGroupData(String groupCode) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		JyGroupEntity group = jyGroupDao.queryByGroupCode(groupCode);
		if(group != null) {
			GroupMemberData groupData = new GroupMemberData();
			groupData.setGroupCode(groupCode);
			groupData.setGroupMemberNum(jyGroupMemberService.queryGroupMemberNum(groupCode));
			result.setData(groupData);
		}else {
			result.toFail("无效的组编码！");
		}
		return result;
	}
	@Override
	public JyGroupEntity queryGroupByGroupCode(String groupCode) {
		return jyGroupDao.queryGroupByGroupCode(groupCode);
	}
}
