package com.jd.bluedragon.distribution.seal.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;

public interface SealVehicleService {

//    /**
//     * @deprecated 此方法已经过时，新增封车信息统一使用 addSealVehicle
//     * @param seal
//     * @return
//     */
//    Integer add(SealVehicle seal);

//    Integer update(SealVehicle seal);

//    void doSealVehicle(Task task);
    
//    /**
//     * @deprecated 此方法已经过时，封车解封车信息使用统一接口
//     * @param seal
//     */
//    void saveOrUpdate(SealVehicle seal);
    
    /**
     * 根据封车号、有效性查询封车信息，统一接口
     * @param sealCode
     * @return
     */
    public SealVehicle findBySealCode(String sealCode);

    /**
     * 增加封车信息，统一接口，所有封车信息都使用这个接口进行增加
     * 
     * @param sealVehicle
     * @return
     */
    public int addSealVehicle(SealVehicle sealVehicle);
    
    /**
     * 增加封车信息，统一接口，所有封车信息都使用这个接口进行增加
     * 
     * @param sealVehicle (添加了新字段)
     * @return
     */
    public int addSealVehicle2(SealVehicle sealVehicle);
    
    /**
     * 增加解封车信息，统一接口，所有解封车信息都是用这个接口进行操作
     * 
     * @param sealVehicle
     * @return
     */
    public int updateSealVehicle(SealVehicle sealVehicle);

    /**
     * 增加解封车信息，统一接口，所有解封车信息都是用这个接口进行操作
     * 注意:添加了批次号校验
     * 
     * @param sealVehicle 
     * @return
     */
    public int updateSealVehicle2(SealVehicle sealVehicle);
    
    /**
     * 根据批次号、有效性查询封车信息，统一接口
     * @param sendCode
     * @return
     */
	public List<SealVehicle> findBySendCode(String sendCode);

	/**
	 * 
	 * @param sealVehicleList
	 * @return
	 */
	public boolean addSealVehicle3(List<SealVehicle> sealVehicleList);

	public boolean updateSealVehicle3(SealVehicle sealVehicle, String sealCodes);
	
	public List<SealVehicle> findByVehicleCode(String vehicleCode);

	/**
	 * 
	 * @param SealVehicle通过封签号、车牌号、批次号取消封车操作
	 * @return
	 */
	public SealVehicleResponse cancelSealVehicle(SealVehicle sealVehicle);

}
