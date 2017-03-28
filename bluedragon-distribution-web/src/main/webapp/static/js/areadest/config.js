//初始化
$(document).ready(main);

var timeId = null;

function main() {
    // 机构变更
    $("#deviceOrg").change(function () {
        orgChange($(this).val());
    });

    // 多级分拣保存
    $('#goAddBtn_1').click(function () {
        doSaveMultiDms();
    });

    // 直发分拣保存
    $('#goAddBtn_2').click(function () {
        doSaveDirectDms();
    });

    // 返回按钮
    $('#goBackBtn').click(function () {
        goBack();
    });

    // 导入按钮
    $("#loadInBtn").click(function () {
        importExcel();
    });

    // 导出按钮
    $("#loadOutBtn").click(function () {
        exportExcel();
    })

    // 下载模版
    $("#downloadModelBtn").click(function () {
        goDownModel();
    })

    $('#multiSelect_from').multiselect({
        search: {
            left: '<input type="text" name="left" class="form-control" style="-webkit-box-sizing:border-box;box-sizing:border-box;" placeholder="输入站点ID/站点名称..." />',
            right: '<input type="text" name="right" class="form-control" style="-webkit-box-sizing:border-box;box-sizing:border-box;" placeholder="输入站点ID/站点名称..." />'
        },
        sort: false,
        beforeMoveToRight: function ($left, $right, $options) {
            delTimeout();
            return doSaveOrDel("add", $options);
        },
        beforeMoveToLeft: function ($left, $right, $options) {
            delTimeout();
            return doSaveOrDel("remove", $options);
        }
    });
    //初始化分拣中心下拉框
    initDms();
    //初始化站点下拉框
    intSite();
    multiDmsLoad(1);
    directDmsLoad(1);
}

function initDms() {
    var url = $("#contextPath").val() + "/services/bases/dms";
    $.getJSON(url, function (data) {
        var dmsList = data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
            return;
        }
        if (dmsList.length > 0 && dmsList[0].code == 200) {
            initDmsSelect("receiveSite", data);
            initDmsSelect("nextSite", data)
        } else if (dmsList.length > 0 && dmsList[0].code == 404) {
            jQuery.messager.alert('提示:', '获取分拣中心列表为空！', 'info');
        } else if (dmsList.length > 0 && dmsList[0].code == 20000) {
            jQuery.messager.alert('提示:', dmsList[0].message, 'info');
        } else {
            jQuery.messager.alert('提示:', '数据异常！', 'info');
        }
    });
}

/**
 * 清空多选框
 */
function clearMultiSelect() {
    $('#multiSelect_from').empty();
    $('#multiSelect_to').empty();
}

function intSite() {
    var url = $("#contextPath").val() + "/services/bases/allsite";
    $.getJSON(url, function (data) {
        var dmsList = data;
        if (data == undefined || data == null) {
            alert("提示:HTTP请求无数据返回！");
            return;
        }
        if (dmsList.length > 0 && dmsList[0].code == 200) {
            clearMultiSelect();
            initDmsSelect("destSite", data);
            loadMultiSelect(data);
        } else if (dmsList.length > 0 && dmsList[0].code == 404) {
            jQuery.messager.alert('提示:', '获取分拣中心列表为空！', 'info');
        } else if (dmsList.length > 0 && dmsList[0].code == 20000) {
            jQuery.messager.alert('提示:', dmsList[0].message, 'info');
        } else {
            jQuery.messager.alert('提示:', '数据异常！', 'info');
            alert("提示:数据异常！");
        }
    });
}

function multiDmsLoad(pageNo) {
    var params = {};
    //多级分拣
    params.routeType = 3;
    params.planId = $.trim($("#planId").val());
    params.pageNo = pageNo;
    doQuery("multiDms", params);
}

function directDmsLoad(pageNo) {
    var params = {};
    //多级分拣
    params.routeType = 2;
    params.planId = $.trim($("#planId").val());
    params.pageNo = pageNo;
    doQuery("directDms", params);
}

// 查询请求
function doQuery(id, params) {
    var url = $("#contextPath").val() + "/areaDest/getList";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', '未配置方案！', 'info');
            return;
        }
        if (data.code == 200 || data.code == 2200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            if (dataList != null && dataList.length > 0) {
                for (var i = 0; i < dataList.length; i++) {
                    temp += "<tr class='a2'>";
                    temp += "<td>" + (dataList[i].createSiteName) + "</td>";
                    if (dataList[i].transferSiteCode == null || dataList[i].transferSiteCode == 0) {
                        temp += "<td>-----</td>";
                    } else {
                        temp += "<td>" + (dataList[i].transferSiteName) + "</td>";
                    }
                    temp += "<td>" + (dataList[i].receiveSiteName) + "</td>";
                    temp += "</tr>";
                }
                $("#" + id + "PaperTable tbody").html(temp);
            }
            // 添加分页显示
            $("#" + id + "Pager").html(PageBar.getHtml(id + "Load", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            alert(data.message);
        }
    });
}

function doSaveOrDel(type, $options) {
    var params = getParams(type, $options);
    if (checkParams(params)) {
        if (type == "add") {
            var url = $("#contextPath").val() + "/areaDest/saveBatch";
        } else {
            var url = $("#contextPath").val() + "/areaDest/delBatch";
        }
        var result = false;
        syncAjaxJson("POST", url, params, function (data) {
            if (data.code == 200) {
                showAlert("successAlert");
                result = true;
            } else {
                showAlert("failureAlert");
                result = false;
            }
        });
        return result;
    }
    return false;
}

function showAlert(id) {
    $("#" + id).show();
    timeId = setTimeout(function () {
        $("#" + id).hide();
    }, 1000);
}

function delTimeout() {
    window.clearTimeout(timeId);
    $("#successAlert").hide();
    $("#failureAlert").hide();
}

function getParams(type, $options) {
    var arr = new Array();
    if ($options != null && $options.length > 0) {
        if (type == "add") {
            for (var i = 0; i < $options.length; i++) {
                arr.push($options[i].text);
            }
        } else {
            for (var i = 0; i < $options.length; i++) {
                arr.push($options[i].value);
            }
        }
    }
    var params = {};
    params.routeType = 1;
    params.planId = $.trim($("#planId").val());
    params.createSiteCode = $.trim($("#currentSiteCode").val());
    params.createSiteName = $.trim($("#currentSiteName").val());
    params.receiveSiteList = arr;
    return params;
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.planId == null || params.planId <= 0) {
        jQuery.messager.alert('提示:', '获取方案信息失败，请重新登录后重试！', 'info');
        return false;
    }
    if (params.createSiteCode == null || params.createSiteCode <= 0) {
        jQuery.messager.alert('提示:', '获取当前分拣中心失败，请确认用户是否关联分拣中心！', 'info');
        return false;
    }
    if (params.routeType == 2) {
        if (params.transferSiteCode == null || params.transferSiteCode <= 0) {
            jQuery.messager.alert('提示:', '请选择下级分拣中心！', 'info');
            return false;
        }
        if (params.receiveSiteCode == null || params.receiveSiteCode <= 0) {
            jQuery.messager.alert('提示:', '请选择预分拣站点！', 'info');
            return false;
        }
    }
    if (params.routeType == 3) {
        if (params.receiveSiteCode == null || params.receiveSiteCode <= 0) {
            jQuery.messager.alert('提示:', '请选择末级分拣中心！', 'info');
            return false;
        }
    }
    return true;
}

// 返回
function goBack() {
    window.location.href = $("#contextPath").val() + "/areaDestPlan/index";
}

/**
 * 机构变更，实现分拣中心联动
 */
function orgChange(orgId) {
    var id = "transferSite";
    clearDmsSelect(id);
    if ($.trim(orgId).length <= 0) {
        initDmsSelect(id);
        return;
    }
    var url = $("#contextPath").val() + "/areaDest/dmsList?" + Math.random();
    var param = {"orgId": orgId};
    $.getJSON(url, param, function (data) {
        initDmsSelect(id, data);
    });
}

function clearDmsSelect(id) {
    $("#" + id + "Name").val("");
    $("#" + id + "Code").val("");
    $("#" + id + "Name").unautocomplete();
}

/**
 * 初始化分拣中心下拉列表
 */
function initDmsSelect(selectId, data) {
    if (data == undefined || null == data || $.trim(data).length <= 0) {
        return;
    } else {
        $("#" + selectId + "Name").autocomplete(data, {
            minChars: 0,
            max: 0,
            mustMatch: true,
            width: 300,
            matchContains: true,
            formatItem: function (data, i, max) {//格式化列表中的条目 row:条目对象,i:当前条目数,max:总条目数
                return data.siteName + "|" + data.siteCode;
            },
            formatMatch: function (data, i, max) {//配合formatItem使用，作用在于，由于使用了formatItem，所以条目中的内容有所改变，而我们要匹配的是原始的数据，所以用formatMatch做一个调整，使之匹配原始数据
                return data.siteName + data.siteCode;
            },
            formatResult: function (data) {//定义最终返回的数据，比如我们还是要返回原始数据，而不是formatItem过的数据
                return data.siteName;
            }
        }).result(function (event, data, formatted) {
            if (data == undefined || data == null) {
                $("#" + selectId + "Code").val("");
            } else {
                $("#" + selectId + "Code").val(data.siteCode);
            }
        });
    }
}

function loadMultiSelect(data) {
    var params = {};
    params.routeType = 1;
    params.planId = $.trim($("#planId").val());
    var getUrl = $("#contextPath").val() + "/areaDest/getSelected?" + Math.random();
    $.getJSON(getUrl, params, function (selectedData) {
        addOptions(data, selectedData);
    });
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
                    var option = "<option value='" + data[i].siteCode + "'>" + data[i].siteCode + "," + data[i].siteName + "</option>";
                    toOption += option;
                    break;
                }
            }
        }
        if (!flag) {
            var option = "<option value='" + data[i].siteCode + "'>" + data[i].siteCode + "," + data[i].siteName + "</option>";
            fromOption += option;
        }
    }
    $('#multiSelect_to').append(toOption);
    $('#multiSelect_from').append(fromOption);
}

function syncAjaxJson(type, url, param, successFunction) {
    jQuery.ajax({
        type: type,
        url: url,
        data: JSON.stringify(param),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        beforeSend: function (jqXHR, settings) {
            /*$.blockUI({ message:"<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});*/
        },
        success: successFunction,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("Error:status[" + jqXHR.status + "],statusText[" + jqXHR.statusText + "]");
        },
        complete: function (jqXHR, textStatus) {
            /*$.unblockUI();*/
        }
    });
};

function getMultiDmsParams() {
    var params = {};
    //多级分拣
    params.routeType = 3;
    params.planId = $.trim($("#planId").val());
    params.createSiteCode = $.trim($("#currentSiteCode").val());
    params.createSiteName = $.trim($("#currentSiteName").val());
    params.transferSiteCode = $.trim(("#transferSiteCode").val());
    params.transferSiteName = $.trim($("#transferSiteName").val());
    params.receiveSiteName = $.trim($("#receiveSiteName").val());
    params.receiveSiteCode = $.trim($("#receiveSiteCode").val());
    return params;
}


function doSaveMultiDms() {
    var params = getMultiDmsParams();
    if (checkParams(params)) {
        doDmsSave(params);
    }
}

function getDirectDmsParams() {
    var params = {};
    //多级分拣
    params.routeType = 2;
    params.planId = $.trim($("#planId").val());
    params.createSiteCode = $.trim($("#currentSiteCode").val());
    params.createSiteName = $.trim($("#currentSiteName").val());
    params.transferSiteCode = $.trim($("#nextSiteCode").val());
    params.transferSiteName = $.trim($("#nextSiteName").val());
    params.receiveSiteName = $.trim($("#destSiteName").val());
    params.receiveSiteCode = $.trim($("#destSiteCode").val());
    return params;
}

function doSaveDirectDms() {
    var params = getDirectDmsParams();
    if (checkParams(params)) {
        doDmsSave(params);
    }
}

function doDmsSave(params) {
    var url = $("#contextPath").val() + "/areaDest/save?" + Math.random();
    CommonClient.post(url, params, function (data) {
        if (data && data.code == 200) {
            jQuery.messager.alert('提示:', "添加成功", 'error');
            if (params.routeType == 2) {
                directDmsLoad(1);
            } else {
                multiDmsLoad(1);
            }
        } else {
            if (data.message) {
                jQuery.messager.alert('提示:', data.message, 'info');
            } else {
                jQuery.messager.alert('提示:', "添加异常", 'info');
            }
        }
    });
}

function goDownModel() {
    location.href = "http://sq.jd.com/ScRG3M";
}

function getImportParam() {
    var params = {};
    params.planId = $.trim($("#planId").val());
    params.createSiteCode = $.trim($("#currentSiteCode").val());
    params.createSiteName = $.trim($("#currentSiteName").val());
    return params;
}

function importExcel() {
    var filePath = $.trim($("#importFileIpt").val());
    if (typeof(filePath) == "undefined" || filePath == null || filePath == "") {
        jQuery.messager.alert('提示:', "请选择需要导入文件", 'info');
        return;
    }
    var url = $("#contextPath").val() + "/areaDest/import";
    $.blockUI({message: "<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
    var params = getImportParam();
    $("#importFileForm").ajaxSubmit({
        url: url, // 请求的url
        data: params,// 请求参数
        type: "post", // 请求方式
        dataType: "json", // 响应的数据类型
        async: true, // 异步
        success: function (data) {
            $.unblockUI();
            var jsonData = eval(data);
            if (200 == jsonData.code) {
                //初始化站点下拉框
                intSite();
                multiDmsLoad(1);
                directDmsLoad(1);
                jQuery.messager.alert('提示:', "导入配置成功", 'info');
            } else {
                jQuery.messager.alert('提示:', jsonData.message, 'info');
            }
        },
        error: function () {
            $.unblockUI();
            jQuery.messager.alert('提示:', "导入配置失败，稍后重试", 'info');
        }
    });
}

function exportExcel() {
    var url = $("#contextPath").val() + "/areaDest/export?planId=" + $.trim($("#planId").val());
    window.open(url, "_parent");
}
