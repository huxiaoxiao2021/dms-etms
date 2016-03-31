//初始化
$(document).ready(main);

var machineAddressList;

function main() {

    // 查询按钮提交处理
    $('#queryBtn').click(function () {
        onQueryBtnClick();
    });

    //初始化机器码和分拣中心地址列表
    initMachineAddressList();

}

function onQueryBtnClick() {
    var params = getParams();
    if (params.waybillOrPackageCode == "") {
        alert("请输入参数[运单号/包裹号]!");
        return false;
    }
    if (params.machineCode == "") {
        alert("请输入参数[分拣机代码]!");
        return false;
    }
    var localDmsUrl = getLocalDmsUrl(params.machineCode);
    if (localDmsUrl == null || localDmsUrl == "") {
        alert("根据分拣机代码,无法定位分拣中心!");
        return false;
    }
    params.localDmsUrl = localDmsUrl;
    doQueryWorker(params);
}

function getParams() {
    var params = {};
    params.waybillOrPackageCode = $.trim($("#waybillOrPackageCode").val());
    params.machineCode = $.trim($("#machineCode").val());
    return params;
}

// 查询请求
function doQueryWorker(params) {
    var url = $("#contextPath").val() + "/admin/remote-access/list";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("HTTP请求无数据返回！");
            return;
        }
        if (data.code == 200) {
            var waybillList = data.data;
            if (waybillList == null) {
                alert("访问成功,但返回数据为空!");
                return;
            }
            var temp = "";
            for (var i = 0; i < waybillList.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (waybillList[i].waybillCode) + "</td>";
                temp += "<td>" + (getData(waybillList[i].originalSiteCode)) + "</td>";
                temp += "<td>" + (getData(waybillList[i].siteCode)) + "</td>";
                temp += "<td>" + (getData(waybillList[i].createTimeLocal)) + "</td>";
                temp += "<td>" + (getData(waybillList[i].receTime)) + "</td>";
                temp += "<td>" + (waybillList[i].receFlag == 1 ? "是" : "否") + "</td>";
            }
            $("#paperTable tbody").html("");
            $("#paperTable").append(temp);
        } else {
            alert(data.message);
        }
    });
}

function getLocalDmsUrl(machineCode) {
    if (machineAddressList != null) {
        for (var index in machineAddressList) {
            var machineAddress = machineAddressList[index];
            if (machineAddress.machine_code == machineCode) {
                return machineAddress.local_dms_url;
            }
        }
    }
    return null;
}

function initMachineAddressList() {
    var url = $("#contextPath").val() + "/static/js/admin/remote-access/machine-address.json";
    $.getJSON(url, function (data) {
        machineAddressList = data;
    });
}


// ----------------------

function getData(value) {
    if (value == null || value == "") {
        return "";
    } else {
        return value;
    }
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