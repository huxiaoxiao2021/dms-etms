package com.jd.bluedragon.distribution.b2bRouter.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterNodeDao;
import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterDao;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import com.jd.bluedragon.utils.ObjectMapHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xumei3 on 2018/2/26.
 */

@Service("b2bRouterService")
public class  B2BRouterServicImpl implements B2BRouterService{

    private static final Logger logger = Logger.getLogger(B2BRouterServicImpl.class);

    @Autowired
    private B2BRouterDao b2bRouterDao;

    @Autowired
    private B2BRouterNodeDao b2bRouterNodeDao;

    /**
     * 向数据表中插入数据
     * 插入逻辑：
     * 向b2b_router中写入数据
     * 向b2b_router_chain中写入数据（以b2b_route表的自增主键-id作为b2b_router_chain表中的chain_id)
     * @param router
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean addRouter(B2BRouter router){
        try {
            //为防止网点的code值与name不一致，重新根据code值获取一遍
            String[] fullLineIdNodes = router.getSiteIdFullLine().split("-");
            String siteNameFullLine = "";
            for (String nodeCode : fullLineIdNodes) {
                siteNameFullLine += getB2BSiteNameByCode(Integer.parseInt(nodeCode)) + "-";
            }
            siteNameFullLine = siteNameFullLine.substring(0, siteNameFullLine.length() - 1);

            if (!siteNameFullLine.equals(router.getSiteNameFullLine())) {
                logger.error("B网路由参数中的名称路线和解析出的名称路线不一致,参数中id路线：" + router.getSiteIdFullLine() +
                        ";参数中的名称路线：" + router.getSiteNameFullLine() + ";解析出的名称路线：" + siteNameFullLine);
                router.setSiteNameFullLine(siteNameFullLine);
            }

            //b2b_rotue表中写入数据
            b2bRouterDao.addB2BRouter(router);
            //获取chain_id
            Integer chainId = router.getId();

            //组织routerNode列表
            List<B2BRouterNode> nodes = new ArrayList<B2BRouterNode>();
            String[] fullLineNameNodes = router.getSiteNameFullLine().split("-");

            for (int i = 0; i < fullLineIdNodes.length - 1; i++) {
                //始发网点和中转网点的网点类型均使用始发网点的类型（快运）
                B2BRouterNode node = new B2BRouterNode();
                node.setChainId(chainId);
                node.setOriginalSiteType(router.getOriginalSiteType());
                node.setOriginalSiteCode(Integer.parseInt(fullLineIdNodes[i]));
                node.setOriginalSiteName(fullLineNameNodes[i]);

                //目的网点要的网点类型要进行区分
                if (i == fullLineIdNodes.length - 1) {
                    node.setDestinationSiteType(router.getOriginalSiteType());
                } else {
                    node.setDestinationSiteType(router.getDestinationSiteType());
                }
                node.setDestinationSiteCode(Integer.parseInt(fullLineIdNodes[i + 1]));
                node.setDestinationSiteName(fullLineNameNodes[i + 1]);
                nodes.add(node);
            }
            //写入b2b_router表
            b2bRouterNodeDao.addB2BRouterNodes(nodes);
        }catch (Exception e){
            logger.error("向数据表中写入B网路由配置失败.",e);
            return false;
        }
        return true;
    }

    /**
     * 校验该B网路由在配置表中是否已经存在
     * 校验逻辑：
     * 从b2b_router表中根据始发和目的查找出所有符合条件的siteIdFullLine，看查询结果是否已经包含
     * @param router
     * @return
     */
    public boolean isHasRouter(B2BRouter router){
        List<String> routerList = b2bRouterDao.selectFullLineId(router);
        if (routerList != null && routerList.size() > 0 && routerList.contains(router.getSiteIdFullLine())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据查询条件查询路由信息
     * @param b2bRouterRequest
     * @param pager
     * @return
     */
    public List<B2BRouter> queryByCondition(B2BRouterRequest b2bRouterRequest,Pager<List<B2BRouter>> pager) throws Exception{
        //将request转换成map
        Map<String, Object> params = ObjectMapHelper.makeObject2Map(b2bRouterRequest);
        List<B2BRouter> list = null;
        try {
            //查询符合条件的记录条数
            int count = b2bRouterDao.countByCondition(params);

            //设置分页对象
            if (pager == null) {
                pager = new Pager<List<B2BRouter>>();
            }

            if (count > 0) {
                pager.setTotalSize(count);
                pager.init();
                b2bRouterRequest.setStartIndex(pager.getStartIndex());
                b2bRouterRequest.setEndIndex(pager.getEndIndex());

                params = ObjectMapHelper.makeObject2Map(b2bRouterRequest);
                params.put("pageSize", pager.getPageSize());

                //查询符合条件的记录
                list = b2bRouterDao.queryByCondition(params);
            }
        }catch (Exception e){
            logger.error("根据查询条件获取路由信息失败.",e);
            throw e;
        }
        return list;
    }

    /**
     * 更新一条路由信息
     * 更新逻辑：
     * 先把原来的删除掉（逻辑删除）
     * 再新增一条新的
     * @param router
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean updateRotuer(B2BRouter router)throws Exception{
        if(router == null || router.getId() == null){
            throw new RuntimeException("更新路由信息失败，id为空.");
        }
        try{
            //根据id逻辑删除掉原来的路由信息
            deleteById(router);
            //再重新插入一条新的
            addRouter(router);
        }catch (Exception e){
            logger.error("更新路由信息失败.",e);
            throw e;
        }
        return true;
    }

    /**
     * 删除一条路由信息（逻辑删除）
     * @param router
     * @return
     */
    public Boolean deleteById(B2BRouter router) throws Exception{
        if(router == null || router.getId() == null){
            throw new RuntimeException("删除路由信息失败，id为空.");
        }
        try{
            //根据id逻辑删除掉原来的路由信息
            b2bRouterDao.deleteById(router);
            //// TODO: 2018/2/28 需要进行优化
            b2bRouterNodeDao.deleteRouterNodeByChainId(router.getId());
        }catch (Exception e){
            logger.error("删除路由信息失败.",e);
            throw e;
        }
        return true;
    }

    /**
     * 获取当前路由节点可以到达的一个节点
     * @param router
     * @param currentRouterNode 操作网点的编码
     * @return
     * @throws Exception
     */
    public List<B2BRouterNode> getNextCode(B2BRouter router, B2BRouterNode currentRouterNode) throws Exception{
        //根据始发网点和目的网点确定chain_id
        //将request转换成map
        List<B2BRouterNode> result = new ArrayList<B2BRouterNode>();
        try {
            Map<String, Object> params = ObjectMapHelper.makeObject2Map(router);
            List<B2BRouter> routerList = b2bRouterDao.queryByCondition(params);
            for (B2BRouter b2bRouter : routerList) {
                B2BRouterNode node = new B2BRouterNode();
                node.setChainId(b2bRouter.getId());
                node.setOriginalSiteType(currentRouterNode.getOriginalSiteType());
                node.setOriginalSiteCode(currentRouterNode.getOriginalSiteCode());
                result.addAll(b2bRouterNodeDao.getNextNode(node));
            }
        }catch (Exception e){
            logger.error("获取当前路由节点可以到达的一个节点失败.",e);
            throw e;
        }
        return result;
    }
    /**
     * 根据b2b_router表中的主键id获取对应的记录
     * @param id
     * @return
     */
    public B2BRouter getRouterById(int id){
        return b2bRouterDao.getRouterById(id);
    }


    public boolean B2BSiteNameVertify(Integer id, String name){
        return true;
    }

    public String getB2BSiteNameByCode(Integer code){
        return "name:"+code;
    }
}
