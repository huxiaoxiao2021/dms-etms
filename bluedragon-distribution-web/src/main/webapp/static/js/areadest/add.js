//初始化
$(document).ready(main);

function main() {
    // 机构变更
    $("#deviceOrg").change(function () {
        orgChange($(this).val());
    });

    $("#transferSite").change(function () {
        siteChange($(this).val());
    });

    // 保存提交按钮
    $('#saveBtn').click(function () {
        doSave();
    });

    // 返回按钮
    $('#goBackBtn').click(function () {
        goBack();
    });

}

function doSave() {
    var params = getParams();
    if (checkParams(params)){
        var contextPath = $("#contextPath").val();
        var url = contextPath + "/areadest/save";
        CommonClient.postJson(url, params, function(data) {
            if (data.code == 200) {
                alert("添加成功");
                document.location.href = contextPath + "/areadest/index";
            } else {
                if (data.message) {
                    alert('提示:' + data.message);
                } else {
                    alert('提示:添加异常');
                }

            }
        });
    }
}

function getParams() {
    var arr = new Array();
    $("#deviceOrg option").each(function () {
        var val = $(this).val(); //获取单个value
        $('#to_' + val + ' option').each(function () {
            arr.push($(this).text());
        });
    });
    var params = {};
    params.createSiteName = $("#createSiteName").val();
    params.createSiteCode = $("#createSiteCode").val();
    params.transferSiteCode = $("#transferSite").val();
    params.transferSiteName = $("#transferSite").find("option:selected").text();
    params.receiveSiteCodeName = arr;
    return params;
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.createSiteCode == null || params.createSiteCode <= 0){
        alert("参数错误，请退出重新登录！");
        return false;
    }
    if (params.transferSiteCode == null || params.transferSiteCode <= 0){
        alert("请选择中转分拣站中心！")
        return false;
    }
    return true;
}

// 返回
function goBack() {
    window.location.href = $("#contextPath").val() + "/areadest/index";
}

/**
 * 机构变更，实现分拣中心联动
 */
function orgChange(orgId) {
    clearMultiSelect();
    if ($.trim(orgId).length <= 0) {
        initDmsSelect();
        return;
    }
    var url = $("#contextPath").val() + "/areadest/dmslist";
    var param = {"orgId": orgId};
    $.getJSON(url, param, function (data) {
        initDmsSelect(data);
    });
}

function siteChange(siteId) {
    $.blockUI({ message:"<span class='pl20 icon-loading'>正在加载,请稍后...</span>"});
    clearMultiSelect();
    var url = $("#contextPath").val() + "/services/bases/dms";
    $.getJSON(url, function (data) {
        var createSiteCode = $("#createSiteCode").val();
        var params = {"createSiteCode": createSiteCode, "transferSiteCode": siteId};
        var getUrl = $("#contextPath").val() + "/areadest/dmsselected";
        $.getJSON(getUrl, params, function (selectedData) {
            loadMultiSelect(data, selectedData);
        });
    });
}

/**
 * 清空多选框
 */
function clearMultiSelect(){
    /*$('#from_all').empty();
    $('#to_all').empty();*/

    $("#deviceOrg option").each(function () {
        var val = $(this).val(); //获取单个value
        $('#from_' + val).empty();
        $('#to_' + val).empty();
    });
}

function loadMultiSelect(dmsList, selectedList) {
   /* var optionFromAll = "";
    var optionToAll = "";*/
    for (var i = 0; i < dmsList.length; i++) {
        var flag = false;
        if (selectedList != null && selectedList.length > 0){
            for (var j = 0; j < selectedList.length; j++) {
                if (dmsList[i].siteCode == selectedList[j].receiveSiteCode){
                    flag = true;
                    var toOption = "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
                    /*optionToAll += toOption;*/
                    $('#to_' + dmsList[i].orgId).append(toOption);
                    break;
                }
            }
        }
        if (!flag){
            var option = "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
            /*optionFromAll += option;*/
            $('#from_' + dmsList[i].orgId).append(option);
        }
    }
    /*$('#from_all').append(optionFromAll);
    $('#to_all').append(optionToAll);*/
    $.unblockUI();
}

/**
 * 初始化分拣中心下拉列表
 */
function initDmsSelect(data) {
    var dmsList = data;
    var optionList = "";
    if (dmsList == undefined || null == dmsList || $.trim(dmsList).length <= 0) {
        optionList = "<option value=''>所有分拣中心</option>";
    } else {
        optionList = "<option value=''>所有分拣中心</option>";
        for (var i = 0; i < dmsList.length; i++) {
            optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteName + "</option>";
        }
    }
    $("#transferSite").html(optionList);
}

