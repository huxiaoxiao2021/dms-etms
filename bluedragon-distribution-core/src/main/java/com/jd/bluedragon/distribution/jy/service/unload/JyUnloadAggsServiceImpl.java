package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanStatisticsDto;
import com.jd.bluedragon.distribution.jy.enums.GoodsTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.utils.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JyUnloadAggsServiceImpl implements JyUnloadAggsService {
    @Autowired
    JyUnloadAggsDao jyUnloadAggsDao;

    @Override
    public int insert(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.insert(entity);
    }

    @Override
    public int insertOrUpdate(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.insertOrUpdate(entity);
    }

    @Override
    public List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.queryByBizId(entity);
    }

    @Override
    public List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity) {
        List<GoodsCategoryDto> categoryDtoList = jyUnloadAggsDao.queryGoodsCategoryStatistics(entity);
        if (ObjectHelper.isNotNull(categoryDtoList)) {
            for (GoodsCategoryDto categoryDto : categoryDtoList) {
                categoryDto.setName(GoodsTypeEnum.getGoodsDesc(categoryDto.getType()));

                if (!ObjectHelper.isNotNull(entity.getBoardCode())) {//板没有待扫的语义数据
                    categoryDto.setWaitScanCount(categoryDto.getShouldScanCount() - categoryDto.getHaveScanCount());
                }
            }
            return categoryDtoList;
        }
        return null;
    }

    @Override
    public List<ExcepScanDto> queryExcepScanStatistics(JyUnloadAggsEntity entity) {
        ScanStatisticsDto scanStatisticsDto = jyUnloadAggsDao.queryExcepScanStatistics(entity);
        if (ObjectHelper.isNotNull(scanStatisticsDto)) {
            List<ExcepScanDto> excepScanDtoList = new ArrayList<>();
            ExcepScanDto waitScan = new ExcepScanDto();
            waitScan.setType(UnloadBarCodeQueryEntranceEnum.TO_SCAN.getCode());
            waitScan.setName(UnloadBarCodeQueryEntranceEnum.TO_SCAN.getName());
            waitScan.setCount(scanStatisticsDto.getWaitScanCount());

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
            return excepScanDtoList;
        }
        return null;
    }
}
