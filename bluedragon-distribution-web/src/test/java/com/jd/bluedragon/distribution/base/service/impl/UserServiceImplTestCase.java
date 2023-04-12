package com.jd.bluedragon.distribution.base.service.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ErpLoginServiceManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthRequest;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.ql.dms.common.cache.CacheService;
/**
 * 
 * @author wuyoude
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTestCase {
	private static final Logger log = LoggerFactory.getLogger(UserServiceImplTestCase.class);
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	

	@Mock
	private BaseMajorManager baseMajorManager;

    @Mock
    private BaseService baseService;

    @Mock
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Mock
    private ErpLoginServiceManager erpLoginServiceManager;

	@Mock
	private UccPropertyConfiguration uccPropertyConfiguration;

	@Mock
	private JyDemotionService jyDemotionService;

	@Mock
    private DeviceLocationService deviceLocationService;

	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testCheckMenuAuth() throws Exception{
    	FuncUsageProcessDto menuData = new FuncUsageProcessDto();
    	menuData.setUrl("test.url");
    	menuData.setMsg("此功能已下线，请移步站长工作台-任务中心-操作任务进行返调度，如有疑问可入群反馈10202603829");
    	when(baseService.getFuncUsageConfig(Mockito.any(FuncUsageConfigRequestDto.class))).thenReturn(menuData);
    	CheckMenuAuthRequest checkMenuAuthRequest = new CheckMenuAuthRequest();
    	checkMenuAuthRequest.setMenuCode("0601012");
    	checkMenuAuthRequest.setSiteCode(910);
    	checkMenuAuthRequest.setSiteType(64);
    	JdResult<CheckMenuAuthResponse> checkMenuAuth = userServiceImpl.checkMenuAuth(checkMenuAuthRequest);
    	log.info("checkMenuAuth:"+JsonHelper.toJson(checkMenuAuth));
	}
}
