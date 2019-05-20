var query_sendCode_by_barCode_url = "/exception/sendCodeHandler/querySendCodeByBarCode";
var summary_by_sendCodes_url = "/exception/sendCodeHandler/summaryPackageNumBySendCodes";
var go_to_detail_pager = "/exception/sendCodeHandler/detailPager";

$(document).ready(function () {

    /* 输入框的回车事件 */
    $("#barCode").bind("keydown",function (event) {
        var event = event || window.event;
        var currKey = event.keyCode || event.which || event.charCode;
        if(currKey == 13){
            barCodeInterEvent($(this).val());
        }
    });

    /* 查询点击事件 */
    $("#btn_query").click(function () {
        var barCode = $("#barCode").val();
        barCode = barCode.trim();
        if (null == barCode || barCode == "") {
            queryClickEvent();
        } else {
            barCodeInterEvent(barCode, function(){
                queryClickEvent();
            });
        }
    });

});

/**
 * 条码输入回车事件：根据单号查询上游批次，或者将批次设置到下面列表
 * @param barCode   条码
 * @param successFunc   成功回调函数
 */
function barCodeInterEvent(barCode,successFunc) {
    barCode = barCode.trim();
    if (null == barCode || barCode == "") {
        return;
    }
    var param = {
        barCode:barCode
    };
    CommonClient.postJson(query_sendCode_by_barCode_url,param,function (res) {
        if (res == null || res.code != 200) {
            jQuery.msg.warn(res.message);
            return;
        }
        /* 清空当前输入框 */
        jQuery("#barCode").val("");

        /* 将获取到的批次设置到下面的面板列表中 */
        var sendCodes = res.data;
        if (null == sendCodes || sendCodes.length == 0) {
            jQuery.msg.warn("该单号未在上游批次中发货");
            return;
        }
        jQuery.each(sendCodes,function (index, sendCode) {
            if (jQuery(".panel").size() < 5) {
                jQuery("#sendCodes").append("<div class='panel panel-primary form-control' title='" + sendCode + "'>" +
                    sendCode + "</div>")
            }
        });
        successFunc();
    })
}

/**
 * 点击查询获取批次号的列表，查询批次号的汇总信息
 */
function queryClickEvent() {
    var sendCodeList = getSendCodeList();
    if (null == sendCodeList || sendCodeList.length == 0) {
        return;
    }
    var param = {
        sendCodes : sendCodeList
    };
    CommonClient.postJson(summary_by_sendCodes_url,param,function (res) {
        if (res == null || res.code != 200) {
            jQuery.msg.warn(res.message);
            return;
        }
        var data = res.data;
        var boxSum = 0, packOutBoxSum = 0, packSum = 0,
            boxOperSum = 0, packOutBoxOperSum = 0, packOperSum = 0,
            boxUnOperSum = 0, packOutBoxUnOperSum = 0, packUnOperSum = 0;
        if (data != null) {
            var summary = data.summary;
            var operatedSummary = data.operatedSummary;
            var unOperatedSummary = data.unOperatedSummary;
            boxSum = summary != null && summary.boxSum != null? summary.boxSum : 0;
            packOutBoxSum = summary != null && summary.packageOutBoxNum != null? summary.packageOutBoxNum : 0;
            packSum = summary != null && summary.packageNum != null? summary.packageNum : 0;

            boxOperSum = operatedSummary != null && operatedSummary.boxSum != null? operatedSummary.boxSum : 0;
            packOutBoxOperSum = operatedSummary != null && operatedSummary.packageOutBoxNum != null? operatedSummary.packageOutBoxNum : 0;
            packOperSum = operatedSummary != null && operatedSummary.packageNum != null? operatedSummary.packageNum : 0;

            boxUnOperSum = unOperatedSummary != null && unOperatedSummary.boxSum != null? unOperatedSummary.boxSum : 0;
            packOutBoxUnOperSum = unOperatedSummary != null && unOperatedSummary.packageOutBoxNum != null? unOperatedSummary.unOperatedSummary : 0;
            packUnOperSum = unOperatedSummary != null && unOperatedSummary.packageNum != null? unOperatedSummary.packageNum : 0;
        }
        $("#boxSum").text(boxSum);
        $("#packOutBoxSum").text(packOutBoxSum);
        $("#packSum").text(packSum);
        $("#boxOperSum").text(boxOperSum);
        $("#packOutBoxOperSum").text(packOutBoxOperSum);
        $("#packOperSum").text(packOperSum);
        $("#boxUnOperSum").text(boxUnOperSum);
        $("#packOutBoxUnOperSum").text(packOutBoxUnOperSum);
        $("#packUnOperSum").text(packUnOperSum);
    })
}

/**
 * 汇总数字点击跳转事件
 * @param type 类型 1 2 3 4 5 6 7 8 9
 */
function goToDetailEvent(type) {
    var param = {
        sendCodes : getSendCodeList(),
        type : type
    };
    layer.open({
        id: 'exceptionDetailFrame',
        type: 2,
        title: '明细查询',
        shade: 0.7,
        maxmin: true,
        shadeClose: false,
        area: ['66%', '66%'],
        content: go_to_detail_pager,
        success: function (layero, index) {
            var frameId = document.getElementById("exceptionDetailFrame").getElementsByTagName("iframe")[0].id;
            var frameWindow = $('#' + frameId)[0].contentWindow;
            frameWindow.$("#param").attr("value",JSON.stringify(param));
            frameWindow.$("#sendCodes").attr("value",JSON.stringify(param.sendCodes));
            frameWindow.$("#type").attr("value",JSON.stringify(param.type));
            param.pageSize = 10;
            param.pageNo = 1;
            // frameWindow.$("#btn_query").click();
        }
    })

}

/**
 * 获取批次号的列表
 */
function getSendCodeList() {
    var sendCodeHtml = document.getElementsByClassName("panel");
    var sendCodeList = [];
    jQuery.each(sendCodeHtml,function (index, sendCodeHtml) {
        sendCodeList.push(sendCodeHtml.innerText);
    });
    return sendCodeList;
}
