package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuzuxiang on 2017/3/24.
 */
public class TrustToThirdFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 北京快递全峰 **/
    private static final Integer THIRD_TRUST_SITE_BJ_QUANFENG = 597579;

    /** 上海快递全峰 **/
    private static final Integer THIRD_TRUST_SITE_SH_QUANFENG = 597580;

    /** 广州快递全峰 **/
    private static final Integer THIRD_TRUST_SITE_GZ_QUANFENG = 597581;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("trust to quanFeng third express delivery filter process...");
        Boolean bool = Boolean.FALSE;
        if(null != request.getReceiveSiteCode()){
            bool = this.trustThirdSite(request.getReceiveSiteCode());
        }
        if(bool){
            throw new SortingCheckException(SortingResponse.CODE_DIRECT_PASS,SortingResponse.WAYBILL_DIRECT_PASS);
        }else{
            chain.doFilter(request,chain);
        }
    }

    private Boolean trustThirdSite(Integer receiveSiteCode) {
        Boolean bool = Boolean.FALSE;
            logger.info("目的站点：" + receiveSiteCode);
        if(null != receiveSiteCode && 0 != receiveSiteCode){
            if(THIRD_TRUST_SITE_BJ_QUANFENG.equals(receiveSiteCode)){
                bool = Boolean.TRUE;
            }else if(THIRD_TRUST_SITE_SH_QUANFENG.equals(receiveSiteCode)){
                bool = Boolean.TRUE;
            }else if(THIRD_TRUST_SITE_GZ_QUANFENG.equals(receiveSiteCode)){
                bool = Boolean.TRUE;
            }
        }
        if(bool){
            logger.info("目的站点为被信任的目的站点：" + receiveSiteCode);
        }
        return bool;
    }
}
