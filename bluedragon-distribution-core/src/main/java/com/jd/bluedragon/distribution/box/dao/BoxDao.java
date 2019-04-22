package com.jd.bluedragon.distribution.box.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.box.domain.Box;

public class BoxDao extends BaseDao<Box> {

    public static final String namespace = BoxDao.class.getName();

    public Integer reprint(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".reprint", box);
    } 
    
    public Box findBoxByCode(String code) {
        return (Box) super.getSqlSession().selectOne(BoxDao.namespace + ".findBoxByCode", code);
    }

    public Box findBoxByBoxCode(Box box) {
        return (Box) super.getSqlSession().selectOne(BoxDao.namespace + ".findBoxByBoxCode", box);
    }

    @SuppressWarnings("unchecked")
    public List<Box> findBoxesBySite(Box box) {
        return super.getSqlSession().selectList(BoxDao.namespace + ".findBoxesBySite", box);
    }

    @SuppressWarnings("unchecked")
    /*public List<Box> findBoxes(Box box) {
        return super.getSqlSession().selectList(BoxDao.namespace + ".findBoxes", box);
    }*/

    public Integer updateStatusByCodes(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".updateStatusByCodes", box);
    }
    public Integer updateVolumeByCode(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".updateVolumeByCode", box);
    }

    public Integer batchUpdateStatus(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".batchUpdateStatus", box);
    }

    /*根据boxCode 更新create_site_name，receive_site_name 用于还原因jproxy故障造成的箱号站点乱码 add by luyue*/
    public int updateMessySiteNameByBoxCode(Box box){
        return super.getSqlSession().update(BoxDao.namespace + ".updateMessySiteNameByBoxCode", box);
    }

}
