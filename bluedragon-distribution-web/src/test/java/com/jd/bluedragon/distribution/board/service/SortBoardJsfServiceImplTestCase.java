package com.jd.bluedragon.distribution.board.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.board.domain.BindBoardRequest;
import com.jd.bluedragon.distribution.board.service.SortBoardJsfServiceImpl;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoComposeServiceImpl;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.sdk.modules.board.BoardChuteJsfService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.transboard.api.service.IVirtualBoardService;
import com.jd.bluedragon.dms.utils.SendPayConstants;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.sun.el.stream.Stream;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandlerTestCase
 * @Description: 打印业务-收、寄件人信息处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SortBoardJsfServiceImplTestCase {
	@InjectMocks
	SortBoardJsfServiceImpl sortBoardJsfServiceImpl;
    @Mock
    private SortBoardGatewayService sortBoardGatewayService;
    @Mock
    private SendCodeService sendCodeService;
    @Mock
    BoardCombinationService boardCombinationService;
    @Mock
    VirtualBoardService virtualBoardService;
    @Mock
    GroupBoardService groupBoardService;
    @Mock
    DeliveryService deliveryService;
    @Mock
    BoardChuteJsfService boardChuteJsfService;
    @Mock
    IVirtualBoardService iVirtualBoardService;
    @Resource
    private CacheService jimdbCacheService;

    @Mock
    private SendMService sendMService;
    @Mock
    private BaseMajorManager baseMajorManager;
    @Mock
    private JyComBoardSendService jyComBoardSendService;
    @Mock
    private JyBizTaskComboardService jyBizTaskComboardService;
	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 
	 * @throws Exception
	 */
    @Test
    public void testSortMachineComboard() throws Exception{
    	BindBoardRequest request = EntityUtil.getInstance(BindBoardRequest.class);
    	sortBoardJsfServiceImpl.sortMachineComboard(request);
    }
	/**
	 * 
	 * @throws Exception
	 */
    @Test
    public void testCancelSortMachineComboard() throws Exception{
    	BindBoardRequest request = EntityUtil.getInstance(BindBoardRequest.class);
    	sortBoardJsfServiceImpl.cancelSortMachineComboard(request);
    }    
}
