package com.jd.bluedragon.distribution.test.print;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.waybill.handler.InterceptWaybillHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;
/**
 * 
 * @ClassName: InterceptWaybillHandlerTestCase
 * @Description: 打印拦截处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class InterceptWaybillHandlerTestCase {
	@InjectMocks
	InterceptWaybillHandler interceptWaybillHandler;
	@Mock
    CancelWaybillJsfManager cancelWaybillJsfManager;
	/**
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testCancel() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		Integer[] codes = {
				100,
				SortingResponse.CODE_39006,
				SortingResponse.CODE_29311,
				SortingResponse.CODE_29302,
				SortingResponse.CODE_29307,
				SortingResponse.CODE_29313,
				SortingResponse.CODE_29316,
				};
		boolean[] resultChecks ={
				false,
				true,
				true,
				true,
				true,
				true,
				true,
				true
				};
		for (int i = 0; i < codes.length; i++) {
			context.getResult().toSuccess();
			context.getRequest().setOperateType(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType());
			System.err.println(codes[i]);
			JsfResponse<WaybillCancelJsfResponse> verJsfResult = new JsfResponse<WaybillCancelJsfResponse>();
			verJsfResult.setData(new WaybillCancelJsfResponse());
			verJsfResult.getData().setCode(codes[i]);
			when(cancelWaybillJsfManager.dealCancelWaybill(context.getResponse().getWaybillCode())).thenReturn(verJsfResult);
			JdResult<String> result = interceptWaybillHandler.handle(context);
			// 预期验证结果
			boolean isIntercept = resultChecks[i];
			Assert.assertEquals(isIntercept, result.isFailed() && codes[i].equals(result.getMessageCode()));
		}
	}
}
