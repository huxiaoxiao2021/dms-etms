package com.jd.bluedragon.distribution.rest.sorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;
import com.jd.bluedragon.distribution.sorting.service.SortingExceptionService;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 自动分拣机分拣拦截日志
 * Created by wangtingwei on 2014/10/24.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SortingExceptionResource {

    private static final Logger log = LoggerFactory.getLogger(SortingExceptionResource.class);

    @Autowired
    private SortingExceptionService sortingExceptionService;
    /**
     * 查询自动分拣拦截日志
     * @param batchCode 波次号
     * @param siteCode  站点ID
     * @return          日志列表
     */
    @GET
    @Path("/sortingexception/search/{batchCode}/{siteCode}")
    public InvokeResult<List<SortingException>> search(@PathParam("batchCode") String batchCode,@PathParam("siteCode") Integer siteCode){
        InvokeResult<List<SortingException>> result=new InvokeResult<List<SortingException>>();
        if(StringHelper.isEmpty(batchCode)||StringHelper.isEmpty(batchCode.trim())){
            result.setCode(499);
            result.setMessage("查询条件：波次号不能为空");
            log.info(result.getMessage());
            return result;
        }
        if(null==siteCode||siteCode.intValue()<=0){
            result.setCode(499);
            result.setMessage("查询条件：发货站点ID不正确");
            log.info(result.getMessage());
            return result;
        }
        result.setCode(200);
        result.setMessage("OK");
        result.setData(sortingExceptionService.search(batchCode,siteCode));
        return result;
    }
}
