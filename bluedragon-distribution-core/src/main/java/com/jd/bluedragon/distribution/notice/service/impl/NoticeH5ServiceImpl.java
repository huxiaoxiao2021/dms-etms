package com.jd.bluedragon.distribution.notice.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.cache.LastNewNoticeForUserDto;
import com.jd.bluedragon.distribution.notice.cache.LastNewNoticeGlobalDto;
import com.jd.bluedragon.distribution.notice.cache.NoticeChangeInfoDto;
import com.jd.bluedragon.distribution.notice.cache.NoticeCountDto;
import com.jd.bluedragon.distribution.notice.dao.NoticeDao;
import com.jd.bluedragon.distribution.notice.dao.NoticeToUserDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeToUser;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;
import com.jd.bluedragon.distribution.notice.request.NoticeToUserQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.response.NoticeLastNewDto;
import com.jd.bluedragon.distribution.notice.service.NoticeH5Service;
import com.jd.bluedragon.distribution.notice.utils.NoticeConstants;
import com.jd.bluedragon.distribution.notice.utils.NoticeReceiveScopeTypeEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.avro.data.Json;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * h5页面通知接口实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-07-01 16:01:29 周三
 */
@Service
public class NoticeH5ServiceImpl implements NoticeH5Service {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeToUserDao noticeToUserDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    /**
     * 查询列表最大分页参数值
     */
    private final int maxPageSize = 1000;

    /**
     * 自动拉取时间间隔值，单位秒
     */
    private final int pdaNoticePullIntervalTime = 1800;

    /**
     * 搜索间隔时间，单位毫秒
     */
    private final int searchTimeInterval = 3000;

    /**
     * 查询未读通知数。此接口数据量较大，注意缓存
     *
     * @param noticePdaQuery 查询参数
     * @return Long 未读通知数
     * @author fanggang7
     * @date 2021-02-24 20:21:42 周三
     */
    @Override
    public Response<Long> getNoticeUnreadCount(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.getNoticeUnreadCount {}", JsonHelper.toJson(noticePdaQuery));

        Response<Long> result = new Response<>();
        result.toSucceed();
        long unreadCount = 0;
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeList(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            // 1. 按条件查询所有通知数
            // 获取用户的创建时间，作为查询条件
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(noticePdaQuery.getUserErp());
            NoticeQuery noticeQuery = new NoticeQuery();
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
            noticeQuery.setCreateTimeStart(baseStaff.getCreateTime());
            long totalCount = noticeDao.queryCount(noticeQuery);

            // 2. 按用户查询所有已读计数
            NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
            noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
            noticeToUserQuery.setIsRead(NoticeConstants.IS_READ_YES);
            long readCount = noticeToUserDao.queryCount(noticeToUserQuery);
            // 3. 得到未读数
            unreadCount = totalCount - readCount;

        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.getNoticeUnreadCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(unreadCount);
        return result;
    }

    /**
     * 查询最新一条未读通知。此接口数据量较大，注意缓存
     *
     * @param noticePdaQuery 查询参数
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:25:38 周三
     */
    @Override
    public Response<NoticeLastNewDto> getLastNewNotice(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.getLastNewNotice param {}", JsonHelper.toJson(noticePdaQuery));

        Response<NoticeLastNewDto> result = new Response<>();
        result.toSucceed();
        NoticeLastNewDto noticeLastNewDto = new NoticeLastNewDto();
        noticeLastNewDto.setPullIntervalTime(uccPropertyConfiguration.getPdaNoticePullIntervalTime() != null ? uccPropertyConfiguration.getPdaNoticePullIntervalTime() : pdaNoticePullIntervalTime);
        noticeLastNewDto.setUnreadCount(0);
        result.setData(noticeLastNewDto);
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeList(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            // 1. 查询最新通知数据
            noticeLastNewDto.setLastNewNotice(this.getUserLastNewNoticeWithCache(noticePdaQuery));

            // 2. 查询未读通知数
            noticeLastNewDto.setUnreadCount(this.getUserUnreadCount(noticePdaQuery));
            log.info("NoticeH5ServiceImpl.getLastNewNotice result {}", JsonHelper.toJson(noticeLastNewDto));

        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.getLastNewNotice exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;

    }

    /**
     * 获取用户未读通知数
     * @param noticePdaQuery 查询参数
     * @return 用户未读通知数
     * @author fanggang7
     * @time 2021-03-12 23:33:08 周日
     */
    private int getUserUnreadCount(NoticePdaQuery noticePdaQuery){
        int unreadCount = 0;

        // 1. 查询全局所有符合条件的通知总数
        long globalTotalCount = this.getUserTotalCountWithCache(noticePdaQuery);
        // 2. 查询用户总已读数
        long readCount = this.getUserReadCountWithCache(noticePdaQuery);
        // 3. 得到未读数
        unreadCount = (int)(globalTotalCount - readCount);
        return unreadCount;
    }

    /**
     * 获取用户已读总通知数，依赖缓存
     * @param noticePdaQuery 查询参数
     * @return 包含已删除的总通知数
     * @author fanggang7
     * @time 2021-03-13 21:50:03 周六
     */
    private long getUserReadCountWithCache(NoticePdaQuery noticePdaQuery){
        long readCount = 0;
        // 查询全局总数缓存
        String cacheKeyFormatClientUserReadCount = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_READ_COUNT, noticePdaQuery.getUserErp());
        String clientUserReadCountValStr = jimdbCacheService.get(cacheKeyFormatClientUserReadCount);
        if(StringUtils.isBlank(clientUserReadCountValStr)){
            readCount = this.getUserReadCountNoCache(noticePdaQuery);
            jimdbCacheService.setEx(cacheKeyFormatClientUserReadCount, readCount + "", CacheKeyConstants.CACHE_KEY_CLIENT_NOTICE_READ_COUNT_TIME_EXPIRE, TimeUnit.HOURS);
        } else {
            readCount = new Long(clientUserReadCountValStr);
        }
        return readCount;
    }

    /**
     * 获取用户已读总通知数，依赖缓存
     * @param noticePdaQuery 查询参数
     * @return 包含已删除的总通知数
     * @author fanggang7
     * @time 2021-03-13 21:50:03 周六
     */
    private long getUserReadCountNoCache(NoticePdaQuery noticePdaQuery){
        // 2. 按用户查询所有已读计数
        NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
        noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
        noticeToUserQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
        noticeToUserQuery.setIsRead(NoticeConstants.IS_READ_YES);
        return noticeToUserDao.queryReadCountByUserExcludeDeleteNotice(noticeToUserQuery);
    }

    /**
     * 获取用户总通知数，不包含已删除的，依赖缓存
     * @param noticePdaQuery 查询参数
     * @return 包含已删除的总通知数
     * @author fanggang7
     * @time 2021-03-13 21:50:03 周六
     */
    private long getUserTotalCountWithCache(NoticePdaQuery noticePdaQuery){
        long totalCount = 0;
        String cacheKeyFormatClientUserTotalCount = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_TOTAL_COUNT, noticePdaQuery.getUserErp());
        String clientUserTotalCountValStr = jimdbCacheService.get(cacheKeyFormatClientUserTotalCount);
        if(StringUtils.isBlank(clientUserTotalCountValStr)){
            totalCount = this.getUserTotalCountNoCache(noticePdaQuery);
            NoticeCountDto noticeCountDto = new NoticeCountDto(totalCount, System.currentTimeMillis());
            jimdbCacheService.setEx(cacheKeyFormatClientUserTotalCount, JsonHelper.toJson(noticeCountDto), CacheKeyConstants.CACHE_KEY_CLIENT_NOTICE_TOTAL_COUNT_TIME_EXPIRE, TimeUnit.HOURS);
        } else {
            // 判断是否已过时
            String cacheKeyNoticeGlobalChangeInfo = CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_GLOBAL_CHANGE_INFO;
            String noticeGlobalChangeInfoValStr = jimdbCacheService.get(cacheKeyNoticeGlobalChangeInfo);
            NoticeChangeInfoDto noticeChangeInfoDto = null;
            if(StringUtils.isNotBlank(noticeGlobalChangeInfoValStr)){
                noticeChangeInfoDto = JsonHelper.fromJson(noticeGlobalChangeInfoValStr, NoticeChangeInfoDto.class);
            }

            NoticeCountDto noticeCountDto = JsonHelper.fromJson(clientUserTotalCountValStr, NoticeCountDto.class);
            if(noticeChangeInfoDto != null && noticeChangeInfoDto.getCacheTime() != null && noticeCountDto != null && noticeCountDto.getCacheTime() != null){
                // 如果已过时，则重新查询并设置缓存
                if(noticeChangeInfoDto.getCacheTime() > noticeCountDto.getCacheTime()){
                    totalCount = this.getUserTotalCountNoCache(noticePdaQuery);
                    noticeCountDto = new NoticeCountDto(totalCount, System.currentTimeMillis());
                    jimdbCacheService.setEx(cacheKeyFormatClientUserTotalCount, JsonHelper.toJson(noticeCountDto), CacheKeyConstants.CACHE_KEY_CLIENT_NOTICE_TOTAL_COUNT_TIME_EXPIRE, TimeUnit.HOURS);
                }
            }
            if(noticeCountDto != null && noticeCountDto.getCount() != null){
                totalCount = noticeCountDto.getCount();
            }
        }
        return totalCount;
    }

    /**
     * 无缓存查询用户总通知数，不包含已删除的
     * @param noticePdaQuery 查询参数
     * @return 包含已删除的总通知数
     * @author fanggang7
     * @time 2021-03-13 21:50:03 周六
     */
    private long getUserTotalCountNoCache(NoticePdaQuery noticePdaQuery){
        // 1. 按条件查询所有通知数
        // 获取用户的创建时间，作为查询条件
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(noticePdaQuery.getUserErp());
        NoticeQuery noticeQuery = new NoticeQuery();
        noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
        noticeQuery.setIsDisplay(Constants.YN_YES);
        noticeQuery.setIsDelete(Constants.YN_NO);
        noticeQuery.setCreateTimeStart(baseStaff.getCreateTime());
        return noticeDao.queryCount(noticeQuery);
    }

    /**
     * 查询用户最新通知数据
     * @param noticePdaQuery 查询参数
     * @return 未读通知数据
     * @author fanggang7
     * @time 2021-03-12 23:29:03 周日
     */
    private NoticeH5Dto getUserLastNewNoticeWithCache(NoticePdaQuery noticePdaQuery){
        NoticeH5Dto noticeH5Dto = null;
        // 1.1 查询全局最新通知数据
        LastNewNoticeGlobalDto lastNewNoticeGlobalDto = this.getGlobalLastNewWithCache();
        // 如果全局最新通知数据为空，直接返回
        if(lastNewNoticeGlobalDto != null){
            // 1.2 查询用户最新通知数据
            LastNewNoticeForUserDto lastNewNoticeForUserDto = getUserLastNewNoticeWithCache(noticePdaQuery, lastNewNoticeGlobalDto);
            if(lastNewNoticeForUserDto != null){
                // 1.3 设置最新通知数据
                noticeH5Dto = new NoticeH5Dto();
                BeanUtils.copyProperties(lastNewNoticeGlobalDto, noticeH5Dto);
            }
        }
        return noticeH5Dto;
    }

    /**
     * 查询用户最新通知数据，操作缓存
     * @param noticePdaQuery 查询参数
     * @param lastNewNoticeGlobalDto 全局最新通知数据
     * @return 用户最新通知数据缓存
     */
    private LastNewNoticeForUserDto getUserLastNewNoticeWithCache(NoticePdaQuery noticePdaQuery, LastNewNoticeGlobalDto lastNewNoticeGlobalDto){
        String cacheKeyFormatClientUserLastNewNotice = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_USER_LAST_NEW_NOTICE, noticePdaQuery.getUserErp());
        // 如果用户未读通知为空，则查一遍库，查询已读记录，存在已读，则说明已推送
        String userLastNewNoticeValueStr = jimdbCacheService.get(cacheKeyFormatClientUserLastNewNotice);
        log.info("NoticeH5ServiceImpl.getUserLastNewNoticeWithCache val {}", userLastNewNoticeValueStr);
        LastNewNoticeForUserDto lastNewNoticeForUserDto;
        if(StringUtils.isBlank(userLastNewNoticeValueStr)){
            lastNewNoticeForUserDto = new LastNewNoticeForUserDto();
            lastNewNoticeForUserDto.setId(lastNewNoticeGlobalDto.getId());
            lastNewNoticeForUserDto.setCacheTime(System.currentTimeMillis());
            lastNewNoticeForUserDto.setIsFetched(Constants.YN_YES);
            jimdbCacheService.set(cacheKeyFormatClientUserLastNewNotice, JsonHelper.toJson(lastNewNoticeForUserDto));
        } else {
            lastNewNoticeForUserDto = JsonHelper.fromJson(userLastNewNoticeValueStr, LastNewNoticeForUserDto.class);
            if(lastNewNoticeForUserDto == null){
                lastNewNoticeForUserDto = new LastNewNoticeForUserDto();
                lastNewNoticeForUserDto.setId(lastNewNoticeGlobalDto.getId());
                lastNewNoticeForUserDto.setCacheTime(System.currentTimeMillis());
                lastNewNoticeForUserDto.setIsFetched(Constants.YN_YES);
                jimdbCacheService.set(cacheKeyFormatClientUserLastNewNotice, JsonHelper.toJson(lastNewNoticeForUserDto));
            } else {
                if(lastNewNoticeForUserDto.getCacheTime() != null && lastNewNoticeForUserDto.getCacheTime() > lastNewNoticeGlobalDto.getCacheTime()
                        && Objects.equals(lastNewNoticeForUserDto.getIsFetched(), Constants.YN_YES)){
                    lastNewNoticeForUserDto = null;
                } else {
                    lastNewNoticeForUserDto.setCacheTime(System.currentTimeMillis());
                    jimdbCacheService.set(cacheKeyFormatClientUserLastNewNotice, JsonHelper.toJson(lastNewNoticeForUserDto));
                }
            }
        }
        return lastNewNoticeForUserDto;
    }

    /**
     * 查询全局最新通知数据，操作缓存
     * @return 用户最新通知数据缓存
     */
    private LastNewNoticeGlobalDto getGlobalLastNewWithCache(){
        // 查看缓存
        String cacheKeyFormatClientGlobalLastNewNotice = CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_GLOBAL_LAST_NEW_NOTICE;
        String globalLastNewNoticeValueStr = jimdbCacheService.get(cacheKeyFormatClientGlobalLastNewNotice);
        log.info("NoticeH5ServiceImpl.getGlobalLastNewWithCache val {}", globalLastNewNoticeValueStr);
        // 如果全局未读通知为空，则查一遍数据库
        LastNewNoticeGlobalDto lastNewNoticeGlobalDto;
        if(StringUtils.isBlank(globalLastNewNoticeValueStr)){
            NoticeH5Dto noticeH5Dto = this.getLastNewNoticeNoCache();
            long currentTimeMillis = System.currentTimeMillis();
            lastNewNoticeGlobalDto = this.genLastNewNoticeGlobalDto(noticeH5Dto, currentTimeMillis);
            jimdbCacheService.set(cacheKeyFormatClientGlobalLastNewNotice, JsonHelper.toJson(lastNewNoticeGlobalDto));
        } else {
            lastNewNoticeGlobalDto = JsonHelper.fromJson(globalLastNewNoticeValueStr, LastNewNoticeGlobalDto.class);
            if(lastNewNoticeGlobalDto == null){
                jimdbCacheService.del(cacheKeyFormatClientGlobalLastNewNotice);
            }
        }
        return lastNewNoticeGlobalDto;
    }

    /**
     * 获取未读通知数据，无缓存直接查库
     * @return 通知数据
     * @author fanggang7
     * @time 2021-03-12 23:20:13 周日
     */
    public NoticeH5Dto getLastNewNoticeNoCache(){
        // 1. 按条件查询最新一条通知
        // 获取用户的创建时间，作为查询条件
        // BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(noticePdaQuery.getUserErp());
        NoticeQuery noticeQuery = new NoticeQuery();
        noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
        // noticeQuery.setCreateTimeStart(baseStaff.getCreateTime());
        noticeQuery.setIsDelete(Constants.YN_NO);
        Notice noticeExist = noticeDao.selectOne(noticeQuery);
        if(noticeExist == null){
            return null;
        }
        NoticeH5Dto noticeH5Dto = this.generateNoticeH5Dto(noticeExist);

        // 2. 查询是否已读
        /*NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
        noticeToUserQuery.setNoticeId(noticeExist.getId());
        noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
        noticeToUserQuery.setYn(Constants.YN_YES);
        NoticeToUser noticeToUser = noticeToUserDao.selectOne(noticeToUserQuery);
        this.judgeIsRead4NoticeH5Dto(noticeH5Dto, noticeToUser);*/

        return noticeH5Dto;
    }

    private NoticeH5Dto generateNoticeH5Dto(Notice notice){
        NoticeH5Dto noticeH5Dto = new NoticeH5Dto();
        noticeH5Dto.setId(notice.getId());
        noticeH5Dto.setTitle(notice.getTheme());
        noticeH5Dto.setLevel(notice.getLevel());
        noticeH5Dto.setType(notice.getType());
        noticeH5Dto.setCategoryName(NoticeTypeEnum.getEnumNameByCode(notice.getType()) != null ? NoticeTypeEnum.getEnumNameByCode(notice.getType()) : "未知类型");
        noticeH5Dto.setIsTopDisplay(notice.getIsTopDisplay());
        noticeH5Dto.setContentBrief(notice.getContentBrief());
        noticeH5Dto.setContent(notice.getContent());
        noticeH5Dto.setPublishTime(notice.getUpdateTime() != null ? notice.getUpdateTime().getTime() : notice.getCreateTime().getTime());
        noticeH5Dto.setPublishDateFormative(DateUtil.format(notice.getCreateTime(), DateUtil.FORMAT_DATE));
        noticeH5Dto.setPublishTimeFormative(DateUtil.format(notice.getCreateTime(), DateUtil.FORMAT_TIME));
        return noticeH5Dto;
    }

    /**
     * 判断是否已读
     * @param noticeH5Dto 通知数据
     * @param noticeToUser 已读记录
     */
    private void judgeIsRead4NoticeH5Dto(NoticeH5Dto noticeH5Dto, NoticeToUser noticeToUser){
        if(noticeToUser != null){
            noticeH5Dto.setIsRead(Constants.YN_YES);
        } else {
            noticeH5Dto.setIsRead(Constants.YN_NO);
        }
    }

    private LastNewNoticeGlobalDto genLastNewNoticeGlobalDto(NoticeH5Dto noticeH5Dto, Long cacheTimeMillSeconds){
        LastNewNoticeGlobalDto lastNewNoticeGlobalDto = new LastNewNoticeGlobalDto();
        BeanUtils.copyProperties(noticeH5Dto, lastNewNoticeGlobalDto);
        lastNewNoticeGlobalDto.setCacheTime(cacheTimeMillSeconds);
        return lastNewNoticeGlobalDto;
    }

    /**
     * 查询通知列表
     *
     * @param noticePdaQuery 查询参数
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:30:10 周三
     */
    @Override
    public Response<PageDto<NoticeH5Dto>> getNoticeList(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.getNoticeList {}", JsonHelper.toJson(noticePdaQuery));

        Response<PageDto<NoticeH5Dto>> result = new Response<>();
        result.toSucceed();

        PageDto<NoticeH5Dto> noticePageDto = new PageDto<>();
        List<NoticeH5Dto> noticeH5List = new ArrayList<>();
        noticePageDto.setResult(noticeH5List);

        result.setData(noticePageDto);

        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeListAndPage(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }

            // 1. 查询主通知列表，按发布时间降序排列
            // 获取用户的创建时间，作为查询条件
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(noticePdaQuery.getUserErp());
            NoticeQuery noticeQuery = new NoticeQuery();
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
            noticeQuery.setCreateTimeStart(baseStaff.getCreateTime());
            noticeQuery.setKeyword(noticePdaQuery.getKeyword());
            long total = noticeDao.queryCount(noticeQuery);
            if(total == 0){
                return result;
            }
            noticePageDto.setTotalRow((int)total);
            noticeQuery.setPageNumber(noticePdaQuery.getPageNumber());
            noticeQuery.setPageSize(noticePdaQuery.getPageSize());
            List<Notice> noticeExistList = noticeDao.queryList(noticeQuery);
            if(CollectionUtils.isEmpty(noticeExistList)){
                return result;
            }
            // 2. 根据id集合查询已读状态
            List<Long> noticeIds = new ArrayList<>();
            for (Notice notice : noticeExistList) {
                noticeIds.add(notice.getId());
            }

            // 3. 查询是否已读
            NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
            noticeToUserQuery.setNoticeIdList(noticeIds);
            noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
            noticeToUserQuery.setYn(Constants.YN_YES);
            List<NoticeToUser> noticeToUsers = noticeToUserDao.queryList(noticeToUserQuery);
            Map<Long, NoticeToUser> noticeToUserGBNoticeIdMap = new HashMap<>();
            for (NoticeToUser noticeToUser : noticeToUsers) {
                noticeToUserGBNoticeIdMap.put(noticeToUser.getNoticeId(), noticeToUser);
            }

            for (Notice notice : noticeExistList) {
                NoticeH5Dto noticeH5Dto = this.generateNoticeH5Dto(notice);
                NoticeToUser noticeToUser = noticeToUserGBNoticeIdMap.get(notice.getId());
                this.judgeIsRead4NoticeH5Dto(noticeH5Dto, noticeToUser);
                noticeH5List.add(noticeH5Dto);
            }
        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.checkParamForGetNoticeList exception ", e);
            result.toError(e.getMessage());
        }
        return result;
    }

    private Response<Void> checkParamForGetNoticeList(NoticePdaQuery noticePdaQuery){
        Response<Void> result = new Response<>();
        result.toSucceed();

        try {
            if(StringUtils.isBlank(noticePdaQuery.getUserErp())){
                result.toError("参数错误，用户erp不能为空");
                return result;
            }
        } catch (Exception e){
            log.error("NoticeH5ServiceImpl.checkParamForGetNoticeList exception ", e);
            result.toError(e.getMessage());
        }
        return result;
    }

    private Response<Void> checkParamForGetNoticeListAndPage(NoticePdaQuery noticePdaQuery){
        Response<Void> result = new Response<>();
        result.toSucceed();

        try {

            Response<Void> checkResult = this.checkParamForGetNoticeList(noticePdaQuery);
            if(!checkResult.isSucceed()){
                return checkResult;
            }
            if(noticePdaQuery.getPageNumber() == null){
                result.toError("参数错误，pageNumber不能为空");
                return result;
            }
            if(noticePdaQuery.getPageSize() == null){
                result.toError("参数错误，pageSize不能为空");
                return result;
            }
            if(noticePdaQuery.getPageSize() > maxPageSize){
                result.toError(String.format("参数错误，pageSize不能超过%s", maxPageSize));
                return result;
            }
        } catch (Exception e){
            log.error("NoticeH5ServiceImpl.checkParamForGetNoticeListAndPage exception ", e);
            result.toError(e.getMessage());
        }
        return result;
    }

    /**
     * 查询通知详情
     *
     * @param noticePdaQuery 查询参数
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:33:49 周三
     */
    @Override
    public Response<NoticeH5Dto> getNoticeDetail(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.getNoticeDetail {}", JsonHelper.toJson(noticePdaQuery));

        Response<NoticeH5Dto> result = new Response<>();
        result.toSucceed();
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeList(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            // 1. 查询详情
            NoticeQuery noticeQuery = new NoticeQuery();
            noticeQuery.setId(noticePdaQuery.getNoticeId());
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode())));
            Notice noticeExist = noticeDao.selectOne(noticeQuery);
            if(noticeExist == null){
                result.toError("未查询到数据");
                return result;
            }
            NoticeH5Dto noticeH5Dto = this.generateNoticeH5Dto(noticeExist);
            // 2. 查询是否已读
            NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
            noticeToUserQuery.setNoticeId(noticeExist.getId());
            noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
            noticeToUserQuery.setYn(Constants.YN_YES);
            NoticeToUser noticeToUser = noticeToUserDao.selectOne(noticeToUserQuery);
            this.judgeIsRead4NoticeH5Dto(noticeH5Dto, noticeToUser);

            // 3. 如果未读，设置为已读
            if(noticeToUser == null){
                NoticeToUser noticeToUserInsert = new NoticeToUser();
                noticeToUserInsert.setNoticeId(noticeExist.getId());
                noticeToUserInsert.setCreateUser(noticePdaQuery.getUserErp());
                noticeToUserInsert.setCreateTime(new Date());
                noticeToUserInsert.setUpdateTime(noticeToUserInsert.getCreateTime());
                noticeToUserInsert.setReceiveUserErp(noticePdaQuery.getUserErp());
                noticeToUserInsert.setIsRead(Constants.YN_YES);
                noticeToUserInsert.setYn(Constants.YN_YES);
                noticeToUserDao.add(noticeToUserInsert);
            }

            // 4. 查询附件 待定
            // 5. 更新缓存
            long readCount = 0;
            // 查询全局总数缓存
            String cacheKeyFormatClientUserReadCount = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_READ_COUNT, noticePdaQuery.getUserErp());
            String clientUserReadCountValStr = jimdbCacheService.get(cacheKeyFormatClientUserReadCount);
            if(StringUtils.isBlank(clientUserReadCountValStr)){
                readCount = this.getUserReadCountNoCache(noticePdaQuery);
            } else {
                readCount = new Long(clientUserReadCountValStr);
                readCount++;
            }
            jimdbCacheService.setEx(cacheKeyFormatClientUserReadCount, readCount + "", CacheKeyConstants.CACHE_KEY_CLIENT_NOTICE_TOTAL_COUNT_TIME_EXPIRE, TimeUnit.HOURS);

            result.setData(noticeH5Dto);
        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.getNoticeDetail exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 根据关键字查询
     *
     * @param noticePdaQuery 查询参数
     * @return Notice 查询结果
     * @author fanggang7
     * @date 2021-02-24 20:33:49 周三
     */
    @Override
    public Response<PageDto<NoticeH5Dto>> searchByKeyword(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.searchByKeyword {}", JsonHelper.toJson(noticePdaQuery));

        Response<PageDto<NoticeH5Dto>> result = new Response<>();
        result.toSucceed();
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeListAndPage(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            String userLastSearchTimeKey = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_LAST_SEARCH_TIME, noticePdaQuery.getUserErp());
            String lastSearchTimeValStr = jimdbCacheService.get(userLastSearchTimeKey);
            long currentTimeMillis = System.currentTimeMillis();
            if(StringUtils.isNotBlank(lastSearchTimeValStr)){
                long lastSearchTimeVal = new Long(lastSearchTimeValStr);
                if(currentTimeMillis - lastSearchTimeVal < searchTimeInterval){
                    result.toError("操作太快，客官慢点");
                    return result;
                }
            }
            jimdbCacheService.setEx(userLastSearchTimeKey, currentTimeMillis, CacheKeyConstants.CACHE_KEY_CLIENT_NOTICE_USER_LAST_SEARCH_TIME_EXPIRE, TimeUnit.SECONDS);

            if(StringUtils.isBlank(noticePdaQuery.getKeyword())){
                result.toError("请输入关键字进行搜索");
                return result;
            }
            Response<PageDto<NoticeH5Dto>> listResult = this.getNoticeList(noticePdaQuery);
            return listResult;

        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.searchByKeyword exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    private final String operate_type_insert = "insert";
    private final String operate_type_update = "update";
    private final String operate_type_delete = "delete";

    /**
     * 更新通知数据时更新通知缓存
     * @param notice
     * @param operateType
     * @return
     */
    @Override
    public Response<Void> updatePdaNoticeCache(Notice notice, String operateType){
        Response<Void> response = new Response<>();
        response.toSucceed();
        try {
            switch (operateType){
                case operate_type_insert: {
                    response = this.updatePdaNoticeCacheForInsert(notice);
                    break;
                }
                case operate_type_update: {
                    response = this.updatePdaNoticeCacheForUpdate(notice);
                    break;
                }
                case operate_type_delete: {
                    response = this.updatePdaNoticeCacheForDelete(notice);
                    break;
                }
                default:
                    log.warn("NoticeServiceImpl.updatePdaNoticeCache unknow operateType {}", operateType);
            }
        } catch (Exception e) {
            log.error("NoticeServiceImpl.updatePdaNoticeCache exception ", e);
            response.toError("更新失败");
        }
        return response;
    }

    /**
     * 判断是否为合乎条件的通知
     * @param notice 通知数据
     * @return 匹配结果
     * @author fanggang7
     * @time 2021-03-14 21:37:14 周日
     */
    @Override
    public boolean checkIsMatchPdaNotice(Notice notice){
        if(notice == null) {
            return false;
        }
        List<Integer> matchReceiveScopeTypeList = new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode(), NoticeReceiveScopeTypeEnum.ALL.getCode()));
        if(Objects.equals(Constants.YN_YES, notice.getIsDisplay()) && matchReceiveScopeTypeList.contains(notice.getReceiveScopeType())){
            return true;
        }
        return false;
    }

    /**
     * 新增通知时更新通知缓存
     * @param notice 通知数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-12 23:51:57 周五
     */
    public Response<Void> updatePdaNoticeCacheForInsert(Notice notice){
        Response<Void> response = new Response<>();
        response.toSucceed();
        if(!this.checkIsMatchPdaNotice(notice)){
            return response;
        }
        // 最新通知：变为新的一条
        String cacheKeyFormatClientGlobalLastNewNotice = CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_GLOBAL_LAST_NEW_NOTICE;
        String globalLastNewNoticeValueStr = jimdbCacheService.get(cacheKeyFormatClientGlobalLastNewNotice);
        // 如果全局未读通知为空，则查一遍数据库
        NoticeH5Dto noticeH5Dto = this.generateNoticeH5Dto(notice);
        long currentTimeMillis = System.currentTimeMillis();
        LastNewNoticeGlobalDto lastNewNoticeGlobalDto = this.genLastNewNoticeGlobalDto(noticeH5Dto, currentTimeMillis);
        jimdbCacheService.set(cacheKeyFormatClientGlobalLastNewNotice, JsonHelper.toJson(lastNewNoticeGlobalDto));

        // 更新全局通知变更缓存
        this.updateNoticeGlobalChangeInfoCache();

        return response;
    }

    private void updateNoticeGlobalChangeInfoCache(){
        String cacheKeyNoticeGlobalChangeInfo = CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_NOTICE_GLOBAL_CHANGE_INFO;
        NoticeChangeInfoDto noticeChangeInfoDto = new NoticeChangeInfoDto(System.currentTimeMillis());
        jimdbCacheService.set(cacheKeyNoticeGlobalChangeInfo, noticeChangeInfoDto);
    }

    /**
     * 更新通知时更新通知缓存
     * @param notice 通知数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-12 23:51:57 周五
     */
    public Response<Void> updatePdaNoticeCacheForUpdate(Notice notice){
        Response<Void> response = new Response<>();
        response.toSucceed();
        if(!this.checkIsMatchPdaNotice(notice)){
            return response;
        }
        // 最新通知：重新查询一条
        this.getGlobalLastNewWithCache();

        // 更新全局通知变更缓存
        this.updateNoticeGlobalChangeInfoCache();

        return response;
    }

    /**
     * 删除通知时更新通知缓存
     * @param notice 通知数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-12 23:51:57 周五
     */
    public Response<Void> updatePdaNoticeCacheForDelete(Notice notice){
        Response<Void> response = new Response<>();
        response.toSucceed();

        if(!this.checkIsMatchPdaNotice(notice)){
            return response;
        }
        // 最新通知：重新查询一条
        this.getGlobalLastNewWithCache();

        // 更新全局通知变更缓存
        this.updateNoticeGlobalChangeInfoCache();

        return response;
    }
}
