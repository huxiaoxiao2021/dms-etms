package com.jd.bluedragon.distribution.send.router;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 发货容器数据访问路由层
 * Created by wangtingwei on 2016/9/26.
 */
public class SendMRouter extends SendMDao {


    @Autowired
    private KvIndexDao kvIndexDao;

    private static final Log LOGGER= LogFactory.getLog(SendMRouter.class);

    /**
     * 新增发货容器数据，将将箱号及创建站点插入索引表
     * @param namespace
     * @param entity
     * @return
     */
    @Override
    public Integer add(String namespace, SendM entity) {

        //新增SEND_M增加超索引
        KvIndex index=new KvIndex();
        index.setKeyword(entity.getBoxCode());
        index.setValue(String.valueOf(entity.getCreateSiteCode()));
        kvIndexDao.add(index);
        return super.add(SendMDao.namespace, entity);
    }


    @Override
    public Integer addBatch(List<SendM> param) {
        for (SendM item:param){
            KvIndex index=new KvIndex();
            index.setKeyword(item.getBoxCode());
            index.setValue(String.valueOf(item.getCreateSiteCode()));
            kvIndexDao.add(index);
        }
        return super.addBatch(param);
    }



//
//    @Override
//    public List<SendM> findSendMByBoxCode2(SendM sendM) {
//        if(null==sendM.getCreateSiteCode()){
//            //查询索引表,循环查询数据库
//            List<Integer> siteCodes= kvIndexDao.queryCreateSiteCodesByKey(sendM.getBoxCode());
//            if(LOGGER.isInfoEnabled()){
//                LOGGER.info(MessageFormat.format("执行索引表查询-SEND_M表创建站点为{0}", JsonHelper.toJson(siteCodes)));
//            }
//            if(null!=siteCodes&&siteCodes.size()>0) {
//
//                List<SendM> list=new ArrayList<SendM>();
//                for (Integer item :siteCodes){
//                    sendM.setCreateSiteCode(item);/*循环变更创建站点查询数据，并进行汇总*/
//                    list.addAll(super.findSendMByBoxCode2(sendM));
//                }
//                if(LOGGER.isInfoEnabled()){
//                    LOGGER.info(MessageFormat.format("执行数据聚合-数据内容为{0}",JsonHelper.toJson(list)));
//                }
//                return list;
//            }
//        }
//        return super.findSendMByBoxCode2(sendM);
//    }

//    @Override
//    public List<SendM> findSendMByBoxCode(SendM sendM) {
//        if(null==sendM.getCreateSiteCode()){
//            //查询索引表,循环查询数据库
//            List<Integer> siteCodes= kvIndexDao.queryCreateSiteCodesByKey(sendM.getBoxCode());
//            if(LOGGER.isInfoEnabled()){
//                LOGGER.info(MessageFormat.format("执行索引表查询-SEND_M表创建站点为{0}", JsonHelper.toJson(siteCodes)));
//            }
//            if(null!=siteCodes&&siteCodes.size()>0) {
//
//                List<SendM> list=new ArrayList<SendM>();
//                for (Integer item :siteCodes){
//                    sendM.setCreateSiteCode(item);/*循环变更创建站点查询数据，并进行汇总*/
//                    list.addAll(super.findSendMByBoxCode(sendM));
//                }
//                if(LOGGER.isInfoEnabled()){
//                    LOGGER.info(MessageFormat.format("执行数据聚合-数据内容为{0}",JsonHelper.toJson(list)));
//                }
//                return list;
//            }
//        }
//        return super.findSendMByBoxCode(sendM);
//    }

    @Override
    public boolean insertSendM(SendM dSendM) {
        KvIndex index=new KvIndex();
        index.setKeyword(dSendM.getBoxCode());
        index.setValue(String.valueOf(dSendM.getCreateSiteCode()));
        kvIndexDao.add(index);
        return super.insertSendM(dSendM);
    }


}
