package com.jd.bluedragon.distribution.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
/**
 * Excel读取测试
 * @author wuyoude
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ExcelDataResolverestCase {
	/**
	 * 2003导入测试
	 * @throws Exception
	 */
    @Test
    public void testExcel2003() throws Exception{
    	String fileName = "D:\\data\\git\\ql-dms-distribution\\bluedragon-distribution-web\\src\\test\\resources\\files\\waybillWeight.xlsx";
    	DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(fileName);
    	List<String> resultMessages = new ArrayList<String>();
//    	InputStream fileIs = ExcelDataResolverestCase.class.getClassLoader().getResourceAsStream(fileName);
    	InputStream fileIs = new FileInputStream(new File(fileName));
    	List<WaybillWeightVO> dataList = dataResolver.resolver(fileIs, WaybillWeightVO.class, new PropertiesMetaDataFactory("/excel/waybillWeight.properties"),true,resultMessages);
    }
}
