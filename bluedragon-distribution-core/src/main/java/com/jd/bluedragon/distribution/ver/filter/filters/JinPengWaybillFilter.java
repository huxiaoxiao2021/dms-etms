package com.jd.bluedragon.distribution.ver.filter.filters;

import com.google.common.base.Strings;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: JinPengWaybillFilter
 * @Description: 金鹏订单
 * @author: hujiping
 * @date: 2018/8/22 18:14
 */
public class JinPengWaybillFilter implements Filter {

    private final String PERFORMANCE_DMSSITECODE_SWITCH = "performance.dmsSiteCode.switch";

    @Autowired
    private BasicPrimaryWS basicPrimaryWS;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //是否是金鹏订单
        if(request.getWaybillCache() != null && request.getWaybillCache().getWaybillSign() != null &&
                BusinessUtil.isPerformanceOrder(request.getWaybillCache().getWaybillSign())){
            //登陆人机构与末级分拣中心是否一致
            Integer destinationDmsId = null;
            BaseStaffSiteOrgDto bDto = basicPrimaryWS.getBaseSiteBySiteId(request.getWaybillCache().getSiteCode());
            if(bDto != null && bDto.getDmsId() != null){
                //末级分拣中心
                destinationDmsId = bDto.getDmsId();
            }
            String dmsIds = PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH);
            String[] dmsCodes = dmsIds.split(",");
            List<String> dmsCodeList = Arrays.asList(dmsCodes);
            if(dmsCodeList.size() > 0 && dmsCodeList.contains(String.valueOf(destinationDmsId)) ||
                    Strings.isNullOrEmpty(PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH))) {
                if(request.getPdaOperateRequest().getCreateSiteCode().equals(destinationDmsId)) {
                    String waybillCode = request.getWaybillCode();
                    String waybillSign = request.getWaybillCache().getWaybillSign();
                    boolean result = storagePackageMService.checkWaybillCanSend(waybillCode,waybillSign);
                    //判断运单是否发货
                    if(Boolean.FALSE.equals(result)){
                        throw new SortingCheckException(SortingResponse.CODE_29310, SortingResponse.MESSAGE_29310);
                    }
                }
            }
        }
        chain.doFilter(request, chain);
    }
}
