package com.jd.bluedragon.distribution.material.dao.impl;

import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MaterialSendDaoImpl
 * @Description
 * @Author wyh
 * @Date 2020/2/21 15:42
 **/
public class MaterialSendDaoImpl extends BaseDao<DmsMaterialSend> implements MaterialSendDao {

    @Override
    public int batchInsertOnDuplicate(List<DmsMaterialSend> list) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("list", list);
        return sqlSession.insert(this.nameSpace + ".batchInsertOnDuplicate", paramMap);
    }

    @Override
    public int deleteBySendCode(String sendCode, Long createSiteCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendCode", sendCode);
        paramMap.put("createSiteCode", createSiteCode);
        return sqlSession.delete(this.nameSpace + ".deleteBySendCode", paramMap);
    }

    @Override
    public int logicalDeleteBatchSendBySendCode(String sendCode, Long createSiteCode, String userErp, String userName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendCode", sendCode);
        paramMap.put("createSiteCode", createSiteCode);
        paramMap.put("updateUserErp", userErp);
        paramMap.put("updateUserName", userName);
        return sqlSession.update(this.nameSpace + ".logicalDeleteBatchSendBySendCode", paramMap);
    }

    @Override
    public List<DmsMaterialSend> listBySendCode(String sendCode, Long createSiteCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendCode", sendCode);
        paramMap.put("createSiteCode", createSiteCode);
        return sqlSession.selectList(this.nameSpace + ".listBySendCode", paramMap);
    }

    @Override
    public int countBySendCode(String sendCode, Long createSiteCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendCode", sendCode);
        paramMap.put("createSiteCode", createSiteCode);
        return sqlSession.selectOne(this.nameSpace + ".countBySendCode", paramMap);
    }
}
