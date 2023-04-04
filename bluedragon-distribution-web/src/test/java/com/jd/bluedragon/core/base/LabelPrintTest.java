package com.jd.bluedragon.core.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.print.engine.DrawSetting;
import com.jd.ql.dms.print.engine.LabelFileLoaderFactory;
import com.jd.ql.dms.print.engine.TemplateFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-label-print-client-test.xml"})
public class LabelPrintTest {
	private static final Log logger = LogFactory.getLog(LabelPrintTest.class);
    @Autowired
    TemplateFactory templateFactory;
    
    @Value("${beans.jssLabelFileLoader.localFilePath}/test/")
    private String rooPath = "D:\\export\\home\\logs\\localFilePath\\test\\";
    private static final int PDF_DPI = 72;
    
    private static Map<String, String> labelParams;

    {
    	File path = new File(rooPath);
    	if(path != null && !path.exists()) {
    		path.mkdirs();
    	}
    }
    static
    {
        /**
         * 数据初始化
         */
		Map<String, String> localParams = new HashMap<>();
	    labelParams = new HashMap<String, String>();
	    labelParams.put("originalCrossCode", "ชิชาญา รัชตะพงศ์ธร");
	    labelParams.put("printSiteName", "北京START");
	    labelParams.put("destinationDmsName", "北京END");
	    labelParams.put("printAddress", "地址");
	    labelParams.put("packageCode", "JDVA00010000101-1-1-");
		if(localParams!=null){
			for(String k:localParams.keySet()){
				Object a = localParams.get(k);
				labelParams.put(k, a.toString());
			}
		}
    }
    {
    	
    }
	/**
	 * 测试标签图片生成
	 * @throws Throwable
	 */
	@Test
	public void testGenerateImg() throws Throwable {
    	File path = new File(rooPath);
    	if(path != null && !path.exists()) {
    		path.mkdirs();
    	}
		DrawSetting drawSetting = new DrawSetting();
		String[] keys = {"jss","dmsHttp","dmsAmazon"};
		while(true) {
			for(String key:keys) {
				LabelFileLoaderFactory.setDefaultLabelFileLoaderKey(key);
				//设置条码缩放开关
				drawSetting.setExpandBarCode(true);
				String templateName = "wms-107yhd-m";
				//调用Api生成图片
				BufferedImage image = templateFactory
						.buildEngine(templateName, 0)
						.SetParameters(labelParams)
						.setSetting(drawSetting)
						.GenerateImage(false, 203, 203);
				
				File localFile = new File(rooPath + key+"-"+templateName+"测试-"+DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)+".jpg");
				String formatName = "jpg";
				ImageIO.write(image, formatName, localFile);
				Thread.sleep(6000);
			}
		}
	}
}
