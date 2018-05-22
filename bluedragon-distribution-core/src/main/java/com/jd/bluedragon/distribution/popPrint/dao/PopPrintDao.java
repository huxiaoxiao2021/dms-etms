package com.jd.bluedragon.distribution.popPrint.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午08:43:23
 * <p/>
 * 类说明
 */
public class PopPrintDao extends BaseDao<PopPrint> {

    public static final String namespace = PopPrintDao.class.getName();

    /**
     * 根据运单号查询打印记录
     *
     * @param waybillCode
     * @return
     */
    public PopPrint findByWaybillCode(String waybillCode) {
        Object obj = this.getSqlSession().selectOne(namespace + ".findByWaybillCode", waybillCode);
        PopPrint popPrint = (obj == null) ? null : (PopPrint) obj;
        return popPrint;
    }
    public PopPrint findByWaybillCodeAndPackageCode(PopPrint popPrint) {
        Object obj = this.getSqlSession().selectOne(namespace + ".findByWaybillCodeAndPackageCode", popPrint);
        PopPrint popPrint1 = (obj == null) ? null : (PopPrint) obj;
        return popPrint1;
    }

    public List<PopPrint> findSitePrintDetail(Map<String, Object> map) {
        Object o = this.getSqlSession().selectList(namespace + ".findSitePrintDetail", map);
        List<PopPrint> lstPopPrint=(List<PopPrint>)o;
        return  lstPopPrint;
    }

    public Integer findSitePrintDetailCount(Map<String, Object> map) {
        Object obj = this.getSqlSession().selectOne(namespace + ".findSitePrintDetailCount", map);
        int totalCount = (Integer)((obj == null) ? 0 : obj);
        return totalCount;
    }


    @SuppressWarnings("unchecked")
    public List<PopPrint> findAllByWaybillCode(String waybillCode) {
        Object obj = this.getSqlSession().selectList(namespace + ".findAllByWaybillCode", waybillCode);
        List<PopPrint> popPrintList = (obj == null) ? null : (List<PopPrint>) obj;
        return popPrintList;
    }

    /**
     * 按条数查询需要补全收货数据
     *
     * @param paramMap
     * @return List<PopPrint>
     */
    @SuppressWarnings("unchecked")
    public List<PopPrint> findLimitListNoReceive(Map<String, Object> paramMap) {
        Object obj = this.getSqlSession().selectList(namespace + ".findLimitListNoReceive", paramMap);
        List<PopPrint> popPrints = (List<PopPrint>) ((obj == null) ? null : obj);
        return popPrints;
    }

    /**
     * 增加POP打印记录
     *
     * @param popPrint
     * @return
     */
    public int add(PopPrint popPrint) {
        return this.getSqlSession().insert(namespace + ".add", popPrint);
    }

    /**
     * 根据运单号、操作人更新打印日志
     *
     * @param popPrint
     * @return
     */
    public int updateByWaybillCode(PopPrint popPrint) {
        return this.getSqlSession().update(namespace + ".updateByWaybillCode", popPrint);
    }

    public int updateByWaybillOrPack(PopPrint popPrint) {
        return this.getSqlSession().update(namespace + ".updateByWaybillOrPack", popPrint);
    }

}
