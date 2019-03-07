package com.jd.bluedragon.distribution.base.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.core.redis.TaskMode;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.etms.vts.dto.CarrierInfo;
import com.jd.etms.vts.dto.CarrierParamDto;
import com.jd.etms.vts.dto.DictDto;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public interface BaseService {

    /**
     * 用户登录包括自营和3PL
     * <pre>
     *    如果是自营
     *    1. 先调人事接口验证用户名和密码
     *    2. 验证通过后，根据员工id调基础资料接口获取员工信息
     *    如果是三方的
     *    1. 先调用户组接口验证京东账号和密码
     *    2. 验证通过后，根据京东账号调基础资料接口获取员工信息
     * </pre>
     *
     * @param userid
     * @param password
     * @return
     */
    com.jd.bluedragon.distribution.base.domain.BasePdaUserDto pdaUserLogin(String userid, String password,ClientInfo clientInfo);

    /**
	 * 账号密码是否存在
	 *
	 * @param String
	 *            erpcode erp账号
	 * @param String
	 *            password erp密码
	 * @return StaffDto 是否登录成功
	 */
	PdaStaff login(String erpcode, String password, ClientInfo clientInfo);

	/**
	 * 返回错误信息列表，便于PDA选择后，进行退货相关业务 根据日期进行增量查询
	 *
	 * @return BaseDataDict[]
	 */

	/**
	 * 根据司机编号查询司机
	 *
	 * @param drivercode
	 *            司机编号
	 * @return BaseStaffSiteOrgDto 司机信息列表
	 */
	BaseStaffSiteOrgDto queryDriverByDriverCode(Integer drivercode);

	/**
	 * 获得所有的机构数据
	 *
	 * @return List<BaseOrg> @
	 */
	List<BaseOrg> getAllOrg();

	/**
	 * 返回错误信息列表，便于PDA选择后，进行退货相关业务
	 *
	 * @return BaseDataDict[]
	 */
	BaseDataDict[] getBaseDataDictListByDate(Integer typeGroup);

    /**
     * 非递归获取字典树
     * @param typeGroup 树类别标识
     * @return
     */
    List<BaseDataDict> getBaseDictionaryTree(int typeGroup);

	/**
	 * 返回错误信息列表，便于PDA选择后，进行退货相关业务
	 *
	 * @return BaseDataDict[]
	 */
	BaseDataDict[] getBaseDataDictListByDate(List<Integer> typeGroups);
	
	/**
	 * 通过VTS已有的数据字典查询接口中获取对应线路类型、运输方式、承运商类型3个数据字典项的值
	 * 替换之前的通过接口查询青龙基础资料中的数据字典获取线路类型、运输方式、运力类型
	 * @return
	 */
	DictDto[] getDictListByGroupType(List<Integer> typeGroups);
	
	/**
	 * 承运商列表
	 * @param carrierParamDto
	 * @return
	 */
	List<CarrierInfo> getCarrierInfoList(CarrierParamDto carrierParamDto);

	/**
	 * 根据站点ID查询站点名称，如果没有匹配数据则返回空值(null)
	 *
	 * @param Integer
	 *            siteid
	 * @return BaseStaffSiteOrgDto
	 */
	BaseStaffSiteOrgDto getSiteBySiteID(Integer siteid);

	/**
	 * 根据机构ID查询机构下站点
	 *
	 * @param orgid
	 *            机构ID
	 * @return List 站点信息列表
	 */
	BaseStaffSiteOrgDto[] querySiteByOrgID(Integer orgid);

	/**
	 * 根据机构ID查询机构下的车辆信息
	 *
	 * @param orgid
	 *            机构ID
	 * @return List 车辆信息列表
	 */

	/**
	 * 根据机构ID查询机构下的司机
	 *
	 * @param orgid
	 *            机构ID
	 * @return List 司机信息列表
	 */
	BaseStaffSiteOrgDto[] queryDriverByOrgId(Integer orgid);

	/**
	 * 通过员工id查询分拣中心信息
	 *
	 * @param baseStaffId
	 * @return
	 */
	Map<Integer, String> getSiteInfoByBaseStaffId(Integer baseStaffId);

	/**
	 * 根据订单号查询订单信息
	 *
	 * @param orderCode
	 * @return ReverseSendWms 返回可能为空
	 */
	ReverseSendWms getWaybillByOrderCode(String orderCode);


	/**
	 * 根据pop商家编号获取名称
	 *
	 * @param popCode
	 */
	String getPopBusinessNameByCode(String popCode);

	/**
	 * 根据基础设置的KEY查询基础设置
	 *
	 * @param key
	 * @return
	 */
	List<SysConfig> queryConfigByKey(Map<String, Object> params);

	List<SysConfig> queryConfigByKey(String key);

    /**
     * 根据基础设置的KEY查询基础设置 带CACHE
     * @param key
     * @return
     */
    List<SysConfig> queryConfigByKeyWithCache(String key);

	/**
	 * 根据SysConfig的configName进行更新
	 *
	 * @param key
	 * @return
	 */
	void updateSysConfig(SysConfig sysconfig);

	/**
	 * 新增Sysconfig
	 */
	void insertSysConfig(SysConfig sysconfig);

	/**
	 * 根据基础设置的id查询基础设置
	 *
	 * @param key
	 * @return
	 */
	SysConfig getSysConfig(Long sysconfigId);

	/**
	 * 根据站点类型，查询站点集合
	 *
	 * @param orderCode
	 * @return ReverseSendWms
	 */

	public BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer staffId);

    public BaseStaffSiteOrgDto getCachedStaffByStaffId(Integer staffId);

	public List<BaseStaffSiteOrgDto> getPopBaseSiteByOrgId(Integer paramInteger);

	public List<BaseDataDict> getBaseDataDictList(Integer parentId, Integer nodeLevel,
	        Integer typeGroup);


	/**
	 * 根据参数查询 基础参数设置的总数据量
	 * @param map
	 * @return INTEGET
	 */
	public Integer totalSysconfigSizeByParams(String key);

	/**
	 * 获得整体worker运行模式 DB or Redis
	 * @return
	 */
	public TaskMode getTaskMode();
	String  getSiteNameBySiteID(Integer siteid);

	/**
	 * 根据自提柜code查询自提柜所属站点code
	 * @param 自提柜code
	 * @return
	 */
	public Integer getSiteSelfDBySiteCode(Integer sitecode);

	/**
	 * 根据分拣中心id与目的地id获取任务区和电子标签信息
	 * @param
	 * @return
	 */
	public ElectronSite getBaseGoodsPositionDmsCodeSiteCode(Integer createCode,Integer receiveCode);

	/**
	 * 根据assId获取单条省市县信息
	 * 注意：Assort对象中的中文名称取值assDis属性，非assName属性，使用者请注意
	 *
	 * @param assortId
	 * @return
	 */
    public Assort getAssortById(Integer assortId);

	BaseStaffSiteOrgDto queryDmsBaseSiteByCode(String siteCode);

	/**
	 * 根据assId获取单条省市县信息
	 * 注意：Assort对象中的中文名称取值assDis属性，非assName属性，使用者请注意
	 *
	 * @param assId
	 * @return
	 */
	public Assort getOneAssortById(int assId);

	/**
	 * 根据分拣中心编码获取分拣中心名称并截取掉 ”分拣中心","中转场","分拨中心"等
	 * @param dmsCode
	 * @return
     */
	public String getDmsShortNameByCode(Integer dmsCode);
}
