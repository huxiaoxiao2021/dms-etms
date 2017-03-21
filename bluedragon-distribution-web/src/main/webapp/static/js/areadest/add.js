//初始化
$(document).ready(main);

function main() {
    // 返回按钮
    $('#goBackBtn').click(function () {
        goBack();
    });

    // 添加按钮
    $('#addBtn').click(function () {
        onAddBtnClick();
    });
}

// 返回
function goBack() {
    window.location.href = $("#contextPath").val() + "/areaDestPlan/index";
}

function onAddBtnClick() {
    var params = getParams();
    if (!checkParams(params)) {
        return false;
    }
    doSave(params);
}


function getParams() {
    var params = {};
    params.currentSiteCode = $.trim($("#currentSiteCode").val());
    params.machineId = $.trim($("#machineId").val());
    params.planName = $.trim($("#planName").val());
    return params;
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.currentSiteCode == null || params.currentSiteCode <= 0) {
        alert("获取当前分拣中心失败，请确认用户是否关联分拣中心！");
        return false;
    }
    if (params.machineId == null || params.machineId <= 0) {
        alert("获取龙门架编号为空，请重试！");
        return false;
    }
    if (params.planName == null || params.planName.trim() == '') {
        alert("输入方案名称非法，请重新输入！");
        return false;
    }
    return true;
}

function doSave(params) {
    alert('保存');
}
