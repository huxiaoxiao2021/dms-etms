package com.jd.bluedragon.distribution.rest.storage;

import IceInternal.Ex;
import com.jcloud.jss.service.StorageService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DmsLocalServerManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 储位相关rest接口
 *
 *  2018年8月20日14:08:52
 *
 *  刘铎
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class StorageResource {

    private static Log logger = LogFactory.getLog(StorageResource.class);


    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("storagePackageMService")
    private StoragePackageMService storagePackageMService;

    @Autowired
    @Qualifier("dmsLocalServerManager")
    private DmsLocalServerManager dmsLocalServerManager;
    /**
     * 根据所属分拣中心 获取储位号
     *
     * @return
     */
    @GET
    @Path("/storage/getStorageInfo/{siteCode}")
    public InvokeResult<List<String>> getStorageInfo(@PathParam("siteCode") Long siteCode) {
        InvokeResult<List<String>> result = new InvokeResult<List<String>>();

        try {
            result.setData(dmsLocalServerManager.getStorageCodeByDmsId(siteCode.intValue()));
        }catch (Exception e){
            result.error("获取储位信息失败");
        }

        return result;
    }


    /**
     * 检查储位号是否存在
     *
     * @return
     */
    @GET
    @Path("/storage/getStorageInfo/{siteCode}/{storageCode}")
    public InvokeResult<Boolean> checkStorage(@PathParam("siteCode") Long siteCode,@PathParam("storageCode") String storageCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        try{
            result.setData(dmsLocalServerManager.checkStorage(siteCode.intValue(),storageCode));
        }catch (Exception e){
            result.error("校验储位信息失败");
        }

        return result;
    }


    /**
     * 储位暂存 上架接口
     *
     * @param putawayDTO
     * @return
     */
    @POST
    @Path("/storage/putaway")
    public InvokeResult<Boolean> putaway(PutawayDTO putawayDTO){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();
        try{
            //初始化 基础数据
            BaseStaffSiteOrgDto site = baseService.queryDmsBaseSiteByCode(putawayDTO.getCreateSiteCode().toString());
            if(site == null || site.getsId() == null){
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("未获取到对应站点信息");
            }
            putawayDTO.setCreateSiteName(site.getSiteName());
            putawayDTO.setCreateSiteType(site.getSiteType());
            putawayDTO.setOrgId(site.getOrgId());
            putawayDTO.setOrgName(site.getOrgName());

            //上架
            storagePackageMService.putaway(putawayDTO);
        }catch (StorageException e){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(e.getMessage());
        }catch (Throwable e){
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("暂存失败！系统异常");
            logger.error("暂存失败！"+putawayDTO.getBarCode(),e);
        }
        return result;
    }


    /**
     * 获取当前运单或包裹所属的履约单已经上架过的储位号
     * （如若上架过多个储位，则返回最近一次储位号）
     *
     * @param barCode 运单号/包裹号
     * @return
     */
    @GET
    @Path("/storage/getExistStorageCode/{barCode}")
    public InvokeResult<String> getExistStorageCode(@PathParam("barCode") String barCode){
        InvokeResult<String> result = new InvokeResult<String>();

        String waybillCode = barCode;
        if(BusinessHelper.isPackageCode(barCode)){
            waybillCode = BusinessHelper.getWaybillCodeByPackageBarcode(barCode);
        }
        //先检查此包裹或者运单是否已经上架
        StoragePackageD lastStoragePackageD = storagePackageMService.checkExistStorage(barCode);
        if(lastStoragePackageD != null){
            result.error("已上架，储位号："+lastStoragePackageD.getStorageCode());
        }else{
            String storageCode = storagePackageMService.getExistStorageCode(waybillCode);
            if(!StringUtils.isBlank(storageCode)){
                result.setData(storageCode);
            }
        }

        return result;
    }

    @GET
    @Path("/storage/checkStatus/{waybillCode}/{waybillSign}")
    public InvokeResult<Boolean> checkStatus(@PathParam("waybillCode") String waybillCode,@PathParam("waybillSign") String waybillSign){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        
        result.setData(storagePackageMService.checkWaybillCanSend(waybillCode,waybillSign));

        return result;
    }


}
