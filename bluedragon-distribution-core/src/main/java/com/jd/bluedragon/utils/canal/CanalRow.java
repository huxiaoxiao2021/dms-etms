package com.jd.bluedragon.utils.canal;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhanglei51 on 2016/11/28. canal行 即一个message消息
 */
public class CanalRow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2779624090137972955L;
	private String eventType; // 时间类型 insert update delete等
	private String tableName; // 表名
	private String originTableName; // 源表名
	private String database; // 数据库实例名
	private Long executeTime; // 执行时间
	private List<CanalColumn> beforeChangeOfColumns; // 变化之前的列 insert时为空
	private List<CanalColumn> afterChangeOfColumns; // 变化之后的列 delete时为空

	public CanalRow() {
	}

	public String getEventType() {
		return this.eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOriginTableName() {
		return this.originTableName;
	}

	public void setOriginTableName(String originTableName) {
		this.originTableName = originTableName;
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setExecuteTime(Long executeTime) {
		this.executeTime = executeTime;
	}

	public Long getExecuteTime() {
		return this.executeTime;
	}

	public List<CanalColumn> getBeforeChangeOfColumns() {
		return this.beforeChangeOfColumns;
	}

	public void setBeforeChangeOfColumns(List<CanalColumn> beforeChangeOfColumns) {
		this.beforeChangeOfColumns = beforeChangeOfColumns;
	}

	public List<CanalColumn> getAfterChangeOfColumns() {
		return this.afterChangeOfColumns;
	}

	public void setAfterChangeOfColumns(List<CanalColumn> afterChangeOfColumns) {
		this.afterChangeOfColumns = afterChangeOfColumns;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("CanalRow{");
		sb.append("eventType=\'").append(this.eventType).append('\'');
		sb.append(", tableName=\'").append(this.tableName).append('\'');
		sb.append(", originTableName=\'").append(this.originTableName)
				.append('\'');
		sb.append(", database=\'").append(this.database).append('\'');
		sb.append(", executeTime=").append(this.executeTime);
		sb.append(", beforeChangeOfColumns=")
				.append(this.beforeChangeOfColumns);
		sb.append(", afterChangeOfColumns=").append(this.afterChangeOfColumns);
		sb.append('}');
		return sb.toString();
	}

	public String toSimpleString() {
		StringBuilder sb = new StringBuilder("CanalRow{");
		sb.append("eventType=\'").append(this.eventType).append('\'');
		sb.append(", tableName=\'").append(this.tableName).append('\'');
		sb.append(", originTableName=\'").append(this.originTableName)
				.append('\'');
		sb.append(", database=\'").append(this.database).append('\'');
		sb.append(", executeTime=").append(this.executeTime);
		sb.append(", beforeChangeOfColumns=").append(
				this.beforeChangeOfColumns != null ? this.beforeChangeOfColumns
						.size() : null);
		sb.append(", afterChangeOfColumns=").append(
				this.afterChangeOfColumns != null ? this.afterChangeOfColumns
						.size() : null);
		sb.append('}');
		return sb.toString();
	}

	public boolean changed() {
		boolean changed = false;
		Iterator<CanalColumn> it = this.getAfterChangeOfColumns().iterator();
		while (it.hasNext()) {
			CanalColumn column = (CanalColumn) it.next();
			if (column.isUpdate()) {
				changed = true;
				break;
			}
		}

		return changed;
	}
}
