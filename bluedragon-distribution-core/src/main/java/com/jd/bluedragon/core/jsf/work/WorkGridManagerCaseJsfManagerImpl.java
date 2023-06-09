package com.jd.bluedragon.core.jsf.work;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.work.WorkGridManagerCaseWithItem;
import com.jdl.basic.api.service.work.WorkGridManagerCaseJsfService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wuyoude
 *
 */
@Slf4j
@Service("workGridManagerCaseJsfManager")
public class WorkGridManagerCaseJsfManagerImpl implements WorkGridManagerCaseJsfManager {

    @Autowired
    private WorkGridManagerCaseJsfService workGridManagerCaseJsfService;
    
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridManagerCaseJsfManagerImpl.queryCaseListByTaskCode",mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public List<WorkGridManagerCaseWithItem> queryCaseWithItemListByTaskCode(String taskCode) {
		return workGridManagerCaseJsfService.queryCaseWithItemListByTaskCode(taskCode);
	}
}
