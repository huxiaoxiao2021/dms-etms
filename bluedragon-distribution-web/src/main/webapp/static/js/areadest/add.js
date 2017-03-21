//初始化
$(document).ready(main);

function main() {
    // 返回按钮
    $('#backBtn').click(function () {
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
    params.operateSiteCode = $.trim($("#currentSiteCode").val());
    params.machineId = $.trim($("#machineId").val());
    params.planName = $.trim($("#planName").val());
    return params;
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.operateSiteCode == null || params.operateSiteCode <= 0) {
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
    var contextPath = $("#contextPath").val();
    jQuery.ajax({
        url: contextPath + "/areaDestPlan/save?" + Math.random(),
        type: "POST",
        data: JSON.stringify(params),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        async: false,
        success: function (data) {
            if (data && data.code == 0) {
                if (data.message) {
                    alert(data.message);
                } else {
                    alert("添加异常");
                }
            } else {
                alert("添加成功");
                document.location.href = contextPath + "/areaDestPlan/index";
            }
        }
    });
}
