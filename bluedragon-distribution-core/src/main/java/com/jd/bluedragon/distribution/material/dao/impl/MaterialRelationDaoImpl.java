package com.jd.bluedragon.distribution.material.dao.impl;

import com.jd.bluedragon.distribution.material.dao.MaterialRelationDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MaterialRelationDaoImpl
 * @Description
 * @Author wyh
 * @Date 2020/2/21 15:21
 **/
public class MaterialRelationDaoImpl extends BaseDao<DmsMaterialRelation> implements MaterialRelationDao {

    @Override
    public int batchInsertOnDuplicate(List<DmsMaterialRelation> relations) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("list", relations);
        return sqlSession.insert(this.nameSpace + ".batchInsertOnDuplicate", paramMap);
    }

    @Override
    public List<DmsMaterialRelation> listRelationsByReceiveCode(String receiveCode) {
        return sqlSession.selectList(this.nameSpace + ".listRelationsByReceiveCode", receiveCode);
    }

    @Override
    public int deleteByReceiveCode(String receiveCode) {
        return sqlSession.delete(this.nameSpace + ".deleteByReceiveCode", receiveCode);
    }

    @Override
    public PagerResult<RecycleMaterialScanVO> queryReceiveAndSend(RecycleMaterialScanQuery query) {

        String statementName = "queryReceiveAndSend";
        PagerResult<RecycleMaterialScanVO> pagerResult = new PagerResult<>();
        int total = this.sqlSession.selectOne(this.nameSpace + ".pageNum_" + statementName, query);
        if (total>0) {
            pagerResult.setTotal(total);
            pagerResult.setRows(this.sqlSession.<RecycleMaterialScanVO>selectList(this.nameSpace + "." + statementName, query));
        }
        else {
            pagerResult.setRows(new ArrayList<RecycleMaterialScanVO>());
        }
        return pagerResult;
    }
}
