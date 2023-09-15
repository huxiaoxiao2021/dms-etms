package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.service.DmsSortingService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.external.service.impl
 * @Description:
 * @date Date : 2023年09月14日 15:14
 */
@Service("dmsSortingService")
public class DmsSortingServiceImpl implements DmsSortingService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SortingService sortingService;
    /**
     *
     * @param packageCode
     * @param createSiteCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsSortingServiceImpl.findSortingByPackageCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<List<SortingDto>> findSortingByPackageCode(String packageCode, Integer createSiteCode) {
        logger.info("DmsSortingServiceImpl->findSortingByPackageCode绑定集包袋,包裹号：{}，操作单位：{}", packageCode,createSiteCode);
        InvokeResult<List<SortingDto>> result = new InvokeResult<List<SortingDto>>();
        if(!WaybillUtil.isPackageCode(packageCode)){
            result.parameterError("包裹号不合法");
            return result;
        }
        if(createSiteCode == null){
            result.parameterError("站点编号为空");
            return result;
        }
        List<Sorting> sortingList = sortingService.findByWaybillCodeOrPackageCode(createSiteCode,WaybillUtil.getWaybillCodeByPackCode(packageCode),packageCode);
        result.setData(transferSortingDtoList(sortingList));
        return result;
    }

    /**
     * 转换
     * @param sortingList
     * @return
     */
    private List<SortingDto> transferSortingDtoList(List<Sorting> sortingList) {
        if(CollectionUtils.isEmpty(sortingList)){
            return new ArrayList<>(0);
        }
        List<SortingDto> result = new ArrayList<>(sortingList.size());
        for(Sorting temp : sortingList){
            SortingDto dto = new SortingDto();
            BeanUtils.copyProperties(temp,dto);
        }
        return result;
    }
}
