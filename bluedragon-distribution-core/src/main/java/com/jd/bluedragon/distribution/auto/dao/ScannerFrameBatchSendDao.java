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
    private static final String NAMESPACE = ScannerFrameBatchSendDao.class.getName();
    private static final String selectCurrentBatchSend_ID = NAMESPACE + ".selectCurrentBatchSend";
    private static final String MACHINE_ID = "machineId";
    private static final String RECEIVE_SITE_CODE = "receiveSiteCode";
    private static final String OPERATE_TIME = "operateTime";
    private static final String YN = "yn";
    private static final String OPERATE_TIME_SUB_24_HOURS = "operateTimeSub24Hours";
    private static final String UPDATE_PRINT_TIMES_SQL = NAMESPACE + ".updatePrintTimes";
    public static final String GET_SPLIT_PAGE_LIST = NAMESPACE + ".getSplitPageList";
    public static final String GET_SPLIT_PAGE_LIST_COUNT = NAMESPACE + ".getSplitPageListCount";
    public static final String GET_CURRENT_SPLIT_PAGE_LIST = NAMESPACE + ".getCurrentSplitPageList";
    public static final String GET_CURRENT_SPLIT_PAGE_LIST_COUNT = NAMESPACE + ".getCurrentSplitPageListCount";
    public static final String QUERY_DOMAIN_BY_IDS = NAMESPACE + ".queryByIds";
    public static final String QUERY_DOMAIN_By_MACHINEID_AND_TIME = NAMESPACE + ".queryByMachineIdAndTime";

    public Integer add(ScannerFrameBatchSend entity) {
        return super.add(NAMESPACE, entity);
    }

    /**
     * @param machineId       龙门架注册号
     * @param receiveSiteCode 接收站点
     * @param operateTime     龙门架扫描时间
     * @return
     */
    public ScannerFrameBatchSend selectCurrentBatchSend(String machineId, long receiveSiteCode, Date operateTime) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put(MACHINE_ID, machineId);
        map.put(RECEIVE_SITE_CODE, receiveSiteCode);
        map.put(OPERATE_TIME, operateTime);
        map.put(OPERATE_TIME_SUB_24_HOURS, DateHelper.add(operateTime, Calendar.HOUR, -24));
        map.put(YN,1);//查询有效的批次（批次在龙门架更换方案之后，会全部置为无效）
        return super.getSqlSession().selectOne(selectCurrentBatchSend_ID, map);
    }

    /**
     * 更新打印记录
     *
     * @param id
     * @return
     */
    public Integer updatePrintTimes(long id) {
        return getSqlSession().update(UPDATE_PRINT_TIMES_SQL, id);
    }

    public List<ScannerFrameBatchSend> getSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return getSqlSession().selectList(GET_SPLIT_PAGE_LIST, argumentPager);
    }

    public long getSplitPageListCount(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return (Long) getSqlSession().selectOne(GET_SPLIT_PAGE_LIST_COUNT, argumentPager);
    }

    public List<ScannerFrameBatchSend> getCurrentSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return getSqlSession().selectList(GET_CURRENT_SPLIT_PAGE_LIST, argumentPager);
    }

    public long getCurrentSplitPageListCount(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return (Long) getSqlSession().selectOne(GET_CURRENT_SPLIT_PAGE_LIST_COUNT, argumentPager);
    }

    public List<ScannerFrameBatchSend> queryByIds(List<Long> ids) {
        return getSqlSession().selectList(QUERY_DOMAIN_BY_IDS, ids);
    }

    public List<ScannerFrameBatchSend> queryByMachineIdAndTime(ScannerFrameBatchSendSearchArgument request) {
        return getSqlSession().selectList(QUERY_DOMAIN_By_MACHINEID_AND_TIME, request);
    }

    public List<ScannerFrameBatchSend> queryAllReceiveSites(Map<String, String> params) {
        return super.getSqlSession().selectList(NAMESPACE + ".queryAllReceiveSites", params);
    }

    public Long queryAllUnPrintCount(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return super.getSqlSession().selectOne(NAMESPACE + ".queryAllUnPrintCount", argumentPager);
    }

    public List<ScannerFrameBatchSend> queryAllUnPrint(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return super.getSqlSession().selectList(NAMESPACE + ".queryAllUnPrint", argumentPager);
    }

    public long updateYnByMachineId(String machineId){
        return super.getSqlSession().update(NAMESPACE + ".updateYnByMachineId",machineId);
    }
}
