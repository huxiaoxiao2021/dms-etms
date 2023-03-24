package com.jd.bluedragon.distribution.web.spwms;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.external.domain.SpWmsCreateInProduct;
import com.jd.bluedragon.distribution.external.domain.SpWmsCreateInRequest;
import com.jd.bluedragon.distribution.external.service.SpWmsToolService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.spwms.SpwmsToolsTemplateVO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;

/**
 * PDA建箱包裹数配置表
 * TODO 接口权限修改
 */
@Controller
@RequestMapping("/spwmsTools")
public class SpwmsToolsController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(SpwmsToolsController.class);

    @Autowired
    private SpWmsToolService spWmsToolService;

    @Autowired
    SendCodeService sendCodeService;

    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){

        return "/spwms/spwmsTools";
    }

    /**
     * 获取列表
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<BoxLimitVO> listData(@RequestBody BoxLimitQueryDTO dto){

        return  new PagerResult();
    }
    /**
     * 获取站点名称
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/getSiteNameById")
    @ResponseBody
    public JdResponse getSiteNameById(Integer siteId){

        return  new JdResponse();

    }
    /**
     * 新建/修改
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/save")
    @ResponseBody
    public JdResponse save(@RequestBody BoxLimitDTO dto){
       return  new JdResponse();
    }
    /**
     * 删除
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/delete")
    @ResponseBody
    public JdResponse delete(@RequestBody ArrayList<Long> ids){

        return new JdResponse();
    }
    /**
     * 导入
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xlsx")) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
            List<SpwmsToolsTemplateVO> dataList = dataResolver.resolver(file.getInputStream(), SpwmsToolsTemplateVO.class, new PropertiesMetaDataFactory("/excel/spwmsTools.properties"));

            //转换对象
            if(CollectionUtils.isEmpty(dataList)){
                return new JdResponse(JdResponse.CODE_FAIL, "空内容,请检查文件");
            }
            List<SpWmsCreateInRequest> batchRequest = new ArrayList<>();

            Map<String,SpWmsCreateInRequest> batchRequestOfOrderId = new HashMap<>();

            Map<String,String> spwmsBatch = new HashMap<>();
            for(SpwmsToolsTemplateVO spwmsToolsTemplateVO : dataList){
                //按备件库散列 生成批次号
                if(StringUtils.isBlank(spwmsBatch.get(spwmsToolsTemplateVO.getSpwmsId()))){
                    spwmsBatch.put(spwmsToolsTemplateVO.getSpwmsId(),makeSendCode(spwmsToolsTemplateVO.getSpwmsId()));
                }

                //已处理订单直接获取
                SpWmsCreateInRequest spWmsRequest = batchRequestOfOrderId.get(spwmsToolsTemplateVO.getOrderId());

                SpWmsCreateInProduct spWmsCreateInProduct = new SpWmsCreateInProduct();
                spWmsCreateInProduct.setSpareCode(spwmsToolsTemplateVO.getSpCode());
                spWmsCreateInProduct.setProductCode(spwmsToolsTemplateVO.getWareId());
                if(spWmsRequest == null){
                    // 不存在则创建一个新的
                    spWmsRequest =  new SpWmsCreateInRequest();

                    spWmsRequest.setWaybillCode(spwmsToolsTemplateVO.getNewWaybillCode());
                    spWmsRequest.setOpeTime(new Date());
                    spWmsRequest.setSendCode(spwmsBatch.get(spwmsToolsTemplateVO.getSpwmsId()));
                    spWmsRequest.setSpWmsCode(Integer.valueOf(spwmsToolsTemplateVO.getSpwmsId()));
                    spWmsRequest.setSpareCodes(new ArrayList<>());

                }
                //追加商品
                spWmsRequest.getSpareCodes().add(spWmsCreateInProduct);

                batchRequestOfOrderId.put(spwmsToolsTemplateVO.getOrderId(),spWmsRequest);


            }

            for(String orderId : batchRequestOfOrderId.keySet()){
                batchRequest.add(batchRequestOfOrderId.get(orderId));
            }

            InvokeResult<List<String>> invokeResult =  spWmsToolService.batchVirtualSpWmsCreateIn(batchRequest);
            if(!invokeResult.codeSuccess()){
                log.error("SpwmsToolsController toImport fail, {}", JsonHelper.toJson(invokeResult));
                return new JdResponse(JdResponse.CODE_FAIL, "失败"+invokeResult.getData().size()+"条！");
            }
            return new JdResponse();
        } catch (Exception e) {
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
    }


    /**
     * 导入2
     * 商品明细拍平的模板
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping(value = "/toImport2", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport2(@RequestParam("importExcelFile") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xlsx")) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
            List<SpwmsToolsTemplateVO> dataList = dataResolver.resolver(file.getInputStream(), SpwmsToolsTemplateVO.class, new PropertiesMetaDataFactory("/excel/spwmsTools2.properties"));

            //转换对象
            if(CollectionUtils.isEmpty(dataList)){
                return new JdResponse(JdResponse.CODE_FAIL, "空内容,请检查文件");
            }
            List<SpWmsCreateInRequest> batchRequest = new ArrayList<>();

            Map<String,SpWmsCreateInRequest> batchRequestOfOrderId = new HashMap<>();

            Map<String,String> spwmsBatch = new HashMap<>();
            for(SpwmsToolsTemplateVO spwmsToolsTemplateVO : dataList){
                //按备件库散列 生成批次号
                if(StringUtils.isBlank(spwmsBatch.get(spwmsToolsTemplateVO.getSpwmsId()))){
                    spwmsBatch.put(spwmsToolsTemplateVO.getSpwmsId(),makeSendCode(spwmsToolsTemplateVO.getSpwmsId()));
                }

                //已处理订单直接获取
                SpWmsCreateInRequest spWmsRequest = batchRequestOfOrderId.get(spwmsToolsTemplateVO.getOrderId());

                SpWmsCreateInProduct spWmsCreateInProduct = new SpWmsCreateInProduct();
                spWmsCreateInProduct.setSpareCode(spwmsToolsTemplateVO.getSpCode());
                spWmsCreateInProduct.setProductCode(spwmsToolsTemplateVO.getWareId());
                if(spWmsRequest == null){
                    // 不存在则创建一个新的
                    spWmsRequest =  new SpWmsCreateInRequest();

                    spWmsRequest.setWaybillCode(spwmsToolsTemplateVO.getNewWaybillCode());
                    spWmsRequest.setOpeTime(new Date());
                    spWmsRequest.setSendCode(spwmsBatch.get(spwmsToolsTemplateVO.getSpwmsId()));
                    spWmsRequest.setSpWmsCode(Integer.valueOf(spwmsToolsTemplateVO.getSpwmsId()));
                    spWmsRequest.setSpareCodes(new ArrayList<>());

                }
                //追加商品
                spWmsRequest.getSpareCodes().add(spWmsCreateInProduct);

                batchRequestOfOrderId.put(spwmsToolsTemplateVO.getOrderId(),spWmsRequest);


            }

            for(String orderId : batchRequestOfOrderId.keySet()){
                batchRequest.add(batchRequestOfOrderId.get(orderId));
            }

            InvokeResult<List<String>> invokeResult =  spWmsToolService.batchVirtualSpWmsCreateIn2(batchRequest);
            if(!invokeResult.codeSuccess()){
                log.error("SpwmsToolsController toImport fail, {}", JsonHelper.toJson(invokeResult));
                return new JdResponse(JdResponse.CODE_FAIL, "失败"+invokeResult.getData().size()+"条！");
            }
            return new JdResponse();
        } catch (Exception e) {
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
    }

    private String makeSendCode(String endSiteCode){
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(733578));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, endSiteCode);
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, "sys");
    }

}
