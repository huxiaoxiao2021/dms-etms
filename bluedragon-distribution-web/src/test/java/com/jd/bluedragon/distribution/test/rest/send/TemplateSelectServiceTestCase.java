package com.jd.bluedragon.distribution.test.rest.send;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.PackageTemplate;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.waybill.handler.TemplateSelectorWaybillHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.utils.JsonHelper;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/distribution-web-context-test.xml"})
public class TemplateSelectServiceTestCase {
	
	public static void main(String[] args) throws Exception{
		testPackageTemplate();
	}
    @Autowired
    private TemplateSelectorWaybillHandler templateSelectorWaybillHandler;
    @Test
    public void testUseNewTemplate() throws Exception{
    	//http://dmswebtest.360buy.com/sysconfig/list?pageNo=1&pageSize=10&configName=print.dmsSiteCodes.useNewTemplate
    	//测试启用新模板配置功能
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
    	Integer[] dmsSiteCodes = {null,910,364605,123};
    	Boolean[] useNewTemplates = {false,true,false,false};
    	context.setBasePrintWaybill(context.getResponse());
    	int sucNum = 0;
    	for(int i =0 ;i<dmsSiteCodes.length;i++){
    		context.getRequest().setDmsSiteCode(dmsSiteCodes[i]);
    		templateSelectorWaybillHandler.handle(context);
    		if(useNewTemplates[i].equals(context.getBasePrintWaybill().getUseNewTemplate())){
    			System.out.println(i+":"+"suc");
    			sucNum ++;
    		}else{
    			System.err.println(i+":"+"fail");
    		}
    	}
    	Assert.assertEquals(dmsSiteCodes.length, sucNum);
    }
    public static void testPackageTemplate() throws Exception{
    	PackageTemplate packageTemplate = EntityUtil.getInstance(PackageTemplate.class);
    	System.err.println(JsonHelper.toJson(packageTemplate));
    }
    @Test
    public void testC() throws Exception{
    	//测试启用新模板配置功能
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
    	context.getRequest().setTemplateName(null);
    	
    	String[] paperSizeCodes = {null,DmsPaperSize.PAPER_SIZE_CODE_1005,DmsPaperSize.PAPER_SIZE_CODE_1010,DmsPaperSize.PAPER_SIZE_CODE_1011};
    	String[] dmsBusiAliass = {null,Constants.BUSINESS_ALIAS_YHD,Constants.BUSINESS_ALIAS_CMBC,"","eeee"};
    	String[][] templateNames = {
    			{"dms-unite-m","dms-unite-business-m","dms-nopaperyhd-m","dms-unite-m","dms-unite-m"},
    			{"dms-haspaper15-m","dms-haspaper15-m","dms-haspaper15-m","dms-haspaper15-m","dms-haspaper15-m"},
    			{"dms-unite1010-m","dms-unite1010-business-m","dms-nopaperyhd-m","dms-unite1010-m","dms-unite1010-m"},
    			{"dms-unite-m","dms-unite-business-m","dms-nopaperyhd-m","dms-unite-m","dms-unite-m"},
    			};
    	context.setBasePrintWaybill(context.getResponse());
    	int sucNum = 0;
    	for(int i =0 ;i<paperSizeCodes.length;i++){
    		context.getRequest().setPaperSizeCode(paperSizeCodes[i]);
	    	for(int j =0 ;j<dmsBusiAliass.length;j++){
	    		context.getBasePrintWaybill().setDmsBusiAlias(dmsBusiAliass[j]);
	    		templateSelectorWaybillHandler.handle(context);
	    		String templateName = context.getBasePrintWaybill().getTemplateName();
	    		if(templateNames[i][j].equals(templateName)){
	    			sucNum ++;
	    			System.out.println(i+"-"+j+":"+"suc");
	    		}else{
	    			System.err.println(i+"-"+j+":"+"fail");
	    		}
	    	}
    	}
    	Assert.assertEquals(paperSizeCodes.length * dmsBusiAliass.length, sucNum);
    }
}
