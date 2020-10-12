package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 妥投禁止逆向拦截
 * @author hanjiaxing3
 * @Description: 类描述信息
 * @date 2018年10月11日
 */
public class ReverseOperationAllowedFilter implements Filter {

    @Autowired
    private WaybillService waybillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("妥投禁止逆向操作拦截开始...");

        //获取校验结果
        InvokeResult<Boolean> result = waybillService.isReverseOperationAllowed(request.getWaybillCode(), request.getCreateSiteCode());

        logger.info("妥投禁止逆向操作拦截开始，返回结果:" + JsonHelper.toJson(result));

        //获取结果不为空且返回data的code值
        if (result != null) {
            Integer resultCode = result.getCode();
            if (resultCode.equals(SortingResponse.CODE_29121)) {
                throw new SortingCheckException(SortingResponse.CODE_29121, SortingResponse.MESSAGE_29121);
            }
            else if (resultCode.equals(SortingResponse.CODE_31121)) {
                throw new SortingCheckException(SortingResponse.CODE_31121, SortingResponse.MESSAGE_31121);
            }
            else if (request.getPdaOperateRequest().getIsLoss()!= null && request.getPdaOperateRequest().getIsLoss().equals(0) && resultCode.equals(SortingResponse.CODE_31122)) {
                //排除使用报丢分拣时接口
                throw new SortingCheckException(SortingResponse.CODE_31122, SortingResponse.MESSAGE_31122);
            }
            else if (resultCode.equals(SortingResponse.CODE_29122)) {
                throw new SortingCheckException(SortingResponse.CODE_29122, SortingResponse.MESSAGE_29122);
            }
        }
        
        chain.doFilter(request, chain);
    }
}
