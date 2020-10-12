package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.common.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OneWaybillMultiPackFilter implements Filter {
    private static final Log logger = LogFactory.getLog(OneWaybillMultiPackFilter.class);

    @Autowired
    private RuleService ruleService;

    //包裹号后缀
    //fixme 之后需要考虑加上道口号
    private final String SUFFIX_PACKAGE_CODE = "-1-1-";
    private final String SUFFIX_PACKAGE_CODE_LAS = "-1-1"; //大件包裹号后缀

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("一单多件校验开始...");
        //一车一单发货支持扫描运单号，所以这个地方request.getPackageCode可能是箱号、运单号、包裹号
        if(!BusinessUtil.isBoxcode(request.getBoxCode()) && WaybillUtil.isWaybillCode(request.getPackageCode())) {
            WaybillCache waybill = request.getWaybillCache();
            if (waybill == null) {
                throw new SortingCheckException(SortingResponse.CODE_39002,
                        SortingResponse.MESSAGE_39002);
            }
            //不支持一单多件的运单
            if (waybill.getQuantity() > 1) {
                throw new SortingCheckException(SortingResponse.CODE_29404,
                        SortingResponse.MESSAGE_29404);
            }

            //扫的是运单号，而且是一单一件的，根据rule的配置进行判断
            //siteCode是0的1120的规则，如果为空或者IN_OUT为OUT的话表示开关关闭，这种情况不支持扫运单号
            //否则IN_OUT为IN的话，表示开关开启，校验商家列表是否包含当前商家，如果包含，支持运单号；如果不包含，不支持扫运单号
            if (waybill.getQuantity().equals(1)) {
                Rule rule0 = this.ruleService.get(0, "1120");
                //开关关闭不支持扫运单号
                if (rule0 == null || rule0.getInOut().equals("OFF")) {
                    throw new SortingCheckException(SortingResponse.CODE_29404,
                            SortingResponse.MESSAGE_29404);
                }
                //开关开启，只有配置的商家才支持扫运单号（如果以后按运单发货推广开，content为空所有商家的一单一件都可以通过）
                if (rule0.getInOut().equals("ON") && StringUtils.isNotBlank(rule0.getContent()) &&
                        !rule0.getContent().contains(waybill.getBusiId() + "")) {
                    throw new SortingCheckException(SortingResponse.CODE_29404,
                            SortingResponse.MESSAGE_29404);
                }
                if(WaybillUtil.isLasWaybillCode(request.getPackageCode())){
                    request.setPackageCode(request.getPackageCode() + SUFFIX_PACKAGE_CODE_LAS);
                }else{
                    request.setPackageCode(request.getPackageCode() + SUFFIX_PACKAGE_CODE);
                }
            }
        }
        chain.doFilter(request, chain);
    }
}
