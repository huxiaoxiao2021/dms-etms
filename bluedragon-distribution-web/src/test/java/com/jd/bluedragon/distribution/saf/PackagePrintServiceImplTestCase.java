package com.jd.bluedragon.distribution.saf;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.rest.packageMake.PackageResource;
import com.jd.bluedragon.distribution.rest.reverse.ReversePrintResource;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.print.engine.TemplateFactory;

@RunWith(MockitoJUnitRunner.class)
public class PackagePrintServiceImplTestCase {
	@InjectMocks
	PackagePrintServiceImpl packagePrintService = new PackagePrintServiceImpl();
    @Mock
    private JdCommandService jdCommandService;
	@Mock
    SortingService sortingService;
	@Mock
    private CancelWaybillJsfManager cancelWaybillJsfManager;
    
	@Mock
    private TemplateFactory templateFactory;

    @Mock
    private SysConfigService sysConfigService;
    
    @Mock
    ReversePrintResource reversePrintResource;
	@Mock
	ReassignWaybillService reassignWaybillService;
	@Mock
	private WaybillService waybillService;
	@Mock
	private PackageResource packageResource;
	/**
	 * 测试打印数据接口
	 * @throws Exception
	 */
    @Test
    public void testGetPrintInfo() throws Exception{
    	JdCommand<String> printRequest = new JdCommand<String>();
		when(sortingService.findBoxPack(Mockito.anyInt(),Mockito.anyString())).thenReturn(10);
		Box boxInfo = new Box();
		boxInfo.setCreateSiteCode(910);
		JdResult<String> printResult = new JdResult<String>();
		printResult.toFail("拦截测试");
		when(jdCommandService.execute(Mockito.anyString())).thenReturn(JsonHelper.toJson(printResult));
		JdResult<Map<String, Object>> result = packagePrintService.getPrintInfo(printRequest);
		System.err.println("打印结果"+JsonHelper.toJson(result));
//		Assert.assertTrue(result.isSucceed() && result.getData().intValue() == 10);
    }
}
