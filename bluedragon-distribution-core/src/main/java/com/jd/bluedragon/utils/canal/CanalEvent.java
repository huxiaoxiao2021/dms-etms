package com.jd.bluedragon.utils.canal;

import java.io.Serializable;

/**
 * 
 * @ClassName: CanalEvent
 * @Description: (类描述信息)
 * @author wuyoude
 * @date 2017年5月2日 下午10:48:35
 * 
 * @param <T>
 */
public class CanalEvent<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -831897060069658041L;
	/**
	 * 数据库操作类型
	 */
	private DbOperation dbOperation;
	/**
	 * 变更前数据
	 */
	private T dataBefore;
	/**
	 * 变更后数据
	 */
	private T dataAfter;
	public DbOperation getDbOperation() {
		return dbOperation;
	}
	public void setDbOperation(DbOperation dbOperation) {
		this.dbOperation = dbOperation;
	}
	public T getDataBefore() {
		return dataBefore;
	}
	public void setDataBefore(T dataBefore) {
		this.dataBefore = dataBefore;
	}
	public T getDataAfter() {
		return dataAfter;
	}
	public void setDataAfter(T dataAfter) {
		this.dataAfter = dataAfter;
	}
}
