package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestTree;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;

import java.util.List;

/**
 * 区域批次目的地
 * <p>
 * Created by lixin39 on 2016/12/7.
 */
public interface AreaDestService {

    /**
     * 新增
     *
     * @param areaDest 区域批次目的地信息
     * @return
     */
    boolean add(AreaDest areaDest);

    /**
     * 批量新增
     *
     * @param areaDests
     * @return
     */
    boolean add(List<AreaDest> areaDests);

    /**
     * 新增或设置有效
     *
     * @param request
     * @param user
     * @param userCode
     * @return
     */
    boolean saveOrUpdate(AreaDestRequest request, String user, Integer userCode);

    /**
     * 根据id更新
     *
     * @param areaDest 区域批次目的地信息
     * @return
     */
    boolean update(AreaDest areaDest);

    /**
     * 根据id设置为无效
     *
     * @param id
     * @return
     */
    boolean disable(Integer id, String updateUser, Integer updateUserCode);

    /**
     * 根据参数设置区域批次目的地为无效
     *
     * @param createSiteCode   始发分拣中心ID
     * @param transferSiteCode 中转分拣中心ID
     * @param receiveSiteCode  目的地ID列表
     * @param updateUser       修改人erp
     * @param updateUserCode   修改人id
     * @return
     */
    boolean disable(Integer createSiteCode, Integer transferSiteCode, List<Integer> receiveSiteCode, String updateUser, Integer updateUserCode);

    /**
     * 根据id设置为有效
     *
     * @param id
     * @return
     */
    boolean enable(Integer id, String updateUser, Integer updateUserCode);

    /**
     * 根据参数设置区域批次目的地为无效
     *
     * @param createSiteCode   始发分拣中心ID
     * @param transferSiteCode 中转分拣中心ID
     * @param receiveSiteCode  目的地ID
     * @param updateUser       修改人erp
     * @param updateUserCode   修改人id
     * @return
     */
    boolean enable(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode, String updateUser, Integer updateUserCode);

    /**
     * 获取区域批次目的地信息
     *
     * @param createSiteCode   始发分拣中心ID
     * @param transferSiteCode 中转分拣中心ID
     * @param receiveSiteCode  目的地ID
     * @return
     */
    List<AreaDest> getList(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode);

    /**
     * 获取区域批次目的地的树形结构
     *
     * @param createSiteCode   始发分拣中心ID
     * @param transferSiteCode 中转分拣中心ID
     * @param receiveSiteCode  目的地ID
     * @return
     */
    List<AreaDestTree> getTree(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode);

}
