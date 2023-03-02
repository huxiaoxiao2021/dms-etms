package com.jd.bluedragon.distribution.jy.service.unload;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.JySendProductAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.dao.unload.*;
import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanStatisticsDto;
import com.jd.bluedragon.distribution.jy.enums.GoodsTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.unload.DimensionQueryDto;
import com.jd.bluedragon.distribution.jy.enums.UnloadBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/10/9 6:35 PM
 */
@Service("jyUnloadAggsService")
public class JyUnloadAggsServiceImpl implements JyUnloadAggsService {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadAggsServiceImpl.class);

    @Autowired
    private JyUnloadAggsSpecialDao jyUnloadAggsSpecialDao;

    @Override
    public int insert(JyUnloadAggsEntity entity) {
        return jyUnloadAggsSpecialDao.insert(entity);
    }

    @Override
    public List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryByBizId");
        List<JyUnloadAggsEntity> list = jyUnloadAggsDao.queryByBizId(entity);
        Profiler.registerInfoEnd(info);
        return list;
    }

    @Override
    public List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryGoodsCategoryStatistics");
        List<GoodsCategoryDto> categoryDtoList = jyUnloadAggsDao.queryGoodsCategoryStatistics(entity);
        if (ObjectHelper.isNotNull(categoryDtoList)) {
            for (GoodsCategoryDto categoryDto : categoryDtoList) {
                categoryDto.setName(GoodsTypeEnum.getGoodsDesc(categoryDto.getType()));
                categoryDto.setOrder(GoodsTypeEnum.getGoodsOrder(categoryDto.getType()));
                if (!ObjectHelper.isNotNull(entity.getBoardCode())) {//板没有待扫的语义数据
                    categoryDto.setWaitScanCount(getWaitScan(entity.getBizId(), categoryDto.getShouldScanCount(), categoryDto.getHaveScanCount()));
                }
            }
            Collections.sort(categoryDtoList, new GoodsCategoryDto.OrderComparator());
            Profiler.registerInfoEnd(info);
            return categoryDtoList;
        }
        Profiler.registerInfoEnd(info);
        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadAggsServiceImpl.queryExcepScanStatistics", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<ExcepScanDto> queryExcepScanStatistics(JyUnloadAggsEntity entity) {

        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryExcepScanStatistics");
        ScanStatisticsDto scanStatisticsDto = jyUnloadAggsDao.queryExcepScanStatistics(entity);
        if (ObjectHelper.isNotNull(scanStatisticsDto)) {
            List<ExcepScanDto> excepScanDtoList = new ArrayList<>();
            ExcepScanDto waitScan = new ExcepScanDto();
            waitScan.setType(UnloadBarCodeQueryEntranceEnum.TO_SCAN.getCode());
            waitScan.setName(UnloadBarCodeQueryEntranceEnum.TO_SCAN.getName());
            waitScan.setCount(getWaitScan(entity.getBizId(), scanStatisticsDto.getShouldScanCount(), scanStatisticsDto.getHaveScanCount()));

            ExcepScanDto interceptScan = new ExcepScanDto();
            interceptScan.setType(UnloadBarCodeQueryEntranceEnum.INTERCEPT.getCode());
            interceptScan.setName(UnloadBarCodeQueryEntranceEnum.INTERCEPT.getName());
            interceptScan.setCount(scanStatisticsDto.getInterceptCount());

            ExcepScanDto extraScan = new ExcepScanDto();
            extraScan.setType(UnloadBarCodeQueryEntranceEnum.MORE_SCAN.getCode());
            extraScan.setName(UnloadBarCodeQueryEntranceEnum.MORE_SCAN.getName());
            extraScan.setCount(scanStatisticsDto.getExtraScanCount());

            excepScanDtoList.add(waitScan);
            excepScanDtoList.add(interceptScan);
            excepScanDtoList.add(extraScan);
            Profiler.registerInfoEnd(info);
            return excepScanDtoList;
        }
        Profiler.registerInfoEnd(info);
        return null;
    }

    @Override
    public Boolean insertOrUpdateJyUnloadCarAggsMain(JyUnloadAggsEntity entity) {
        return jyUnloadAggsSpecialDao.insertOrUpdateJyUnloadCarAggsMain(entity);
    }

    @Override
    public Boolean insertOrUpdateJyUnloadCarAggsBak(JyUnloadAggsEntity entity) {
        return jyUnloadAggsSpecialDao.insertOrUpdateJyUnloadCarAggsBak(entity);
    }


    @Override
    public JyUnloadAggsEntity queryPackageStatistics(DimensionQueryDto dto) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryPackageStatistics");
        JyUnloadAggsEntity entity = jyUnloadAggsDao.queryPackageStatistics(dto);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public JyUnloadAggsEntity queryWaybillStatisticsUnderTask(DimensionQueryDto dto) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryWaybillStatisticsUnderTask");
        JyUnloadAggsEntity entity = jyUnloadAggsDao.queryWaybillStatisticsUnderTask(dto);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public JyUnloadAggsEntity queryWaybillStatisticsUnderBoard(DimensionQueryDto dto) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryWaybillStatisticsUnderBoard");
        JyUnloadAggsEntity entity = jyUnloadAggsDao.queryWaybillStatisticsUnderBoard(dto);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public JyUnloadAggsEntity queryToScanAndMoreScanStatistics(String bizId) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryToScanAndMoreScanStatistics");
        JyUnloadAggsEntity entity = jyUnloadAggsDao.queryToScanAndMoreScanStatistics(bizId);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public JyUnloadAggsEntity queryBoardStatistics(DimensionQueryDto dto) {
        JyUnloadAggsDaoStrategy jyUnloadAggsDao = jyUnloadAggsSpecialDao.getJyUnloadAggsDao();
        String keyword = jyUnloadAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JyUnloadAggsServiceImpl"+keyword+".queryBoardStatistics");
        JyUnloadAggsEntity entity = jyUnloadAggsDao.queryBoardStatistics(dto);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    private int getWaitScan(String bizId, Integer shouldScan, Integer actualScan) {
        if(shouldScan == null || shouldScan == 0 || actualScan == null) {
            return 0;
        }
        //可能出shouldScan < actualScan
        int res = shouldScan - actualScan;
        if(res < 0) {
            log.warn("JyUnloadAggsServiceImpl.getWaitScan 查任务{}待扫数据异常，待扫为{}", bizId, res);
            return 0;
        }
        return res;
    }


}
