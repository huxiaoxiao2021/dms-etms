package com.jd.bluedragon.distribution.material.dao.impl;

import com.jd.bluedragon.distribution.material.dao.MaterialReceiveDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MaterialReceiveDaoImpl
 * @Description
 * @Author wyh
 * @Date 2020/2/21 15:40
 **/
public class MaterialReceiveDaoImpl extends BaseDao<DmsMaterialReceive> implements MaterialReceiveDao {

    @Override
    public int batchInsertOnDuplicate(List<DmsMaterialReceive> list) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("list", list);
        return this.sqlSession.insert(this.nameSpace + ".batchInsertOnDuplicate", paramMap);
    }

    @Override
    public int deleteByReceiveCode(String receiveCode, Long createSiteCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("receiveCode", receiveCode);
        paramMap.put("createSiteCode", createSiteCode);
        return this.sqlSession.delete(this.nameSpace + ".deleteByReceiveCode", paramMap);
    }
}
