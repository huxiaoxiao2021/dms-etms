package com.jd.bluedragon.distribution.rest.batch;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BatchInfoRequest;
import com.jd.bluedragon.distribution.api.response.BatchInfoResponse;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.ProductResponse;
import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import com.jd.bluedragon.distribution.batch.service.BatchInfoService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.utils.DateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BatchInfoResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BatchInfoService batchInfoService;

    @POST
    @Path("/batchInfo/addbatchinfo")
    public BatchInfoResponse addBatchInfo(BatchInfoRequest request) {
        Assert.notNull(request.getCreateSiteCode(), "波次创建站点号不能为空");
        Assert.notNull(request.getCreateUser(), "波次创建人");
        Assert.notNull(request.getCreateUserCode(), "波次创建人CODE");
        Assert.notNull(request.getUpdateUser(), "波次更新时间");
        Assert.notNull(request.getUpdateUserCode(), "波次更新人CODE");
        BatchInfo batchInfo = this.toBatchInfo(request);
        batchInfoService.add(batchInfo);
        List<BatchInfo> lst = Lists.newArrayList();
        lst.add(batchInfo);
        return this.ok(lst);
    }

    @POST
    @Path("/batchInfo/updatebatchinfo")
    public BatchInfoResponse updateBatchInfo(BatchInfoRequest request) {
        Assert.notNull(request.getCreateSiteCode(), "波次创建站点号不能为空");
        Assert.notNull(request.getCreateUser(), "波次创建人");
        Assert.notNull(request.getCreateUserCode(), "波次创建人CODE");
        Assert.notNull(request.getUpdateUser(), "波次更新时间");
        Assert.notNull(request.getUpdateUserCode(), "波次更新人CODE");
        BatchInfo batchInfo = this.toBatchInfo(request);
        batchInfoService.add(batchInfo);
        List<BatchInfo> lst = Lists.newArrayList();
        lst.add(batchInfo);
        return this.ok(lst);
    }

    @GET
    @Path("/batchinfo/findMaxCreqteTimeBatchInfo/{createSiteCode}")
    public BatchInfoResponse findMaxCreqteTimeBatchInfo(@PathParam("createSiteCode") Integer createSiteCode) {
        Assert.notNull(createSiteCode, "站点号不能为空!");
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setCreateSiteCode(createSiteCode);
        return this.ok(batchInfoService.findMaxCreateTimeBatchInfo(batchInfo));
    }

    private BatchInfo toBatchInfo(BatchInfoRequest request) {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setCreateSiteCode(request.getCreateSiteCode());
        batchInfo.setCreateUser(request.getCreateUser());
        batchInfo.setCreateUserCode(request.getCreateUserCode());
        batchInfo.setUpdateUser(request.getUpdateUser());
        batchInfo.setUpdateUserCode(request.getUpdateUserCode());

        return batchInfo;
    }

    @GET
    @Path("/batch/findBatchInfo/{createSiteCode}")
    @Profiled(tag = "BatchInfoResource.findBatchInfo")
    public BatchInfoResponse findBatchInfo(@PathParam("createSiteCode") Integer createSiteCode) {
        Assert.notNull(createSiteCode, "createSiteCode must not be null");
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setCreateSiteCode(createSiteCode);
        List<BatchInfo> aBatchInfos = batchInfoService.findBatchInfo(batchInfo);
        return ok(aBatchInfos);
    }

    @GET
    @Path("/batch/findAllBatchInfo/{createSiteCode}/{createTime}/{updateTime}")
    @Profiled(tag = "BatchInfoResource.findAllBatchInfo")
    public BatchInfoResponse findAllBatchInfo(@PathParam("createSiteCode") Integer createSiteCode,
                                              @PathParam("createTime") String createTime,
                                              @PathParam("updateTime") String updateTime) {
        Assert.notNull(createSiteCode, "createSiteCode must not be null");
        Assert.notNull(createTime, "createSiteCode must not be null");
        Assert.notNull(updateTime, "createSiteCode must not be null");
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setCreateSiteCode(createSiteCode);
        batchInfo.setCreateTime(DateHelper.parseDateTime(createTime));
        batchInfo.setUpdateTime(DateHelper.parseDateTime(updateTime));
        List<BatchInfo> aBatchInfos = batchInfoService.findAllBatchInfo(batchInfo);
        return ok(aBatchInfos);
    }

    private BatchInfoResponse ok(List<BatchInfo> aBatchInfos) {
        BatchInfoResponse response = new BatchInfoResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        List<com.jd.bluedragon.distribution.api.response.BatchInfo> bBatchInfos = new ArrayList<com.jd.bluedragon.distribution.api.response.BatchInfo>();
        for (BatchInfo aBatchInfo : aBatchInfos) {
            com.jd.bluedragon.distribution.api.response.BatchInfo bBatchInfo = new com.jd.bluedragon.distribution.api.response.BatchInfo();
            BeanUtils.copyProperties(aBatchInfo, bBatchInfo);
            bBatchInfos.add(bBatchInfo);
        }
        response.setData(bBatchInfos);

        return response;
    }
}
