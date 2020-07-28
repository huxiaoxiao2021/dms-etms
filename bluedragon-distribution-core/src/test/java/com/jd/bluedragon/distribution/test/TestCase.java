package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.core.message.MessageConstant;
import com.jd.bluedragon.distribution.api.request.DeliveryBatchRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TestCase {

    @Test
	public void test() {
		String waybillCode1 = "ZYNA00000000422-1-2-";
		String waybillCode2 = "ZYN000000000422-1-2-";
        System.out.println(waybillCode1.split("[-NS]")[1]);
        System.out.println(waybillCode2.split("[-NS]")[1]);
        System.out.println(WaybillUtil.getPackIndexByPackCode(waybillCode1));
        System.out.println(WaybillUtil.getPackIndexByPackCode(waybillCode2));
		
	}
	
	private String name;
	
	public void test(TestCase testCase) {
		System.out.println(testCase.name);
	}
	
	public static void main(String[] args) throws Exception {
		DeliveryBatchRequest t = new DeliveryBatchRequest();
		List<DeliveryRequest>  tt = new ArrayList<DeliveryRequest>();
		t.setSiteCode(511);
		t.setBusinessType(10);
		t.setUserCode(110);
		t.setUserName("userName");
		
		DeliveryRequest d = new DeliveryRequest();
		d.setBoxCode("1,2,3,4,");
		d.setReceiveSiteCode(1);
		d.setSendCode("511-1-1111");
		tt.add(d);
		
		d = new DeliveryRequest();
		d.setBoxCode("11,22,33,44,55");
		d.setReceiveSiteCode(2);
		d.setSendCode("511-2-2222");
		tt.add(d);
		
		d = new DeliveryRequest();
		d.setBoxCode("111,222,333,444,555");
		d.setReceiveSiteCode(3);
		d.setSendCode("511-3-3333");
		tt.add(d);
		t.setDeliverys(tt);
		
		System.out.println(JsonHelper.toJson(t));
        TestCase testCase=new TestCase();
        System.out.printf("======================================================================\n");
        System.out.printf("======================================================================\n");
        System.out.printf("======================================================================\n");
        System.out.printf("======================================================================\n");



        String body=testCase.generateCustomerBody("dms_send", "VirtualTopic.bd_dms_reverse_send",
                "java.util.String", "{\n" +
                        "  \"operatorId\" : \"14640\",\n" +
                        "  \"operatorName\" : \"蔡飞\",\n" +
                        "  \"packageCode\" : \"W0383739160\",\n" +
                        "  \"pickWareCode\" : \"Q222809677\",\n" +
                        "  \"dispatchTime\" : \"2016-04-20 11:18:04\",\n" +
                        "  \"sendCode\" : \"121674-21633-201604201118030\"\n" +
                        "}", MessageConstant.ReverseSend.getName()
                        + "packageCode");

        System.out.printf(body);
    }
    private  String generateCustomerBody(String executorKey,
                                        String statementId,
                                        String dataType,
                                        String data,
                                        String businessId){
        TriggerMessage triggerMessage = new TriggerMessage();
        triggerMessage.setParameter(data);
        triggerMessage.setStatement(statementId);
        triggerMessage.setParamClassName(dataType);
        List<TriggerMessage> dataList = new ArrayList<TriggerMessage>(1);
        dataList.add(triggerMessage);
        return com.jd.bluedragon.utils.JsonHelper.toJson(dataList);
    }

    private class TriggerMessage {
        private String crud;
        private String statement;
        private Object parameter;
        private String paramClassName;

        public TriggerMessage() {
        }

        public String getParamClassName() {
            return this.paramClassName;
        }

        public void setParamClassName(String paramClassName) {
            this.paramClassName = paramClassName;
        }

        public String getStatement() {
            return this.statement;
        }

        public void setStatement(String statement) {
            this.statement = statement;
        }

        public Object getParameter() {
            return this.parameter;
        }

        public void setParameter(Object parameter) {
            this.parameter = parameter;
        }

        public String getCrud() {
            return this.crud;
        }

        public void setCrud(String crud) {
            this.crud = crud;
        }
    }
}
