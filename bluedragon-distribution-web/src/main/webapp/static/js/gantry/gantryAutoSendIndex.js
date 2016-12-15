/**
 * Created by wuzuxiang on 2016/12/8.
 */
var startGantry = 1; //启用
var endGantry = 0; // 释放
var gantryParams = {};//保存龙门架的参数信息

$(document).ready(function(){

    /** 初始化分拣中心 **/
    dmsInit();

    /** 龙门架设备联动显示 **/
    $("#siteOrg").change(gantryDeviceItemShow);

    /** 龙门架配置信息联动显示 **/
    $("#gantryDevice").change(function(){
        gantryLockStatusShow();
        queryBatchSendSub(1)
    });

    /** 启用释放龙门架按钮点击事件 **/
    $("#enOrDisGantryBtn").click(getGantryParams);

    /** 设置打印机的保存点击事件 **/
    $("#printSettingSaveBtn").click(printSettingSave());

    /** 换批次按钮点击事件 **/
    $("#generateSendCodeBtn").click(generateSendCode());

})

/**
 * 加载分拣中心
 */
function dmsInit(){
    var url = $("#contextPath").val() + "/services/bases/dms";
    $.getJSON(url,function(data){
        var dmsList = data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
            return;
        }
        if (dmsList.length > 0 && dmsList[0].code == 200) {// 200:normal
            loadDmsList(dmsList, "siteOrg");
        } else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
            jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
        } else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
            jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
        } else {
            jQuery.messager.alert('提示:', "数据异常！", 'info');
        }
    })
}
function loadDmsList(dmsList, selectId) {
    var dmsObj = $('#' + selectId);
    $('#createDmsList').html("");
    var optionList = "";
    for (var i = 0; i < dmsList.length; i++) {
        optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
    }
    dmsObj.append(optionList);
    $("#paperTable tbody").html("");
}

/**
 * 分拣中心与龙门架设备联动
 */
function gantryDeviceItemShow(){
    var temp = "<option value=''>请选择龙门架<option/>";
    $("#gantryDevice").html(temp);//清空龙门架信息
    var siteNo = parseInt($("#siteOrg option:selected").val());//获取分拣中心ID
    var param= {};
    param.createSiteCode = siteNo;
    var url = $("#contextPath").val() + "/services/gantryDevice/findAllGantryDevice";
    CommonClient.postJson(url,param,function (data) {
        var gantryList = data.data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if (gantryList.length > 0 && data.code == 200) {
            loadGantryList(gantryList, "gantryDevice");
        } else if (gantryList.length <= 0 && data.code == 200){
            jQuery.messager.alert("提示：","该分拣中心没有可供选择的龙门架设备！","info");
        } else if (data.code == 500) {
            jQuery.messager.alert("提示：", "获取该分拣中心的龙门架设备失败!", "info");
        } else {
            jQuery.messager.alert("提示：", "服务器异常!", "info");
        }
    })
}
function loadGantryList(gantryList,selectId){
    var gantryObj = $('#' + selectId);
    gantryObj.html("");
    var optionList = "<option value=''>请选择龙门架</option>";
    for (var i = 0; i < gantryList.length; i++) {
        optionList += "<option value='" + gantryList[i].machineId + "'>" + gantryList[i].machineId + " " + gantryList[i].serialNumber + "</option>";
    }
    gantryObj.append(optionList);
    $("#paperTable tbody").html("");
}

/**
 * 龙门架设备与“配置龙门架功能”的状态联动
 */
function gantryLockStatusShow(){
    var machineId = parseInt($("#gantryDevice option:selected").val());//获取龙门架ID
    var param= {};
    param.machineId = machineId;
    var url = $("#contextPath").val() + "/services/gantryConfig/findMaxStartTimeGantryDeviceConfigByMachineId";
    CommonClient.postJson(url,param,function (data) {
        var gantryConfig = data.data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if (data.code == 200) {
            gantryStateInit(gantryConfig);
        } else if (data.code == 500) {
            jQuery.messager.alert("提示：", "获取该龙门架设备信息失败!", "info");
        } else {
            jQuery.messager.alert("提示：", "服务器异常!", "info");
        }
    })
}
/**
 * 初始化龙门架的锁定状态以及操作模式
 */
function gantryStateInit(gantryConfig) {
    gantryParams = gantryConfig;//储存龙门设备信息的全局变量
    var inspectionObj = $("#inspection");   //验货复选框
    var sendObj = $("#send");               //发货复选框
    var measureObj = $("#measure");         //量方复选框
    if(gantryConfig != null && gantryConfig.lockStatus != null && gantryConfig.businessType != null){
        var businessType = gantryConfig.businessType; //龙门架的操作类型 按位求于,1验货，2发货，4量方。5验货+量方。6发货+量方 添加3验货+发货
        var lockStatus = gantryConfig.lockStatus;     //锁定状态 0释放状态 1启用状态

        switch (businessType){
            case 1 :
                inspectionObj.prop("checked",true);
                sendObj.prop("checked",false);
                measureObj.prop("checked",false);
                break;
            case 2 :
                inspectionObj.prop("checked",false);
                sendObj.prop("checked",true);
                measureObj.prop("checked",false);
                break;
            case 3 :
                inspectionObj.prop("checked",true);
                sendObj.prop("checked",true);
                measureObj.prop("checked",false);
                break;
            case 4 :
                inspectionObj.prop("checked",false);
                sendObj.prop("checked",false);
                measureObj.prop("checked",true);
                break;
            case 5 :
                inspectionObj.prop("checked",true);
                sendObj.prop("checked",false);
                measureObj.prop("checked",true);
                break;
            case 6 :
                inspectionObj.prop("checked",false);
                sendObj.prop("checked",true);
                measureObj.prop("checked",true);
                break;
        }
        if (lockStatus == 0){
            inspectionObj.attr("disabled",false);
            sendObj.attr("disabled",false);
            measureObj.attr("disabled",false);
            $("#gantryBtn").html("<input  type='button' value='启用龙门架' class='btn_c' onclick='enOrDisGantry(getGantryParams(startGantry))'>");//启用龙门架需要传入的参数
        }else if (lockStatus == 1){
            //状态锁定 需要点击解锁
            inspectionObj.attr("disabled",true);
            sendObj.attr("disabled",true);
            measureObj.attr("disabled",true);
            $("#gantryBtn").html("<input  type='button' value='释放龙门架' class='btn_c' onclick='enOrDisGantry(getGantryParams(endGantry))'>");//释放龙门架只需要传入的机器编号参数？！
        }else{
            jQuery.messager.alert("提示","龙门架状态读取失败","info");
        }
    }else{
        jQuery.messager.alert("提示","龙门架配置信息错误！","info");
    }
}

/**
 * 龙门架释放、启动点击事件
 * @param params 龙门架参数
 */
function enOrDisGantry(params){
    if(params.lockStatus == 1){
        /** 启用校验 **/
        if(params.businessType == 4){
            jQuery.messager.alert("错误：","‘量方’不可单独使用！！！","error");
            return;
        }
    }else if(params.lockStatus == 0){
        /** 释放校验 **/
        if(params.operateUserErp != gantryParams.operateUserErp){
            jQuery.messager.alert("警告：","释放该龙门架请联系锁定人:" + gantryParams.operateUserName,"warning");
            return;
        }
    }
    var url = $("#contextPath").val() + "/gantryAutoSend/gantryStatus";
    CommonClient.postJson(url,params,function(data){
        if (data == undefined && data == null){
            jQuery.messager.alert("提示：","HTTP请求无返回数据！","info");
        }
        if (data.code == 200){
            gantryStateInit(data.data);
        }else{
            jQuery.messager.alert("提示：","数据请求失败！","info");
        }
    })
}

/**
 * 获取龙门架参数
 * @param lockStatus 锁定状态
 */
function getGantryParams(lockStatus){
    var params = {};
    params.createSiteCode = $("#siteOrg option:selected").val();//分拣中心ID
    params.machineId = $("#gantryDevice option:selected").val();//龙门架编号
    var businessType = 0;
    var operateTypeRemark = "";
    $("input[name='businessType']:checked").each(function(){
        businessType |= $(this).val();
    })
    switch (businessType){
        case 1: operateTypeRemark = "验货";break;
        case 2: operateTypeRemark = "发货";break;
        case 3: operateTypeRemark = "验货+发货";break;
        case 4: operateTypeRemark = "量方";break;
        case 5: operateTypeRemark = "验货+量方";break;
        case 6: operateTypeRemark = "发货+量方";break;
        case 7: operateTypeRemark = "验货+发货+量方";break;
        default: break;
    }
    params.businessType = businessType;//操作类型
    params.operateTypeRemark = operateTypeRemark;//操作类型文字描述
    var userMarker = $("#operator").val().split("||");//操作人姓名和erp用||分割
    params.operateUserName = userMarker[0];//获取操作人姓名
    params.operateUserErp = userMarker[1];//获取操作人的erp
    params.lockStatus = lockStatus == 1? 1:0; //1启用 0释放
    return params;
}

/**
 * 获取发货异常数据总量
 */
function queryExceptionNum(){
    var url = $("#contextPath").val() + "/gantryAutoSend/queryExceptionNum";
    CommonClient.postJson(url,gantryParams,function (data) {
        if(data.data == undefined || data.data == null){
            return;
        }
        if(data.code == 200){
            $("#exceptionNum").text(data.data);
        }
    })
}

/**
 * 查询分页数据
 */
function queryBatchSendSub(pageNo){
    var params = gantryParams;
    params.pageNo = pageNo;
    queryBatchSendCodes(params);
}
/**
 * 第一次异步获取：批次目的地、批次号、*、*、批次创建时间
 * 需要分页!
 * @param params
 */
function queryBatchSendCodes(params){
    var url = $("#contextPath").val() + "/";
    CommonClient.postJson(url,params,function (data) {
        if(data == undefined && data == null){
            jQuery.messager.alert("错误：","HTTP请求无返回数据！！","error");
            return;
        }
        if(data.code == 200 && data.data != null) {
            var list = data.data;
            var temp = "";
            for (var i = 0;i < list.length;i++) {
                temp += "<tr id='" + i + "'>";
                temp += "<td><input type='checkbox' name='item'></td>";
                temp += "<td title='" + list[i].receiveSiteCode + "'>" + list[i].receiveSiteName + "</td>";
                temp += "<td>" + list[i].sendCode + "</td>";
                temp += "<td>数量</td>";
                temp += "<td>总体积</td>";
                temp += "<td>" + list[i].createTime + "</td>";
                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);

        }else if(data.code == 200 && data.data ==null){
            jQuery.messager.alert("提示：","服务器请求成功，无数据返回!","info");
        }else if(data.code == 500){
            jQuery.messager.alert("提示：","服务器请求数据处理异常","info");
        }else{
            jQuery.messager.alert("提示：","服务器请求数据处理异常","info");
        }
    })
}

/**
 * 设置打印机弹出层事件
 */
function printSettingPopUp() {
    var printSettingParams = $.cookie("printSettingParams");
    $("#labelPrinter").get(0).selectedIndex = printSettingParams.labelPrinterValue;
    $("#listPrinter").get(0).selectedIndex = printSettingParams.listPrinterValue;
    popUp('printSettingPopUp',333,206);
}

/**
 * 弹出层打印机设置保存
 */
function printSettingSave(){
    var labelPrinterValue = $("#labelPrinter option:selected").val();//标签打印机参数
    var listPrinterValue = $("#listPrinter option:selected").val();//清单打印机参数
    var printSettingParams = {};//保存设置的打印机参数
    printSettingParams.labelPrinterValue = labelPrinterValue;
    printSettingParams.listPrinterValue = listPrinterValue;
    $.cookie(
        "printSettingParams",
        JSON.stringify(printSettingParams),
        {expires:1}
    )
}

/**
 * 换批次点击事件
 */
function generateSendCode() {
    var url = $("#contextPath").val() + "/gantryAutoSend/generateSendCode";
    $.ajax({
        url: url,
        data:gantryParams,
        type: "get",
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        async: false,
        beforeSend: function(jqXHR, settings){
            $.blockUI({ message:"<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
        },
        success: function (data) {
            if(data == undefined && data == null){
                jQuery.messager.alert("提示：","HTTP请求无返回数据！！","info");
                return;
            }
            if(data.code == 200){
                //fixme 刷新数据
                queryBatchSendSub(1);
            }else{
                jQuery.messager.alert("错误：","本次换批次失败！！","error");
                return;
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert("Error:status["+jqXHR.status+"],statusText["+ jqXHR.statusText +"]");
        },
        complete: function(jqXHR, textStatus){
            $.unblockUI();
        }
    })
}

