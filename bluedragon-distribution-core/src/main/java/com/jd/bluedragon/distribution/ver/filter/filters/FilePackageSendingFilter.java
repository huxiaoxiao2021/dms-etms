package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.ucc.UccConfigService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <ul>
 *     <li>文件类型的包裹不允许按运单或包裹发货，必须装入WJ开头或者TC开头的箱中</li>
 * </ul>
 *
 * @ClassName FilePackageSortingFilter
 * @Description 文件类型包裹发货限制
 * @Author wyh
 * @Date 2020/12/16 10:20
 **/
public class FilePackageSendingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePackageSendingFilter.class);

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private UccConfigService uccConfigService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        if (uccConfigService.siteEnableFilePackageCheck(request.getCreateSiteCode())) {

            if (waybillService.checkIsFilePack(request.getWaybillCache().getWaybillSign())) {

                // 文件标识的不能按运单或者包裹发货
                if (!BusinessUtil.isBoxcode(request.getBoxCode())) {

                    throw new SortingCheckException(DeliveryResponse.CODE_40100, DeliveryResponse.MESSAGE_40100);
                }
            }
        }

        chain.doFilter(request, chain);
    }
}
