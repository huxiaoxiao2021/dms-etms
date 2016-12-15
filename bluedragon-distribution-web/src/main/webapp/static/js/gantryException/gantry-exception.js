/**
 * Created by dudong on 2016/3/14.
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
        to_add_page();
    });

    gantry_exception_query_sub(1);
}


/**
 * 提交查询
 * */
function gantry_exception_query_sub(pageNo) {
    var params = getParams();
    params.pageNo = pageNo;
    doQuery(params);
}


/**
 * 组装参数
 * */
function getParams() {
    var params = {};
    params.machineId = $.trim($("#gantry_exception_machine_id").val());
    params.beginTime = $.trim($("#gantry_exception_startTime").val());
    params.endTime = $.trim($("#gantry_exception_endTime").val());
    var isSendValue = $.trim($("#gantry_exception_isSend").val());
    if (isSendValue < 3) {
        params.isSend = isSendValue;
    }
    return params;
}

/**
 *查询请求
 * */
function doQuery(params) {
    var url = $("#contextPath").val() + "/gantryException/doQuery";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("没有符合条件的龙门架设备");
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            var reason = "";
            for (var i = 0; i < dataList.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (dataList[i].packageCode) + "</td>";
                temp += "<td>" + (dataList[i].waybillCode) + "</td>";
                temp += "<td>" + (dataList[i].volume) + "</td>";
                if (dataList[i].type == 1)
                    reason = "没有预分拣站点";
                else if (dataList[i].type == 2)
                    reason = "分拣信息乱码";
                temp += "<td>" + reason + "</td>";
                temp += "<td>" + (getDateString(dataList[i].operateTime)) + "</td>";
                temp += "<td>" + (dataList[i].yn ? "未发货" : "已发货") + "</td>";
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

function getDateString(millis) {
    if (null == millis) {
        return "";
    }
    var date = new Date();
    date.setTime(millis);
    return date.format('yyyy-MM-dd HH:mm:ss');
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

