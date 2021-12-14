package com.jd.bluedragon.dms.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 执行处理可并发的任务处理器，子类需要实现以下3个方法：
 * 1、split负责拆分成子任务
 * 2、doJob负责执行每个子任务
 * 3、merge负责将执行后的子任务结果进行合并，返回一个结果
 * @author wuyoude
 *
 * @param <J> 任务实体
 * @param <R> 任务执行结果
 */
public abstract class ConcurrentJobHandler<J,R> implements JobHandler<J,R>{
	/**
	 * Future执行器
	 */
	private ExecutorService executorService;
	/**
	 * 过期时间(单位：毫秒)
	 */
	private long defaultTimeOutMiliSeconds = 30 * 1000;
	
	public ConcurrentJobHandler(ExecutorService executorService){
		this.executorService = executorService;
	}
	/**
	 * 拆分成子任务
	 * @param t
	 * @return
	 */
	protected abstract List<J> split(J job);
	/**
	 * 执行任务逻辑
	 * @param t
	 * @return
	 */
	protected abstract R doJob(J job);
	/**
	 * 合并处理结果
	 * @param resultList
	 * @return
	 */
	protected abstract R merge(List<R> resultList);
	
	@Override
	public JobResult<R> handle(J job) throws InterruptedException, ExecutionException, TimeoutException{
		return this.handle(job, defaultTimeOutMiliSeconds);
	}
	@Override
	public JobResult<R> handle(J job,long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		long startTime = System.currentTimeMillis();
		JobResult<R> result = new JobResult<R>();
		List<J> jobList = this.split(job);
		if(jobList == null || jobList.size() == 0) {
			result.toEmpty();
			result.setCostTime(System.currentTimeMillis() - startTime);
			return result;
		}
		//只有一个子任务，不需要线程池执行
		if(jobList.size() == 1) {
			result.toSuc(doJob(jobList.get(0)));
			result.setCostTime(System.currentTimeMillis() - startTime);
			return result;
		}
		List<Future<R>> futureList = new ArrayList<Future<R>>();
		/**
		 * 第1个子任务交给当前线程处理，其他的由线程池执行
		 */
        for (int i=1;i<jobList.size();i++) {
        	final J t0 = jobList.get(i);
            Future<R> future = this.executorService.submit(new Callable<R>(){
				@Override
				public R call() throws Exception {
					return doJob(t0);
				}
            });
            futureList.add(future);
        }
        //执行第一个任务
        List<R> resultList = new ArrayList<R>();
        R firstJobResult = doJob(jobList.get(0));
        resultList.add(firstJobResult);
        //获取子任务执行结果
        for (Future<R> future: futureList) {
			resultList.add(future.get(timeout,TimeUnit.MILLISECONDS));
        }
		result.setData(this.merge(resultList));
		result.setCostTime(System.currentTimeMillis() - startTime);
		return result;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	public long getDefaultTimeOutMiliSeconds() {
		return defaultTimeOutMiliSeconds;
	}
	public void setDefaultTimeOutMiliSeconds(long defaultTimeOutMiliSeconds) {
		this.defaultTimeOutMiliSeconds = defaultTimeOutMiliSeconds;
	}
}
