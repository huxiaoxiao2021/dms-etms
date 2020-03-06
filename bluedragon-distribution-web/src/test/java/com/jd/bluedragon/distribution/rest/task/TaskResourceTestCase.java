package com.jd.bluedragon.distribution.rest.task;

import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.jd.bluedragon.distribution.api.JdResponse.CODE_OK;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-web-context.xml" })
public class TaskResourceTestCase {

	String urlRoot = "http://localhost:8088/services";

	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskResource taskResource;

	@Test
	public void testSortingAdd(){
		TaskRequest request = new TaskRequest();
		request.setReceiveSiteCode(509);
		request.setBody("[{ \"receiveSiteCode\" : 511, \"receiveSiteName\" : \"北京大鲁店分拣中心\", \"boxCode\" : \"BC010F001010F00500003029\", \"packageCode\" : \"383851175-1-1-4\", \"isCancel\" : 0, \"id\" : 42960, \"businessType\" : 10, \"userCode\" : 13445, \"userName\" : \"司营\", \"siteCode\" : 1006, \"siteName\" : \"北京双树分拣中心\", \"operateTime\" : \"2012-11-29 18:43:14.000\" }]");
		request.setBoxCode("BC010F001010F00500003029");
		request.setType(1200);
		request.setSiteCode(1006);
		
		String url = this.urlRoot + "/tasks";
        RestTemplate template = new RestTemplate();
        
        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	@Test
	public void autoAddInspectionTaskTest(){
		InspectionAS insp = new InspectionAS();
		insp.setPackageBarOrWaybillCode("JDV000051299890-99-222-");
		insp.setExceptionType("");
		insp.setOperateType(0);
		insp.setReceiveSiteCode(0);
		insp.setBusinessType(10);
		insp.setUserCode(17907);
		insp.setUserName("邢松");
		insp.setSiteCode(910);
		insp.setSiteName("马驹桥分拣中心");
		insp.setOperateTime(DateHelper.formatDateTime(new Date()));
		List<InspectionAS> list = new ArrayList<>();
		list.add(insp);
		TaskRequest request = new TaskRequest();
		request.setReceiveSiteCode(509);
		request.setBody(JsonHelper.toJson(insp));
		request.setBoxCode("JDV000051299890-99-222-");
		request.setType(1130);
		request.setSiteCode(910);
		TaskResponse taskResponse = taskResource.autoAddInspectionTask(request);
		Assert.assertEquals(CODE_OK, taskResponse.getCode());

	}

	@Test
	public void addInspectSortingTaskTest(){
		AutoSortingPackageDto dto = new AutoSortingPackageDto();
		dto.setBoxCode("BC010F001010F00500003029");
		dto.setCreateTime(DateHelper.formatDateTime(new Date()));
		dto.setDistributeID(910);
		dto.setDistributeName("马驹桥分拣中心");
		dto.setOperatorErp("bjxings");
		dto.setOperatorID(17907);
		dto.setOperatorName("邢松");
		dto.setWaybillCode("JDV000051299890-99-222-");
		TaskResponse taskResponse = taskResource.addInspectSortingTask(dto);
		Assert.assertEquals(CODE_OK, taskResponse.getCode());
	}
	@Test
	public void testReceiveAdd(){
		TaskRequest request = new TaskRequest();
		
		request.setReceiveSiteCode(509);
		request.setBody("[{ \"shieldsCarCode\" : \"\", \"carCode\" : \"\", \"packOrBox\" : \"TC377Y001027F00200003055\", \"id\" : 2163, \"businessType\" : 20, \"userCode\" : 22281, \"userName\" : \"王堤建\", \"siteCode\" : 1609, \"siteName\" : \"武汉沌口分拣中心\", \"operateTime\" : \"2012-11-29 18:48:31.000\" }]");
		request.setBoxCode("BC010F001010F00500003029");
		request.setType(1110);
		request.setSiteCode(1609);
		
		String url = this.urlRoot + "/tasks";
        RestTemplate template = new RestTemplate();
        
        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testInspectionJiaojieAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(908);
		//request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"393093673-1-1-2\", \"exceptionType\" : \"\", \"operateType\" : 0, \"receiveSiteCode\" : 0, \"id\" : 767191, \"businessType\" : 50, \"userCode\" : 35442, \"userName\" : \"雷亚男\", \"siteCode\" : 908, \"siteName\" : \"广州萝岗分拣中心\", \"operateTime\" : \"2012-11-29 18:54:15.000\" }]");
		request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"393598115-2-2-7\", \"exceptionType\" : \"\", \"receiveSiteCode\" : 0, \"id\" : 251330, \"businessType\" : 52, \"userCode\" : 29562, \"userName\" : \"陈长英\", \"siteCode\" : 909, \"siteName\" : \"上海嘉定分拣中心\", \"operateTime\" : \"2012-11-29 18:57:39.717\" }]");
		request.setBoxCode("BC010F001010F00500003029");
		request.setType(1130);
		request.setSiteCode(908);
		
		String url = this.urlRoot + "/tasks";
        RestTemplate template = new RestTemplate();
        
        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testInspectionPartnerAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(1255);
		request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"BC010F002991E00100003041\", \"packageBarOrWaybillCode\" : \"388426945-1-1-19\", \"exceptionType\" : \"\", \"operateType\" : 0, \"receiveSiteCode\" : 1255, \"id\" : 3095, \"businessType\" : 30, \"userCode\" : 7000303, \"userName\" : \"刘小峰\", \"siteCode\" : 910, \"siteName\" : \"北京马驹桥分拣中心\", \"operateTime\" : \"2012-11-29 18:59:25.000\" }]");
		request.setBoxCode("BC010F001010F00500003029");
		request.setType(1130);
		request.setSiteCode(910);
		
		String url = this.urlRoot + "/tasks";
        RestTemplate template = new RestTemplate();
        
        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testInspectionRevertAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(1255);
		//request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"387765949\", \"exceptionType\" : \"\", \"operateType\" : \"23\", \"receiveSiteCode\" : 1273, \"id\" : 5481, \"businessType\" : 20, \"userCode\" : 42490, \"userName\" : \"王欢\", \"siteCode\" : 2015, \"siteName\" : \"北京双树分拣中心\", \"operateTime\" : \"2012-11-30 15:50:36.000\" }]");
		//request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"387765949\", \"exceptionType\" : \"\", \"operateType\" :\"\" , \"receiveSiteCode\" : 1273, \"id\" : 5481, \"businessType\" : 20, \"userCode\" : 42490, \"userName\" : \"王欢\", \"siteCode\" : 2015, \"siteName\" : \"北京双树分拣中心\", \"operateTime\" : \"2012-11-30 15:50:36.000\" }]");
		//request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"\", \"exceptionType\" : \"\", \"operateType\" :\"\" , \"receiveSiteCode\" : 1273, \"id\" : 5481, \"businessType\" : 20, \"userCode\" : 42490, \"userName\" : \"王欢\", \"siteCode\" : 2015, \"siteName\" : \"北京双树分拣中心\", \"operateTime\" : \"2012-11-30 15:50:36.000\" }]");
		//request.setBody("[{ \"sealBoxCode\" : \"\", \"boxCode\" : \"\", \"packageBarOrWaybillCode\" : \"387765949\", \"exceptionType\" : \"\", \"receiveSiteCode\" : 1273, \"id\" : 5481, \"businessType\" : 20, \"userCode\" : 42490, \"userName\" : \"王欢\", \"siteCode\" : 2015, \"siteName\" : \"北京双树分拣中心\", \"operateTime\" : \"2012-11-30 15:50:36.000\" }]");
		request.setBody("[{ \"sealBoxCode\" : \"30073218X\", \"boxCode\" : \"TC020Y015020T00200003051\", \"packageBarOrWaybillCode\" : \"392103853-1-1-2\", \"exceptionType\" : \"\", \"operateType\" : 21, \"receiveSiteCode\" : 0, \"id\" : 14331, \"businessType\" : 20, \"userCode\" : 31457, \"userName\" : \"冯泽磊\", \"siteCode\" : 908, \"siteName\" : \"广州萝岗分拣中心\", \"operateTime\" : \"2012-11-30 15:50:47.000\" }]");
		//request.setBoxCode("BC010F001010F00500003029");
		request.setType(1130);
		request.setSiteCode(910);
		
		String url = this.urlRoot + "/tasks";
        RestTemplate template = new RestTemplate();
        
        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Test
	public void testQualityControlTask(){
		for(int i = 0; i < 30; i++){
			QualityControlRequest request = generateQcRequest();
			Task qcTask = new Task();
			qcTask.setKeyword1(request.getQcType() + "");
			qcTask.setKeyword2(request.getQcValue());
			qcTask.setOwnSign(BusinessHelper.getOwnSign());
			qcTask.setStatus(Task.TASK_STATUS_UNHANDLED);
			qcTask.setType(Task.TASK_TYPE_REVERSE_QUALITYCONTROL);
			qcTask.setTableName(Task.getTableName(qcTask.getType()));
			qcTask.setSequenceName(Task.getSequenceName(qcTask.getTableName()));
			qcTask.setBody(JsonHelper.toJson(request));
			qcTask.setCreateTime(new Date());
			qcTask.setCreateSiteCode(Integer.parseInt(String.valueOf(request.getDistCenterID())));
			qcTask.setExecuteCount(0);
			StringBuilder fringerprint = new StringBuilder();
			fringerprint.append(request.getDistCenterID() + "_" + qcTask.getType() + "_" + qcTask.getKeyword1() + "_" + qcTask.getKeyword2());
			qcTask.setFingerprint(Md5Helper.encode(fringerprint.toString()));
			taskService.add(qcTask);
		}

	}


	public QualityControlRequest generateQcRequest(){
		String[] values = QCVALUES.split("\n");
		QualityControlRequest qualityControlRequest = new QualityControlRequest();
		qualityControlRequest.setDistCenterID(910);
		qualityControlRequest.setDistCenterName("北京马驹桥分拣中心");
		qualityControlRequest.setQcCode(new Random().nextInt(1000));
		qualityControlRequest.setOperateTime(new Date());
		qualityControlRequest.setQcType(new Random().nextInt(5));
		qualityControlRequest.setQcValue(values[new Random().nextInt(values.length - 1)]);
		qualityControlRequest.setUserERP("zhangsan4");
		qualityControlRequest.setUserID(44751);
		qualityControlRequest.setUserName("张三");
		qualityControlRequest.setQcName("包裹损坏");
		return qualityControlRequest;
	}

	private static final String QCVALUES = "000000002763-1-1-\n" +
			"000000003124-1-3-\n" +
			"000000017776-1-1-\n" +
			"000000017861-1-1-\n" +
			"000000018126-1-4-\n" +
			"000000018126-3-4-\n" +
			"000000018255-1-13-\n" +
			"000000020790-5-5-\n" +
			"311001003N1S3H22\n" +
			"654844487-1-2-\n" +
			"170003978N3S3H11\n" +
			"171099620-2-3-\n" +
			"201206274N1S1H22\n" +
			"170001131N2S2H33\n" +
			"90400950963702-1-1-\n" +
			"90400950963945-1-1-\n" +
			"170371502-1-3-\n" +
			"170171648-16-22-\n" +
			"170171648-18-22-\n" +
			"170171648-22-22-\n" +
			"170171648-4-22-\n" +
			"170171648-9-22-\n" +
			"200010005N1S8\n" +
			"2012032805-1-1\n" +
			"BC010F001010F00200003002\n" +
			"BC010F001010F00200003003\n" +
			"BC010F511010Y04200000110\n" +
			"BC010F511010Y04200000127\n" +
			"BC010F511010Y04200000172\n" +
			"BC010F511010Y04200000190\n" +
			"BC010F511010Y04200000195\n" +
			"BC010F511010Y04200000197\n" +
			"BC010F511010Y04200000216\n" +
			"BC010F511010Y04200000218\n" +
			"BC010F511010Y04200000222\n" +
			"BC010F511010Y04200000232\n" +
			"BC010F511010Y04200000233\n" +
			"BC010F511010Y04200000236\n" +
			"BC010F511010Y04200000241\n" +
			"BC010F511010Y04200000245\n" +
			"BC010F511010Y04200000246\n" +
			"BC010F511010Y04200000248\n" +
			"BC010F511010Y04200000253\n" +
			"BC010F511010Y04200000279\n" +
			"BC010F511010Y04200000282\n" +
			"BC010F511010Y04200000288\n" +
			"BC010F511010Y04200000292\n" +
			"BC010F511010Y04200000304\n" +
			"BC010F511010Y04200000310\n" +
			"BC010F511010Y04200000324\n" +
			"BC010F511010Y04200000325\n" +
			"BC010F511010Y04200000331\n" +
			"BC010F511010Y04200000336\n" +
			"BC010F511010Y04200000339\n" +
			"BC010F511010Y04200000372\n" +
			"BC010F511010Y04200000376\n" +
			"BC010F511010Y04200000382\n" +
			"BC010F511010Y04200000386\n" +
			"BC010F511010Y04200000391\n" +
			"BC010F511010Y04200000393\n" +
			"BC010F511010Y04200000395\n" +
			"BC010F511010Y04200000399\n" +
			"BC010F511010Y04200000409\n" +
			"BC010F511010Y04200000425\n" +
			"BC010F511010Y04200000435\n" +
			"BC010F511010Y04200000441\n" +
			"BC010F511010Y04200000454\n" +
			"BC010F511010Y04200000475\n" +
			"BC010F511010Y04200000480\n" +
			"BC010F511010Y04200000482\n" +
			"BC010F511010Y04200000486\n" +
			"BC010F511010Y04200000503\n" +
			"BC010F511010Y04200000508\n" +
			"BC010F511010Y04200000513\n" +
			"BC010F511010Y04200000516\n" +
			"BC010F511010Y04200000518\n" +
			"511201203280756360\n" +
			"511201203300447000\n" +
			"511201203300458240\n" +
			"1006-39-20120405064836236236\n" +
			"511-39-20120406161228231\n" +
			"392012040511251218\n" +
			"1006-511-20120406140628654\n" +
			"511-10089-20120411172327000\n" +
			"1006-909-20120412151900909\n" +
			"1006-39-20120717141504509\n" +
			"511-20400-20120720031130640\n" +
			"1006-39-20120720054450173\n" +
			"1006-20400-20120709043946929\n" +
			"1006-20400-20120709050354850\n" +
			"1006-1-20120720060249765\n" +
			"511-20400-20120716020405221\n" +
			"511-20400-20120716070034481\n" +
			"1006-161-20120627034140291\n" +
			"1006-161-20120627051519644\n" +
			"511-20400-20120724112821975\n" +
			"1006-1379-20120727074255151\n" +
			"1006-39-20120731114820465\n" +
			"910-1029-201208170555304\n" +
			"1006-22-20120817063720552\n" +
			"87259873764411377615\n" +
			"1006-39-20120724203557313\n" +
			"480-1006-20120810172717912\n" +
			"1006-909-20120416195158368\n" +
			"1006-10-20120416202853487\n" +
			"1006-10-20120424153147418\n" +
			"1006-909-20120427160304392\n" +
			"1006-39-20120522140319835\n" +
			"511-10091-20120605221449000\n" +
			"511-10089-20120605222426000\n" +
			"910-1029-201208130937310\n" +
			"1006-39-20120613095815094\n" +
			"1006-39-20120613174518155\n" +
			"1006-10-20120613151228166\n" +
			"480-39-20120815031027205\n" +
			"511-39-20120815155724102\n" +
			"1006-39-20120815040047291\n" +
			"6892\n" +
			"6809\n" +
			"7362\n" +
			"7351\n" +
			"7348\n" +
			"6857\n" +
			"6858\n" +
			"6597\n" +
			"6815\n" +
			"6787\n" +
			"6831\n" +
			"7981\n" +
			"8111\n" +
			"7917\n" +
			"8116\n" +
			"7969\n" +
			"8081\n" +
			"7672\n" +
			"8125\n" +
			"8070\n" +
			"8086\n" +
			"8078\n" +
			"6769\n" +
			"6868\n" +
			"7352\n" +
			"6913\n" +
			"6608\n" +
			"6825\n" +
			"6829\n" +
			"7765\n" +
			"8124\n" +
			"8118\n" +
			"6866\n" +
			"7346\n" +
			"7349\n" +
			"7025\n" +
			"7166\n" +
			"8113\n" +
			"8117\n" +
			"7968\n" +
			"7673\n" +
			"7918\n" +
			"8076\n" +
			"8119\n" +
			"8089\n";
}
