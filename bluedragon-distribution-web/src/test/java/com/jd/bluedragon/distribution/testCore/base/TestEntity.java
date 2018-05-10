package com.jd.bluedragon.distribution.testCore.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private List<Date> list;
	private Map<Date,Date> map;
	private Date date;
	private TestEntity testEntity;
	public TestEntity(){
		
	}
	public TestEntity(int id,String name){
		this.id = id;
		this.name = name;
	}
	public String toString(){
		return "{\nid:"+id+",\nname:"+name+"\n}";
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the list
	 */
	public List<Date> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List<Date> list) {
		this.list = list;
	}
	/**
	 * @return the map
	 */
	public Map<Date,Date> getMap() {
		return map;
	}
	/**
	 * @param map the map to set
	 */
	public void setMap(Map<Date,Date> map) {
		this.map = map;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the testEntity
	 */
	public TestEntity getTestEntity() {
		return testEntity;
	}
	/**
	 * @param testEntity the testEntity to set
	 */
	public void setTestEntity(TestEntity testEntity) {
		this.testEntity = testEntity;
	}
}
