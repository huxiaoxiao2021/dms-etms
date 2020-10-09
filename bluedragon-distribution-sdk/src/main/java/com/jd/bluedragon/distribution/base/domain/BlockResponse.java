package com.jd.bluedragon.distribution.base.domain;


import com.jd.bluedragon.distribution.api.JdResponse;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 运单号或包裹号拦截查询返回结果
 * @author jinjingcheng
 * @date 2019/4/23.
 */
public class BlockResponse extends JdResponse implements Serializable{
    private static final long serialVersionUID = 834262935551698553L;
    /**有拦截已解除*/
    public static final Integer UNBLOCK = 1;
    /**无拦截*/
    public static final Integer NO_NEED_BLOCK = 2;
    /**参数错误*/
    public static final Integer ERROR_PARAM = 3;
    /**有拦截 拦截中*/
    public static final Integer BLOCK = 4;



    /**拦截结果*/
    private Boolean result;
    /** 按运单拦截时 还未打印（或其他操作处理）的包裹号 最多返回200个 */
    private List<String> blockPackages;
    /** 按运单拦截时 还未打印（或其他操作处理）的包裹数量 */
    private Long blockPackageCount;

    public BlockResponse(){}

    public List<String> getBlockPackages() {
        return blockPackages;
    }

    public void setBlockPackages(List<String> blockPackages) {
        this.blockPackages = blockPackages;
    }

    public Long getBlockPackageCount() {
        return blockPackageCount;
    }

    public void setBlockPackageCount(Long blockPackageCount) {
        this.blockPackageCount = blockPackageCount;
    }

    public Boolean getResult() {
        if(getCode() == null){
            return null;
        }
        if(getCode().equals(BLOCK)){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
