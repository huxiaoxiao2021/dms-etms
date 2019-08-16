package com.jd.bluedragon.distribution.print.waybill.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.service.JdCloudPrintService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ObjectHelper;

/**
 * 
 * @ClassName: pdfLabelFileGenerateHandler
 * @Description: pdf标签文件生成处理
 * @author: wuyoude
 * @date: 2019年8月14日 上午11:35:15
 *
 */
public class PdfLabelFileGenerateHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Log logger = LogFactory.getLog(PdfLabelFileGenerateHandler.class);
    /**
     * 云打印输出文件oss配置
     */
    private JdCloudPrintService jdCloudPrintService;
    
    /**
     * 包裹列表字段
     */
    private static final String FIELD_NAME_PACKLIST = "packList";
    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.info("PdfLabelFileGenerateHandler-pdf标签文件生成处理");
        InterceptResult<String> interceptResult = context.getResult();
        WaybillPrintRequest waybillPrintRequest = context.getRequest();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        List<Map<String,String>> printDatas = convertPrintDatas(context);
        JdCloudPrintRequest<Map<String,String>> jdCloudPrintRequest = jdCloudPrintService.getDefaultPdfRequest();
        if(waybillPrintRequest.getSiteCode() != null){
        	jdCloudPrintRequest.setLocation(waybillPrintRequest.getSiteCode().toString());
        }
        if(waybillPrintRequest.getUserCode() != null){
        	jdCloudPrintRequest.setUser(waybillPrintRequest.getUserCode().toString());
        }
        jdCloudPrintRequest.setOrderNum(waybillPrintRequest.getBarCode());
        jdCloudPrintRequest.setTemplate(basePrintWaybill.getTemplateName());
        if(basePrintWaybill.getTemplateVersionStr() != null){
        	jdCloudPrintRequest.setTemplateVer(basePrintWaybill.getTemplateVersionStr());
        }
        jdCloudPrintRequest.setModel(printDatas);
        JdResult<List<JdCloudPrintResponse>> printResult = jdCloudPrintService.jdCloudPrint(jdCloudPrintRequest);
        if(printResult.isSucceed()){
        	List<JdCloudPrintResponse> pdfList = printResult.getData();
        	if(pdfList.size()>0){
        		JdCloudPrintResponse jdCloudPrintResponse = pdfList.get(0);
        		if(JdCloudPrintResponse.STATUS_SUC.equals(jdCloudPrintResponse.getStatus())
        				&& jdCloudPrintResponse.getOutputMsg() != null
        				&& jdCloudPrintResponse.getOutputMsg().size()>0){
        			basePrintWaybill.setLabelFileType(jdCloudPrintResponse.getOutputType());
        			basePrintWaybill.setLabelFileUrl(jdCloudPrintResponse.getOutputMsg().get(0));
        		}else{
        			interceptResult.toFail("生成pdf文件失败！");
        		}
        	}
        }else{
        	interceptResult.toFail(printResult.getMessageCode(), printResult.getMessage());
        }
        return interceptResult;
    }
    /**
     * 组装打印数据
     * @param context
     * @return
     */
    private List<Map<String,String>> convertPrintDatas(WaybillPrintContext context){
    	List<Map<String,String>> printDatas = new ArrayList<Map<String,String>>();
    	WaybillPrintResponse printResponse = context.getResponse();
    	Set<String> excludeFieldNames = new HashSet<String>();
    	excludeFieldNames.add(FIELD_NAME_PACKLIST);
    	Map<String,String> waybillInfo = convertToMap(printResponse,excludeFieldNames);
    	List<PrintPackage> packList = printResponse.getPackList();
    	String barCode = context.getRequest().getBarCode();
    	boolean isPackage = WaybillUtil.isPackageCode(barCode);
    	if(packList != null && packList.size()>0){
    		for(PrintPackage printPackage : packList){
    			if(isPackage && !barCode.equals(printPackage.getPackageCode())){
    				continue;
    			}
        		Map<String,String> printData = new HashMap<String,String>();
        		printData.putAll(waybillInfo);
        		printData.putAll(convertToMap(printPackage,null));
        		printDatas.add(printData);
    		}
    	}
    	return printDatas;
    }
    /**
     * 对象转成map
     * @param object
     * @param excludeFieldNames
     * @return
     */
    private <T> Map<String,String> convertToMap(T object,Set<String> excludeFieldNames){
    	List<Field> fields = ObjectHelper.getAllFieldsList(object.getClass());
    	Map<String,String> map = new HashMap<String,String>();
    	for(Field field : fields){
    		String key = field.getName();
    		if(excludeFieldNames == null || !excludeFieldNames.contains(key)){
    			try {
					Object val = ObjectHelper.getValue(object, field);
					if(val!=null){
						map.put(key, val.toString());
					}else{
						map.put(key, "");
					}
				} catch (IllegalArgumentException e) {
					logger.error("ObjectHelper.getValue异常！"+key, e);
				} catch (IllegalAccessException e) {
					logger.error("ObjectHelper.getValue异常！"+key, e);
				}
    		}
    	}
    	return map;
    }
	/**
	 * @return the jdCloudPrintService
	 */
	public JdCloudPrintService getJdCloudPrintService() {
		return jdCloudPrintService;
	}
	/**
	 * @param jdCloudPrintService the jdCloudPrintService to set
	 */
	public void setJdCloudPrintService(JdCloudPrintService jdCloudPrintService) {
		this.jdCloudPrintService = jdCloudPrintService;
	}
}
