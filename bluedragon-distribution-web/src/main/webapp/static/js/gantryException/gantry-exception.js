/**
 * Created by hanjiaxing on 2016/12/14.
 */
$(document).ready(init);

/**
 * 页面加载执行
 * */
function init() {

    $("#gantry_exception_query_sub").click(function () {
        gantry_exception_query_sub(1);
    })

    $("#gantry_exception_export_sub").click(function () {
        gantry_exception_export_sub();
    });
    gantry_exception_query_sub(1);
}


/**
 * 提交查询
 * */
function gantry_exception_query_sub(pageNo) {
    var params = getParams();

    params.pageNo = pageNo;
    if (params != false) {
        doQuery(params);
    }

}


/**
 *
 * 数据导出
 *
 * */
function gantry_exception_export_sub() {
    var params = getParams();

    if (params != false) {
        exportData(params);
    }

}

/**
 *
 * 组装参数
 *
 * */
function getParams() {
    var params = {};
    params.machineId = $.trim($("#gantry_exception_machine_id").val());
    params.startTime = $.trim($("#gantry_exception_startTime").val());
    params.endTime = $.trim($("#gantry_exception_endTime").val());
    var sendStatusValue = $.trim($("#gantry_exception_sendStatus").val());
    var type = $.trim($("#gantry_exception_type").val());

    if (sendStatusValue < 2) {
        params.sendStatus = sendStatusValue;
    } else {
        params.sendStatus = null;
    }

    if (type == 0) {
        params.type = null;

    } else {
        params.type = type;
    }

    if (params.machineId == null || params.machineId == "") {
        alert("请选择龙门架");
        return false;
    }
    if (params.startTime == null || params.startTime == "") {
        alert("请选择起始时间");
        return false;
    }
    if (params.endTime == null || params.endTime == "") {
        alert("请选择结束时间");
        return false;
    }
    var result = checkDate(params.startTime, params.endTime);
    if (result == 1) {
        alert("查询日期间隔不能超过24小时");
        return false;
    }
    else if (result == 2) {
        alert("开始日期不能大于结束日期");
        return false;
    }
    return params;
}

/**
 *
 *查询请求
 *
 * */
function doQuery(params) {
    var url = $("#contextPath").val() + "/gantryException/doQuery";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("没有符合条件的结果");
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            var reason = "";
            for (var i = 0; i < dataList.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (dataList[i].machineId) + "</td>";
                temp += "<td>" + (dataList[i].barCode) + "</td>";
                temp += "<td>" + (dataList[i].waybillCode) + "</td>";
                temp += "<td>" + (dataList[i].sendCode == null ? "" : dataList[i].sendCode) + "</td>";
                temp += "<td>" + (dataList[i].volume) + "</td>";
                if (dataList[i].type == 1)
                    reason = "无预分拣站点";
                else if (dataList[i].type == 2)
                    reason = "无运单信息";
                else if (dataList[i].type == 3)
                    reason = "无箱号信息";
                else if (dataList[i].type == 4)
                    reason = "拦截订单";
                else if (dataList[i].type == 5)
                    reason = "龙门架未绑该站点";
                else if (dataList[i].type == 6)
                    reason = "无启用方案信息";
                else if (dataList[i].type == 7)
                    reason = "上传操作时间异常";
                else if (dataList[i].type == 21)
                    reason = "发货始发地站点无效";
                else if (dataList[i].type == 22)
                    reason = "无发货目的地站点";
                else if (dataList[i].type == 23)
                    reason = "订单拦截";
                else if (dataList[i].type == 24)
                    reason = "无落格时间";
                else if (dataList[i].type == 25)
                    reason = "运单已妥投";
                temp += "<td>" + reason + "</td>";
                temp += "<td>" + (getDateString(dataList[i].operateTime)) + "</td>";
                temp += "<td>" + (getDateString(dataList[i].createTime)) + "</td>";
                temp += "<td>" + (dataList[i].sendStatus ? "已发货" : "未发货") + "</td>";
                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("gantry_exception_query_sub", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            alert(data.message);
        }
    });
}

/**
 *
 *导出数据
 *
 * */
function exportData(params) {

    var url = $("#contextPath").val() + "/gantryException/doQueryCount";
    CommonClient.post(url, params, function (result) {
        if (result == undefined || result == null || result.data == 0) {
            alert("没有符合条件的结果");
            return;
        }
        if (result.code == 200 && result.data > 0) {
            var url = $("#contextPath").val() + "/gantryException/doExport?machineId="
                + params.machineId + "&startTime="
                + params.startTime + "&endTime="
                + params.endTime;
            if (params.sendStatus != null)
                url += "&sendStatus=" + params.sendStatus;
            window.open(url, "_parent");
        } else {
            alert(result.message);
        }
    });
}

/**
 *
 * 规范时间格式
 *
 */
function getDateString(millis) {
    if (null == millis) {
        return "";
    }
    var date = new Date();
    date.setTime(millis);
    return date.format('yyyy-MM-dd HH:mm:ss');
}

/**
 *
 * 检测两个日期不超过24小时、开始日期不大于结束日期，多加1分钟为了避免对秒进行精确
 * @param startDate 开始日期
 * @param endDate   结束日期
 * @returns 1，大于24小时，2，开始日期大于结束日期
 *
 */
function checkDate(startDateStr, endDateStr) {
    var gap = 1000 * 60 * (60 * 24 + 1);
    var startDate = new Date(startDateStr.replace('-', '/'));
    var endDate = new Date(endDateStr.replace('-', '/'));
    if (startDate.getTime() > endDate.getTime()) {
        return 2;
    }
    if (endDate.getTime() - startDate.getTime() > gap) {
        return 1;
    }
    return 0;
}


Date.prototype.format = function (f) {
    var o = {
        "M+": this.getMonth() + 1, // month
        "d+": this.getDate(), // day
        "H+": this.getHours(), // hour
        "m+": this.getMinutes(), // minute
        "s+": this.getSeconds(), // second
        "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
        "S": this.getMilliseconds()
        // millisecond
    }
    if (/(y+)/.test(f))
        f = f.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(f))
            f = f.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    return f
}

/**
 *
 * 当选择起始日期后，规定结束日期不能超过起始日期24小时
 *
 */
function getMaxDate() {
    var startDateStr = $.trim($("#gantry_exception_startTime").val());

    var now = new Date();
    if (startDateStr == null || startDateStr == "") {
        return now.format('yyyy-MM-dd HH:mm:ss');
    }
    var startDate = new Date(startDateStr.replace('-', '/'));

    //timestamp
    var maxDate = new Date();
    maxDate = maxDate.setDate(startDate.getDate() + 1);
    var maxDateStr = new Date(maxDate).format('yyyy-MM-dd HH:mm:ss');

    if (maxDate > now) {
        return now.format('yyyy-MM-dd HH:mm:ss');
    }

    return maxDateStr;

}

