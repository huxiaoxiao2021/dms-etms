package com.jd.bluedragon.distribution.test.print;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoServiceImpl;
import com.jd.bluedragon.utils.JsonHelper;

public class HideInfoServiceImplTestCase {
	@Test
    public void testSetHideInfo() throws Exception{
    	HideInfoServiceImpl hideInfoServiceImpl = new HideInfoServiceImpl();
    	List<String> sendPays = new ArrayList<String>();
    	sendPays.add(null);
    	sendPays.add(getSignChar(188,'0'));
    	sendPays.add(getSignChar(188,'1'));
    	sendPays.add(getSignChar(188,'2'));
    	sendPays.add(getSignChar(188,'3'));
    	sendPays.add(getSignChar(188,'4'));
    	sendPays.add(getSignChar(188,'5'));
    	sendPays.add(getSignChar(188,'6'));
    	sendPays.add(getSignChar(188,'7'));
    	sendPays.add(getSignChar(188,'8'));
    	for(String sendPay:sendPays){
    		BasePrintWaybill waybill = new BasePrintWaybill();
        	waybill.setCustomerName("收件人姓名");
        	waybill.setPrintAddress("收件人地址67890123456");
        	waybill.setTelFirst("1501001");
        	waybill.setTelLast("1002");
        	waybill.setMobileFirst("1511002");
        	waybill.setMobileLast("1003");
        	waybill.setCustomerContacts(waybill.getTelFirst()+waybill.getTelLast()+","+waybill.getMobileFirst()+waybill.getMobileLast());
    		hideInfoServiceImpl.setHideInfo(null, sendPay, waybill);
    		System.err.println(JsonHelper.toJson(waybill));
    	}
    }
	/**
	 * 
	 * @ClassName: TestCase
	 * @Description: TODO
	 * @author: wuyoude
	 * @date: 2020年2月28日 上午11:16:29
	 * 
	 * @param <T> 目标对象，入参
	 * @param <R> 处理结果对象，出参
	 * @param <C_R>预期结果对象， 用于检查校验结果
	 */
    private static abstract class TestCase<T,R,C_R>{
    	T target;
    	R result;
    	C_R checkResult;
    	/**
    	 * 结果验证方法
    	 * @return
    	 */
		public abstract boolean isSuccess() throws Exception;
    	/**
		 * @return the target
		 */
		public T getTarget() {
			return target;
		}

		/**
		 * @param target the target to set
		 */
		public void setTarget(T target) {
			this.target = target;
		}

		/**
		 * @return the result
		 */
		public R getResult() {
			return result;
		}

		/**
		 * @param result the result to set
		 */
		public void setResult(R result) {
			this.result = result;
		}

		/**
		 * @return the checkResult
		 */
		public C_R getCheckResult() {
			return checkResult;
		}

		/**
		 * @param checkResult the checkResult to set
		 */
		public void setCheckResult(C_R checkResult) {
			this.checkResult = checkResult;
		}
    }
    private static class TargetObject{
    	String waybillSign;
    	String sendPay;
    	BasePrintWaybill waybill;
    }
    private static class ResultObject{
    	BasePrintWaybill waybill;
    }
    private String getSignChar(int position,char c){
    	char[] chars = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".toCharArray();
    	chars[position - 1] = c;
    	return new String(chars);
    }
}
