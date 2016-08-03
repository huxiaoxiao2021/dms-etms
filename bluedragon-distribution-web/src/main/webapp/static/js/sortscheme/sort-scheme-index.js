//初始化
$(document).ready(main);

// 全局变量
var taskTableList = null;

// 当前分拣中心
var siteNo = null;

function main() {

    // 查询按钮提交处理
    $('#queryBtn').click(function () {
        onQueryBtnClick(1);
    });

    // 添加按钮
    $('#addBtn').click(function () {
        goAddBtnClick();
    });

    // 激活按钮
    $("#activeBtn").click(function () {
        goActiveBtnClick();
    });

    // 下载分拣计划模版
    $("#downBtn").click(function () {
        goDownSortSchemeModel();
    });

    $("#siteNo").change(function () {
        clearPager();
    });

    // 加载所有的分拣中心
    initDms();
}

function goAddBtnClick() {
    location.href = $("#contextPath").val() + "/autosorting/sortScheme/goAdd";
}

function goDetailClick(id) {
    location.href = $("#contextPath").val() + "/autosorting/sortScheme/goDetail?id=" + id + "&siteNo=" + siteNo;
}

function goDownSortSchemeModel() {
    location.href = "http://sq.jd.com/dXSzs3";
}

//--------------激活方案-----------------

function goActiveBtnClick() {
    var singleBtns = $("input[name='singleBtn']:checked");
    if (singleBtns == null || singleBtns.length < 1) {
        jQuery.messager.alert('提示:', "至少选择 1 条数据!", 'info');
        return;
    }
    if (singleBtns.length > 1) {
        jQuery.messager.alert('提示:', "最多选择 1 条数据!", 'info');
        return;
    }
    // 校验状态:如果已经激活,则提示不用激活
    if (singleBtns[0].value == 1) {
        jQuery.messager.alert('提示:', "当前分拣计划已经激活!", 'info');
        return;
    }
    if (confirm("确定要激活此分拣计划 ?")) {
        var url = $("#contextPath").val() + "/autosorting/sortScheme/update/able/id";
        var params = {};
        params.id = singleBtns[0].id;
        params.siteNo = $.trim($("#siteNo").val());
        CommonClient.postJson(url, params, function (data) {
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
                return;
            }
            if (data.code == 200) {
                jQuery.messager.alert('提示:', "当前分拣计划激活成功", 'info');
                // 对当前页做一次分页查询
                onQueryBtnClick($("#pageNo").val());
            } else {
                jQuery.messager.alert('提示:', data.message, 'info');
            }
        });
    }
}

function goImportExcel(id) {
    $("#dialog").dialog();
    var html = '';
    html += '<div class="div_btn" style="float:left;margin-left: 10px; margin-top: 20px;">';
    html += '<div style="width: 200px;float: right;">';
    html += '<input id="loadInBtn" value="导入" style="margin-left:50px;" type="button" onclick="importExcel(' + id + ')" class="btn_c"></input>';
    html += '</div>';
    html += '<form action="" method="post" id="importFileForm" name="importFileForm" style="float:left;width:200px;">';
    html += '<input type="file" id="importFileIpt" name="importExcelFile" style="height: 28px;display: block;margin-top:5px;"/>';
    html += '</form>';
    html += '</div>';
    $("#dialog").html(html);
}

function importExcel(id) {
    var url = $("#contextPath").val() + "/autosorting/sortScheme/import/?id=" + id + "&siteNo=" + siteNo;
    $.blockUI({message: "<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
    $("#importFileForm").ajaxSubmit({
        url: url, // 请求的url
        data: $("#importFileIpt").serialize(),// 请求参数
        type: "post", // 请求方式
        dataType: "json", // 响应的数据类型
        async: true, // 异步
        success: function (data) {
            $.unblockUI();
            var jsonData = eval(data);
            if (200 == jsonData.code) {
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

function exportExcel(id) {
    var url = $("#contextPath").val() + "/autosorting/sortScheme/export?id=" + id + "&siteNo=" + siteNo;
    window.open(url, "_parent");
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
    $("#paperTable tbody").html("");
}

function onQueryBtnClick(pageNo) {
    var params = getParams();
    params.pageNo = pageNo;
    if (!checkParams(params)) {
        jQuery.messager.alert('提示:', '分拣中心不能为空!', 'info');
        return false;
    }
    doQueryCrossSorting(params);
}

function checkParams(params) {
    if (null == params) {
        return false;
    }
    if (params.siteNo == null || params.siteNo == "") {
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

// 查询请求
function doQueryCrossSorting(params) {
    var url = $("#contextPath").val() + "/autosorting/sortScheme/list";
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
                temp += "<td><input id='" + dataList[i].id + "' value='" + dataList[i].yn + "' name='singleBtn' type='checkbox'/></td>";
                temp += "<td>" + (dataList[i].name) + "</td>";
                siteNo = dataList[i].siteNo;
                temp += "<td>" + (dataList[i].siteName) + "</td>";
                temp += "<td>" + (dataList[i].machineCode) + "</td>";
                var sortMode = dataList[i].sortMode;
                if (sortMode == 1) {
                    temp += "<td>最近</td>";
                } else if (sortMode == 2) {
                    temp += "<td>瀑布</td>";
                } else {
                    temp += "<td>循环</td>";
                }
                temp += "<td>" + (dataList[i].receFlag == 1 ? '接收' : '未接收') + "</td>";
                temp += "<td>" + (dataList[i].receTime == null ? '' : dataList[i].receTime) + "</td>";
                temp += "<td>" + (dataList[i].yn == 1 ? '<font color="red">激活</font>' : '未激活') + "</td>";
                temp += "<td>" + "<input type='button' value='批量导入' onclick='goImportExcel(" + dataList[i].id + ")' style='margin-right:10px;'>"
                    + "<input type='button' value='批量导出' onclick='exportExcel(" + dataList[i].id + ")' style='margin-right:10px;'>"
                    + "<input type='button' value='删除' onclick='sortSchemeDelete(" + dataList[i].id + ")' style='margin-right:10px;'>"
                    + "<input type='button' value='查看明细' onclick='goDetailClick(" + dataList[i].id + ")'>" + "</td>";
                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);
            $("#pageNo").val(pager.pageNo); // 当前页码
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            siteNo = null;
            $("#paperTable tbody").html("");
            $("#pager").html("");
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    });
}

function clearPager() {
    $("#paperTable tbody").html("");
}

// 将任务的状态和执行次数重置
function sortSchemeDelete(id) {
    if (confirm("确定要删除此分拣计划 ?")) {
        var params = {};
        params.id = $.trim(id);
        params.siteNo = siteNo;
        doSortSchemeDelete(params);
    }
}

function doSortSchemeDelete(params) {
    var url = $("#contextPath").val() + "/autosorting/sortScheme/delete/id";
    CommonClient.postJson(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
            return;
        }
        if (data.code == 200) {
            //对当前页面做重新查询
            onQueryBtnClick($.trim($("#pageNo").val()));
        } else {
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    });
}

