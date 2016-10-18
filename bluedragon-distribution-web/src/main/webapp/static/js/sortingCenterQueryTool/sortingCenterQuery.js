
/**
 * Created by wuzuxiang on 2016/10/14.
 */
$(document).ready(function(){
    $("#expTypeList").multiselect();

    siteOrgInit();

    $("#query").click(function(){
        doQuery();
    })
})

function doQuery(){
    var params = getQueryParams();
    goQuery(params);
}

function goQuery(params){
    var url = $("#contextPath").val() + "/sortingCenter/query";
    CommonClient.postJson(url,params,function(data){
        if(data.code == 200){
            var countNum = data.data;
            var temp = "<tr><td>" + countNum + "</td></tr>";
            $("#numTable tbody").append(temp);
        }else{
            $("#numTable tbody").append("<tr>查询数据失败</tr>");
            jQuery.messager.alert("提示：",data.message,'info');
        }
    })
}

function getQueryParams(){
    var params = {};
    params.siteCode = $("#siteNo").val();
    params.tableName = $("#tableName").val();
    params.startTime = $("#startTime").val();
    params.endTime = $("#endTime").val;
    params.exptypeList = [];

    $("#expTypeList:selected").each(function () {
        exptypeList.push(this.val());
    })
    return params;
}

/** 初始化所有分拣中心 **/
function siteOrgInit(){
    var url = " " + "/services/bases/dms";
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
