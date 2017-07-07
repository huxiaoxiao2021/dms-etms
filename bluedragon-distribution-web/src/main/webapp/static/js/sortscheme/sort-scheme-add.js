//初始化
$(document).ready(main);

// 当前分拣中心
var siteNo = null;

function main() {

    // 添加按钮
    $('#addBtn').click(function () {
        addSortScheme();
    });

    // 返回按钮
    $("#backBtn").click(function () {
        var siteCode = $("#siteNo").val();
        var siteName = $("#siteNo").find("option:selected").text();

        location.href = $("#contextPath").val() + "/autosorting/sortScheme/index?siteCode="
            + siteCode + "&siteName=" + encodeURIComponent(encodeURIComponent(siteName));
    });

    // 加载所有的分拣中心
    initDms();
}

function addSortScheme() {
    try {
        var params = getParams();
        checkParams(params);
    } catch (e) {
        jQuery.messager.alert('提示:', e.message, 'info');
        return;
    }
    var url = $("#contextPath").val() + "/autosorting/sortScheme/add";
    CommonClient.postJson(url, params, function(data) {
        if (data == undefined || data == null) {
            alert("提示请求无数据返回");
            return;
        }
        if (data.code == 200) {
            alert(data.message);
        } else {
            alert("分拣计划添加异常,请正确选择分拣中心!!");
        }
    });
}

function checkParams(params) {
    if (null == params) {
        throw new Error("参数为空!!");
    }
    if (params.siteNo == null || params.siteNo == "") {
        throw new Error("分拣中心为空!!");
    }
    if (params.machineCode == null || params.machineCode == "" || params.machineCode.length > 30) {
        throw new Error("机器码为空,或者长度超过30个字符!!");
    }
    if (params.sortMode == null || params.sortMode == "") {
        throw new Error("分拣模式为空!!");
    }
    if (params.name == null || params.name == "" || params.name.length > 20) {
        throw new Error("分拣机方案名称为空,或者长度超过20个字符!!");
    }
}

function getParams() {
    var params = {};
    params.siteNo = $.trim($("#siteNo").val());
    params.machineCode = $.trim($("#machineCode").val());
    params.sortMode = $.trim($("#sortMode").val());
    params.autoSend = $.trim($("#autoSend").val());
    params.name = $.trim($("#name").val());
    return params;
}


// 加载分拣中心列表
function initDms() {
    var url = $("#contextPath").val() + "/services/bases/dms";
    $.getJSON(url, function (data) {
        var dmsList = data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
            return;
        }
        if (dmsList.length > 0 && dmsList[0].code == 200) {// 200:normal
            loadDmsList(dmsList, "siteNo");
        } else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
            jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
        } else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
            jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
        } else {
            jQuery.messager.alert('提示:', "数据异常！", 'info');
        }
    });
}

function loadDmsList(dmsList, selectId) {
    var dmsObj = $('#' + selectId);
    $('#createDmsList').html("");
    var optionList = "";
    //optionList += "<option value='' selected='selected'></option>";
    for (var i = 0; i < dmsList.length; i++) {
        optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
    }
    dmsObj.append(optionList);
}
