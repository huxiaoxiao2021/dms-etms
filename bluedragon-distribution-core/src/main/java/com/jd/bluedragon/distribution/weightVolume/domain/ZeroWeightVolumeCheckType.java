package com.jd.bluedragon.distribution.weightVolume.domain;

/**
 *
 */
public enum ZeroWeightVolumeCheckType{
        //不校验
        NOT_CHECK,
        //校验复重
        CHECK_AGAIN_WEIGHT,
        //校验分拣中心复重
        CHECK_DMS_AGAIN_WEIGHT,
        //校验复重和复量方
        CHECK_AGAIN_WEIGHT_VOLUME,
        //校验复重或者复量方或者商品重量或者商品量方 
        CHECK_GOOD_OR_AGAIN_WEIGHT_OR_VOLUME;
}