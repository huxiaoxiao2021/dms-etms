package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyComboardDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.BatchUpdateCancelReq;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.kom.common.util.IRedisClient;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.Constants.DATE_FORMAT1;
import static com.jd.bluedragon.Constants.TIME_SECONDS_ONE_HOUR;
import static com.jd.bluedragon.dms.utils.BusinessUtil.encryptIdCard;

/**
 * @author liwenji
 * @date 2022-11-23 21:17
 */
@Service
@Slf4j
public class JyComboardServiceImpl implements JyComboardService {

    @Autowired
    private JyComboardDao jyComboardDao;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisDao;


    @Override
    public List<User> queryUserByStartSiteCode(JyComboardEntity entity) {
        List<User> userList =new ArrayList<>();
        try {
            String key = entity.getGroupCode()+ ":"+DateHelper.formatDate(new Date(),DATE_FORMAT1);
            Set<String> users = redisDao.sMembers(key);
            if (CollectionUtils.isNotEmpty(users)){
                userList= users.stream().map(userJson->{
                  User user =JsonHelper.fromJson(userJson,User.class);
                  return user;
                }).collect(Collectors.toList());
                hideInfo(userList);
            }
        }catch (Exception e) {
            log.error("查询租板扫描人数异常",e);
        }
        return userList;
    }

    private void hideInfo(List<User> userList) {
        for (User user : userList) {
            user.setUserErp(encryptIdCard(user.getUserErp()));
        }
    }

    @Override
    public int save(JyComboardEntity entity) {
        cacheUserComboardScanLog(entity);
        return jyComboardDao.insertSelective(entity);
    }

    void cacheUserComboardScanLog(JyComboardEntity entity){
        User user =new User();
        user.setUserErp(entity.getUpdateUserErp());
        user.setUserName(entity.getUpdateUserName());
        String key = entity.getGroupCode()+ ":"+DateHelper.formatDate(new Date(),DATE_FORMAT1);
        try {
            if (redisDao.exists(key)){
                redisDao.sAdd(key, JsonHelper.toJson(user));
            }
            else {
                redisDao.sAdd(key, JsonHelper.toJson(user));
                redisDao.expire(key,24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            log.error("cache用户租板扫描记录异常",e);
        }
    }

    @Override
    public JyComboardEntity queryIfScaned(JyComboardEntity condition) {
        condition.setForceSendFlag(false);
        condition.setInterceptFlag(false);
        condition.setCancelFlag(false);
        return jyComboardDao.queryByBarCode(condition);
    }

    @Override
    public boolean batchUpdateCancelFlag(BatchUpdateCancelReq req) {
        return jyComboardDao.batchUpdateCancelFlag(req) > 0;
    }

    @Override
    public String queryWayBillCodeByBoardCode(JyComboardEntity entity) {
        return jyComboardDao.queryWayBillCodeByBoardCode(entity);
    }
}
