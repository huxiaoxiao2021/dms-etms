package com.jd.bluedragon.distribution.station.dao;

import java.util.List;

import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.domain.WorkStationGridCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;

/**
 * 工序岗位网格表--Dao接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface WorkStationGridDao {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(WorkStationGrid insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	int updateById(WorkStationGrid updateData);
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	int deleteById(WorkStationGrid deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	WorkStationGrid queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryList(WorkStationGridQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	long queryCount(WorkStationGridQuery query);
	/**
	 * 查询站点下所有网格
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryAllGridBySiteCode(WorkStationGridQuery query);
	/**
	 * 
	 * @param deleteData
	 */
	int deleteByBusinessKey(WorkStationGrid deleteData);
	/**
	 * 根据业务主键查询
	 * @param data
	 * @return
	 */
	WorkStationGrid queryByBusinessKey(WorkStationGrid data);
	/**
	 * 查询场地网格作业区列表
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryGridAreaDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格工序列表
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryGridWorkDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格序号列表
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryGridNoDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格楼层列表
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryGridFloorDictList(WorkStationGridQuery query);
	/**
	 * 按条件查询统计信息
	 * @param query
	 * @return
	 */
	WorkStationGridCountVo queryPageCount(WorkStationGridQuery query);
	/**
	 * 根据refStationKey 查询数据记录条数
	 * @param stationKey
	 * @return
	 */
	long queryCountByRefStationKey(String stationKey);
	/**
	 * 查询有网格的站点列表
	 * @param query
	 */
	List<WorkStationGrid> querySiteListByOrgCode(WorkStationGridQuery query);
	/**
	 * 查询erp列表
	 * @param query
	 */
	List<String> queryOwnerUserErpListBySiteCode(WorkStationGridQuery query);
	/**
	 * 导出查询列表
	 * @param query
	 * @return
	 */
	List<WorkStationGrid> queryListForExport(WorkStationGridQuery query);
	/**
	 * 根据id列表查询
	 * @param deleteRequest
	 * @return
	 */
	List<WorkStationGrid> queryByIds(DeleteRequest<WorkStationGrid> deleteRequest);
	/**
	 * 根据id批量删除
	 * @param deleteRequest
	 * @return
	 */
	int deleteByIds(DeleteRequest<WorkStationGrid> deleteRequest);
}
