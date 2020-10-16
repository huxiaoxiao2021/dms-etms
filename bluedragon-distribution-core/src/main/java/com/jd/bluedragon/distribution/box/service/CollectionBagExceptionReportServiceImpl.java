package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.bagException.enums.CollectionBagExceptionReportTypeEnum;
import com.jd.bluedragon.distribution.bagException.service.CollectionBagExceptionReportService;
import com.jd.bluedragon.distribution.bagException.vo.CollectionBagExceptionReportVo;
import com.jd.bluedragon.distribution.bagException.dao.CollectionBagExceptionReportDao;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.bagException.request.CollectionBagExceptionReportQuery;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 集包异常举报
 *
 * @author fanggang7
 * @time 2020-09-23 21:44:53 周三
 */
@Service("dmsCollectionBagExceptionReportService")
@Slf4j
public class CollectionBagExceptionReportServiceImpl implements CollectionBagExceptionReportService {

    @Autowired
    private CollectionBagExceptionReportDao collectionBagExceptionReportDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 分页查询通知数据
     *
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-07-02 16:41:33 周四
     */
    @Override
    public Response<PageDto<CollectionBagExceptionReportVo>> queryPageList(CollectionBagExceptionReportQuery query) {
        log.info("CollectionBagExceptionReportServiceImpl.queryPageList param: {}", JSON.toJSONString(query));
        Response<PageDto<CollectionBagExceptionReportVo>> result = new Response<>();
        result.toSucceed();
        PageDto<CollectionBagExceptionReportVo> pageData = new PageDto<>();
        pageData.setCurrentPage(query.getPageNumber());
        pageData.setPageSize(query.getLimit());
        List<CollectionBagExceptionReportVo> dataList = new ArrayList<>();
        long total = 0;
        query.setYn(1);
        try {
            Response<Boolean> checkResult = this.checkParam4QueryPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            total = collectionBagExceptionReportDao.queryCount(query);
            if(total > 0){
                List<CollectionBagExceptionReport> recordList = collectionBagExceptionReportDao.queryList(query);
                for (CollectionBagExceptionReport collectionBagExceptionReport : recordList) {
                    dataList.add(this.generateCollectionBagExceptionReportVo(collectionBagExceptionReport));
                }
            }

        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("CollectionBagExceptionReportServiceImpl.queryPageList exception: {}", e.getMessage(), e);
        }
        pageData.setTotalRow((int)total);
        pageData.setResult(dataList);
        result.setData(pageData);
        return result;
    }

    private Response<Boolean> checkParam4QueryPageList(CollectionBagExceptionReportQuery query){
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        if(StringUtils.isNotEmpty(query.getCreateTimeFromStr())){
            query.setCreateTimeFrom(DateUtil.parse(query.getCreateTimeFromStr(), DateUtil.FORMAT_DATE_TIME));
        }
        if(StringUtils.isNotEmpty(query.getCreateTimeToStr())){
            query.setCreateTimeTo(DateUtil.parse(query.getCreateTimeToStr(), DateUtil.FORMAT_DATE_TIME));
        }
        return result;
    }

    private CollectionBagExceptionReportVo generateCollectionBagExceptionReportVo(CollectionBagExceptionReport collectionBagExceptionReport){
        CollectionBagExceptionReportVo vo = new CollectionBagExceptionReportVo();
        BeanUtils.copyProperties(collectionBagExceptionReport, vo);
        vo.setReportTypeName(CollectionBagExceptionReportTypeEnum.getEnumNameByCode(vo.getReportType()));
        vo.setReportTimeFormative(DateUtil.format(collectionBagExceptionReport.getCreateTime(), DateUtil.FORMAT_DATE_TIME));
        List<String> imgUrlList = new ArrayList<>();
        if(StringUtils.isNotBlank(vo.getReportImg())){
            // imgUrlList = Arrays.asList(vo.getReportImg().split(","));
            imgUrlList = JSON.parseArray(vo.getReportImg(), String.class);
        }
        vo.setReportImgUrlList(imgUrlList);
        // 查询箱号始发地、目的地站点名称
        BaseStaffSiteOrgDto siteStart = baseMajorManager.getBaseSiteBySiteId(vo.getBoxStartId().intValue());
        if(siteStart != null){
            vo.setBoxStartSiteName(siteStart.getSiteName());
        }
        BaseStaffSiteOrgDto siteEnd = baseMajorManager.getBaseSiteBySiteId(vo.getBoxEndId().intValue());
        if(siteEnd != null){
            vo.setBoxEndSiteName(siteEnd.getSiteName());
        }
        return vo;
    }

    /**
     * 按主键查询
     *
     * @param id 主键ID
     * @return CollectionBagExceptionReport 通知详情
     * @author fanggang7
     * @date 2020-07-07 19:52:00 周二
     */
    @Override
    public Response<CollectionBagExceptionReport> selectByPrimaryKey(Long id) {
        log.info("CollectionBagExceptionReportServiceImpl.selectByPrimaryKey param: {}", id);
        Response<CollectionBagExceptionReport> result = new Response<>();
        result.toSucceed();
        try {
            CollectionBagExceptionReport exceptionReport = collectionBagExceptionReportDao.selectByPrimaryKey(id);
            result.setData(exceptionReport);
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("CollectionBagExceptionReportServiceImpl.selectByPrimaryKey exception: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 新增
     *
     * @param collectionBagExceptionReport CollectionBagExceptionReport
     * @return Response 插入后自增ID
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    @Override
    public Response<Integer> add(CollectionBagExceptionReport collectionBagExceptionReport) {
        log.info("CollectionBagExceptionReportServiceImpl.add param: {}", JSON.toJSONString(collectionBagExceptionReport));
        Response<Integer> result = new Response<>();
        result.toSucceed();
        try {
            int insertCount = collectionBagExceptionReportDao.insertSelective(collectionBagExceptionReport);
            result.setData(insertCount);
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("CollectionBagExceptionReportServiceImpl.add exception: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按主键更新
     *
     * @param collectionBagExceptionReport CollectionBagExceptionReport
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    @Override
    public Response<Integer> updateByPrimaryKey(CollectionBagExceptionReport collectionBagExceptionReport) {
        log.info("CollectionBagExceptionReportServiceImpl.updateByPrimaryKey param: {}", JSON.toJSONString(collectionBagExceptionReport));
        Response<Integer> result = new Response<>();
        result.toSucceed();
        try {
            int updateCount = collectionBagExceptionReportDao.updateByPrimaryKey(collectionBagExceptionReport);
            result.setData(updateCount);
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("CollectionBagExceptionReportServiceImpl.updateByPrimaryKey exception: {}", e.getMessage(), e);
        }
        return result;
    }

}
