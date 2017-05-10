package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.utils.RouteType;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;

/**
 * 发货路线关系
 * <p>
 * Created by lixin39 on 2016/12/7.
 */
public interface AreaDestService {

    /**
     * 新增
     *
     * @param areaDest
     * @return
     */
    boolean add(AreaDest areaDest);

    /**
     * 批量新增
     *
     * @param areaDests
     * @return
     */
    Integer addBatch(List<AreaDest> areaDests);

    /**
     * 批量新增
     *
     * @param request
     * @param user
     * @param userCode
     * @return
     */
    Integer addBatch(AreaDestRequest request, String user, Integer userCode) throws Exception;

    /**
     * 根据id更新
     *
     * @param areaDest
     * @return
     */
    boolean update(AreaDest areaDest);

    /**
     * 根据方案编号设置发货路线关系为无效
     *
     * @param planId
     * @return
     */
    boolean disable(Integer planId, String updateUser, Integer updateUserCode);

    /**
     * 根据参数设置发货路线关系为无效
     *
     * @param request
     * @param updateUser
     * @param updateUserCode
     * @return
     */
    boolean disable(AreaDestRequest request, String updateUser, Integer updateUserCode);

    /**
     * 根据id设置为有效
     *
     * @param id
     * @return
     */
    boolean enable(Integer id, String updateUser, Integer updateUserCode);

    /**
     * 根据方案编号、当前分拣中心、目的站点获取方案信息
     *
     * @param planId
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    AreaDest get(Integer planId, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 根据方案编号、线路类型获取龙门架发货路线关系
     *
     * @param planId 方案编号
     * @param type   线路类型
     * @param pager  分页
     * @return
     */
    List<AreaDest> getList(Integer planId, RouteType type, Pager pager);

    /**
     * 根据方案编号、线路类型获取龙门架发货路线关系
     *
     * @param planId 方案编号
     * @param type   线路类型
     * @return
     */
    List<AreaDest> getList(Integer planId, RouteType type);

    /**
     * 根据方案编号、始发分拣中心编号、目的站点编号获取发货线路关系
     *
     * @param planId          方案编号
     * @param createSiteCode  始发分拣中心编号
     * @param receiveSiteCode 目的站点编号
     * @return
     */
    List<AreaDest> getList(Integer planId, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 根据方案编号、线路类型获取龙门架发货路线关系数量
     *
     * @param planId
     * @param type
     * @return
     */
    Integer getCount(Integer planId, RouteType type);

    /**
     * 根据参数获取龙门架发货路线关系数量
     *
     * @param planId
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    Integer getCount(Integer planId, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * Excel导入
     *
     * @param sheets
     * @param request
     * @param userName
     * @param userCode
     * @throws Exception
     */
    Map<RouteType, Integer> importForExcel(Map<RouteType, Sheet> sheets, AreaDestRequest request, String userName, Integer userCode) throws Exception;

}
