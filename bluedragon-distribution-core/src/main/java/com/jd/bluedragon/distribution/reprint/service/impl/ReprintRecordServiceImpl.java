package com.jd.bluedragon.distribution.reprint.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.KeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.bagException.request.CollectionBagExceptionReportQuery;
import com.jd.bluedragon.distribution.reprint.dao.ReprintRecordDao;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordVo;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReprintRecordServiceImpl implements ReprintRecordService {

    private final Logger log = LoggerFactory.getLogger(ReprintRecordServiceImpl.class);

    @Autowired
    private ReprintRecordDao rePrintRecordDao;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public boolean isBarCodeRePrinted(String barCode) {
        //读取redis的缓存记录
        String cachedKey = KeyConstants.genConstantsKey(KeyConstants.REDIS_PREFIX_KEY_PACK_REPRINT_NEW, barCode);
        String barCodeCached = redisManager.getCache(cachedKey);
        //如果缓存有记录
        if (StringHelper.isNotEmpty(barCodeCached)) {
            return true;
        }
        //查询运单是否有补打记录
        int count = rePrintRecordDao.getCountByCondition(barCode);
        if (count > 0) {
            //3小时缓存
            redisManager.setex(cachedKey, 3 * 3600, barCode);
            return true;
        }

        return false;
    }

    @Override
    public void insertRePrintRecord(ReprintRecord rePrintRecord) {
        try {
            Date date = new Date();
            rePrintRecord.setOperateTime(date);
            rePrintRecord.setCreateTime(date);
            if (rePrintRecordDao.add(rePrintRecord) > 0) {
                String barCode = rePrintRecord.getBarCode();
                //读取redis的缓存记录
                String cachedKey = KeyConstants.genConstantsKey(KeyConstants.REDIS_PREFIX_KEY_PACK_REPRINT_NEW, barCode);
                //3小时缓存
                redisManager.setex(cachedKey, 3 * 3600, barCode);
            }
        } catch (Exception e) {
            log.error("插入包裹补打记录表异常", e);
        }
    }

    /**
     * 统计总数
     *
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-11-03 14:30:34 周二
     */
    @Override
    public Response<Long> queryCount(ReprintRecordQuery query) {
        if(log.isInfoEnabled()) {
            log.info("ReprintRecordServiceImpl.queryCount param: {}", JSON.toJSONString(query));
        }
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            Response<Boolean> checkResult = this.checkParam4QueryPageList(query);
            if(!checkResult.isSucceed()){
                result.toWarn(checkResult.getMessage());
                return result;
            }
            long total = rePrintRecordDao.queryCount(query);
            result.setData(total);
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("ReprintRecordServiceImpl.queryCount exception: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 分页查询通知数据
     *
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-11-03 14:30:34 周二
     */
    @Override
    public Response<PageDto<ReprintRecordVo>> queryPageList(ReprintRecordQuery query) {
        if(log.isInfoEnabled()) {
            log.info("ReprintRecordServiceImpl.queryPageList param: {}", JSON.toJSONString(query));
        }
        Response<PageDto<ReprintRecordVo>> result = new Response<>();
        result.toSucceed();
        long total = 0;
        PageDto<ReprintRecordVo> pageData = new PageDto<>();
        pageData.setCurrentPage(query.getPageNumber());
        pageData.setPageSize(query.getLimit());
        List<ReprintRecordVo> dataList = new ArrayList<>();
        try {
            Response<Boolean> checkResult = this.checkParam4QueryPageList(query);
            if(!checkResult.isSucceed()){
                result.toWarn(checkResult.getMessage());
                return result;
            }
            total = rePrintRecordDao.queryCount(query);
            if(total > 0){
                pageData.setTotalRow((int)total);
                List<ReprintRecord> recordList = rePrintRecordDao.queryList(query);
                Set<Integer> staffNoSet = new HashSet<>();
                for (ReprintRecord reprintRecord : recordList) {
                    ReprintRecordVo reprintRecordVo = this.generateReprintRecordVo(reprintRecord);
                    dataList.add(reprintRecordVo);
                    staffNoSet.add(reprintRecord.getOperatorCode());
                }
                // 组装用户erp信息
                Map<Integer, BaseStaffSiteOrgDto> staffGbStaffNoMap = new HashMap<>();
                for (Integer staffNo : staffNoSet) {
                    BaseStaffSiteOrgDto baseStaffIgnoreIsResignByErp = baseMajorManager.getBaseStaffInAllRoleByStaffNo(staffNo);
                    if(baseStaffIgnoreIsResignByErp != null) {
                        staffGbStaffNoMap.put(staffNo, baseStaffIgnoreIsResignByErp);
                    }
                }
                for (ReprintRecordVo reprintRecordVo : dataList) {
                    BaseStaffSiteOrgDto baseStaffSiteOrgDto = staffGbStaffNoMap.get(reprintRecordVo.getOperatorCode());
                    if (baseStaffSiteOrgDto != null) {
                        reprintRecordVo.setOperatorErp(baseStaffSiteOrgDto.getErp());
                    }
                }
            }
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("ReprintRecordServiceImpl.queryPageList exception: {}", e.getMessage(), e);
        }
        pageData.setTotalRow((int)total);
        pageData.setResult(dataList);
        result.setData(pageData);
        return result;
    }

    /**
     * 查询参数校验
     * @param query 查询参数
     * @return 校验结果
     * @author fanggang7
     * @time 2020-11-04 13:52:08 周三
     */
    private Response<Boolean> checkParam4QueryPageList(ReprintRecordQuery query){
        Response<Boolean> result = new Response<>();
        result.setData(true);
        result.toSucceed();

        if (query.getSiteCode() == null) {
            result.setData(false);
            result.toError("参数错误，siteCode不能为空");
            return result;
        }

        query.setIsDelete(Constants.YN_NO);
        // 转换用户ID为用户erp
        if(StringUtils.isNotEmpty(query.getOperatorErpOrName())){
            BaseStaffSiteOrgDto baseStaffIgnoreIsResignByErp = baseMajorManager.getBaseStaffIgnoreIsResignByErp(query.getOperatorErpOrName());
            if(baseStaffIgnoreIsResignByErp != null) {
                query.setOperatorId(baseStaffIgnoreIsResignByErp.getStaffNo());
            }
        }

        if(StringUtils.isNotEmpty(query.getOperateTimeFromStr())){
            query.setOperateTimeFrom(DateUtil.parse(query.getOperateTimeFromStr(), DateUtil.FORMAT_DATE_TIME));
        }
        if(StringUtils.isNotEmpty(query.getOperateTimeToStr())){
            query.setOperateTimeTo(DateUtil.parse(query.getOperateTimeToStr(), DateUtil.FORMAT_DATE_TIME));
        }
        return result;
    }

    private ReprintRecordVo generateReprintRecordVo(ReprintRecord reprintRecord) {
        ReprintRecordVo reprintRecordVo = new ReprintRecordVo();
        BeanUtils.copyProperties(reprintRecord, reprintRecordVo);
        reprintRecordVo.setOperateTimeFormative(DateUtil.format(reprintRecord.getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        return reprintRecordVo;
    }
}
