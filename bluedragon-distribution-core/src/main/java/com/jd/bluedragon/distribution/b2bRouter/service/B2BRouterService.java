package com.jd.bluedragon.distribution.b2bRouter.service;

import IceInternal.Ex;
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
    public boolean isHasRouter(B2BRouter router);

    /**
     * 增加一条路由配置信息
     * @param router
     * @return
     */
    public boolean addRouter(B2BRouter router);

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
     * 校验网点code和网点名称是否匹配
     * @param code
     * @param name
     * @return
     */
    public boolean B2BSiteNameVertify(Integer code, String name);

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
    public Boolean updateRotuer(B2BRouter router)throws Exception;

    /**
     * 删除一条路由信息（逻辑删除）
     * @param router
     * @return
     */
    public Boolean deleteById(B2BRouter router)throws Exception;

    /**
     * 获取当前路由节点可以到达的一个节点
     * @param router
     * @param currentRouterNode 操作网点的编码
     * @return
     * @throws Exception
     */
    public List<B2BRouterNode> getNextCode(B2BRouter router, B2BRouterNode currentRouterNode) throws Exception;

}
