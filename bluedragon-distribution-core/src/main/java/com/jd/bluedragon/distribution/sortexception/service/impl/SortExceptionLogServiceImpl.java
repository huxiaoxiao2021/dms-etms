package com.jd.bluedragon.distribution.sortexception.service.impl;

import com.jd.bluedragon.distribution.sortexception.service.SortExceptionLogService;
import com.jd.bluedragon.distribution.sorting.dao.SortingExceptionDao;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;
import com.jd.bluedragon.distribution.sorting.service.SortingExceptionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * Created by guoyongzhi on 2014/11/17.
 */
@Service("sortExceptionLogService")
public class SortExceptionLogServiceImpl implements SortExceptionLogService {

    @Autowired
    private SortingExceptionService sortingExceptionService;

    @Autowired
    @Qualifier("sortingExceptionDao")
    private SortingExceptionDao sortingExceptionDao;

    private static final Logger log = LoggerFactory.getLogger(SortExceptionLogServiceImpl.class);

    /**
     * 分拣异常数据插入 sorting—EC表中
     *
     * @param task
     * @return
     */
    @Override
    public boolean addExpectionLog(Task task) {
        log.debug("分拣异常日志 29302，29303 插入Sorting_EC表开始 ：{}" , task);
        SortingException sortingExceptionList = this.prepareSortEC(task);
        if(sortingExceptionList!=null) {
            sortingExceptionList.setYn(1);
            sortingExceptionDao.add(sortingExceptionList);
            return true;
		} else {
            log.info("addExpectionLog 29302，29303 SortingException 对象是NULL:{}" , task);
            return false;
        }
    }

    /**
     * task数据转化 sorting_ec 对象
     *
     * @param task
     * @return
     */
    private SortingException prepareSortEC(Task task) {
        if (StringHelper.isEmpty(task.getBody())) {
            return null;
        }
        String body = task.getBody().substring(1, task.getBody().length() - 1);
        SortingException request = JsonHelper.jsonToObject(body, SortingException.class);
        if (request != null) {
            return request;
        }
        return null;
    }


}


