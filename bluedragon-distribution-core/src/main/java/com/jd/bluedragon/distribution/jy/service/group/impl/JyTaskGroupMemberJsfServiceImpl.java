package com.jd.bluedragon.distribution.jy.service.group.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.api.group.JyTaskGroupMemberJsfService;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;

/**
 * 任务小组成员-jsf服务
 *
 * @author wyd
 */
@Service("jyTaskGroupMemberJsfService")
public class JyTaskGroupMemberJsfServiceImpl implements JyTaskGroupMemberJsfService {

    private static final Logger logger = LoggerFactory.getLogger(JyTaskGroupMemberJsfServiceImpl.class);

    @Autowired
    private JyTaskGroupMemberService jyTaskGroupMemberService;

	@Override
	public Result<List<JyTaskGroupMemberEntity>> queryMemberListByTaskId(JyTaskGroupMemberQuery query) {
		return jyTaskGroupMemberService.queryMemberListByTaskId(query);
	}
}
