package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: DmsBaseDictService
 * @Description: 数据字典--Service接口
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
public interface DmsBaseDictService extends Service<DmsBaseDict> {

    /**
     * 根据parentId和typeGroup查找分拣基础数据
     * @param parentId
     * @param typeGroup
     * @return
     */
    public List<DmsBaseDict> queryByParentIdAndTypeGroup(Integer parentId, Integer typeGroup);

    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
    public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition);
    /**
     * 根据parentId查找所有下级节点数据
     * @param parentId
     * @return
     */
    List<DmsBaseDict> queryByParentId(Integer parentId);
}
