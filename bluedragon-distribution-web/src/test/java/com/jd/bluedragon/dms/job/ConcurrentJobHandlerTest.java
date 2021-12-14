/**
 * 
 */
package com.jd.bluedragon.dms.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * @ClassName: ConcurrentJobHandlerTest
 * @Description: TODO
 * @author wuyoude
 * @date 2021年12月15日 下午10:19:45
 *
 */
public class ConcurrentJobHandlerTest {
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("test-concurrentJobHandler-%d").build();
	private static final ExecutorService cachedThreadPool = new ThreadPoolExecutor(10, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue(),namedThreadFactory);
	private static final Random r = new Random();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}

	public static void test() {
		try {
			long s1 = System.currentTimeMillis();
			Api api = new ApiImpl();
			RequestA request = new RequestA();
			request.strA = "test";
			request.strList = new ArrayList<String>();
			int total = 99;
			for(int i=1;i<=total;i++) {
				request.strList.add("第"+i+"个");
			}
			System.out.println("request:"+JsonHelper.toJson(request));
//			ResultA result = api.testRequestA(request);
			
			long u1 = System.currentTimeMillis() - s1;
			
			long s2 = System.currentTimeMillis();
			AHandler handler1 = new AHandler(cachedThreadPool);
			handler1.setMaxThreadNum(5);
	        JobResult<ResultA> mergedResult1 = handler1.handle(request);
	        long u2 = System.currentTimeMillis() - s2;
			
			
			long s3 = System.currentTimeMillis();
			AHandler handler2 = new AHandler(cachedThreadPool);
			handler2.setMaxThreadNum(10);
			JobResult<ResultA> mergedResult2 = handler2.handle(request);
	        long u3 = System.currentTimeMillis() - s3;
	        
//	        System.out.println("不并发：use:" + u1 +" ms ," + "result:"+JsonHelper.toJson(result));
	        System.out.println("5并发use:" + mergedResult1.getCostTime() +" ms ," + "result:"+JsonHelper.toJson(mergedResult1));
			System.out.println("10并发use:" + mergedResult2.getCostTime() +" ms ," + "result:"+JsonHelper.toJson(mergedResult2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class AHandler extends ConcurrentJobHandler<RequestA,ResultA>{
		public AHandler(ExecutorService executorService) {
			super(executorService);
		}
		private int maxThreadNum = 10;
		private int perNum = 5;
		private int minSplitNum = 10;
		@Override
		public List<RequestA> split(RequestA requestA) {
			List<RequestA> list = new ArrayList<RequestA>();
			
			List<List<String>> strLists = CollectionHelper.splitList(requestA.strList, maxThreadNum, minSplitNum);
			if(CollectionUtils.isNotEmpty(strLists)) {
				for(List<String> strList : strLists) {
					RequestA temp = new RequestA();
					temp.strA = requestA.strA;
					temp.strList = strList;
					list.add(temp);
				}
			}
			return list;
		}

		@Override
		public ResultA merge(List<ResultA> resultList) {
			ResultA mergedResult = new ResultA();
			mergedResult.suc = true;
			mergedResult.resultList = new ArrayList<String>();
			for(ResultA resultA : resultList) {
				if(resultA.suc) {
					mergedResult.resultList.addAll(resultA.resultList);
				}
			}
			return mergedResult;
		}
		@Override
		protected ResultA doJob(RequestA t) {
			return new ApiImpl().testRequestA(t);
		}

		public int getMaxThreadNum() {
			return maxThreadNum;
		}

		public void setMaxThreadNum(int maxThreadNum) {
			this.maxThreadNum = maxThreadNum;
		}

		public int getPerNum() {
			return perNum;
		}

		public void setPerNum(int perNum) {
			this.perNum = perNum;
		}

		public int getMinSplitNum() {
			return minSplitNum;
		}

		public void setMinSplitNum(int minSplitNum) {
			this.minSplitNum = minSplitNum;
		}
		
	}
	public static class RequestA{
		private String strA;
		private List<String> strList;
		public String getStrA() {
			return strA;
		}
		public void setStrA(String strA) {
			this.strA = strA;
		}
		public List<String> getStrList() {
			return strList;
		}
		public void setStrList(List<String> strList) {
			this.strList = strList;
		}
	}
	public static class ResultA implements Mergeable<ResultA>{
		private boolean suc;
		private List<String> resultList;
		
		@Override
		public ResultA merge(List<ResultA> resultList) {
			ResultA mergedResult = new ResultA();
			mergedResult.suc = true;
			mergedResult.resultList = new ArrayList<String>();
			for(ResultA resultA : resultList) {
				if(resultA.suc) {
					mergedResult.resultList.addAll(resultA.resultList);
				}
			}
			return mergedResult;
		}

		public boolean isSuc() {
			return suc;
		}

		public void setSuc(boolean suc) {
			this.suc = suc;
		}

		public List<String> getResultList() {
			return resultList;
		}

		public void setResultList(List<String> resultList) {
			this.resultList = resultList;
		}
	}
	public static interface Mergeable<R>{
		R merge(List<R> resultList);
	}	
	public static interface Api{
		ResultA testRequestA(RequestA request);
	}
	public static class ApiImpl implements Api{
		private static Random r = new Random();
		public ResultA testRequestA(RequestA request) {
			ResultA result = new ResultA();
			result.suc = true;
			result.resultList = new ArrayList<String>();
			String threadDesc = Thread.currentThread().getId()+"-"+Thread.currentThread().getName();
			for(String str: request.strList) {
				System.out.println("线程"+threadDesc+"开始处理："+str);
				try {
					Thread.sleep(100 +  r.nextInt(1));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				result.resultList.add(request.strA + "-" + str+"-执行线程："+threadDesc);
				System.out.println("线程"+threadDesc+"结束处理："+str);
			}
			return result;
		}
	}
}
