package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.impl;

import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler.DiscardedStorageSortingTempStorageHandler;
import com.jd.dms.workbench.utils.sdk.base.Result;
import org.springframework.stereotype.Service;

/**
 * @author pengchong28
 * @description 弃件暂存处理类，扫描箱号专用
 * @date 2024/4/8
 */
@Service("discardedStorageWithBoxCodeHandler")
public class DiscardedStorageWithBoxCodeHandler extends DiscardedStorageSortingTempStorageHandler {
    /**
     * 处理带有箱码的废弃存储上下文    。
     * 这个方法将调用父类的处理方法来执行具体的业务逻辑    。
     * @param context 废弃存储上下文，包含了处理所需的相关信息
     * @return Result<Boolean> 返回一个包含布尔类型结果的Result对象，用于指示处理是否成功
     */
    public Result<Boolean> doHandleWithBoxCode(DiscardedStorageContext context){
        return super.doHandle(context);
    }

    /**
     * 处理废弃存储上下文之后的方法    。
     * 此方法通常在处理完废弃存储上下文后调用，以执行任何必要的后续操作    。
     *
     * @param context 废弃存储上下文对象，包含了处理废弃存储所需的信息    。
     */
    public void handleAfterWithBoxCode(DiscardedStorageContext context){
        super.handleAfter(context);
    }
}
