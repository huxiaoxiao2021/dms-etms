//初始化
$(document).ready(main);

// 全局变量
var siteList = null;
var chuteCodeList = null;

function main() {

    // 查询按钮提交处理
    $('#queryBtn').click(function () {
        onQueryBtnClick(1);
    });

    // 初始化搜索信息
    initSearch();

}

//---------------模糊搜索-------start------------

function initSearch() {
    initMixSite();
    initChuteCode();

    $("#boxSiteCode").autocomplete({
        source: siteList
    });

    $("#chuteCode").autocomplete({
        source: chuteCodeList
    });
}

function initMixSite() {
    var params = getSearchParams();
    if (params.siteNo == null || params.schemeId == null) {
        jQuery.messager.alert('提示:', "参数不全,初始化站点信息失败", 'info');
        return;
    }
    var url = $("#contextPath").val() + "/autosorting/sortSchemeDetail/list/mixsite";
    CommonClient.syncPostJson(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
            return;
        }
        if (data.code == 200) {
            siteList = data.data;
        } else {
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    });
}

function initChuteCode() {
    var params = getSearchParams();
    if (params.siteNo == null || params.schemeId == null) {
        jQuery.messager.alert('提示:', "参数不全,初始化格口信息失败", 'info');
        return;
    }
    var url = $("#contextPath").val() + "/autosorting/sortSchemeDetail/list/chutecode";
    CommonClient.syncPostJson(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
            return;
        }
        if (data.code == 200) {
            chuteCodeList = data.data;

        } else {
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    });
}


function getSearchParams() {
    var params = {};
    params.schemeId = $.trim($("#schemeId").val()); //方案id
    params.siteNo = $.trim($("#siteNo").val()); //分拣中心
    params.schemeId = 1;
    params.siteNo = 480;
    return params;
}

//---------------模糊搜索--------end-----------


function onQueryBtnClick(pageNo) {
    var params = getParams();
    params.pageNo = pageNo;
    doQueryCrossSorting(params);
}

function getParams() {
    var params = {};
    params.schemeId = $.trim($("#schemeId").val()); //方案id
    params.siteNo = $.trim($("#siteNo").val()); //分拣中心
    params.boxSiteCode = $.trim($("#boxSiteCode").val());	//站点
    params.chuteCode1 = $.trim($("#chuteCode").val()); //物理滑槽
    //alert(params.siteNo + "" +params.boxSiteCode + "" +params.chuteCode1);
    return params;
}

// 查询请求
function doQueryCrossSorting(params) {
    var url = $("#contextPath").val() + "/autosorting/sortSchemeDetail/list";
    CommonClient.postJson(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            for (var i = 0; i < dataList.length; i++) {
                temp += "<tr class='a2' style=''>";

                temp += "<td>" + (dataList[i].chuteCode1) + "</td>";
                siteNo = dataList[i].siteNo;
                temp += "<td>" + (dataList[i].siteCode) + "</td>";
                temp += "<td>" + (dataList[i].boxSiteCode) + "</td>";
                temp += "<td>" + (dataList[i].pkgLabelName) + "</td>";
                temp += "<td>" + (dataList[i].currChuteCode) + "</td>";
                temp += "<td>" + (dataList[i].yn == 1 ? '激活' : '未激活') + "</td>";
                temp += "<td>" + (dataList[i].receFlag == 1 ? '接收' : '未接收') + "</td>";
                temp += "<td>" + (dataList[i].receTime) + "</td>";

                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);
            $("#pageNo").val(pager.pageNo); // 当前页码
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            siteNo = null;
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    });
}


