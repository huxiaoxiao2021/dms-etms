package com.jd.bluedragon.distribution.station.api;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.domain.WorkStationGridCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 工序岗位网格表--JsfService接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface WorkStationGridJsfService {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	Result<Boolean> insert(WorkStationGrid insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	Result<Boolean> updateById(WorkStationGrid updateData);
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	Result<Boolean> deleteById(WorkStationGrid deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	Result<WorkStationGrid> queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	Result<PageDto<WorkStationGrid>> queryPageList(WorkStationGridQuery query);
	/**
	 * 按条件查询统计信息
	 * @param query
	 * @return
	 */	
	Result<WorkStationGridCountVo> queryPageCount(WorkStationGridQuery query);	
	/**
	 * 查询站点下所有网格
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryAllGridBySiteCode(WorkStationGridQuery query);
	/**
	 * 导入数据
	 * @param dataList
	 * @return
	 */
	Result<Boolean> importDatas(List<WorkStationGrid> dataList);
	/**
	 * 查询场地网格作业区列表
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryGridAreaDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格工序列表
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryGridWorkDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格序号列表
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryGridNoDictList(WorkStationGridQuery query);
	/**
	 * 查询场地网格楼层列表
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryGridFloorDictList(WorkStationGridQuery query);
	/**
	 * 批量删除
	 * @param deleteRequest
	 * @return
	 */
	Result<Boolean> deleteByIds(DeleteRequest<WorkStationGrid> deleteRequest);
	/**
	 * 查询数量
	 * @param query
	 * @return
	 */
	Result<Long> queryCount(WorkStationGridQuery query);
	/**
	 * 查询列表导出
	 * @param query
	 * @return
	 */
	Result<List<WorkStationGrid>> queryListForExport(WorkStationGridQuery query);
	List<String> queryOwnerUserErpListBySiteCode(WorkStationGridQuery query);
	List<WorkStationGrid> querySiteListByOrgCode(WorkStationGridQuery query);	
}
