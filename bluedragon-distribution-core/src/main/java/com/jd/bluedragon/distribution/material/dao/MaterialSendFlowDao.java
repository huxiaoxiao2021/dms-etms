package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.ql.dms.common.web.mvc.api.Dao;

public interface MaterialSendFlowDao extends Dao<DmsMaterialSendFlow> {

    int logicalDeleteSendFlowBySendCode(String sendCode, Long createSiteCode, String userErp, String userName);
}