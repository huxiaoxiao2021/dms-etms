package com.jd.bluedragon.distribution.fBarCode.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;

public class FBarCodeDao extends BaseDao<FBarCode> {

    public static final String namespace = FBarCodeDao.class.getName();

    public Integer print(FBarCode fBarCode) {
        return super.getSqlSession().update(FBarCodeDao.namespace + ".print", fBarCode);
    }

    public Integer reprint(FBarCode fBarCode) {
        return super.getSqlSession().update(FBarCodeDao.namespace + ".reprint", fBarCode);
    }

    public FBarCode findFBarCodeByCode(String code) {
        return (FBarCode) super.getSqlSession().selectOne(FBarCodeDao.namespace + ".findFBarCodeByCode", code);
    }

    public FBarCode findFBarCodeByFBarCodeCode(FBarCode fBarCode) {
        return (FBarCode) super.getSqlSession().selectOne(FBarCodeDao.namespace + ".findFBarCodeByFBarCodeCode", fBarCode);
    }

    @SuppressWarnings("unchecked")
    public List<FBarCode> findFBarCodeesBySite(FBarCode fBarCode) {
        return super.getSqlSession().selectList(FBarCodeDao.namespace + ".findFBarCodeesBySite", fBarCode);
    }

    @SuppressWarnings("unchecked")
    public List<FBarCode> findFBarCodees(FBarCode fBarCode) {
        return super.getSqlSession().selectList(FBarCodeDao.namespace + ".findFBarCodees", fBarCode);
    }

    public Integer updateStatusByCodes(FBarCode fBarCode) {
        return super.getSqlSession().update(FBarCodeDao.namespace + ".updateStatusByCodes", fBarCode);
    }

    public Integer batchUpdateStatus(FBarCode fBarCode) {
        return super.getSqlSession().update(FBarCodeDao.namespace + ".batchUpdateStatus", fBarCode);
    }

}
