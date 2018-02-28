package com.jd.bluedragon.distribution.b2bRouter.service;

import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterNodeDao;
import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterDao;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2018/2/26.
 */

@Service("b2bRouterService")
public class B2BRouterServicImpl implements B2BRouterService{

    @Autowired
    private B2BRouterDao b2bRouterDao;

    @Autowired
    private B2BRouterNodeDao b2bRouterNodeDao;

    //Todo 加个事务
    public Integer addRouter(B2BRouter router){
        b2bRouterDao.addB2BRouter(router);
        Integer chainId = router.getId();

        //组织routerNode
        //分割fullLine列表
        String [] fullLineIdNodes = router.getSiteIdFullLine().split("-");
        String [] fullLineNameNodes = router.getSiteNameFullLine().split("-");

        List<B2BRouterNode> nodes = new ArrayList<B2BRouterNode>();

        for(int i= 0; i<fullLineIdNodes.length - 1; i++){
            B2BRouterNode node = new B2BRouterNode();
            node.setChainId(chainId);
            node.setOriginalSiteType(router.getOriginalSiteType());
            node.setOriginalSiteCode(Integer.parseInt(fullLineIdNodes[i]));
            node.setOriginalSiteName(fullLineNameNodes[i]);

            if( i == fullLineIdNodes.length-1) {
                node.setDestinationSiteType(router.getOriginalSiteType());
            }else{
                node.setDestinationSiteType(router.getDestinationSiteType());
            }
            node.setDestinationSiteCode(Integer.parseInt(fullLineIdNodes[i]));
            node.setDestinationSiteName(fullLineNameNodes[i+1]);

            nodes.add(node);

        }
        b2bRouterNodeDao.addB2BRouterNodes(nodes);

        return 1;
    }

    /**
     * 删除一条路由
     * 加事务
     */
    public boolean deleteRouterById(B2BRouter router){
        b2bRouterDao.deleteById(router);
        b2bRouterNodeDao.deleteRouterNodeByChainId(router.getId());

        return true;
    }

    //加事务
    public boolean updateRouterById(B2BRouter router){
        deleteRouterById(router);
        addRouter(router);

        return true;
    }

    public List<B2BRouter> getAllRouters(){
        //查询所有 有效的路由信息
        List<B2BRouter> routers = b2bRouterDao.selectAllRouter();

        return routers;
    }

    public List<B2BRouter> getRoutersByCondition(B2BRouter router){
        //查询特定始发、目的对应的路由信息
        List<B2BRouter> routers  = b2bRouterDao.selectFullLineBySiteCode(router);
        return routers;
    }

    public List<B2BRouterNode> getNextNode(B2BRouterNode node){
        return b2bRouterNodeDao.getNextNode(node);
    }

    public boolean isHasRouter(B2BRouter router){
        //从b2b_router表中查出始发和目的对应的记录
        List<String> routerList = b2bRouterDao.selectFullLineId(router);
        if(routerList.contains(router.getSiteIdFullLine())){
            return true;
        }else{
            return false;
        }
    }
}
