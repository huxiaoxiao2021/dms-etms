package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.ucc.UccConfigService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BusinessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <ul>
 *     <li>文件类型的包裹必须装入WJ开头或者TC开头的箱中</li>
 * </ul>
 *
 * @ClassName FilePackageSortingFilter
 * @Description 文件类型包裹集包限制
 * @Author wyh
 * @Date 2020/12/16 10:20
 **/
public class FilePackageSortingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePackageSortingFilter.class);

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private UccConfigService uccConfigService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        if (uccConfigService.siteEnableFilePackageCheck(request.getCreateSiteCode())) {

            if (waybillService.checkIsFilePack(request.getWaybillCache().getWaybillSign())) {

                if (BusinessHelper.isBoxcode(request.getBoxCode())
                        && !(request.getBoxCode().startsWith(Box.TYPE_WJ) || request.getBoxCode().startsWith(Box.TYPE_TC))) {

                    throw new SortingCheckException(SortingResponse.CODE_29601, SortingResponse.MESSAGE_29601);
                }
            }
        }

        chain.doFilter(request, chain);
    }
}
