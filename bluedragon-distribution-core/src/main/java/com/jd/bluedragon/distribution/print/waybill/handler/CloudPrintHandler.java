package com.jd.bluedragon.distribution.print.waybill.handler;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.CloudPrintDataServiceManager;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.CloudPrintDocument;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.print.dto.data.CustomerPrintDataDto;
import com.jdl.print.dto.data.DataDto;
import com.jdl.print.dto.data.PrintDataResult;
import com.jdl.print.dto.data.QueryPrintDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jd.bluedragon.core.base.CloudPrintDataServiceManagerImpl.SCENE_EXCHANGE_PRINT;
import static com.jd.bluedragon.core.base.CloudPrintDataServiceManagerImpl.SCENE_PRINT;
import static com.jd.bluedragon.distribution.api.JdResponse.CODE_OK;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT_TYPE;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description: 云打印执行接口
 */
@Slf4j
@Service("cloudPrintHandler")
public class CloudPrintHandler extends AbstractInterceptHandler<WaybillPrintContext,String> {


    /**
     * 是否跳过执行，当返回true时跳过此handler
     *
     * @param target
     * @return
     */
    @Override
    public Boolean isSkip(WaybillPrintContext target) {
        //有云打印标识执行此handler
        return !target.getUseCloudPrint();
    }

    /**
     * 云打印服务封装类
     */
    @Autowired
    private CloudPrintDataServiceManager cloudPrintDataServiceManager;
    /**
     * 执行处理，返回处理结果
     *
     * @param target
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.CloudPrintHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext target) {
        InterceptResult<String> interceptResult = target.getResult();
        //调用云打印服务
        PrintDataResult printDataResult = cloudPrintDataServiceManager.innerPlainText(makeQueryPrintDataDto(target));
        if(printDataResult != null && CODE_OK.equals(printDataResult.getCode()) && printDataResult.getPrintDatas() != null){
            //检查一次返回的打印信息中是否包含用户传入的打印信息
            interceptResult = includeCheck(target,printDataResult);
            if(interceptResult.isSucceed()){
                //成功 组装云打印返回信息
                target.getResponse().setCloudPrintDocuments(
                        makeCloudPrintDocuments(printDataResult.getPrintDatas()));
            }else{
                return interceptResult;
            }
        }else{
            interceptResult.toFail("获取云打印信息失败！云打印接口返回信息：" + JSON.toJSONString(printDataResult));
            return interceptResult;
        }
        return interceptResult;
    }


    /**
     * 检查是否包含目标打印上下文和打印数据结果
     * @param target 目标打印上下文
     * @param printDataResult 打印数据结果
     * @return 拦截结果
     */
    private InterceptResult<String> includeCheck(WaybillPrintContext target, PrintDataResult printDataResult){

        InterceptResult<String> interceptResult = target.getResult();
        //target.getRequest().getBarCode()不能为空，别怕空指针
        if(WaybillUtil.isPackageCode(target.getRequest().getBarCode())){
            //当是包裹号是检查集合中是否包含此包裹信息
            if(printDataResult.getPrintDatas().stream().noneMatch(printData ->
                    target.getRequest().getBarCode().equals(printData.getPackageCode()))){
                //不包含则返回异常
                interceptResult.toFail("获取云打印信息失败！云打印接口返回信息中不包含需要打印的信息：" + target.getRequest().getBarCode());
                return interceptResult;
            }
        }

        return interceptResult;
    }

    /**
     * 组装云打印所需数据
     * @param target
     * @return
     */
    private QueryPrintDataDto makeQueryPrintDataDto(WaybillPrintContext target){
        String barCode = target.getRequest().getRealBarCode();
        Integer dmsSiteCode = target.getRequest().getDmsSiteCode();
        String paperSize = target.getRequest().getPaperSizeCode();
        WeightOperFlow weightOperFlow = target.getRequest().getWeightOperFlow();

        QueryPrintDataDto queryPrintDataDto = new QueryPrintDataDto();
        //默认场景
        queryPrintDataDto.setScene(SCENE_PRINT);
        //换单打印场景
        if(SWITCH_BILL_PRINT_TYPE.equals(target.getRequest().getOperateType())){
            queryPrintDataDto.setScene(SCENE_EXCHANGE_PRINT);
        }
        queryPrintDataDto.setNeedTemplate(Boolean.TRUE);
        queryPrintDataDto.setBillCodeValue(barCode);
        queryPrintDataDto.setTemplateSize(templateSizeMapping(paperSize));
        //自定义属性
        CustomerPrintDataDto customerPrintDataDto = new CustomerPrintDataDto();
        queryPrintDataDto.setCustomerPrintDataDto(customerPrintDataDto);
        customerPrintDataDto.setSourceSiteId(dmsSiteCode);
        //称重信息
        if(weightOperFlow != null){
            customerPrintDataDto.setWeight(weightOperFlow.getWeight());
            customerPrintDataDto.setLength(weightOperFlow.getLength());
            customerPrintDataDto.setHeight(weightOperFlow.getHigh());
            customerPrintDataDto.setWidth(weightOperFlow.getWidth());
        }
        return queryPrintDataDto;
    }

    /**
     * 生成云打印文档
     * @param dataDtos
     * @return
     */
    private List<CloudPrintDocument> makeCloudPrintDocuments(List<DataDto> dataDtos){
        List<CloudPrintDocument> cloudPrintDocuments = dataDtos.stream()
                .map(dataDto -> {
                    CloudPrintDocument cloudPrintDocument = new CloudPrintDocument();
                    cloudPrintDocument.setDocumentId(dataDto.getPackageCode());
                    cloudPrintDocument.setDataType(CloudPrintDocument.DATA_TYPE_PLAIN);
                    //模板编码不需要在此处回传，直接放在PrintData中
                    //cloudPrintDocument.setTemplateCode(dataDto.getPrintData().getTemplateCode());
                    cloudPrintDocument.setPrintData(JSON.toJSONString(dataDto.getPrintData()));
                    return cloudPrintDocument;
                })
                .collect(Collectors.toList());
        return cloudPrintDocuments;
    }

    /**
     * 匹配云打印模板尺寸 未匹配到则返回空使用云打印默认逻辑
     * @param paperSizeCode
     * @return
     */
    private String templateSizeMapping(String paperSizeCode){

        if(DmsPaperSize.PAPER_SIZE_CODE_1005.equals(paperSizeCode)){
            return "100x50";
        } else if (DmsPaperSize.PAPER_SIZE_CODE_1010.equals(paperSizeCode)) {
            return "100x100";
        }else if(DmsPaperSize.PAPER_SIZE_CODE_1011.equals(paperSizeCode)){
            return "100x110";
        }
        return null;
    }
}
