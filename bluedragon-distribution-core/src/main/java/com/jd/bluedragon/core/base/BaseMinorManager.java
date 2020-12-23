package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ldop.basic.dto.BasicTraderReturnDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.AirTransport;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseSiteGoods;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.ReverseCrossPackageTag;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;

import java.util.List;


public interface BaseMinorManager {

    public abstract BasicTraderInfoDTO getBaseTraderById(Integer paramInteger);

    public abstract AirTransport getAirConfig(Integer originalProvinceId, Integer originalCityId, Integer destinationProvinceId, Integer destinationCityId);

    public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID, String flage, Integer siteCode);

    public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(Integer dmsID, String flage, Integer taskAreaNo);

    public List<BasicTraderInfoDTO> getBaseAllTrader();

    public List<BasicTraderInfoDTO> getBaseTraderByName(String name);

    /**
     * 获取签约商家ID列表
     *
     * @return 签约商家ID列表
     */
    public List<String> getSignCustomer();

    /**
     * 根据站点code查找三方承运超限指标
     *
     * @param siteCode 站点编码
     * @return
     */
    BaseSiteGoods getGoodsVolumeLimitBySiteCode(Integer siteCode);


    public BasicTraderInfoDTO getTraderInfoByPopCode(String popCode);

    /**
     * 逆向-根据始发分拣中心和目的站点获取目的分拣中心、道口号、笼车号信息
     *
     * @param originalDmsId
     * @param targetSiteId
     * @return
     */
    JdResult<ReverseCrossPackageTag> getReverseCrossPackageTag(Integer originalDmsId, Integer targetSiteId);

    /**
     * 根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息,支持 1 -- 普通 2 -- 航空 3 -- 填仓
     *
     * @param baseDmsStore      库房
     * @param targetSiteId      目的站点ID -- 必填
     * @param originalDmsId     始发分拣中心ID
     * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
     * @return
     */
    JdResult<CrossPackageTagNew> queryCrossPackageTag(BaseDmsStore baseDmsStore, Integer targetSiteId, Integer originalDmsId, Integer originalCrossType);

    /**
     * 打印业务-根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
     * 先调用正向业务，获取不到数据会调用逆向接口
     *
     * @param baseDmsStore      库房
     * @param targetSiteId      目的站点ID -- 必填
     * @param originalDmsId     始发分拣中心ID
     * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
     * @return
     */
    JdResult<CrossPackageTagNew> queryCrossPackageTagForPrint(BaseDmsStore baseDmsStore, Integer targetSiteId, Integer originalDmsId, Integer originalCrossType);

    /**
     * 根据商家id获取返单信息
     *
     * @param busiId 商家id
     * @return
     */
    ResponseDTO<List<BasicTraderReturnDTO>> getBaseTraderReturnListByTraderId(Integer busiId);

    /**
     * 打印业务-目的站点ID、始发分拣中心ID，目的站点类型不能为分拣中心(64)和财务专用(98)，目的获取取包裹标签打印信息
     * 先调用正向业务，获取不到数据会调用逆向接口
     *
     * @param targetSiteId
     * @param originalDmsId
     * @return
     */
    CrossPackageTagNew queryNonDmsSiteCrossPackageTagForPrint(Integer targetSiteId, Integer originalDmsId);

    /**
     * 通过青龙业主号获取-业主基础信息(商家类型等)
     * @param traderCode
     * @return
     */
    BasicTraderNeccesaryInfoDTO getBaseTraderNeccesaryInfo(String traderCode);

}
