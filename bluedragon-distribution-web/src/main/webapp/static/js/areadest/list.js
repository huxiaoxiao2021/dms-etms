//初始化
$(document).ready(main);

function main() {

    // 查询按钮提交处理
    $('#queryBtn').click(function () {
        onQueryBtnClick();
    });

    // 重置按钮
    $('#funReset').click(function () {
        funReset();
    });

    // 添加按钮
    $('#addBtn').click(function () {
        goAddBtnClick();
    });

    // Expand/collapse all
    $('#btn-expand-all').on('click', function (e) {
        var levels = $('#select-expand-all-levels').val();
        $('#treeview').treeview('expandAll', {silent: $('#chk-expand-silent').is(':checked')});
    });

    $('#btn-collapse-all').on('click', function (e) {
        $('#treeview').treeview('collapseAll', {silent: $('#chk-expand-silent').is(':checked')});
    });

    $('#input-select-node').on('keyup', function (e) {
        var selectableNodes = findSelectableNodes();
        $('.select-node').prop('disabled', !(selectableNodes.length >= 1));
    });

    // 加载所有的分拣中心
    initDms();

}

function findSelectableNodes() {
    return $('#treeview').treeview('search', [$('#input-select-node').val(), {ignoreCase: false, exactMatch: false}]);
};

function funReset() {
    $("#transferSite").val(-1);
    $("#receiveSite").val(-1);
}

// 加载分拣中心列表
function initDms() {
    var url = $("#contextPath").val() + "/services/bases/dms";
    $.getJSON(url, function (data) {
        var dmsList = data;
        if (data == undefined || data == null) {
            alert('提示:HTTP请求无数据返回！');
            return;
        }
        if (dmsList.length > 0 && dmsList[0].code == 200) {// 200:normal
            loadDmsList(dmsList, "transferSite");
            loadDmsList(dmsList, "receiveSite");
        } else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
            alert('提示:获取分拣中心列表为空！');
        } else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
            alert('提示:获取分拣中心列表为空！');
        } else {
            alert('提示:数据异常！');
        }
    });
}

function loadDmsList(dmsList, selectId) {
    var dmsObj = $('#' + selectId);
    var optionList = "";
    for (var i = 0; i < dmsList.length; i++) {
        optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
    }
    dmsObj.append(optionList);
}

function onQueryBtnClick() {
    var params = getParams();
    if (!checkParams(params)) {
        return false;
    }
    doQueryAreaDest(params);
}

function getParams() {
    var params = {};
    params.createSiteCode = $.trim($("#createSiteCode").val());
    params.transferSiteCode = $.trim($("#transferSite").val());
    params.receiveSiteCode = $.trim($("#receiveSite").val());
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
    return true;
}

// 查询请求
function doQueryAreaDest(params) {
    var url = $("#contextPath").val() + "/areadest/list";
    CommonClient.postJson(url, params, function (data) {
        if (data == undefined || data == null) {
            alert('提示:HTTP请求无数据返回！');
            return;
        }
        if (data.code == 200) {
            $('#treeview').treeview({
                color: "#428bca",
                data: data.data
            });
        } else {
            alert('提示:' + data.message);
        }
    });
}

function goAddBtnClick() {
    var contextPath = $("#contextPath").val();
    location.href = contextPath + "/areadest/addview";
}