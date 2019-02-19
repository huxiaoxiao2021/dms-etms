package com.jd.bluedragon.distribution.b2bRouter.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterDao;
import com.jd.bluedragon.distribution.b2bRouter.dao.B2BRouterNodeDao;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private BaseMajorManager baseSiteManager;

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
            //填充完整路径信息
            setFullLine(router);

            //b2b_router表中写入数据
            b2bRouterDao.addB2BRouter(router);
            //获取chain_id
            Integer chainId = router.getId();

            //组织routerNode列表
            List<B2BRouterNode> nodes = new ArrayList<B2BRouterNode>();
            String [] nodeCodeList = router.getSiteIdFullLine().split("-");
            String [] nodeNameList = router.getSiteNameFullLine().split("-");

            for (int i = 0; i < nodeCodeList.length - 1; i++) {
                //始发网点和中转网点的网点类型均使用始发网点的类型
                B2BRouterNode node = new B2BRouterNode();
                //设置操作人信息和操作时间
                node.setOperatorUserErp(router.getOperatorUserErp());
                node.setOperatorUserName(router.getOperatorUserName());
                node.setCreateTime(router.getCreateTime());
                node.setUpdateTime(router.getUpdateTime());
                node.setYn(router.getYn());

                node.setChainId(chainId);
                node.setOriginalSiteType(router.getOriginalSiteType());
                node.setOriginalSiteCode(Integer.parseInt(nodeCodeList[i]));
                node.setOriginalSiteName(nodeNameList[i]);

                //目的网点要的网点类型要进行区分
                if (i == nodeCodeList.length - 2) {
                    node.setDestinationSiteType(router.getDestinationSiteType());
                } else {
                    node.setDestinationSiteType(router.getOriginalSiteType());
                }
                node.setDestinationSiteCode(Integer.parseInt(nodeCodeList[i + 1]));
                node.setDestinationSiteName(nodeNameList[i + 1]);
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
     * @param router
     * @return 已经存在的路由的链id
     */
    public Integer isHasRouter(B2BRouter router){
        // 获取完整路线
        setFullLine(router);

        //根据完整链路查找符合条件的路由信息
        List<B2BRouter> routerList = b2bRouterDao.selectByFullLine(router.getSiteIdFullLine());
        if (routerList != null && routerList.size() > 0) {
            return routerList.get(0).getId();
        } else {
            return null;
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
     * @param router
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean updateRouter(B2BRouter router)throws Exception{
        try{
            if(router != null) {
                //获取完整路径信息
                setFullLine(router);

                //修改后做更新，需要先将原链路删除
                if(router.getId() != null){
                    deleteById(router);
                }

                //定位到这一条消路由
                List<B2BRouter> routerList = b2bRouterDao.selectByFullLine(router.getSiteIdFullLine());

                //表中没有该条数据，直接插入
                if (routerList == null || routerList.size() < 1) {
                    logger.error("b2b_router表中无" + router.getSiteNameFullLine() + "这条路径，无法更新，直接插入.");
                    addRouter(router);
                }

                //表中有重复数据，只保留最新的一条，其他的逻辑删除
                if(routerList != null && routerList.size()>0){
                    //更新第一条
                    B2BRouter routerTemp = routerList.get(0);
                    routerTemp.setOperatorUserErp(router.getOperatorUserErp());
                    routerTemp.setOperatorUserName(router.getOperatorUserName());
                    routerTemp.setUpdateTime(router.getUpdateTime());

                    b2bRouterDao.updateById(routerTemp);

                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("chainId",routerTemp.getId());
                    params.put("operatorUserErp",routerTemp.getOperatorUserErp());
                    params.put("operatorUserName",routerTemp.getOperatorUserName());
                    params.put("updateTime",routerTemp.getUpdateTime());

                    b2bRouterNodeDao.updateByChainId(params);
                    //逻辑删除其他的
                    for(int i= 1; i<routerList.size(); i++ ){
                        deleteById(router);
                    }
                }
            }
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean deleteById(B2BRouter router) throws Exception{
        if(router == null || router.getId() == null){
            throw new RuntimeException("删除路由信息失败，id为空.");
        }
        try{
            //根据id逻辑删除掉原来的路由信息
            b2bRouterDao.deleteById(router);

            Map<String,Object> params = new HashMap<String, Object>();
            params.put("chainId",router.getId());
            params.put("operatorUserErp",router.getOperatorUserErp());
            params.put("operatorUserName",router.getOperatorUserName());
            params.put("updateTime",router.getUpdateTime());
            params.put("yn",0);

            b2bRouterNodeDao.deleteRouterNodeByChainId(params);
        }catch (Exception e){
            logger.error("删除路由信息失败.",e);
            throw e;
        }
        return true;
    }

    /**
     * 获取当前路由节点可以到达的一个节点集合
     * @param originalSiteCode
     * @param destinationSiteCode
     * @param nextSiteCode
     * @return
     * @throws Exception
     */
    @Cache(key = "b2bRouterService.getNextCodes@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<B2BRouterNode> getNextCodes(Integer originalSiteCode, Integer destinationSiteCode, Integer nextSiteCode) throws Exception{
        //根据始发网点和目的网点确定chain_id
        //将request转换成map
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("originalSiteCode",originalSiteCode);
            params.put("destinationSiteCode",destinationSiteCode);
            params.put("nextSiteCode",nextSiteCode);
            return b2bRouterNodeDao.getNextNode(params);
        }catch (Exception e){
            logger.error("获取当前路由节点可以到达的一个节点失败.",e);
            throw e;
        }
    }

    /**
     * 根据始发网点和目的网点获取路由信息
     * @param originalSiteCode
     * @param destinationSiteCode
     * @return
     * @throws Exception
     */
    @Cache(key = "b2bRouterService.getB2BRouters@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<B2BRouter> getB2BRouters(Integer originalSiteCode,Integer destinationSiteCode) throws Exception{
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("originalSiteCode",originalSiteCode);
            params.put("destinationSiteCode",destinationSiteCode);
            return b2bRouterDao.queryByCondition(params);
        }catch (Exception e){
            logger.error("获取当前路由节点可以到达的一个节点失败.",e);
            throw e;
        }
    }

    /**
     * 根据b2b_router表中的主键id获取对应的记录
     * @param id
     * @return
     */
    public B2BRouter getRouterById(int id){
        return b2bRouterDao.getRouterById(id);
    }

    /**
     * 根据网点编码获取网点名称
     * @param code  网点编码
     * @return
     */
    @Cache(key = "b2bRouterServiceImpl.getB2BSiteNameByCode-@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public String getB2BSiteNameByCode(Integer code){
        try {
            BaseStaffSiteOrgDto result = baseSiteManager.getBaseSiteBySiteId(code);
            if (result != null) {
                logger.info("调基础资料接口获取的网点" + code + "的名称为：" + result.getSiteName());
                return result.getSiteName();
            }
        }catch (Exception e){
            logger.error("根据网点编码获取网点名称失败.",e);
        }
        return null;
    }

    /**
     * 批量录入路由信息
     * @param routers
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String handleRouterBatch(List<B2BRouter> routers){
        String errString = "";
        try {
            for (B2BRouter router : routers) {
                setFullLine(router);
                updateRouter(router);
            }
        }catch (Exception e){
            logger.error("执行批量录入B网路由信息失败.",e);
            errString = "执行批量录入B网路由信息失败.";
        }
        return errString;
    }


    /**
     * 校验导入功能中excel表中的数据是否正确
     * @param router
     * @return
     */
    public String verifyRouterImportParam(B2BRouter router) {
        String errorString = "";

        //校验始发网点Id、目的网点类型、目的网点ID为必填
        Integer originalSiteType = router.getOriginalSiteType();
        Integer originalSiteCode = router.getOriginalSiteCode();
        Integer destinationSiteCode = router.getDestinationSiteCode();
        Integer destinationSiteType = router.getDestinationSiteType();
        if(originalSiteCode != null && originalSiteCode>0){
            router.setOriginalSiteType(originalSiteType);
            String siteName = getB2BSiteNameByCode(originalSiteCode);
            if(StringHelper.isNotEmpty(siteName)){
                router.setOriginalSiteName(siteName);
            }else{
                errorString = "始发网点_id[" + originalSiteCode + "]不存在\n";
            }
        }

        if(destinationSiteCode != null && destinationSiteCode>0){
            String siteName = getB2BSiteNameByCode(destinationSiteCode);
            if(StringHelper.isNotEmpty(siteName)){
                router.setDestinationSiteName(siteName);
            }else{
                errorString += "目的网点_id[" + destinationSiteCode + "]不存在\n";
            }
        }

        //检验始发网点和目的网点不能相同
        if(originalSiteCode == destinationSiteCode){
            errorString += "始发网点_id[" + originalSiteCode + "]和目的网点_id[" +destinationSiteCode +"]不能相同\n";
        }

        //校验各级中转站点是否存在
        if(router.getTransferOneSiteCode()!= null && router.getTransferOneSiteCode()>0){
            String siteName = getB2BSiteNameByCode(router.getTransferOneSiteCode());
            if(StringHelper.isNotEmpty(siteName)){
                router.setTransferOneSiteName(siteName);
            }else{
                errorString += "中转网点1_id[" + router.getTransferOneSiteCode() + "]不存在\n";
            }
        }

        if(router.getTransferTwoSiteCode()!= null && router.getTransferTwoSiteCode() > 0){
            String siteName = getB2BSiteNameByCode(router.getTransferTwoSiteCode());
            if(StringHelper.isNotEmpty(siteName)){
                router.setTransferTwoSiteName(siteName);
            }else{
                errorString += "中转网点2_id[" + router.getTransferTwoSiteCode() + "]不存在\n";
            }
        }

        if(router.getTransferThreeSiteCode()!= null && router.getTransferThreeSiteCode() > 0){
            String siteName = getB2BSiteNameByCode(router.getTransferThreeSiteCode());
            if(StringHelper.isNotEmpty(siteName)){
                router.setTransferThreeSiteName(siteName);
            }else{
                errorString += "中转网点3_id[" + router.getTransferThreeSiteCode() + "]不存在\n";
            }
        }

        if(router.getTransferFourSiteCode()!= null && router.getTransferFourSiteCode()>0){
            String siteName = getB2BSiteNameByCode(router.getTransferFourSiteCode());
            if(StringHelper.isNotEmpty(siteName)){
                router.setTransferFourSiteName(siteName);
            }else{
                errorString += "中转网点4_id[" + router.getTransferFourSiteCode() + "]不存在\n";
            }
        }

        if(router.getTransferFiveSiteCode()!= null && router.getTransferFiveSiteCode()>0){
            String siteName = getB2BSiteNameByCode(router.getTransferFiveSiteCode());
            if(StringHelper.isNotEmpty(siteName)){
                router.setTransferFiveSiteName(siteName);
            }else{
                errorString += "中转网点5_id[" + router.getTransferFiveSiteCode() + "]不存在\n";
            }
        }
        return errorString;
    }

    /**
     * 校验新增路的路由信息是否正确
     * @param router
     * @return
     */
    public String verifyRouterAddParam(B2BRouter router){
        //校验始发网点Id、目的网点类型、目的网点ID为必填
        if(StringHelper.isEmpty(router.getOriginalSiteName())){
            return "始发网点不能为空";
        }

        if(StringHelper.isEmpty(router.getDestinationSiteName())){
            return "目的网点不能为空";
        }

        if(StringHelper.isNotEmpty(router.getOriginalSiteName())) {
            Integer code = router.getOriginalSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName) || !siteName.equals(router.getOriginalSiteName())){
                    return "始发网点[" + router.getOriginalSiteName() + "]不存在";
                }
                router.setOriginalSiteName(siteName);
            }else{
                return "始发网点[" + router.getOriginalSiteName() + "]不存在";
            }
        }

        if(StringHelper.isNotEmpty(router.getDestinationSiteName())) {
            Integer code = router.getDestinationSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName) || !siteName.equals(router.getDestinationSiteName())){
                    return "目的网点[" + router.getDestinationSiteName() + "]不存在";
                }
                router.setDestinationSiteName(siteName);
            }else{
                return "目的网点[" + router.getDestinationSiteName() + "]不存在";
            }
        }

        //检验始发网点和目的网点不能相同
        if(router.getOriginalSiteName().equals(router.getDestinationSiteName())){
            return "始发网点[" + router.getOriginalSiteName() + "]和目的网点[" +router.getDestinationSiteName() +"]不能相同";
        }

        //校验各级中转站点是否存在
        if(StringHelper.isNotEmpty(router.getTransferOneSiteName())) {
            Integer code = router.getTransferOneSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName) || !siteName.equals(router.getTransferOneSiteName())){
                    return "中转网点1[" + router.getTransferOneSiteName() + "]不存在";
                }
                router.setTransferOneSiteName(siteName);
            }else{
                return "中转网点1[" + router.getTransferOneSiteName() + "]不存在";
            }
        }

        if(StringHelper.isNotEmpty(router.getTransferTwoSiteName())) {
            Integer code = router.getTransferTwoSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName)){
                    return "中转网点2[" + router.getTransferTwoSiteName() + "]不存在";
                }
                router.setTransferTwoSiteName(siteName);
            }else{
                return "中转网点2[" + router.getTransferTwoSiteName() + "]不存在";
            }
        }

        if(StringHelper.isNotEmpty(router.getTransferThreeSiteName())) {
            Integer code = router.getTransferThreeSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName)){
                    return "中转网点3[" + router.getTransferThreeSiteName() + "]不存在";
                }
                router.setTransferThreeSiteName(siteName);
            }else{
                return "中转网点3[" + router.getTransferThreeSiteName() + "]不存在";
            }
        }

        if(StringHelper.isNotEmpty(router.getTransferFourSiteName())) {
            Integer code = router.getTransferFourSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName)){
                    return "中转网点4[" + router.getTransferFourSiteName() + "]不存在";
                }
                router.setTransferFourSiteName(siteName);
            }else{
                return "中转网点4[" + router.getTransferFourSiteName() + "]不存在";
            }
        }

        if(StringHelper.isNotEmpty(router.getTransferFiveSiteName())) {
            Integer code = router.getTransferFiveSiteCode();
            if(code!=null && code >0){
                String siteName = getB2BSiteNameByCode(code);
                if(StringHelper.isEmpty(siteName)){
                    return "中转网点5[" + router.getTransferFiveSiteName() + "]不存在";
                }
                router.setTransferFiveSiteName(siteName);
            }else{
                return "中转网点5[" + router.getTransferFiveSiteName() + "]不存在";
            }
        }
        return null;
    }

    /**
     * 组织完整路径
     * @param router
     */
    public void setFullLine(B2BRouter router){
        String siteIdFullLine = router.getOriginalSiteCode()+"-";
        String siteNameFullLine = router.getOriginalSiteName()+"-";

        if(router.getTransferOneSiteCode()!=null && router.getTransferOneSiteCode()>0){
            siteIdFullLine +=  router.getTransferOneSiteCode() + "-";
            siteNameFullLine += getB2BSiteNameByCode(router.getTransferOneSiteCode())+"-";
        }

        if(router.getTransferTwoSiteCode()!=null && router.getTransferTwoSiteCode()>0){
            siteIdFullLine +=  router.getTransferTwoSiteCode() + "-";
            siteNameFullLine += getB2BSiteNameByCode(router.getTransferTwoSiteCode())+"-";
        }

        if(router.getTransferThreeSiteCode()!=null && router.getTransferThreeSiteCode()>0){
            siteIdFullLine +=  router.getTransferThreeSiteCode() + "-";
            siteNameFullLine += getB2BSiteNameByCode(router.getTransferThreeSiteCode())+"-";
        }

        if(router.getTransferFourSiteCode()!=null && router.getTransferFourSiteCode() >0){
            siteIdFullLine +=  router.getTransferFourSiteCode() + "-";
            siteNameFullLine += getB2BSiteNameByCode(router.getTransferFourSiteCode())+"-";
        }

        if(router.getTransferFiveSiteCode()!=null && router.getTransferFiveSiteCode()>0){
            siteIdFullLine +=  router.getTransferFiveSiteCode() + "-";
            siteNameFullLine += getB2BSiteNameByCode(router.getTransferFiveSiteCode())+"-";
        }

        siteIdFullLine += router.getDestinationSiteCode();
        siteNameFullLine += getB2BSiteNameByCode(router.getDestinationSiteCode());

        router.setSiteIdFullLine(siteIdFullLine);
        router.setSiteNameFullLine(siteNameFullLine);
    }
}
