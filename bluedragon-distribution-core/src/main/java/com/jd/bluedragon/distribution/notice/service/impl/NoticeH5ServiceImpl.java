package com.jd.bluedragon.distribution.notice.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.dao.NoticeDao;
import com.jd.bluedragon.distribution.notice.dao.NoticeToUserDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeToUser;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;
import com.jd.bluedragon.distribution.notice.request.NoticeToUserQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.service.NoticeH5Service;
import com.jd.bluedragon.distribution.notice.utils.NoticeConstants;
import com.jd.bluedragon.distribution.notice.utils.NoticeReceiveScopeTypeEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private final int maxPageSize = 1000;

    /**
     * 查询未读通知数
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
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.WORKBENCH.getCode(), NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode())));
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
     * 查询最新一条未读通知
     *
     * @param noticePdaQuery 查询参数
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:25:38 周三
     */
    @Override
    public Response<NoticeH5Dto> getLastNewNotice(NoticePdaQuery noticePdaQuery) {
        log.info("NoticeH5ServiceImpl.getLastNewNotice {}", JsonHelper.toJson(noticePdaQuery));

        Response<NoticeH5Dto> result = new Response<>();
        result.toSucceed();
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeList(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            // 1. 按条件查询最新一条通知
            // 获取用户的创建时间，作为查询条件
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(noticePdaQuery.getUserErp());
            NoticeQuery noticeQuery = new NoticeQuery();
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.WORKBENCH.getCode(), NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode())));
            noticeQuery.setCreateTimeStart(baseStaff.getCreateTime());
            Notice noticeExist = noticeDao.selectOne(noticeQuery);
            if(noticeExist == null){
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

            result.setData(noticeH5Dto);
        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.getLastNewNotice exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
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
        noticeH5Dto.setPublishTime(notice.getCreateTime() != null ? notice.getCreateTime().getTime() : 0L);
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
            noticeQuery.setReceiveScopeTypeList(new ArrayList<>(Arrays.asList(NoticeReceiveScopeTypeEnum.WORKBENCH.getCode(), NoticeReceiveScopeTypeEnum.PDA_ANDROID.getCode())));
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
            Notice noticeExist = noticeDao.selectOne(noticeQuery);
            NoticeH5Dto noticeH5Dto = this.generateNoticeH5Dto(noticeExist);
            // 2. 查询是否已读
            NoticeToUserQuery noticeToUserQuery = new NoticeToUserQuery();
            noticeToUserQuery.setNoticeId(noticeExist.getId());
            noticeToUserQuery.setUserErp(noticePdaQuery.getUserErp());
            noticeToUserQuery.setYn(Constants.YN_YES);
            NoticeToUser noticeToUser = noticeToUserDao.selectOne(noticeToUserQuery);
            this.judgeIsRead4NoticeH5Dto(noticeH5Dto, noticeToUser);

            // 3. 查询附件

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
        log.info("NoticeH5ServiceImpl.getNoticeDetail {}", JsonHelper.toJson(noticePdaQuery));

        Response<PageDto<NoticeH5Dto>> result = new Response<>();
        result.toSucceed();
        try {
            // 0. 校验必要参数
            Response<Void> checkResult = this.checkParamForGetNoticeListAndPage(noticePdaQuery);
            if(!checkResult.isSucceed()){
                result.toError(result.getMessage());
                return result;
            }
            if(StringUtils.isBlank(noticePdaQuery.getKeyword())){
                result.toError("请输入关键字进行搜索");
                return result;
            }
            Response<PageDto<NoticeH5Dto>> listResult = this.getNoticeList(noticePdaQuery);
            return listResult;

        } catch (Exception e) {
            log.error("NoticeH5ServiceImpl.getNoticeDetail exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }
}
