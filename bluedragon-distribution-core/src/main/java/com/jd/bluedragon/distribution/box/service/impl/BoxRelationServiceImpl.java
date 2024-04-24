package com.jd.bluedragon.distribution.box.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.dao.BoxRelationDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.dms.utils.DmsMessageConstants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

/**
 * @ClassName BoxRelationServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/12/14 16:48
 **/
@Service
public class BoxRelationServiceImpl implements BoxRelationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxRelationServiceImpl.class);
    private static final int EXPIRE_TIME_TEN_SECOND = 10;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private BoxRelationDao boxRelationDao;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingDao sortingDao;

    @Override
    @JProfiler(jKey = "dms.web.BoxRelationService.queryBoxRelation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = { JProEnum.TP, JProEnum.FunctionError })
    public InvokeResult<List<BoxRelation>> queryBoxRelation(BoxRelation relation) {
        InvokeResult<List<BoxRelation>> result = new InvokeResult<>();
        if (null == relation
                || (StringUtils.isBlank(relation.getBoxCode()) && StringUtils.isBlank(relation.getRelationBoxCode()))
                || null == relation.getCreateSiteCode()) {
            result.parameterError("缺少必要参数");
            return result;
        }
        try {

            result.setData(boxRelationDao.queryBoxRelation(relation));

        }
        catch (Exception ex) {
            LOGGER.error("查询箱号绑定记录异常. param:{}", JsonHelper.toJson(relation), ex);
            result.error();
        }

        return result;
    }

    @Override
    public InvokeResult<List<BoxRelation>> getRelationsByBoxCode(String boxCode) {
        InvokeResult<List<BoxRelation>> result = new InvokeResult<>();
        if (StringUtils.isBlank(boxCode)) {
            result.parameterError("缺少必要参数");
            return result;
        }
        try {

            result.setData(boxRelationDao.getByBoxCode(boxCode));

        }
        catch (Exception ex) {
            LOGGER.error("根据箱号查新绑定记录失败. param:{}", boxCode, ex);
            result.error();
        }

        return result;
    }

    @Override
    @JProfiler(jKey = "dms.web.BoxRelationService.saveBoxRelation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = { JProEnum.TP, JProEnum.FunctionError })
    public InvokeResult<Boolean> saveBoxRelation(BoxRelation relation) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        if (null == relation
                || StringUtils.isBlank(relation.getBoxCode())
                || StringUtils.isBlank(relation.getRelationBoxCode())
                || null == relation.getCreateSiteCode()) {
            result.parameterError("缺少必要参数");
            return result;
        }

        String nxKey = CacheKeyConstants.BOX_BIND_NX_KEY + relation.getCreateSiteCode() + Constants.SEPARATOR_COLON + relation.getRelationBoxCode();
        boolean setKeySuccess = cacheService.setNx(nxKey, "lock", EXPIRE_TIME_TEN_SECOND, TimeUnit.SECONDS);
        if (!setKeySuccess) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("WJ箱号[{}]获得绑定Redis锁失败", relation.getRelationBoxCode());
            }
            result.customMessage(DmsMessageConstants.CODE_50004, MessageFormat.format(DmsMessageConstants.MESSAGE_50004, relation.getRelationBoxCode()));
            return result;
        }

        try {

            // 校验是否符合绑定的条件
            InvokeResult<Boolean> chkRet = this.checkAllowBind(relation);
            if (!chkRet.codeSuccess()) {
                return chkRet;
            }

            doSaveBoxRelation(relation);
        }
        catch (Exception ex) {
            LOGGER.error("保存箱号绑定记录异常. body:{}", JsonHelper.toJson(relation), ex);
            result.error("后端服务处理异常，请咚咚联系分拣小秘[xnfjxm]");
        }
        finally {
            cacheService.del(nxKey);
        }

        return result;
    }

    @Override
    public InvokeResult<Boolean> saveBoxRelationWithoutCheck(BoxRelation relation) {
        try {
            doSaveBoxRelation(relation);
        } catch (Exception e) {
            LOGGER.error("saveBoxRelationWithoutCheck err out box:{},inner box:{} ",relation.getBoxCode(),relation.getRelationBoxCode(),e);
            return  new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE,false);
        }
        return  new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,true);
    }

    private void doSaveBoxRelation(BoxRelation relation) {
        boolean saveNewRelations = false;
        BoxRelation upRecord = new BoxRelation(relation.getCreateSiteCode(), relation.getRelationBoxCode());
        List<BoxRelation> existRelations = boxRelationDao.queryBoxRelation(upRecord);
        if (CollectionUtils.isNotEmpty(existRelations)) {
            BoxRelation oneRecord = existRelations.get(0);
            // 绑定的箱号不同时，逻辑删除之前的绑定记录
            if (!ObjectUtils.equals(oneRecord.getBoxCode(), relation.getBoxCode())) {
                saveNewRelations = true;
                upRecord.setYn(Constants.YN_NO);
                upRecord.setUpdateUserErp(relation.getUpdateUserErp());
                upRecord.setUpdateUserName(relation.getUpdateUserName());
                upRecord.setUpdateTime(new Date());
                boxRelationDao.updateByUniqKey(upRecord);
            }
        }
        else {
            saveNewRelations = true;
        }

        if (saveNewRelations) {
            boxRelationDao.insert(relation);
        }
    }

    private InvokeResult<Boolean> checkAllowBind(BoxRelation relation) {

        InvokeResult<Boolean> result = new InvokeResult<>();

        // 超过绑定数量限制不能绑定
        int existRelations = boxRelationDao.countByBoxCode(relation);
        if (dmsConfigManager.getPropertyConfig().getBCContainWJNumberLimit() > 0
                && existRelations >= dmsConfigManager.getPropertyConfig().getBCContainWJNumberLimit()) {
            result.customMessage(DmsMessageConstants.CODE_50001, MessageFormat.format(DmsMessageConstants.MESSAGE_50001, dmsConfigManager.getPropertyConfig().getBCContainWJNumberLimit()));
            return result;
        }

        // BC箱号已发货不能绑定
        if (boxService.checkBoxIsSent(relation.getBoxCode(), relation.getCreateSiteCode().intValue())) {
            result.customMessage(DmsMessageConstants.CODE_50002, DmsMessageConstants.MESSAGE_50002);
            return result;
        }

        // BC箱号和WJ箱号目的地不一致不能绑定
        Box BCBox = boxService.findBoxByCode(relation.getBoxCode());
        Box fileBox = boxService.findBoxByCode(relation.getRelationBoxCode());
        if (null != BCBox
                && null != fileBox
                && !ObjectUtils.equals(BCBox.getReceiveSiteCode(), fileBox.getReceiveSiteCode())) {

            result.customMessage(DmsMessageConstants.CODE_50003, DmsMessageConstants.MESSAGE_50003);
            return result;
        }

        // WJ是空箱不允许绑定
        if (sortingDao.findPackCount(relation.getCreateSiteCode().intValue(), relation.getRelationBoxCode()) == 0) {
            result.customMessage(DmsMessageConstants.CODE_50005, DmsMessageConstants.MESSAGE_50005);
            return result;
        }

        return result;
    }

    @Override
    public InvokeResult<Boolean> releaseBoxRelation(BoxRelation relation) {
        try {
            relation.setYn(Constants.YN_NO);
            relation.setUpdateTime(new Date());
            boxRelationDao.updateByUniqKey(relation);
        } catch (Exception e) {
            LOGGER.error("releaseBoxRelation err out box:{},inner box:{} ",relation.getBoxCode(),relation.getRelationBoxCode(),e);
            return  new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE,false);
        }
        return  new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,true);
    }

    @Override
    public int countByBoxCode(BoxRelation relation) {
        return boxRelationDao.countByBoxCode(relation);
    }

    @Override
    public InvokeResult<List<BoxRelation>> getBoxCodeByRelationCode(String boxCode) {
        InvokeResult<List<BoxRelation>> result = new InvokeResult<>();
        if (StringUtils.isBlank(boxCode)) {
            result.parameterError("缺少必要参数");
            return result;
        }
        try {

            result.setData(boxRelationDao.getByRelationCode(boxCode));

        }
        catch (Exception ex) {
            LOGGER.error("根据箱号查新绑定记录失败. param:{}", boxCode, ex);
            result.error();
        }

        return result;
    }
}
