package com.jd.bluedragon.distribution.jy.service.group;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupQuery;

/**
 * @ClassName: JyGroupService
 * @Description: 工作小组表--Service接口
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public interface JyGroupService {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	Result<Boolean> addGroupData(JyGroupEntity data);
	/**
	 * 导入数据
	 * @param dataList
	 * @return
	 */
	Result<JyGroupEntity> queryGroupByPosition(JyGroupQuery query);
	/**
	 * 查询小组信息
	 * @param groupCode
	 * @return
	 */
	JdCResponse<GroupMemberData> queryGroupData(String groupCode);
	/**
	 * 根据组编码查询小组信息
	 * @param groupCode
	 * @return
	 */
	JyGroupEntity queryGroupByGroupCode(String groupCode);
}
