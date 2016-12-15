package com.jd.bluedragon.distribution.auto.dao;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.utils.DateHelper;

import java.util.*;

/**
 * Created by wangtingwei on 2016/12/8.
 */
public class ScannerFrameBatchSendDao extends BaseDao<ScannerFrameBatchSend> {
    private static final String NAMESPACE=ScannerFrameBatchSendDao.class.getName();
    private static final String selectCurrentBatchSend_ID = NAMESPACE + ".selectCurrentBatchSend";
    private static final String MACHINE_ID = "machineId";
    private static final String RECEIVE_SITE_CODE = "receiveSiteCode";
    private static final String OPERATE_TIME = "operateTime";
    private static final String OPERATE_TIME_SUB_24_HOURS = "operateTimeSub24Hours";
    private static final String UPDATE_PRINT_TIMES_SQL=NAMESPACE+".updatePrintTimes";
    public static final String GET_SPLIT_PAGE_LIST =NAMESPACE+".getSplitPageList";
    public static final String GET_SPLIT_PAGE_LIST_COUNT =NAMESPACE+".getSplitPageListCount";
    public static final String GET_CURRENT_SPLIT_PAGE_LIST =NAMESPACE+".getCurrentSplitPageList";
    public static final String GET_CURRENT_SPLIT_PAGE_LIST_COUNT =NAMESPACE+".getCurrentSplitPageListCount";
    public Integer add(ScannerFrameBatchSend entity) {
        return super.add(NAMESPACE, entity);
    }

    /**
     *
     * @param machineId         龙门架注册号
     * @param receiveSiteCode   接收站点
     * @param operateTime       龙门架扫描时间
     * @return
     */
    public ScannerFrameBatchSend selectCurrentBatchSend(long machineId,long receiveSiteCode,Date operateTime){
        Map<String,Object> map=new HashMap<String, Object>(4);
        map.put(MACHINE_ID,machineId);
        map.put(RECEIVE_SITE_CODE,receiveSiteCode);
        map.put(OPERATE_TIME,operateTime);
        map.put(OPERATE_TIME_SUB_24_HOURS,DateHelper.add(operateTime,Calendar.HOUR,-24));
        return super.getSqlSession().selectOne(selectCurrentBatchSend_ID,map);
    }

    /**
     * 更新打印记录
     * @param id
     * @return
     */
    public Integer updatePrintTimes(long id){
        return getSqlSession().update(UPDATE_PRINT_TIMES_SQL,id);
    }

    public List<ScannerFrameBatchSend> getSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager){
        return getSqlSession().selectList(GET_SPLIT_PAGE_LIST,argumentPager);
    }

    public long getSplitPageListCount(Pager<ScannerFrameBatchSendSearchArgument> argumentPager){
        return getSqlSession().selectOne(GET_SPLIT_PAGE_LIST_COUNT,argumentPager);
    }
    public List<ScannerFrameBatchSend> getCurrentSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager){
        return getSqlSession().selectList(GET_CURRENT_SPLIT_PAGE_LIST,argumentPager);
    }

    public long getCurrentSplitPageListCount(Pager<ScannerFrameBatchSendSearchArgument> argumentPager){
        return getSqlSession().selectOne(GET_CURRENT_SPLIT_PAGE_LIST_COUNT,argumentPager);
    }
}
