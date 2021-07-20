package com.jd.bluedragon.distribution.send.router;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
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

    private static final Log LOGGER= LogFactory.getLog(SendMRouter.class);

    @Autowired
    private KvIndexDao kvIndexDao;


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
        
        String turnoverBoxCode = entity.getTurnoverBoxCode();
    	if(StringUtils.isNotBlank(turnoverBoxCode)){
    		index.setKeyword(turnoverBoxCode);
    		index.setValue(String.valueOf(entity.getBoxCode()));
    		kvIndexDao.add(index);
        }
        
        return super.add(SendMDao.namespace, entity);
    }


    @Override
    @JProfiler(jKey = "DMSWEB.SendMRouter.addBatch", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Integer addBatch(List<SendM> param) {
        for (SendM item:param){
            KvIndex index=new KvIndex();
            index.setKeyword(item.getBoxCode());
            index.setValue(String.valueOf(item.getCreateSiteCode()));
            kvIndexDao.add(index);
            
            String turnoverBoxCode = item.getTurnoverBoxCode();
        	if(StringUtils.isNotBlank(turnoverBoxCode)){
        		index.setKeyword(turnoverBoxCode);
        		index.setValue(String.valueOf(item.getBoxCode()));
        		kvIndexDao.add(index);
            }
        }
        return super.addBatch(param);
    }


    @Override
    public List<SendM> findSendMByBoxCode2(SendM sendM) {
        if (null == sendM.getCreateSiteCode()) {
            //查询索引表,循环查询数据库
            List<Integer> siteCodes = kvIndexDao.queryCreateSiteCodesByKey(sendM.getBoxCode());
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(MessageFormat.format("执行索引表查询-SEND_M表创建站点为{0}", JsonHelper.toJson(siteCodes)));
            }
            List<SendM> list = new ArrayList<SendM>();
            if (null != siteCodes && siteCodes.size() > 0) {
                for (Integer item : siteCodes) {
                    sendM.setCreateSiteCode(item);/*循环变更创建站点查询数据，并进行汇总*/
                    List<SendM> index = super.findSendMByBoxCode2(sendM);
                    if (null != index && index.size() > 0) {
                        list.addAll(index);
                    }
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(MessageFormat.format("执行数据聚合-数据内容为{0}", JsonHelper.toJson(list)));
                }
            }
            return list;
        }
        return super.findSendMByBoxCode2(sendM);
    }

    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM) {
        if (null == sendM.getCreateSiteCode()) {
            //查询索引表,循环查询数据库
            List<Integer> siteCodes= kvIndexDao.queryCreateSiteCodesByKey(sendM.getBoxCode());
            if(LOGGER.isInfoEnabled()){
                LOGGER.info(MessageFormat.format("执行索引表查询-SEND_M表创建站点为{0}", JsonHelper.toJson(siteCodes)));
            }

            List<SendM> list = new ArrayList<SendM>();
            if (null != siteCodes && siteCodes.size() > 0) {
                for (Integer item : siteCodes) {
                    sendM.setCreateSiteCode(item);/*循环变更创建站点查询数据，并进行汇总*/
                    List<SendM> index = super.findSendMByBoxCode(sendM);
                    if (null != index && index.size() > 0) {
                        list.addAll(index);
                    }
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(MessageFormat.format("执行数据聚合-数据内容为{0}", JsonHelper.toJson(list)));
                }
            }
            return list;
        }
        return super.findSendMByBoxCode(sendM);
    }

    @Override
    public boolean insertSendM(SendM dSendM) {
        KvIndex index=new KvIndex();
        index.setKeyword(dSendM.getBoxCode());
        index.setValue(String.valueOf(dSendM.getCreateSiteCode()));
        kvIndexDao.add(index);
        
        String turnoverBoxCode = dSendM.getTurnoverBoxCode();
    	if(StringUtils.isNotBlank(turnoverBoxCode)){
    		index.setKeyword(turnoverBoxCode);
    		index.setValue(String.valueOf(dSendM.getBoxCode()));
    		kvIndexDao.add(index);
        }

        boolean res=super.insertSendM(dSendM);

        return res;
    }

}
