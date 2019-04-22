package com.jd.bluedragon.utils;


import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.util.*;

/**
 * 
 * @ClassName: AreaHelper
 * @Description: (区域、机构、省、市、工具类)
 * @author wuyoude
 * @date 2017年6月7日 下午2:30:22
 *
 */
public class AreaHelper {
	
	private final static Map<Integer, AreaNode> AREA_DATAS = new TreeMap<Integer, AreaNode>();
	private final static Map<Integer, ProvinceNode> PROVINCE_DATAS = new TreeMap<Integer, ProvinceNode>();
	private final static Map<Integer, Set<Integer>> AREA_PROVINCE_MAPPING = new TreeMap<Integer, Set<Integer>>();
	/** 直辖市省id 1:北京市 2:上海市  3：天津市  4：重庆市 **/
	private static final List<Integer> municipalityList = Arrays.asList(1,2,3,4);

	static{
		init();
	}
	private static void init() {
		//注册7大区域
		registerArea(new AreaNode(3,"华东"));
		registerArea(new AreaNode(4,"西南"));
		registerArea(new AreaNode(6,"华北"));
		registerArea(new AreaNode(10,"华南"));
		registerArea(new AreaNode(600,"华中"));
		registerArea(new AreaNode(611,"东北"));
		registerArea(new AreaNode(645,"西北"));
		
		//3-华东
		registerProvince(new ProvinceNode(2,"上海",getArea(3)));
		registerProvince(new ProvinceNode(12,"江苏",getArea(3)));
		registerProvince(new ProvinceNode(14,"安徽",getArea(3)));
		registerProvince(new ProvinceNode(15,"浙江",getArea(3)));
		//4-西南
		registerProvince(new ProvinceNode(4,"重庆",getArea(4)));
		registerProvince(new ProvinceNode(22,"四川",getArea(4)));
		registerProvince(new ProvinceNode(24,"贵州",getArea(4)));
		registerProvince(new ProvinceNode(25,"云南",getArea(4)));
		registerProvince(new ProvinceNode(26,"西藏",getArea(4)));
		//6-华北
		registerProvince(new ProvinceNode(1,"北京",getArea(6)));
		registerProvince(new ProvinceNode(3,"天津",getArea(6)));
		registerProvince(new ProvinceNode(5,"河北",getArea(6)));
		registerProvince(new ProvinceNode(6,"山西",getArea(6)));
		registerProvince(new ProvinceNode(11,"内蒙古",getArea(6)));
		registerProvince(new ProvinceNode(13,"山东",getArea(6)));
		//10-华南
		registerProvince(new ProvinceNode(16,"福建",getArea(10)));
		registerProvince(new ProvinceNode(19,"广东",getArea(10)));
		registerProvince(new ProvinceNode(20,"广西",getArea(10)));
		registerProvince(new ProvinceNode(23,"海南",getArea(10)));
		registerProvince(new ProvinceNode(32,"台湾",getArea(10)));
		registerProvince(new ProvinceNode(42,"香港",getArea(10)));
		registerProvince(new ProvinceNode(43,"澳门",getArea(10)));
		registerProvince(new ProvinceNode(52993,"港澳",getArea(10)));
		//600-华中
		registerProvince(new ProvinceNode(7,"河南",getArea(600)));
		registerProvince(new ProvinceNode(17,"湖北",getArea(600)));
		registerProvince(new ProvinceNode(18,"湖南",getArea(600)));
		registerProvince(new ProvinceNode(21,"江西",getArea(600)));
		//611-东北
		registerProvince(new ProvinceNode(8,"辽宁",getArea(611)));
		registerProvince(new ProvinceNode(9,"吉林",getArea(611)));
		registerProvince(new ProvinceNode(10,"黑龙江",getArea(611)));
		//645-西北
		registerProvince(new ProvinceNode(27,"陕西",getArea(645)));
		registerProvince(new ProvinceNode(28,"甘肃",getArea(645)));
		registerProvince(new ProvinceNode(29,"青海",getArea(645)));
		registerProvince(new ProvinceNode(30,"宁夏",getArea(645)));
		registerProvince(new ProvinceNode(31,"新疆",getArea(645)));
	}
	/**
	 * 注册一个区域信息
	 * @param area
	 */
	public static void registerArea(AreaNode area){
		AREA_DATAS.put(area.getId(), area);
		AREA_PROVINCE_MAPPING.put(area.getId(), new TreeSet<Integer>());
	}
	/**
	 * 注册一个省份信息，并建立区域和省对应关系
	 * @param province
	 */
	public static void registerProvince(ProvinceNode province){
		PROVINCE_DATAS.put(province.getId(), province);
		if(province.getArea()!=null){
			Integer areaId = province.getArea().getId();
			if(areaId!=null){
				if(!AREA_DATAS.containsKey(areaId)){
					registerArea(province.getArea());
				}
				AREA_PROVINCE_MAPPING.get(areaId).add(province.getId());
			}
		}
	}
	public static AreaNode getArea(Integer areaId){
		return areaId==null?null:AREA_DATAS.get(areaId);
	}
	/**
	 * 获取所有的区域id
	 * @return
	 */
	public static Set<Integer> getAllAreaIds(){
		return AREA_DATAS.keySet();
	}
	/**
	 * 通过省id,获取对应的区域信息
	 * @param provinceId
	 * @return
	 */
	public static AreaNode getAreaByProvinceId(Integer provinceId){
		if(provinceId!=null && PROVINCE_DATAS.containsKey(provinceId)){
			return PROVINCE_DATAS.get(provinceId).getArea();
		}
		return null;
	}
	/**
	 * 获取省份信息
	 * @param provinceId
	 * @return
	 */
	public static ProvinceNode getProvince(Integer provinceId){
		return provinceId==null?null:PROVINCE_DATAS.get(provinceId);
	}
	/**
	 * 获取所有的省份ids
	 * @return
	 */
	public static Set<Integer> getAllProvinceIds(){
		return PROVINCE_DATAS.keySet();
	}

	public static List<ProvinceNode> getAllProvince (){
		List<ProvinceNode> provinceList = new ArrayList<ProvinceNode>();

		Iterator<Integer> it = PROVINCE_DATAS.keySet().iterator();
		while (it.hasNext()) {
			provinceList.add(PROVINCE_DATAS.get(it.next()));
		}

		return provinceList;
	}
	public static List<AreaNode> getAllArea(){
		List<AreaNode> areaList = new ArrayList<AreaNode>();

		Iterator<Integer> it = AREA_DATAS.keySet().iterator();
		while (it.hasNext()) {
			areaList.add(AREA_DATAS.get(it.next()));
		}

		return areaList;
	}
	/**
	 * 获取区域所有的省份id
	 * @param areaId
	 * @return
	 */
    public static Set<Integer> getProvinceIdsByAreaId(Integer areaId){
        return areaId==null?null:AREA_PROVINCE_MAPPING.get(areaId);
    }
	/**
	 * 获取区域所有的省份信息
	 * @param areaId
	 * @return
	 */
    public static List<ProvinceNode> getProvincesByAreaId(Integer areaId){
    	List<ProvinceNode> res = new ArrayList<ProvinceNode>();
    	if(areaId!=null&&AREA_PROVINCE_MAPPING.containsKey(areaId)){
    		for(Integer pid:AREA_PROVINCE_MAPPING.get(areaId)){
    			res.add(getProvince(pid));
    		}
    	}
    	return res;
    }

    /**
	 * 	根据省得名字获得省Id
	 * 	@param proName
	 * @return
	 * */
    public static Integer getProIdByProName(String proName){
    	Integer proId = -1;
    	//获得所有的区域id
		Set<Integer> allAreaIds = getAllAreaIds();
		for(Integer i : allAreaIds){
			//根据区域id获得省份信息
			List<ProvinceNode> provincesByAreaId = getProvincesByAreaId(i);
			for (ProvinceNode p : provincesByAreaId){
				if(p.getName().equals(proName)){
					proId = p.getId();
				}
			}
		}
		return proId;
	}

	/**
	 * 定义一个标题型的站点
	 * @return
	 */
	public static BaseStaffSiteOrgDto getDmsSiteTitle(){
		BaseStaffSiteOrgDto all = new BaseStaffSiteOrgDto();
		all.setDmsSiteCode("-1");
		all.setSiteName("全部");
		return all;
	}
	/**
	 * 定义一个标题型的省
	 * @return
	 */
	public static ProvinceNode getProvinceNodeTitle(){
		return new ProvinceNode(-1, "全部");
	}
	public static AreaNode getAreaNodeTitle(){
		return new AreaNode(-1, "全部");
	}
	public static ProvinceAndCity getCityNodeTitle (){
		return new ProvinceAndCity("-1", "全部");
	}
	/**
	 * 判断是不是空或者选的全部
	 * @param id
	 * @return
	 */
	public static boolean isNotEmptyAndTitle(Integer id){
		return (id != null && id != -1);
	}

	/**
	 * 判断是否是直辖市 1:北京市  2：上海市  3：天津市 4：重庆市
	 * @param provinceId
	 * @return
	 */
	public boolean isMunicipality(Integer provinceId){
		return municipalityList.contains(provinceId);
	}
}
