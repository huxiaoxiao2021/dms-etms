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
        location.href = $("#contextPath").val() + "/autosorting/sortScheme/index";
    });

    // 加载所有的分拣中心
    initDms();
}

function addSortScheme() {
    var params = getParams();
    if (!checkParams(params)) {
        jQuery.messager.alert('提示:', '参数均不能为空!', 'info');
        return false;
    } else {
        var url = $("#contextPath").val() + "/autosorting/sortScheme/add";
        CommonClient.postJson(url, params, function(data) {
            if (data == undefined || data == null) {
                alert("提示请求无数据返回");
                return;
            }
            if (data.code == 200) {
                alert(data.message);
            } else {
                alert(data.message);
            }
        });
    }
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.siteNo == null || params.siteNo == "") {
        return false;
    }
    if (params.machineCode == null || params.machineCode == "") {
        return false;
    }
    if (params.sortMode == null || params.sortMode == "") {
        return false;
    }
    if (params.name == null || params.name == "") {
        return false;
    }
    return true;
}

function getParams() {
    var params = {};
    params.siteNo = $.trim($("#siteNo").val());
    params.machineCode = $.trim($("#machineCode").val());
    params.sortMode = $.trim($("#sortMode").val());
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