package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.sql.DataSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractScheduler<T> implements IScheduleTaskDealMulti<T>,ApplicationListener{
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private TBScheduleManagerFactory managerFactory;
    /**
     * 任务开关，默认开启
     */
    protected boolean open = true;
    /**
     * 尝试初始化次数，超过指定次数才抛出异常
     */
    protected int tryInitMaxTimes = 3;
    protected DataSource dataSource;
    protected String taskType;
    protected String ownSign;
    protected Integer type;
	/**
	 * 标识是否处于激活状态，任务初始化成功并且spring容器初始化成功，才能正常运行
	 */
    protected boolean isActive = false;
	
    public void init() throws Exception {
    	if(open){
    		int initTimes = 0;
    		boolean hasInit = false;
    		while(initTimes < tryInitMaxTimes && !hasInit){
    			initTimes ++;
    	    	try {
    				this.managerFactory.createTBScheduleManager(this.taskType, this.ownSign);
    				hasInit = true;
    			} catch (Exception e) {
    				log.error("任务[{}-{}]第{}次初始化失败！", this.taskType, this.ownSign, initTimes);
    				log.error(e.getMessage(),e);
    				//初始化失败，休眠100ms
    				Thread.sleep(100);
    			}
    		}
    		if(!hasInit){
    			throw new Exception("任务["+ownSign+"-"+taskType+"]初始化失败！");
    		}
    	}else{
    		log.warn("task[{}-{}] is not open!",ownSign,taskType);
    	}
    }
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		//监听初始化、刷新事件，isActive标识为true
		if (event instanceof ContextRefreshedEvent) {
			log.warn("Spring初始化完成，任务[{}-{}]开始执行！", this.taskType, this.ownSign);
			isActive = true;
		}
		//监听关闭事件，停止异步缓冲任务
		if (event instanceof ContextClosedEvent) {
			log.warn("Spring关闭，任务[{}-{}]停止执行！", this.taskType, this.ownSign);
			isActive = false;
		}
	}
    public List<Task> asList(Object[] taskArray) {
        if (taskArray == null) {
            return Collections.emptyList();
        }
        
        List<Task> tasks = new ArrayList<Task>();
        for (Object task : taskArray) {
            if (task != null && task instanceof Task) {
                tasks.add((Task) task);
            }
        }
        return tasks;
    }
    
    /**
     * 计算本机需要取得的任务数据数量, 当只有一台机器运行的情况下取得fetchNum数量的任务,
     * 否则计算本机要取得的任务数量(此数量使命中本机的数量约为fetchNum)
     * 
     * @param queueNum 对应任务类型的总处理队列数
     * @param fetchNum  每次取任务的总数
     * @param subQueues 分配到本机的队列数(可能有多台机器运行总共queueNum数量的队列)
     * @return
     */
    public int fecthNum(int queueNum, int fetchNum, List<String> subQueues) {
        if (queueNum != subQueues.size()) {
            fetchNum = fetchNum * queueNum / subQueues.size();
        }
        return fetchNum;
    }
    
    /**
     * 用于判断由id指定的任务是否由本机上的队列处理
     * @param queueNum
     *            抓取到的队列总数
     * @param id 任务id或其它
     * @param subQueues
     *            当前实例分配到的队列,队列编号(1,2,3,4.....)
     * @return
     */
    public boolean isMyTask(long queueNum, long id, List<?> subQueues) {
        return true;
        /*if (queueNum == subQueues.size()) {//说明只有一台机器
            return true;
        }
        
        long m = id % queueNum;//计算此task的模值,用于确定分配到哪个队列，对应PAMIRS_SCHEDULE_QUEUE表中的queue_id
        for (Object o : subQueues) {//遍历本机器上的分配的任务队列，如果计算得出的队列在本机器上，返回true
            if (m == Long.parseLong(o.toString())) {
                return true;
            }
        }
        return false;*/
    }
    
    /**
     * 筛选出应由本机处理的任务
     * 
     * @param queueNum 对应任务类型的总处理队列数
     * @param subQueues 分配到本机的队列数(可能有多台机器运行总共queueNum数量的队列)
     * @param unhandles 未处理的任务
     * @param methodName 用于取得任务的id或者其它可以唯一指定此任务的整型值(用于计算此任务属于哪个队列)
     * @return
     */
    public List<T> assign(int queueNum, List<String> subQueues, List<T> unhandles, String methodName) {
        if (queueNum == subQueues.size()) {
            return unhandles;
        }
        
        List<T> assigns = new ArrayList<T>();
        for (T t : unhandles) {
            try {
                Method method = t.getClass().getMethod(methodName);
                Long value = new Long(String.valueOf(method.invoke(t)));
                
                if (this.isMyTask(queueNum, value, subQueues)) {
                    assigns.add(t);
                }
            } catch (Exception e) {
                this.log.error("error!", e);
                continue;
            }
        }
        return assigns;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public TBScheduleManagerFactory getManagerFactory() {
        return this.managerFactory;
    }
    
    public void setManagerFactory(TBScheduleManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}
	/**
	 * @return the tryInitMaxTimes
	 */
	public int getTryInitMaxTimes() {
		return tryInitMaxTimes;
	}
	/**
	 * @param tryInitMaxTimes the tryInitMaxTimes to set
	 */
	public void setTryInitMaxTimes(int tryInitMaxTimes) {
		this.tryInitMaxTimes = tryInitMaxTimes;
	}
    
}
