package com.jd.bluedragon.distribution.cross.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingDto;
import org.apache.poi.ss.usermodel.Sheet;

public interface CrossSortingService {

	Integer findCountCrossSorting(Map<String, Object> params);

	List<CrossSorting> findPageCrossSorting(Map<String, Object> params);

	int deleteCrossSorting(Map<String, Object> params);

	int addBatchCrossSorting(List<CrossSorting> csList);

	List<CrossSorting> findMixDms(Map<String, Object> params);

    List<CrossSortingDto>  getQueryByids(Integer createDmsCode ,Integer destinationDmsCode,Integer mixDmsCode ,Integer dmsType);

	void importCrossSortingRules(Sheet sheet, String userName, String userCode)  throws Exception;

	int updateCrossSorting(CrossSorting cs);

	int updateCrossSortingForDelete(CrossSorting cs);

}
