
/**
 * Created by wuzuxiang on 2016/10/14.
 */
$(document).ready(function(){
    $("#expTypeList").niceForm({
        eventType:'click',  //默认为单击事件
        change:function(){} //下拉菜单发生change事件时触发的回调函数
});

    /** 初始化分拣中心 **/
    siteOrgInit();

    /** EXP_TYPE控件隐藏与显示 **/
    // $("#tableName").click(function(){
    //     if($(this).val()=="scan_lists"){
    //         $("#type").visibility = "visible";
    //     }
    // })

    $("#query").click(function(){
        if(formCheck()){
            doQuery();
        }else{
            return;
        }
    })
})

function doQuery(){
    var params = getQueryParams();
    goQuery(params);
}

function goQuery(params){
    var url = $("#contextPath").val() + "/sortingCenter/query";
    CommonClient.postJson(url, params, function (data){
        if(data.code == 200){
            var countNum = data.data;
            var temp = "<tr title='select count(*) from " + params.tableName
                + " where create_time &gt; " + params.startTime + " AND create_time &lt; " + params.endTime + "'><td>" + countNum + "</td></tr>";
            $("#numTable tbody").html(temp);
        }else{
            $("#numTable tbody").html("<tr><td>查询数据失败</td></tr>");
            jQuery.messager.alert("提示：",data.message,'info');
        }
    })
}

function getQueryParams(){
    var params = {};
    var expTypeList = [];
    params.siteNo = $("#siteNo").val();
    params.tableName = $("#tableName").val();
    params.startTime = $("#startTime").val();
    params.endTime = $("#endTime").val();
    $("#expTypeList option:selected").each(function () {
        expTypeList.push($(this).val());
    })
    params.expTypeList = expTypeList;
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

/** 检查表单的有效性 **/
function formCheck(){
    var siteNo = $("#siteNo").val(); /** 获取分拣中心ID **/
    var tableName = $("#tableName").val(); /** 获取表名 **/
    var startTime = $("#startTime").val(); /** 获取开始时间 **/
    var endTime = $("#endTime").val(); /** 获取结束时间 **/

    if("" == siteNo){
        alert("请选择分拣中心!!");
        return false;
    }else if("" == tableName){
        alert("请选择表名!!");
        return false;
    }else if("" == startTime && "" == endTime){
        alert("请至少选择一个时间!!");
        return false;
    }
    return true;
}

/** 将时间字符串转化为时间对象 **/
function stringTimeToDate(str){
    var time = str.toString().replace(/-/g,"/");
    var date = new Date(time);
    return date;
}
