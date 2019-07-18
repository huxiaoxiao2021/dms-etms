package com.jd.bluedragon.distribution.stash.service;

import com.jd.bluedragon.distribution.stash.domain.EMGGoodsInfoDto;

import java.util.List;

/**
 * <P>
 *     收纳暂存项目的业务处理类
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/6/10
 */
public interface PackageStashService {

    /**
     * 根据数量获取一定数量的EMGCode
     * @param codeNum 需要的条码数量
     * @return 返回条码列表
     */
    List<String> genEMGCodeByNum(Integer codeNum);

    /**
     * 根据EMGCode获取商品信息
     * <attention>
     *     EMG码的组成为: "EMG" + <code>goodsNo<code/>;
     *     获取信息接口的提供方ECLP未提供直接根据EMGCode的查询接口，要求我们将EMG码的前缀“EMG”去除之后在调用商品信息获取接口！？
     * <attention/>
     * @param emgCode EMGCode
     * @return 返回EMG商品信息
     */
    EMGGoodsInfoDto getGoodsInfoByEMGCode(String emgCode);
}
