package com.jd.bluedragon.distribution.material.dao.impl;

import com.jd.bluedragon.distribution.material.dao.MaterialSendFlowDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MaterialSendFlowDaoImpl
 * @Description
 * @Author wyh
 * @Date 2020/2/21 15:42
 **/
public class MaterialSendFlowDaoImpl extends BaseDao<DmsMaterialSendFlow> implements MaterialSendFlowDao {

    @Override
    public int logicalDeleteSendFlowBySendCode(String sendCode, Long createSiteCode, String userErp, String userName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendCode", sendCode);
        paramMap.put("createSiteCode", createSiteCode);
        paramMap.put("updateUserErp", userErp);
        paramMap.put("updateUserName", userName);
        return sqlSession.update(this.nameSpace + ".logicalDeleteSendFlowBySendCode", paramMap);
    }
}
