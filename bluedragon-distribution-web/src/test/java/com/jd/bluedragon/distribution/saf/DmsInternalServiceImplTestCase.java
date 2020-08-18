package com.jd.bluedragon.distribution.saf;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.saf.DmsInternalServiceImpl;
import com.jd.bluedragon.distribution.sorting.service.SortingService;

@RunWith(MockitoJUnitRunner.class)
public class DmsInternalServiceImplTestCase {
	@InjectMocks
	DmsInternalServiceImpl dmsInternalServiceImpl = new DmsInternalServiceImpl();
	@Mock
    private BoxService boxService;
	@Mock
    SortingService sortingService;
	/**
	 * 测试获取箱号数据接口
	 * @throws Exception
	 */
    @Test
    public void testGetSortingNumberInBox() throws Exception{
    	String boxCode = "BC1001200813110000000101";
		when(sortingService.findBoxPack(Mockito.anyInt(),Mockito.anyString())).thenReturn(10);
		Box boxInfo = new Box();
		boxInfo.setCreateSiteCode(910);
		when(boxService.findBoxByCode(Mockito.anyString())).thenReturn(boxInfo);
		JdResult<Integer> result = dmsInternalServiceImpl.getSortingNumberInBox(boxCode, null);
		Assert.assertTrue(result.isSucceed() && result.getData().intValue() == 10);
    }
}
