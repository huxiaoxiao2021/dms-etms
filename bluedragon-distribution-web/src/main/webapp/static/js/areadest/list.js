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

    // 加载分拣中心对应龙门架
    initGantryDevice(parseInt($("#currentSiteCode").val()));//获取当前分拣中心编号
}

function funReset() {
    $("#machineId").val('');
}

// 加载分拣中心列表
function initGantryDevice(currentSiteCode) {
    var param= {};
    param.createSiteCode = currentSiteCode;
    param.version = 1; //表示只读取新的龙门架设备
    if(currentSiteCode == null || currentSiteCode == ""|| isNaN(currentSiteCode)){
        return;
    }
    var url = $("#contextPath").val() + "/services/gantryDevice/findAllGantryDevice";
    CommonClient.postJson(url,param,function (data) {
        var gantryList = data.data;
        if (gantryList == undefined || gantryList == null) {
            alert("提示：HTTP请求无返回数据！");
            return;
        }
        if (gantryList.length > 0 && data.code == 200) {
            loadGantryList(gantryList, "machineId");
        } else if (gantryList.length <= 0 && data.code == 200){
            alert("提示：该分拣中心没有可供选择的龙门架设备！");
        } else if (data.code == 500) {
            alert("提示：获取该分拣中心的龙门架设备失败!");
        } else {
            alert("提示：服务器异常!");
        }
    });
}

function loadGantryList(gantryList, selectId){
    var gantryObj = $('#' + selectId);
    var optionList = "<option value= ''>选择龙门架</option>";
    for (var i = 0; i < gantryList.length; i++) {
        optionList += "<option value='" + gantryList[i].machineId + "'>" + gantryList[i].machineId + "</option>";
    }
    gantryObj.append(optionList);
}

function onQueryBtnClick() {
    var params = getParams();
    if (!checkParams(params)) {
        return false;
    }
    doQuery(params);
}

function getParams() {
    var params = {};
    params.operateSiteCode = $.trim($("#currentSiteCode").val());
    params.machineId = $.trim($("#machineId").val());
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
    return true;
}

// 查询请求
function doQuery(params) {
    var url = $("#contextPath").val() + "/areaDestPlan/getList";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("未配置方案");
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            for (var i = 0; i < dataList.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (dataList[i].machineId) + "</td>";
                temp += "<td>" + (dataList[i].planName) + "</td>";
                temp += "<td>"
                    + ("<a href='javascript:void(0)' onclick='doQueryDetail(" + dataList[i].machineId + ")''>查看</a>"
                    + "<a href='javascript:void(0)' onclick='doConfig(" + dataList[i].machineId + ")' id='config'>配置</a>"
                    + "<a href='javascript:void(0)' onclick='doDelete(" + dataList[i].machineId + ")' id='delete'>删除</a>")
                    + "</td>";
                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            alert(data.message);
        }
    });
}

function goAddBtnClick() {
    var contextPath = $("#contextPath").val();
    location.href = contextPath + "/areaDestPlan/addView";
}