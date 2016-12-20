//初始化
$(document).ready(main);

function main() {
    // 机构变更
    $("#deviceOrg").change(function () {
        orgChange($(this).val());
    });

    // 中转分拣中心变更
    $("#transferSite").change(function () {
        siteChange();
    });

    // 保存提交按钮
    $('#saveBtn').click(function () {
        doSave();
    });

    // 返回按钮
    $('#goBackBtn').click(function () {
        goBack();
    });

    $("#multiSelect_from").multiselect({
        afterMoveToRight: function ($left, $right, $options) {

        }
    });

    $("#multiSelect_to").multiselect({
        afterMoveToLeft: function ($left, $right, $options) {

        }
    });

}

function doSave() {
    var params = getParams();
    if (checkParams(params)) {
        var contextPath = $("#contextPath").val();
        var url = contextPath + "/areadest/save";
        CommonClient.postJson(url, params, function (data) {
            if (data.code == 200) {
                alert("保存成功！");
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

    $('#multiSelect_to option').each(function () {
        arr.push($(this).text());
    });

    var params = {};
    params.receiveSiteOrg = $("#selectedOrg").val();
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
    if (params.createSiteCode == null || params.createSiteCode <= 0) {
        alert("获取始发分拣中心失败，请确认用户是否关联分拣中心！");
        return false;
    }
    if (params.transferSiteCode == null || params.transferSiteCode <= 0) {
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
    if ($.trim(orgId).length <= 0) {
        initDmsSelect();
        return;
    }
    var url = $("#contextPath").val() + "/areadest/dmslist?" + Math.random();
    var param = {"orgId": orgId};
    $.getJSON(url, param, function (data) {
        initDmsSelect(data);
    });
}

function siteChange() {
    $('#myTab li:eq(0) a').tab('show');
    loadSelected("all");
}

/**
 * 清空多选框
 */
function clearMultiSelect() {
    $('#multiSelect_from').empty();
    $('#multiSelect_to').empty();
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

function loadSelected(orgId) {
    $.blockUI({message: "<span class='pl20 icon-loading'>正在加载,请稍后...</span>"});
    clearMultiSelect();
    if (orgId == 'all') {
        $("#selectedOrg").val("");
        var url = $("#contextPath").val() + "/services/bases/dms?" + Math.random();
    } else {
        $("#selectedOrg").val(orgId);
        var url = $("#contextPath").val() + "/areadest/dmslist?" + Math.random();
    }
    var createSiteCode = $("#createSiteCode").val();
    var transferSiteCode = $("#transferSite").find("option:selected").val();
    if (createSiteCode != "" && transferSiteCode != "") {
        var param = {"orgId": orgId};
        $.getJSON(url, param, function (data) {
            if (data != null && data.length > 0) {
                var innerParams = {"createSiteCode": createSiteCode, "transferSiteCode": transferSiteCode};
                var getUrl = $("#contextPath").val() + "/areadest/dmsselected?" + Math.random();
                $.getJSON(getUrl, innerParams, function (selectedData) {
                    addOptions(data, selectedData);
                });
            } else {
                $.unblockUI();
            }
        });
    } else {
        $.unblockUI();
    }
}

function addOptions(data, selectedData) {
    var fromOption = "";
    var toOption = "";
    for (var i = 0; i < data.length; i++) {
        var flag = false;
        if (selectedData != null && selectedData.length > 0) {
            for (var j = 0; j < selectedData.length; j++) {
                if (data[i].siteCode == selectedData[j].receiveSiteCode) {
                    flag = true;
                    var option = "<option value='" + data[i].siteCode + "'>" + data[i].siteCode + " " + data[i].siteName + "</option>";
                    toOption += option;
                    break;
                }
            }
        }
        if (!flag) {
            var option = "<option value='" + data[i].siteCode + "'>" + data[i].siteCode + " " + data[i].siteName + "</option>";
            fromOption += option;
        }
    }
    $('#multiSelect_to').append(toOption);
    $('#multiSelect_from').append(fromOption);
    $.unblockUI();
}

