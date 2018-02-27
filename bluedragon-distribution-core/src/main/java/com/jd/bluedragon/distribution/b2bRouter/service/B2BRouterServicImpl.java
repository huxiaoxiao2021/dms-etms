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

    public boolean isHasRouter(B2BRouter router){
        //从b2b_router表中查出始发和目的对应的记录
        List<B2BRouter> routerList = b2bRouterDao.selectFullLine(router);
        if(routerList.contains(router.getSiteIdFullLine())){
            return true;
        }else{
            return false;
        }
    }

    //Todo 加个事务
    public Integer addRouter(B2BRouter router){
        b2bRouterDao.addB2BRouter(router);
        //获得chainId
        //组织routerNode
        //分割fullLine列表
        String [] fullLineIdNodes = router.getSiteIdFullLine().split("-");
        String [] fullLineNameNodes = router.getSiteNameFullLine().split("-");

        List<B2BRouterNode> nodes = new ArrayList<B2BRouterNode>();

        for(int i= 0; i<fullLineIdNodes.length - 1; i++){
            B2BRouterNode node = new B2BRouterNode();
            node.setChainId(1);
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
}
