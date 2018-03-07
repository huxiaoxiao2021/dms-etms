package com.jd.bluedragon.distribution.b2bRouter.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;

import java.util.List;

/**
 * Created by xumei3 on 2018/2/26.
 */
public interface B2BRouterService {
    /**
     * 校验该B网路由在配置表中是否已经存在
     * @param router
     * @return
     */
    public Integer isHasRouter(B2BRouter router);

    /**
     * 增加一条路由配置信息
     * @param router
     * @return
     */
    public boolean addRouter(B2BRouter router);

    /**
     * 导入时批量添加路由信息
     * @param routers
     * @return
     */
    public String handleRouterBatch(List<B2BRouter> routers);

    /**
     * 根据查询条件查询路由信息
     * @param b2bRouterRequest
     * @param pager
     * @return
     */
    public List<B2BRouter> queryByCondition(B2BRouterRequest b2bRouterRequest, Pager<List<B2BRouter>> pager) throws Exception;

    /**
     * 根据b2b_router表中的主键id获取对应的记录
     * @param id
     * @return
     */
    public B2BRouter getRouterById(int id);

    /**
     * 根据网点Code获取网点名称
     * @param code
     * @return
     */
    public String getB2BSiteNameByCode(Integer code);

    /**
     * 更新一条路由信息
     * @param router
     * @return
     */
    public Boolean updateRouter(B2BRouter router)throws Exception;

    /**
     * 删除一条路由信息（逻辑删除）
     * @param router
     * @return
     */
    public Boolean deleteById(B2BRouter router)throws Exception;

    /**
     * 获取当前路由节点可以到达的一个节点集合
     * @param originalSiteCode
     * @param destinationSiteCode
     * @param nextSiteCode
     * @return
     * @throws Exception
     */
    public List<B2BRouterNode> getNextCodes(Integer originalSiteCode, Integer destinationSiteCode, Integer nextSiteCode) throws Exception;

    /**
     * 根据始发网点和目的网点获取路由信息
     * @param originalSiteCode
     * @param destinationSiteCode
     * @return
     * @throws Exception
     */
    public List<B2BRouter> getB2BRouters(Integer originalSiteCode,Integer destinationSiteCode) throws Exception;

    /**
     * 校验导入router数据是否正确
     * @param router
     * @return
     */
    public String verifyRouterImportParam(B2BRouter router);


    /**
     * 校验新增的Router数据是否正确
     * @param router
     * @return
     */
    public String verifyRouterAddParam(B2BRouter router);

    /**
     * 组织完整路径
     * @param router
     */
    public void setFullLine(B2BRouter router);
}
