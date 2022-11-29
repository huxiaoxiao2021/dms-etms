package com.jd.bluedragon.distribution.jy.api.group;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;

/**
 * 任务小组成员对外jsf服务
 *
 * @author wyd
 * @date 2022/7/6 6:13 PM
 */
public interface JyTaskGroupMemberJsfService {

    /**
     * 根据taskId查询人员列表
     * @param query
     * @return
     */
    Result<List<JyTaskGroupMemberEntity>> queryMemberListByTaskId(JyTaskGroupMemberQuery query);
}
