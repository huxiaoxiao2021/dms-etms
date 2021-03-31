package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageStorageTempStatusEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo.DiscardedPackageStorageTempVo;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 快递弃件暂存
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-31 14:45:51 周三
 */
@Service("discardedPackageStorageTempService")
public class DiscardedPackageStorageTempServiceImpl implements DiscardedPackageStorageTempService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscardedPackageStorageTempDao discardedPackageStorageTempDao;

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<Long> selectCount(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectCount");
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            long total = discardedPackageStorageTempDao.selectCount(query);
            result.setData(total);
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<List<DiscardedPackageStorageTemp>> selectList(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectList");
        Response<List<DiscardedPackageStorageTemp>> result = new Response<>();
        result.toSucceed();
        try {
            List<DiscardedPackageStorageTemp> dataList = discardedPackageStorageTempDao.selectList(query);
            result.setData(dataList);
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<PageDto<DiscardedPackageStorageTempVo>> selectPageList(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectPageList");
        Response<PageDto<DiscardedPackageStorageTempVo>> result = new Response<>();
        result.toSucceed();
        PageDto<DiscardedPackageStorageTempVo> pageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
        List<DiscardedPackageStorageTempVo> dataList = new ArrayList<>();
        try {
            Response<Void> checkResult = this.checkPram4SelectPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            long total = discardedPackageStorageTempDao.selectCount(query);
            pageDto.setTotalRow(new Long(total).intValue());
            if (total > 0) {
                List<DiscardedPackageStorageTemp> rawDataList = discardedPackageStorageTempDao.selectList(query);
                Map<Integer, String> discardedPackageStorageTempStatusEnumMap = DiscardedPackageStorageTempStatusEnum.ENUM_MAP;
                for (DiscardedPackageStorageTemp discardedPackageStorageTemp : rawDataList) {
                    DiscardedPackageStorageTempVo vo = new DiscardedPackageStorageTempVo();
                    BeanUtils.copyProperties(discardedPackageStorageTemp, vo);
                    vo.setFirstScanTimeFormative(DateUtil.formatDateTime(discardedPackageStorageTemp.getCreateTime()));
                    vo.setLastOperateTimeFormative(DateUtil.formatDateTime(discardedPackageStorageTemp.getUpdateTime()));
                    String statusStr = discardedPackageStorageTempStatusEnumMap.get(vo.getStatus());
                    vo.setStatusStr(statusStr != null ? statusStr : DiscardedPackageStorageTempStatusEnum.UNKNOW.getName());
                    vo.setIsCodStr(Objects.equals(vo.getCod(), Constants.YN_YES) ? "是" : "否");
                    dataList.add(vo);
                }
            }
            pageDto.setResult(dataList);
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectPageList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(pageDto);
        return result;
    }

    private Response<Void> checkPram4SelectPageList(DiscardedPackageStorageTempQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(query.getPageNumber() <= 0){
            result.toError("参数错误，pageNumber必须大于0");
            return result;
        }
        if(query.getPageSize() == null){
            result.toError("参数错误，pageSize不能为空");
            return result;
        }
        return result;

    }
}
