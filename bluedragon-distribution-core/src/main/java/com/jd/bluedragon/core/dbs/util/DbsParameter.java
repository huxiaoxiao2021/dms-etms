package com.jd.bluedragon.core.dbs.util;

import java.io.Serializable;

public class DbsParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String CurTable = "curTable";
	public static String AllTables = "allTables";
	public static String JoinTable1 = "joinTable1";
	public static String JoinTable2 = "joinTable2";
	public static String JoinTable3 = "joinTable3";
	public static String JoinTable4 = "joinTable4";
	private String curTable;
	private String allTables;
	private Object parameter;

	public String getCurTable() {
		return curTable;
	}

	public void setCurTable(String curTable) {
		this.curTable = curTable;
	}

	public String getAllTables() {
		return allTables;
	}

	public void setAllTables(String allTables) {
		this.allTables = allTables;
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

}
