package com.jd.bluedragon.distribution.rest.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxPackResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 根据箱号获取箱内包裹数
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class BoxPackResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SortingService sortingService;

	@Autowired
	private BaseService baseService;

	/**
	 * 根据订单号或包裹号查询箱号包裹信息
	 * @param createSiteCode
	 * @param code
	 * @param type
	 * @return
	 */
	@GET
	@Path("/boxPackList/{createSiteCode}/{code}/{type}")
	public BoxPackResponse getBoxPackList(
			@PathParam("createSiteCode") Integer createSiteCode,
			@PathParam("code") String code, @PathParam("type") Integer type) {
		BoxPackResponse boxPackResponse = new BoxPackResponse();
		if (createSiteCode == null || StringUtils.isBlank(code) || type == null) {
			this.logger.error("根据订单号或包裹号查询箱号包裹信息 --> 传入操作站点或查询号（包裹/订单）或类型为空");
			boxPackResponse.setCode(JdResponse.CODE_PARAM_ERROR);
			boxPackResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
			return boxPackResponse;
		}
		try {
			Sorting sorting = new Sorting();
			sorting.setCreateSiteCode(createSiteCode);
			sorting.setType(type);
			if (WaybillUtil.isPackageCode(code)) {
				sorting.setPackageCode(code);
			} else {
				sorting.setWaybillCode(code);
			}
			this.logger.info("根据订单号或包裹号查询箱号包裹信息 --> 获取数据集合开始");
			List<Sorting> sortingList = this.sortingService.findBoxPackList(sorting);
			if (sortingList != null && !sortingList.isEmpty()) {
				List<Sorting> resultSortings = this.parseSortToResultList(sortingList);
				this.logger.info("根据订单号或包裹号查询箱号包裹信息 --> 获取数据集合大小【“" + sortingList.size() + "”】，转换后集合大小【“" + resultSortings.size() + "”】");
				boxPackResponse.setBoxPackList(resultSortings);
			} else {
				this.logger.error("根据订单号或包裹号查询箱号包裹信息 --> 获取数据集合数据为空");
				boxPackResponse.setBoxPackList(Collections.emptyList());
			}
			boxPackResponse.setCode(JdResponse.CODE_OK);
			boxPackResponse.setMessage(JdResponse.MESSAGE_OK);
		} catch (Exception e) {
			this.logger.error("根据订单号或包裹号查询箱号包裹信息 --> 获取信息异常", e);
			boxPackResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
			boxPackResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return boxPackResponse;
	}

	/**
	 * 根据箱号，获取箱内包裹数
	 *
	 * @param boxCode
	 * @return
	 */
	@GET
	@Path("/boxPack/{createSiteCode}/{boxCode}")
	public BoxPackResponse getBoxPack(
			@PathParam("createSiteCode") Integer createSiteCode,
			@PathParam("boxCode") String boxCode) {
		BoxPackResponse boxPackResponse = new BoxPackResponse();
		if (createSiteCode == null || StringUtils.isBlank(boxCode)) {
			this.logger.error("根据箱号，获取箱内包裹数 --> 传入操作站点或箱号为空");
			boxPackResponse.setCode(JdResponse.CODE_PARAM_ERROR);
			boxPackResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
			return boxPackResponse;
		}
		try {
			this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹总数开始");
			int totalPack = sortingService.findBoxPack(createSiteCode, boxCode);
			this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹总数【“" + totalPack + "”】结束");
			boxPackResponse.setTotalPack(totalPack);
			if (totalPack > 0) {
				this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹分拣目的站点信息开始");
				Sorting sorting = this.sortingService.findBoxDescSite(
						createSiteCode, boxCode);
				if (sorting != null) {
					this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹分拣目的站点ID结束");
					Integer receiveSiteCode = sorting.getReceiveSiteCode();
					boxPackResponse.setReceiveSiteCode(receiveSiteCode);
					try {
						this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹分拣目的站点名称开始，站点号【" + receiveSiteCode + "】");
						BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService
								.queryDmsBaseSiteByCode(String.valueOf(receiveSiteCode));
						this.logger.info("根据箱号，获取箱内包裹数 --> 获取包裹分拣目的站点名称，站点号【" + receiveSiteCode + "】,结束");
						if (baseStaffSiteOrgDto != null) {
							boxPackResponse.setReceiveSiteName(baseStaffSiteOrgDto
									.getSiteName());
						} else {
							this.logger.error("根据箱号，获取箱内包裹数 --> 获取包裹分拣目的站点名称为空，站点号【" + receiveSiteCode + "】");
						}
					} catch(Exception e) {
						this.logger.error("根据箱号，获取箱内包裹数 --> 根据目的站点Code【" + receiveSiteCode + "】 获取站点名称接口异常", e);
					}
				}
				boxPackResponse.setCode(JdResponse.CODE_OK);
				boxPackResponse.setMessage(JdResponse.MESSAGE_OK);
			} else {
				boxPackResponse.setCode(BoxPackResponse.CODE_OK_NODATA);
				boxPackResponse.setMessage(BoxPackResponse.MESSAGE_OK_NODATA);
			}
		} catch (Exception e) {
			this.logger.error("根据箱号，获取箱内包裹数 --> 获取信息异常", e);
			boxPackResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
			boxPackResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return boxPackResponse;
	}

	/**
	 * 转换sortings数据集合
	 * @param sortings
	 * @return
	 */
	/**
	 * @param sortings
	 * @return
	 */
	private List<Sorting> parseSortToResultList(List<Sorting> sortings) {
		if (sortings == null || sortings.size() <= 0) {
			this.logger.error("转换sortings数据集合 参数为空");
			return Collections.emptyList();
		}
		this.logger.debug("转换sortings数据集合开始");
		List<Sorting> resultSortings = new ArrayList<Sorting>();

		Map<String, List<Sorting>> sortingMap = new HashMap<String, List<Sorting>>();
		for (Sorting sorting : sortings) {
			String packageCode = sorting.getPackageCode();
			if (sortingMap.containsKey(packageCode)) {
				sortingMap.get(packageCode).add(sorting);
			} else {
				List<Sorting> aSortings = new ArrayList<Sorting>();
				aSortings.add(sorting);
				sortingMap.put(packageCode, aSortings);
			}
		}

		for (Map.Entry<String, List<Sorting>> mSortingEntry : sortingMap.entrySet()) {
			List<Sorting> aSortings = mSortingEntry.getValue();
			if (aSortings.size() > 1) {
				Collections.sort(aSortings, new Comparator<Sorting>() {
					public int compare(Sorting o1, Sorting o2) {
						Date operateTime1 = o1.getOperateTime();
						Date operateTime2 = o2.getOperateTime();
						if (operateTime1 != null && operateTime2 != null) {
							return (int)(operateTime2.getTime() - operateTime1.getTime());
						}
						return 0;
					}
				});
			}
			resultSortings.add(aSortings.get(0));
		}

		this.logger.debug("转换sortings数据集合结束");

		for (Sorting sorting : resultSortings) {
			sorting.setCreateTime(null);
		}

		return resultSortings;
	}
}
