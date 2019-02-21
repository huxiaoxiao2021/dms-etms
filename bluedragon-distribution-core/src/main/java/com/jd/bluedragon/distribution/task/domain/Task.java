package com.jd.bluedragon.distribution.task.domain;

import com.jd.bluedragon.core.redis.QueueKeyInfo;
import com.jd.bluedragon.core.redis.RedisTaskHelper;
import com.jd.bluedragon.core.redis.TaskMode;
import com.jd.bluedragon.core.redis.TaskModeAware;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;

import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;

public class Task implements java.io.Serializable, TaskModeAware{

    private final Log logger = LogFactory.getLog(this.getClass());
    /**
	 * 
	 */
	private static final long serialVersionUID = -3856203284357675794L;
	public static final Integer INTERVAL_TIME = 15;
    public static final Integer INITIAL_COUNT = 0;
    public static final Integer STEP = 1;
    
    public static final Integer TASK_STATUS_UNHANDLED = 0; // 未处理
    public static final Integer TASK_STATUS_PROCESSING = 1; // 处理中
    public static final Integer TASK_STATUS_FINISHED = 2; // 完成
    public static final Integer TASK_STATUS_FAILED = 3; // 处理失败
    public static final Integer TASK_STATUS_PARSE_ERROR = 4; // 格式转换失败
    
    public static final Integer TASK_YN_YES = 1; // 使用
    public static final Integer TASK_YN_NO = 0; // 已删除
    
    public static final Integer TASK_TYPE_POP = 1030; // pop收货
    public static final Integer TASK_TYPE_RECEIVE_COUNT = 1040; // 运单通知
    public static final Integer TASK_TYPE_BOUNDARY = 1050; // pop上门取货
    public static final Integer TASK_TYPE_MESSAGE=8000;//消息队列
    /** 收货相关　 */
    public static final Integer TASK_TYPE_RECEIVE = 1110; // 分拣中心收货
    /** 收货-空铁提货　 */
    public static final Integer TASK_TYPE_AR_RECEIVE = 1810;
    /** 收货-空铁提货并发货　 */
    public static final Integer TASK_TYPE_AR_RECEIVE_AND_SEND = 1811;
    /**
     * 空铁项目 - 发货登记任务
     */
    public static final Integer TASK_TYPE_AR_SEND_REGISTER = 1809;

    public static final Integer TASK_TYPE_SHIELDS_CAR_ERROR = 1120; // 分拣中心收货封车号封签异常
    public static final Integer TASK_TYPE_PUSH_MQ = 1150;    // 分拣中心收货发干线信息到计费系统

    /** 验货相关　 */
    public static final Integer TASK_TYPE_INSPECTION = 1130; // 分拣中心验货
    public static final Integer TASK_TYPE_SHIELDS_BOX_ERROR = 1140;// 分拣中心验货箱号封签异常
    public static final Integer TASK_TYPE_PARTNER_WAY_BILL = 1600;// 运单号关联包裹信息
    public static final Integer TASK_TYPE_PARTNER_WAY_BILL_NOTIFY = 1601;// 运单号关联包裹回传
    public static final Integer TASK_TYPE_WEIGHT = 1160;// 称重信息回传运单中心
    public static final Integer TASK_TYPE_POP_PRINT_INSPECTION=1180;//平台打印 补验货数据
    
    /** 分拣相关　 */
    public static final Integer TASK_TYPE_SORTING = 1200; // 分拣
    public static final Integer TASK_TYPE_SEAL_BOX = 1210; // 分拣封箱
    public static final Integer TASK_TYPE_RETURNS = 1220;// 分拣退货
    public static final Integer TASK_TYPE_SORTING_EXCEPTION=1240; //分拣异常记录日志
    public static final Integer TASK_TYPE_ZHIPEI_SORTING = 1250; // 智配分拣
    
    /** 发货发车相关　 */
    public static final Integer TASK_TYPE_DEPARTURE = 1400; // 发车
    /**
     * 1300-发货
     */
    public static final Integer TASK_TYPE_SEND_DELIVERY = 1300; // 发货
    public static final Integer TASK_TYPE_ACARABILL_SEND_DELIVERY = 1301; // 一车一单离线发货
    public static final Integer TASK_TYPE_WATBILL_NOTIFY = 1310; // 运单通知
    public static final Integer TASK_TYPE_CYCLE_BOX_STATUS = 1330; //同步青流箱状态

    /**
     * 整板发货任务
     */
    public static final Integer TASK_TYPE_BOARD_SEND = 1320;
    /**
     * 整板取消发货任务
     */
    public static final Integer TASK_TYPE_BOARD_SEND_CANCEL = 1321;

    public static final Integer TASK_TYPE_GLOBAL_TRADE = 1340; // 全球购

    public static final Integer TASK_TYPE_SCANNER_FRAME=7779;//龙门架自动发货
    /** PDA log */
    public static final Integer TASK_TYPE_PDA = 1700;// PDA日志
    
    /** 接货平台 */
    public static final Integer TASK_TYPE_RECEIVE_PICKUP = 2110;
    public static final Integer TASK_TYPE_RECEIVE_RECEIVE = 2120;
    public static final Integer TASK_TYPE_RECEIVE_EXCEPTION = 2130;
    
    /** 逆向物流 */
    public static final Integer TASK_TYPE_REVERSE_RECEIVE = 3100;
    public static final Integer TASK_TYPE_REVERSE_SEND = 3200;// 逆向发货
    public static final Integer TASK_TYPE_REVERSE_SPWARE = 3300;// 逆向备件库分拣
    public static final Integer TASK_TYPE_REVERSE_QUALITYCONTROL = 3340; //质控、外呼
    
    /** 运单 */
    public static final Integer TASK_TYPE_WAYBILL = 9999;
    public static final Integer TASK_TYPE_WAYBILL_FINISHED = 6667;
    public static final Integer TASK_TYPE_WAYBILL_TRACK = 6666;

    /**
     * 自动分拣机分拣准备任务
     */
    public static final Integer TASK_TYPE_AUTO_SORTING_PREPARE=7777;

    /**
     * 自动分拣机交接验货准备任务
     */
    public static final Integer TASK_TYPE_AUTO_INSPECTION_PREPARE=7778;

    /**
     * 离线任务类型
     */
    public static final Integer TASK_TYPE_OFFLINE = 1800;
    public static final Integer TASK_TYPE_SEAL_OFFLINE = 1880;
    public static final Integer CANCEL_SORTING = 1201;
    public static final Integer CANCEL_THIRD_INSPECTION = 1131;
    
    /**
     * 离线超区处理
     */
    public static final Integer TASK_TYPE_OFFLINE_EXCEEDAREA = 6000;

    /**
     * 发车 发大波次新建任务类型 任务写到 task_send 任务表中
     */
    public static final Integer TASK_TYPE_DEPARTURE_CAR=1410;
    
    /**
     * xumei 基础资料-->跨分拣箱号中转维护导入文件中信息定时生效
     */
    public static final Integer TASK_TYPE_CROSS_BOX=2222;

    /**
     * 第三发发货数据推送财务 中间转换表
     * 按批次号写到该任务表，跑任务的时候根据批次号查send_d表
     * 再写到task_delivery_to_finance表
     */

    public static final Integer TASK_TYPE_DELIVERY_TO_FINANCE_BATCH = 1900;

    /**
     * 第三方发货数据推送财务
     */
    public static final Integer TASK_TYPE_DELIVERY_TO_FINANCE= 1910;

    /** 相关数据库表 */
    public static final String TABLE_NAME_WAYBILL = "task_waybill";
    public static final String TABLE_NAME_REVERSE = "task_reverse";
    public static final String TABLE_NAME_SORTING = "task_sorting";
    public static final String TABLE_NAME_SORTING_EXCEPTION= "task_sorting_ec";
    public static final String TABLE_NAME_POP = "task_pop";
    public static final String TABLE_NAME_POPBOUNDARY = "task_boundary";
    public static final String TABLE_NAME_SEND = "task_send";
    public static final String TABLE_NAME_INSPECTION = "task_inspection";
    public static final String TABLE_NAME_PDA = "task_pda";
    public static final String TABLE_NAME_RECEIVE_PICKUP = "task_receive_pickup";
    public static final String TABLE_NAME_RECEIVE_RECEIVE = "task_receive_receive";
    public static final String TABLE_NAME_RECEIVE_EXCEPTION = "task_receive_exception";
    
    public static final String TABLE_NAME_OFFLINE = "task_offline";
    public static final String TABLE_NAME_AUTOSORTING_HANDOVER = "task_handover";
    public static final String TABLE_NAME_WEIGHT = "task_weight";
    public static final String TABLE_NAME_GLOBAL_TRADE = "task_global_trade";
    public static final String TABLE_NAME_MESSAGE="task_message";
    public static final String TABLE_NAME_SCANNER_FRAME="task_scanner_frame";

    /**
     * 整板发货任务表
     */
    public static final String TABLE_NAME_BOARD_SEND="task_board_send";
    /**
     * 整板取消发货任务表
     */
    public static final String TABLE_NAME_BOARD_SEND_CANCEL = "task_board_send_cancel";

    /**
     * 同步青流箱状态
     */
    public static final String TABLE_NAME_CYCLE_BOX_STATUS = "task_cycle_box";

    /**xumei**/
    public static final String TABLE_NAME_CROSSBOX="task_crossbox";

    public static final String TABLE_NAME_DELIVERY_TO_FINANCE_BATCH = "task_delivery_to_finance_batch";

    public static final String TABLE_NAME_DELIVERY_TO_FINANCE = "task_delivery_to_finance";
    
    public static final String TABLE_NAME_AR_RECEIVE = "task_ar_receive";

    public static final String TABLE_NAME_POP_PRINT_INSPECTION = "task_pop_print_inspection";


    /** 相关数据库序列 */
    public static final String TABLE_NAME_WAYBILL_SEQ = "SEQ_TASK_WAYBILL";
    public static final String TABLE_NAME_REVERSE_SEQ = "SEQ_TASK_REVERSE";
    public static final String TABLE_NAME_SORTING_SEQ = "SEQ_TASK_SORTING";
    public static final String TABLE_NAME_SORTING_EXCEPTION_SEQ = "SEQ_TASK_SORTING_EC";
    public static final String TABLE_NAME_POP_SEQ = "SEQ_TASK_POP";
    public static final String TABLE_NAME_BOUNDARY_SEQ = "SEQ_TASK_BOUNDARY";
    public static final String TABLE_NAME_INSPECTION_SEQ = "SEQ_TASK_INSPECTION";
    public static final String TABLE_NAME_PDA_SEQ = "SEQ_TASK_PDA";
    public static final String TABLE_NAME_RECEIVE_PICKUP_SEQ = "SEQ_TASK_RECEIVE_PICKUP";
    public static final String TABLE_NAME_RECEIVE_RECEIVE_SEQ = "SEQ_TASK_RECEIVE_RECEIVE";
    public static final String TABLE_NAME_RECEIVE_EXCEPTION_SEQ = "SEQ_TASK_RECEIVE_EXCEPTION";
    public static final String TABLE_NAME_POP_RECEIVE_COUNT = "TASK_POP_RECIEVE_COUNT";
    
    public static final String TABLE_NAME_OFFLINE_SEQ = "SEQ_TASK_OFFLINE";
    public static final String TABLE_NAME_HANDOVER_SEQ = "SEQ_TASK_HANDOVER";
    public static final String TABLE_NAME_WEIGHT_SEQ = "SEQ_TASK_WEIGHT";
    public static final String TABLE_NAME_GLOBAL_TRADE_SEQ = "SEQ_TASK_GLOBAL_TRADE";
    public static final String TABLE_NAME_MESSAGE_SEQ="SEQ_TASK_MESSAGE";
    public static final String TABLE_NAME_SCANNER_FRAME_SEQ ="SEQ_TASK_SCANNER_FRAME";
    
    /**xumei**/
    public static final String TABLE_NAME_CORSS_BOX_SEQ ="TABLE_NAME_CROSSBOX_SEQ";
    //平台打印，补发货数据tangcq
    public static final String TABLE_NAME_POP_PRINT_INSPECTION_SEQ ="TABLE_NAME_POP_PRINT_INSPECTION_SEQ";


    /** 任务数据通过redis,还是通过数据库 **/
    public static final int TASK_DATA_SOURCE_REDIS = 1;
    public static final int TASK_DATA_SOURCE_DB = 2;
    
    /** 全局唯一ID */
    private Long id;
    
    /** 创建站点编号 */
    private Integer createSiteCode;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 执行次数 */
    private Integer executeCount;
    
    /** 下次执行次数 */
    private Date executeTime;
    
    /** 类型 */
    private Integer type;
    
    /** 状态 '0' 未处理 '1' 已处理 */
    private Integer status;
    
    private String statuses;
    
    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;
    
    /** 关键词1 */
    private String keyword1;
    
    /** 关键词2 */
    private String keyword2;
    
    /** 数据内容 */
    private String body;
    
    /** 动态表名 */
    private String tableName;
    
    /** 动态序列名 */
    private String sequenceName;
    
    /** 数据内容解析的对象 */
    private Object parsedObject;
    
    /** 箱号 */
    private String boxCode;
    
    /** 收货单位Code */
    private Integer receiveSiteCode;
    
    /** 信息指纹 */
    private String fingerprint;
    
    /** 部署环境 */
    private String ownSign;
    
    /** 业务类型 */
    private Integer businessType;
    
    /** 操作类型 */
    private Integer operateType;
    
    /** 操作时间 */
    private Date operateTime;

    /** 所属队列 */
    private Integer queueId;



    public Task() {
    }
    
    public Task(Long id) {
        this.id = id;
    }
    
    public String getBoxCode() {
        return this.boxCode;
    }
    
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
    
    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }
    
    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }
    
    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }
    
    public Integer getExecuteCount() {
        return this.executeCount;
    }
    
    public void setExecuteCount(Integer executeCount) {
        this.executeCount = executeCount;
    }
    
    public Date getExecuteTime() {
        return executeTime!=null?(Date)executeTime.clone():new Date();
    }
    
    public void setExecuteTime(Date executeTime) {
    	this.executeTime = executeTime!=null?(Date)executeTime.clone():null;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatuses() {
        return this.statuses;
    }
    
    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }
    
    public Integer getYn() {
        return this.yn;
    }
    
    public void setYn(Integer yn) {
        this.yn = yn;
    }
    
    public String getKeyword1() {
        return this.keyword1;
    }
    
    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }
    
    public String getKeyword2() {
        return this.keyword2;
    }
    
    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }
    
    public String getBody() {
        return this.body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }
    
    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getFingerprint() {
        return this.fingerprint;
    }
    
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
    
    public Object getParsedObject() {
        return this.parsedObject;
    }
    
    public void setParsedObject(Object parsedObject) {
        this.parsedObject = parsedObject;
    }
    
    public String getSequenceName() {
        return this.sequenceName;
    }
    
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
    
    public String getOwnSign() {
        return this.ownSign;
    }
    
    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }
    
    public Integer getBusinessType() {
		return this.businessType;
	}

	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public static String getTableName(Integer type) {
        Assert.notNull(type, "type must not be null");
        
        if (Task.TASK_TYPE_SORTING.equals(type) || Task.TASK_TYPE_SEAL_BOX.equals(type)
                || Task.TASK_TYPE_RETURNS.equals(type)) {
            return Task.TABLE_NAME_SORTING;
        } else if (Task.TASK_TYPE_RECEIVE.equals(type) || Task.TASK_TYPE_INSPECTION.equals(type)
                || Task.TASK_TYPE_SHIELDS_CAR_ERROR.equals(type)
                || Task.TASK_TYPE_SHIELDS_BOX_ERROR.equals(type)
                || Task.TASK_TYPE_PARTNER_WAY_BILL.equals(type)
                || Task.TASK_TYPE_PARTNER_WAY_BILL_NOTIFY.equals(type)
                || Task.TASK_TYPE_PUSH_MQ.equals(type)) {       // 增加干线计费系统任务
            return Task.TABLE_NAME_INSPECTION;
        } else if (Task.TASK_TYPE_DEPARTURE.equals(type)
                || TASK_TYPE_DEPARTURE_CAR.equals(type)
                || Task.TASK_TYPE_SEND_DELIVERY.equals(type)
                || Task.TASK_TYPE_WATBILL_NOTIFY.equals(type)) {
            return Task.TABLE_NAME_SEND;
        } else if (Task.TASK_TYPE_POP.equals(type)||Task.TASK_TYPE_WAYBILL_TRACK.equals(type)) {
            return Task.TABLE_NAME_POP;
        } else if (Task.TASK_TYPE_PDA.equals(type)) {
            return Task.TABLE_NAME_PDA;
        } else if (Task.TASK_TYPE_RECEIVE_PICKUP.equals(type)) {
            return Task.TABLE_NAME_RECEIVE_PICKUP;
        } else if (Task.TASK_TYPE_RECEIVE_RECEIVE.equals(type)) {
            return Task.TABLE_NAME_RECEIVE_RECEIVE;
        } else if (Task.TASK_TYPE_RECEIVE_EXCEPTION.equals(type)) {
            return Task.TABLE_NAME_RECEIVE_EXCEPTION;
        } else if (Task.TASK_TYPE_WAYBILL.equals(type)||TASK_TYPE_WAYBILL_FINISHED.equals(type)) {
            return Task.TABLE_NAME_WAYBILL;
        } else if (Task.TASK_TYPE_REVERSE_RECEIVE.equals(type) || Task.TASK_TYPE_REVERSE_SPWARE.equals(type)
                   || Task.TASK_TYPE_REVERSE_QUALITYCONTROL.equals(type) ) {
            return Task.TABLE_NAME_REVERSE;
        } else if (Task.TASK_TYPE_REVERSE_SEND.equals(type)) {
            return Task.TABLE_NAME_SEND;
        } else if (Task.TASK_TYPE_BOUNDARY.equals(type)) {
        	return Task.TABLE_NAME_POPBOUNDARY;
        } else if(Task.TASK_TYPE_RECEIVE_COUNT.equals(type)){
        	return Task.TABLE_NAME_POP_RECEIVE_COUNT;
        } else if (Task.TASK_TYPE_OFFLINE.equals(type)) {
        	return Task.TABLE_NAME_OFFLINE;
        } else if (Task.TASK_TYPE_AUTO_INSPECTION_PREPARE.equals(type)
                    || Task.TASK_TYPE_AUTO_SORTING_PREPARE.equals(type)){
            return Task.TABLE_NAME_AUTOSORTING_HANDOVER;
        }else if(Task.TASK_TYPE_SORTING_EXCEPTION.equals(type)){
            return Task.TABLE_NAME_SORTING_EXCEPTION;
        }else if(Task.TASK_TYPE_WEIGHT.equals(type)){
            return Task.TABLE_NAME_WEIGHT;
        }else if(Task.TASK_TYPE_GLOBAL_TRADE.equals(type)){
            return Task.TABLE_NAME_GLOBAL_TRADE;
        }else if(Task.TASK_TYPE_MESSAGE.equals(type)){
            return Task.TABLE_NAME_MESSAGE;
        }
        else if(Task.TASK_TYPE_SCANNER_FRAME.equals(type)){
            return Task.TABLE_NAME_SCANNER_FRAME;
        }
        else if(Task.TASK_TYPE_CROSS_BOX.equals(type)){
        	return Task.TABLE_NAME_CROSSBOX;
        }else if(Task.TASK_TYPE_DELIVERY_TO_FINANCE.equals(type)){
            return Task.TABLE_NAME_DELIVERY_TO_FINANCE;
        }else if(Task.TASK_TYPE_DELIVERY_TO_FINANCE_BATCH.equals(type)){
            return Task.TABLE_NAME_DELIVERY_TO_FINANCE_BATCH;
        }else if(Task.TASK_TYPE_AR_RECEIVE.equals(type)){
            return Task.TABLE_NAME_AR_RECEIVE;
        }else if (Task.TASK_TYPE_POP_PRINT_INSPECTION.equals(type)){
            return Task.TABLE_NAME_POP_PRINT_INSPECTION;
        }else if (Task.TASK_TYPE_BOARD_SEND.equals(type)){
            return Task.TABLE_NAME_BOARD_SEND;
        }else if(Task.TASK_TYPE_BOARD_SEND_CANCEL.equals(type)){
            return Task.TABLE_NAME_BOARD_SEND_CANCEL;
        }else if(Task.TASK_TYPE_CYCLE_BOX_STATUS.equals(type)){
            return Task.TABLE_NAME_CYCLE_BOX_STATUS;
        }
        
        return Task.TABLE_NAME_SORTING;
    }
    
    public static String getTaskWaybillTableName() {
        return Task.TABLE_NAME_WAYBILL;
    }
    
    public static String getSequenceName(String tableName) {
        if (Task.TABLE_NAME_SORTING.equals(tableName)) {
            return Task.TABLE_NAME_SORTING_SEQ;
        } else if (Task.TABLE_NAME_REVERSE.equals(tableName)) {
            return Task.TABLE_NAME_REVERSE_SEQ;
        } else if (Task.TABLE_NAME_WAYBILL.equals(tableName)) {
            return Task.TABLE_NAME_WAYBILL_SEQ;
        } else if (Task.TABLE_NAME_INSPECTION.equals(tableName)) {
            return Task.TABLE_NAME_INSPECTION_SEQ;
        } else if (Task.TABLE_NAME_POP.equals(tableName)) {
            return Task.TABLE_NAME_POP_SEQ;
        } else if (Task.TABLE_NAME_PDA.equals(tableName)) {
            return Task.TABLE_NAME_PDA_SEQ;
        } else if (Task.TABLE_NAME_RECEIVE_PICKUP.equals(tableName)) {
            return Task.TABLE_NAME_RECEIVE_PICKUP_SEQ;
        } else if (Task.TABLE_NAME_RECEIVE_RECEIVE.equals(tableName)) {
            return Task.TABLE_NAME_RECEIVE_RECEIVE_SEQ;
        } else if (Task.TABLE_NAME_RECEIVE_EXCEPTION.equals(tableName)) {
            return Task.TABLE_NAME_RECEIVE_EXCEPTION_SEQ;
        } else if (Task.TABLE_NAME_POPBOUNDARY.equals(tableName)) {
        	return Task.TABLE_NAME_BOUNDARY_SEQ;
        } else if (Task.TABLE_NAME_OFFLINE.equals(tableName)) {
        	return Task.TABLE_NAME_OFFLINE_SEQ;
        } else if (Task.TABLE_NAME_AUTOSORTING_HANDOVER.equals(tableName)){
            return Task.TABLE_NAME_HANDOVER_SEQ;
        }else if(Task.TABLE_NAME_SORTING_EXCEPTION.equals(tableName)){
            return Task.TABLE_NAME_SORTING_EXCEPTION_SEQ;
        }else if(Task.TABLE_NAME_WEIGHT.equals(tableName)){
            return Task.TABLE_NAME_WEIGHT_SEQ;
        }else if(Task.TABLE_NAME_GLOBAL_TRADE.equals(tableName)){
            return Task.TABLE_NAME_GLOBAL_TRADE_SEQ;
        }else if(Task.TABLE_NAME_MESSAGE.equals(tableName)){
            return Task.TABLE_NAME_MESSAGE_SEQ;
        }else if(Task.TABLE_NAME_SCANNER_FRAME.equals(tableName)){
            return Task.TABLE_NAME_SCANNER_FRAME_SEQ;
        }else if(Task.TABLE_NAME_CROSSBOX.equals(tableName)){
        	return Task.TABLE_NAME_CORSS_BOX_SEQ;
        }else if (Task.TABLE_NAME_POP_PRINT_INSPECTION.equals(tableName)){
            return Task.TABLE_NAME_POP_PRINT_INSPECTION_SEQ;
        }
        
        return Task.TABLE_NAME_SORTING_SEQ;
    }
    
    @Override
    public String toString() {
        return "Task [id=" + this.id + ", createSiteCode=" + this.createSiteCode + ", type="
                + this.type + "]";
    }

	@Override
	// 提示：redis改造的任务需要检查本接口
	public TaskMode findTaskMode() {
		if (type == null) {
			return TaskMode.DB;
		}
		if (Task.TASK_TYPE_SEAL_BOX.equals(type)
				|| Task.TASK_TYPE_RETURNS.equals(type)
				|| Task.TASK_TYPE_SHIELDS_CAR_ERROR.equals(type)
				|| Task.TASK_TYPE_SHIELDS_BOX_ERROR.equals(type)
				|| Task.TASK_TYPE_PARTNER_WAY_BILL.equals(type)
				|| ( Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1) && keyword1.equals("1"))
				|| ( Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1) && keyword1.equals("2"))
				|| ( Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1) && keyword1.equals("3"))
//				|| ( Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1) && keyword1.equals("4"))
				|| ( Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1) && keyword1.equals("5"))
				|| (Task.TASK_TYPE_DEPARTURE.equals(type)
						&& StringHelper.isNotEmpty(keyword1) && keyword1
							.equals("5"))
				|| Task.TASK_TYPE_BOUNDARY.equals(type)
				|| Task.TASK_TYPE_POP.equals(type)
				|| Task.TASK_TYPE_OFFLINE.equals(type)
				|| Task.TASK_TYPE_RECEIVE.equals(type)
				|| Task.TASK_TYPE_INSPECTION.equals(type)
				|| Task.TASK_TYPE_REVERSE_SPWARE.equals(type)
				|| Task.TASK_TYPE_SORTING.equals(type)
				|| Task.TASK_TYPE_REVERSE_RECEIVE.equals(type)
				|| Task.TASK_TYPE_WEIGHT.equals(type)
                || Task.TASK_TYPE_PUSH_MQ.equals(type)
                ||Task.TASK_TYPE_MESSAGE.equals(type)
                ||Task.TASK_TYPE_SCANNER_FRAME.equals(type)) {  // 增加干线计费系统任务
			return TaskMode.REDIS;
		}
		return TaskMode.DB;
	}

	// 提示：redis改造的任务需要检查本接口
	public QueueKeyInfo findQueueKey() {
		QueueKeyInfo result = null;
		
		//1.设定task type, 如果为空则直接返回空
		String taskType = null;
		if (Task.TASK_TYPE_SEAL_BOX.equals(type)) {
			taskType = "SealBoxRedisTask";
		} else if (Task.TASK_TYPE_RETURNS.equals(type)) {
			taskType = "SortingReturnRedisTask";
		} else if (Task.TASK_TYPE_SHIELDS_BOX_ERROR.equals(type)) {
			taskType = "ShieldsBoxErrorRedisTask";
		} else if (Task.TASK_TYPE_SHIELDS_CAR_ERROR.equals(type)) {
			taskType = "ShieldsCarErrorRedisTask";
		} else if (Task.TASK_TYPE_PARTNER_WAY_BILL.equals(type)) {
			taskType = "PartnerWaybillRedisTask";
		} else if (Task.TASK_TYPE_SEND_DELIVERY.equals(type) && StringHelper.isNotEmpty(keyword1)) {
			if (keyword1.equals("1")) {
				taskType = "SendDeliveryTowaybillRedisTask";
			} else if (keyword1.equals("2")) {
				taskType = "SendDeliveryTotmsRedisTask";
			} else if (keyword1.equals("3")) {
				taskType = "ReverseDeliveryRedisTask";
//			} else if (keyword1.equals("4")) {
//				taskType = "ReverseSendRedisTask";
			} else if (keyword1.equals("5")) {
				taskType = "TransitSendRedisTask";
			}
		} else if (Task.TASK_TYPE_DEPARTURE.equals(type)
				&& StringHelper.isNotEmpty(keyword1) && keyword1.equals("5")) {
			taskType = "ThirdDepartureRedisTask";
		} else if (Task.TASK_TYPE_BOUNDARY.equals(type)) {
			taskType = "PopPickupRedisTask";
		} else if (Task.TASK_TYPE_POP.equals(type)) {
			taskType = "PopReceiveRedisTask";
		} else if (Task.TASK_TYPE_OFFLINE.equals(type)) {
			taskType = "OfflineCoreRedisTask";
		} else if(Task.TASK_TYPE_RECEIVE.equals(type)){
			taskType = "ReceiveRedisTask";
		} else if(Task.TASK_TYPE_INSPECTION.equals(type)){
			taskType = "InspectionRedisTask";
		} else if(Task.TASK_TYPE_REVERSE_SPWARE.equals(type)){
			taskType = "ReverseSpareRedisTask";
		} else if(Task.TASK_TYPE_SORTING.equals(type)){
			taskType = "SortingRedisTask";
		} else if(Task.TASK_TYPE_REVERSE_RECEIVE.equals(type)){
			taskType = "ReverseRejectRedisTask";
		} else if(Task.TASK_TYPE_PUSH_MQ.equals(type)){  // 增加干线计费系统任务
            taskType = "PushMQ2ArteryBillingSysRedisTask";
        }else if(Task.TASK_TYPE_SORTING_EXCEPTION.equals(type)){
            taskType="SortingExceptionRedisTask";
        }else if(Task.TASK_TYPE_WEIGHT.equals(type)){
            taskType="WeightRedisTask";
        }else if(Task.TASK_TYPE_MESSAGE.equals(type)){
            taskType="MessageRedisTask";
        }else if(Task.TASK_TYPE_SCANNER_FRAME.equals(type)){
            taskType="ScannerFrameRedisTask";
        }

		if(StringUtils.isEmpty(taskType))
			return null;
		
		//2.计算任务的灰度 FIXME:是否应从配置文件中得来
		if(StringUtils.isEmpty(ownSign))
			ownSign = BusinessHelper.getOwnSign();;
		
		//3.计算任务的队列号
		Integer queueId = getFingerprint() != null ? Math.abs(getFingerprint()
				.hashCode()) % RedisTaskHelper.getQueueNum() : Math.abs(getBody()
				.hashCode()) % RedisTaskHelper.getQueueNum();
				
		StringBuilder queueKey = new StringBuilder(taskType).append("$").append(ownSign).append(queueId);
		
		//4.设定QueueKeyInfo
		result = new QueueKeyInfo(taskType, ownSign, queueId, queueKey.toString());
		
		return result;
	}

    /**
     * 获取当前任务 任务类型
     * @return
     */
	public  String findTaskNameByTaskType(){

        if(TASK_TYPE_POP.equals(type)){
            return "PopReceiveTaskN";
        }else if(TASK_TYPE_RECEIVE_COUNT.equals(type)){
            return  "PopRecieveCountTask";
        }else if(TASK_TYPE_BOUNDARY.equals(type)){
            return "PopPickupTask";
        }else if(TASK_TYPE_MESSAGE.equals(type)){
            return "MessageTask";
        }else if(TASK_TYPE_RECEIVE.equals(type)){
            return "ReceiveTaskN";
        }else if(TASK_TYPE_AR_RECEIVE.equals(type)){
            return "ArReceiveTask";
        }else if(TASK_TYPE_AR_RECEIVE_AND_SEND.equals(type)){
            //TASK_TYPE_AR_RECEIVE_AND_SEND = 1811; //不会有


        }else if(TASK_TYPE_AR_SEND_REGISTER.equals(type)){
            //TASK_TYPE_AR_SEND_REGISTER = 1809; //不会有

        }else if(TASK_TYPE_SHIELDS_CAR_ERROR.equals(type)){
            return "ShieldsCarErrorTask";
        }else if(TASK_TYPE_PUSH_MQ.equals(type)){
            return "PushMQ2ArteryBillingSysTask";
        }else if(TASK_TYPE_INSPECTION.equals(type)){
            return "InspectionTaskN";
        }else if(TASK_TYPE_SHIELDS_BOX_ERROR.equals(type)){
            return "ShieldsBoxErrorTask";
        }else if(TASK_TYPE_PARTNER_WAY_BILL.equals(type)){
            return "PartnerWaybillTaskN";
        }else if(TASK_TYPE_PARTNER_WAY_BILL_NOTIFY.equals(type)){
            return "PartnerWaybillSynchroTaskN";
        }else if(TASK_TYPE_WEIGHT.equals(type)){
            return "WeightTask";
        }else if(TASK_TYPE_SORTING.equals(type)){
            return "SortingTaskN";
        }else if(TASK_TYPE_SEAL_BOX.equals(type)){
            return "SealBoxTask";
        }else if(TASK_TYPE_RETURNS.equals(type)){
            return "SortingReturnTask";
        }else if(TASK_TYPE_SORTING_EXCEPTION.equals(type)){
            return "SortingExceptionTask";
        }else if(TASK_TYPE_ZHIPEI_SORTING.equals(type)){
            return "SortingZhiPeiTask";
        }else if(TASK_TYPE_DEPARTURE.equals(type)){
            return "thirdDepartureTask";
        }else if(TASK_TYPE_SEND_DELIVERY.equals(type)){
            if("1".equals(keyword1)){
                return "SendDeliveryTowaybillRedisTask";
            }else if("2".equals(keyword1)){
                return "SendDeliveryTotmsTaskN";
            }else if("3".equals(keyword1)){
                return "ReverseDeliveryTaskN";
            }else if("4".equals(keyword1)){
                return "ReverseSendTask";
            }else if("5".equals(keyword1)){
                return "TransitSendTask";
            }else if("6".equals(keyword1)){
                return "SendDetailMQTask";
            }else if("7".equals(keyword1)){
                return "BoardDeliveryTask";
            }else if("8".equals(keyword1)){
                return "BatchForwardTask";
            }
        }else if(TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(type)){
            //TASK_TYPE_ACARABILL_SEND_DELIVERY = 1301; // 不会有


        }else if(TASK_TYPE_WATBILL_NOTIFY.equals(type)){
            //TASK_TYPE_WATBILL_NOTIFY = 1310; //  不能确定

        }else if(TASK_TYPE_GLOBAL_TRADE.equals(type)){
            //TASK_TYPE_GLOBAL_TRADE = 1340; //  不能确定

        }else if(TASK_TYPE_SCANNER_FRAME.equals(type)){
            return "ScannerFrameTask";
        }else if(TASK_TYPE_PDA.equals(type)){
            //TASK_TYPE_PDA = 1700;// PDA日志 不插库

        }else if(TASK_TYPE_RECEIVE_PICKUP.equals(type)){
           // TASK_TYPE_RECEIVE_PICKUP = 2110; //不确定

        }else if(TASK_TYPE_RECEIVE_RECEIVE.equals(type)){
            //TASK_TYPE_RECEIVE_RECEIVE = 2120; //不确定
        }else if(TASK_TYPE_RECEIVE_EXCEPTION.equals(type)){
            //TASK_TYPE_RECEIVE_EXCEPTION = 2130; //不确定
        }else if(TASK_TYPE_REVERSE_RECEIVE.equals(type)){
            return "ReverseRejectTask";
        }else if(TASK_TYPE_REVERSE_SEND.equals(type)){
            //TASK_TYPE_REVERSE_SEND = 3200;// 逆向发货 不确定

        }else if(TASK_TYPE_REVERSE_SPWARE.equals(type)){
            return "ReverseSpareTask";
        }else if(TASK_TYPE_REVERSE_QUALITYCONTROL.equals(type)){
            return "QualityControlTask";
        }else if(TASK_TYPE_WAYBILL.equals(type)){
            return "WaybillStatusTaskN";
        }else if(TASK_TYPE_WAYBILL_FINISHED.equals(type)){
            return "WaybillStatusTaskN";
        }else if(TASK_TYPE_WAYBILL_TRACK.equals(type)){
            return "WaybillTrackTask";
        }else if(TASK_TYPE_AUTO_SORTING_PREPARE.equals(type)){
            return "SortingPrepareTask";
        }else if(TASK_TYPE_AUTO_INSPECTION_PREPARE.equals(type)){
            return "InspectionPrepareTask";
        }else if(TASK_TYPE_OFFLINE.equals(type)){
            return "OfflineCoreTask";
        }else if(TASK_TYPE_SEAL_OFFLINE.equals(type)){
            //TASK_TYPE_SEAL_OFFLINE = 1880; //通过1800进来的

        }else if(TASK_TYPE_OFFLINE_EXCEEDAREA.equals(type)){
            //TASK_TYPE_OFFLINE_EXCEEDAREA = 6000; // 没有直接添加 转换成了1200的任务

        }else if(TASK_TYPE_DEPARTURE_CAR.equals(type)){
            return "BatchSendCarTask";
        }else if(TASK_TYPE_CROSS_BOX.equals(type)){
            return "CrossBoxTask";
        }else if(TASK_TYPE_DELIVERY_TO_FINANCE_BATCH.equals(type)){
            return "DeliveryToFinanceBatchTask";
        }else if(TASK_TYPE_DELIVERY_TO_FINANCE.equals(type)){
            return "DeliveryToFinanceTask";
        }else if(TASK_TYPE_POP_PRINT_INSPECTION.equals(type)){
            return "PopPrintInspectionTask";
        }else if(TASK_TYPE_BOARD_SEND.equals(type)){
            return "BoardDeliveryTask";
        }else if(TASK_TYPE_BOARD_SEND_CANCEL.equals(type)){
            return "BoardDeliveryCancelTask";
        }else if(TASK_TYPE_CYCLE_BOX_STATUS.equals(type)){
            return "CycleBoxStatusTask";
        }
        //未根据类型获取到相应任务的，按表名处理 ，需要确保此表只有一个task在执行
        if(StringUtils.isNotBlank(tableName)){
            if(TABLE_NAME_WAYBILL.equals(tableName)){
                return "WaybillStatusTaskN"; //同步运单状态相关任务
            }
        }

        //抛异常
        logger.error("获取任务类型异常 "+allToString());
        String errorMessage ="task type not found taskType:"+type;
        Profiler.businessAlarm("Task.getTaskNameByTaskType", System.currentTimeMillis(), errorMessage);
        return null;
    }

    /**
     * 获取当前任务队列数
     * 未获取到任务类型队列数量或者异常时 返回1
     * @return
     */
    public int findTaskQueueSize(Map<String, Integer> allQueueSize){
        CallerInfo info = null;
        int queueSize = 1;
	    try{
            info = Profiler.registerInfo( "DMSWEB.Task.getTaskQueueSize",false, true);
            String taskType = findTaskNameByTaskType();
            if(StringUtils.isNotBlank(taskType) && allQueueSize.containsKey(taskType)){
                queueSize =  allQueueSize.get(taskType);
            }
        }catch (Exception e){
            Profiler.functionError(info);
            logger.error("获取任务队列数异常 "+e.getMessage());
        }finally {
            Profiler.registerInfoEnd(info);
            return queueSize;
        }


    }

    public String allToString() {
        return "Task{" +
                "id=" + id +
                ", createSiteCode=" + createSiteCode +
                ", createTime=" + createTime +
                ", executeCount=" + executeCount +
                ", executeTime=" + executeTime +
                ", type=" + type +
                ", status=" + status +
                ", statuses='" + statuses + '\'' +
                ", yn=" + yn +
                ", keyword1='" + keyword1 + '\'' +
                ", keyword2='" + keyword2 + '\'' +
                ", body='" + body + '\'' +
                ", tableName='" + tableName + '\'' +
                ", sequenceName='" + sequenceName + '\'' +
                ", parsedObject=" + parsedObject +
                ", boxCode='" + boxCode + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", fingerprint='" + fingerprint + '\'' +
                ", ownSign='" + ownSign + '\'' +
                ", businessType=" + businessType +
                ", operateType=" + operateType +
                ", operateTime=" + operateTime +
                ", queueId=" + queueId +
                '}';
    }
}