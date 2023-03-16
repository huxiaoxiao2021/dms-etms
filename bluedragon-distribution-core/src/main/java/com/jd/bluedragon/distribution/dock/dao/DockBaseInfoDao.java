package com.jd.bluedragon.distribution.dock.dao;


import com.jd.bluedragon.distribution.dock.domain.DockBaseInfoPo;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.dao
 * @ClassName: DockBaseInfoDao
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/23 13:54
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DockBaseInfoDao extends BaseDao<DockBaseInfoPo> {

    /**
     * 根据场地和dockCode 查询dockInfo信息
     * @param dockBaseInfoPo
     * @return
     */
    public DockBaseInfoPo findByDockCode(DockBaseInfoPo dockBaseInfoPo) {
        return sqlSession.selectOne(this.nameSpace.concat(".findByDockCode"), dockBaseInfoPo);
    }

    public Boolean LogitechDeleteById(DockBaseInfoPo dockBaseInfoPo) {
        return Objects.equals(sqlSession.update(this.nameSpace.concat(".LogitechDeleteById"), dockBaseInfoPo), 1) ;
    }

    public List<DockBaseInfoPo> listAllDockInfoBySiteCode(Integer siteCode) {
        return sqlSession.selectList(this.nameSpace.concat(".listAllDockInfoBySiteCode"), siteCode);
    }

    public List<String> findAllDockCodeBySiteCode(Integer siteCode) {
        return sqlSession.selectList(this.nameSpace.concat(".findAllDockCodeBySiteCode"), siteCode);
    }


}
