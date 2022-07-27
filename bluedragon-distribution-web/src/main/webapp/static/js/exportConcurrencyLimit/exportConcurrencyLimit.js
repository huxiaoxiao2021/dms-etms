const exportReportEnum = {
    ABNORMAL_UNKNOWN_WAYBILL_REPORT:"ABNORMAL_UNKNOWN_WAYBILL_REPORT",
    DMS_BAR_CODE_REPORT:"DMS_BAR_CODE_REPORT",
    POP_ABNORMAL_REPORT:"POP_ABNORMAL_REPORT",
    POP_RECEIVE_ABNORMAL_REPORT:"POP_RECEIVE_ABNORMAL_REPORT",
    BUSINESS_RETURN_ADDRESS_REPORT:"BUSINESS_RETURN_ADDRESS_REPORT",
    CROSS_BOX_REPORT:"CROSS_BOX_REPORT",
    GOODS_PRINT_REPORT:"GOODS_PRINT_REPORT",
    REVERSE_PART_DETAIL_REPORT:"REVERSE_PART_DETAIL_REPORT",
    MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT:"MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT",
    INVENTORY_TASK_REPORT: "INVENTORY_TASK_REPORT",
    INVENTORY_EXCEPTION_REPORT: "INVENTORY_EXCEPTION_REPORT",
    RMA_HAND_OVER_REPORT: "RMA_HAND_OVER_REPORT",
    WAYBILL_CONSUMABLE_RECORD_REPORT:"WAYBILL_CONSUMABLE_RECORD_REPORT",
    STORAGE_PACKAGE_M_REPORT: "STORAGE_PACKAGE_M_REPORT",
    COLLECT_GOODS_DETAIL_REPORT: "COLLECT_GOODS_DETAIL_REPORT",
    DMS_SCHEDULE_INFO_REPORT: "DMS_SCHEDULE_INFO_REPORT",
    REVIEW_WEIGHT_SPOT_CHECK_REPORT: "ReviewWeightSpotCheck",
    WEIGHT_AND_VOLUME_CHECK_REPORT:"WEIGHT_AND_VOLUME_CHECK_REPORT",
    SIGN_RETURN_REPORT:"SIGN_RETURN_REPORT",
    ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION:"ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION"
};

function checkConcurrencyLimit({
                                   currentKey,
                                   checkPassCallback,
                                   checkFailCallback,
                                   errorCallback
                               }) {
    function isFunction(fn) {
        return Object.prototype.toString.call(fn) === '[object Function]';
    }

    $.ajax({
        type: 'post',
        url: "/exportConcurrencyLimit/checkConcurrencyLimit",
        data: {"currentKey": currentKey},
        async: false,
        success: function (result) {
            if (result.code === 200) {
                // 检查通过，执行回调，括号中的result为回调函数的可用参数
                isFunction(checkPassCallback) && checkPassCallback(result)
            } else {
                // 检查不通过，执行回调
                isFunction(checkFailCallback) && checkFailCallback(result)
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // 调用失败，执行回调
            isFunction(errorCallback) && errorCallback(jqXHR, textStatus, errorThrown)
        }
    })
};

