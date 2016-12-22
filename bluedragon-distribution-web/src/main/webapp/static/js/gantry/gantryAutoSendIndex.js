/**
 * Created by wuzuxiang on 2016/12/8.
 */
var startGantry = 1; //启用
var endGantry = 0; // 释放
var gantryParams = {};//保存龙门架的参数信息

$(document).ready(function(){

    /** 初始化分拣中心 **/
    // dmsInit();

    /** 龙门架设备联动显示 **/
    // $("#siteOrg").change(function () {
    //     clearInfo();
    //     gantryDeviceItemShow();
    // });

    /** 初始化龙门架设备下拉列表 **/
    gantryDeviceItemShow();

    /** 龙门架配置信息联动显示 **/
    $("#gantryDevice").change(function(){
        clearInfo();
        if($(this).val() == null || $(this).val() == ""){
            return;
        }
        gantryLockStatusShow();
        queryExceptionNum();
        queryBatchSendSub(1)
    });

    /** 设置打印机的保存点击事件 **/
    $("#printSettingSaveBtn").click(function () {
        printSettingSave();
    });

    /** 换批次按钮点击事件 **/
    $("#generateSendCodeBtn").click(function () {
        //得到勾选框的值
        var list = [];
        $("input[name=item]:checked").each(function () {
            var param = {}
            param.machineId = gantryParams.machineId;
            param.receiveSiteCode = $(this).parents("tr").find("[name=receiveSite]").attr("title");
            param.sendCode = $(this).parents("tr").find("[name=sendCode]").text();
            param.createTime = new Date($(this).parents("tr").find("[name=createTime]").text());
            list.push(param);
        });
        if(list.length == 0){
            return;
        }
        generateSendCode(list);
    });

    /** 补打印按钮点击事件 **/
    $("#replenishPrint").click(function () {
        toReplenishPrintPage();
    })

    /** 异常数据点击事件 **/
    $("#exceptionNum").click(function(){
        toGantryExceptionPage();
    })

    /**
     * 全选/取消
     */
    $("#all").click(function () {
        if($(this).prop("checked")){
            $("input[name=item]").each(function(){
                $(this).prop("checked",true);
            })
        }else{
            $("input[name=item]:checked").each(function(){
                $(this).prop("checked",false);
            })
        }
    })

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
    var temp = "<option value=''>选择龙门架</option>";
    $("#gantryDevice").html(temp);//清空龙门架信息
    var siteNo = parseInt($("#siteOrg option:selected").val());//获取分拣中心ID
    var param= {};
    param.createSiteCode = siteNo;
    // if(siteNo == null || siteNo == ""){
    //     loadGantryList("", "gantryDevice");
    //     return;
    // }
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
    var optionList = "<option value=''>选择龙门架</option>";
    for (var i = 0; i < gantryList.length; i++) {
        optionList += "<option value='" + gantryList[i].machineId + "'>" + gantryList[i].machineId + "</option>";
    }
    gantryObj.html(optionList);
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
    $.ajax({
        url: url,
        data: JSON.stringify(param),
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        async: false,//同步获取ajax数据
        beforeSend: function(jqXHR, settings){
            $.blockUI({ message:"<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
        },
        success: function (data) {
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
                return;
            }
            var gantryConfig = data.data;
            gantryParams = gantryConfig;//储存龙门设备信息的全局变量
            if (data.code == 200) {
                gantryStateInit(gantryConfig);
            } else if (data.code == 500) {
                jQuery.messager.alert("提示：", "获取该龙门架设备信息失败!", "info");
            } else {
                jQuery.messager.alert("提示：", "服务器异常!", "info");
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert("Error:status["+jqXHR.status+"],statusText["+ jqXHR.statusText +"]");
        },
        complete: function(jqXHR, textStatus){
            $.unblockUI();
        }
    })

    // CommonClient.postJson(url,param,function (data) {
    //     if (data == undefined || data == null) {
    //         jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
    //         return;
    //     }
    //     var gantryConfig = data.data;
    //     gantryParams = gantryConfig;//储存龙门设备信息的全局变量
    //     if (data.code == 200) {
    //         gantryStateInit(gantryConfig);
    //     } else if (data.code == 500) {
    //         jQuery.messager.alert("提示：", "获取该龙门架设备信息失败!", "info");
    //     } else {
    //         jQuery.messager.alert("提示：", "服务器异常!", "info");
    //     }
    // })
}
/**
 * 初始化龙门架的锁定状态以及操作模式
 */
function gantryStateInit(gantryConfig) {
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
        }
        // else{
        //     jQuery.messager.alert("提示","龙门架状态读取失败","info");
        // }
    }
    // else{
    //     jQuery.messager.alert("提示","龙门架配置信息错误！","info");
    // }
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
        }else if(params.businessType == 3 || params.businessType == 7){
            jQuery.messager.alert("错误：","‘发货’、‘验货’不可同时使用！！！","error");
            return;
        }
    }else if(params.lockStatus == 0){
        /** 释放校验 **/
        if(params.operateUserErp != gantryParams.lockUserErp){
            jQuery.messager.alert("警告：","释放该龙门架请联系锁定人:" + gantryParams.lockUserName,"warning");
            return;
        }else{
            /** 释放龙门架时清空异常数据 **/
            $("#exceptionNum").text(0);
        }
    }
    var url = $("#contextPath").val() + "/gantryAutoSend/updateOrInsertGantryDeviceStatus";
    CommonClient.post(url,params,function(data){
        if (data == undefined && data == null){
            jQuery.messager.alert("提示：","HTTP请求无返回数据！","info");
        }
        if (data.code == 200){
            gantryStateInit(data.data);
            gantryParams = data.data;
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
    var params = {};
    if(gantryParams != undefined && gantryParams != null ){
        params.machineId = gantryParams.machineId;
        params.startTime = new Date(gantryParams.startTime);
        params.endTime = new Date(gantryParams.endTime);
    }
    CommonClient.post(url,params,function (data) {
        if(data.data == undefined || data.data == null){
            jQuery.messager.alert("提示：","HTTP请求无数据返回!!","info")
        }
        if(data.code == 200){
            $("#exceptionNum").text(data.data);
        }
        // else{
        //     jQuery.messager.alert("提示","获取龙门架自动发货异常数据失败!!","info");
        // }
    })
}

/**
 * 查询分页数据
 */
function queryBatchSendSub(pageNo){
    var params = {};
    if(gantryParams != undefined && gantryParams != null ){
        params.machineId = gantryParams.machineId;
        // params.startTime = new Date(gantryParams.startTime);
        // params.endTime = new Date(gantryParams.endTime);
    }
    params.pageNo = pageNo;
    queryBatchSendCodes(params);
}
/**
 * 第一次异步获取：批次目的地、批次号、*、*、批次创建时间
 * 需要分页!
 * @param params
 */
function queryBatchSendCodes(params){
    var url = $("#contextPath").val() + "/gantryAutoSend/pageList";
    var url2 = $("#contextPath").val() + "/gantryAutoSend/summaryBySendCode";
    CommonClient.post(url,params,function (data) {
        if(data == undefined && data == null){
            jQuery.messager.alert("错误：","HTTP请求无返回数据！！","error");
        }
        if(data.code == 200 && data.data != null) {
            var page = data.data;
            var list = page.data;
            var temp = "";
            for (var i = 0;i < list.length;i++) {
                var packageSum = 0.00;//总数量
                var volumeSum = 0.00;//总体积
                CommonClient.syncPost(url2,{"sendCode":list[i].sendCode},function (data) {
                    if (data != undefined && data != null){
                        var sum = data.data;
                        if(sum.packageSum != null && sum.volumeSum != null){
                            packageSum = sum.packageSum;
                            volumeSum = sum.volumeSum;
                        }
                    }
                })
                temp += "<tr id='" + (i+1) + "'>";
                temp += "<td><input type='checkbox' name='item'></td>";
                temp += "<td name='receiveSite' title='" + list[i].receiveSiteCode + "'>" + list[i].receiveSiteName + "</td>";
                temp += "<td name='sendCode'>" + list[i].sendCode + "</td>";
                temp += "<td>"+packageSum+"</td>";
                temp += "<td>"+volumeSum+"</td>";
                temp += "<td name='createTime'>" + timeStampToDate(list[i].createTime) + "</td>";
                temp += "</tr>";
            }
            $("#pagerTable tbody").html(temp);
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("queryBatchSendSub", page.totalSize, page.pageNo, page.totalNo));

        }else if(data.code == 200 && data.data ==null){
            jQuery.messager.alert("提示：","服务器请求成功，无数据返回!","info");
        }
        // else if(data.code == 500){
        //     jQuery.messager.alert("提示：","服务器请求数据处理异常","info");
        // }
        // else{
        //     jQuery.messager.alert("提示：","服务器请求数据处理异常","info");
        // }
    })
}

/**
 * 设置打印机弹出层事件
 */
function printSettingPopUp() {
    // var printSettingParams = $.cookie.get("printSettingParams");
    // $("#labelPrinter").get(0).selectedIndex = printSettingParams.labelPrinterValue;
    // $("#listPrinter").get(0).selectedIndex = printSettingParams.listPrinterValue;
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
    // $.cookie(
    //     "printSettingParams",
    //     JSON.stringify(printSettingParams),
    //     {expires:1}
    // )
}

/**
 * 换批次点击事件
 */
function generateSendCode(list) {
    var url = $("#contextPath").val() + "/gantryAutoSend/generateSendCode";
    CommonClient.postJson(url,list,function (data) {
        if(data == undefined && data == null){
            jQuery.messager.alert("提示：","HTTP请求无返回数据！！","info");
            return;
        }
        if(data.code == 200){
            queryBatchSendSub(1);
        }else{
            jQuery.messager.alert("错误：","本次换批次失败！！","error");
            return;
        }
    })

    // $.ajax({
    //     url: url,
    //     data:JSON.stringify(list),
    //     type: "post",
    //     dataType: "json",
    //     contentType: "application/json; charset=utf-8",
    //     async: false,
    //     beforeSend: function(jqXHR, settings){
    //         $.blockUI({ message:"<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
    //     },
    //     success: function (data) {
    //         if(data == undefined && data == null){
    //             jQuery.messager.alert("提示：","HTTP请求无返回数据！！","info");
    //             return;
    //         }
    //         if(data.code == 200){
    //             queryBatchSendSub(1);
    //         }else{
    //             jQuery.messager.alert("错误：","本次换批次失败！！","error");
    //             return;
    //         }
    //     },
    //     error: function(jqXHR, textStatus, errorThrown){
    //         alert("Error:status["+jqXHR.status+"],statusText["+ jqXHR.statusText +"]");
    //     },
    //     complete: function(jqXHR, textStatus){
    //         $.unblockUI();
    //     }
    // })
}

/**
 * 联动效果前清空信息
 */
function clearInfo(){
    $("input[name='businessType']").each(function () {
        $(this).prop("checked",false);//清空龙门架的配置信息
    });
    gantryParams = {};//清空龙门架参数信息
    $("#pagerTable tbody").html("");//清空列表
    $("#pager").html("");//清空分页信息
}

/**
 * 点击补打印跳转到补打印界面
 */
function toReplenishPrintPage(){
    var url = $("#contextPath").val() + "/GantryBatchSendReplenishPrint/index";
    var param = {};
    if(gantryParams == undefined || gantryParams == null || gantryParams.machineId == null){
        jQuery.messager.alert("提示：","请选择有效的补打信息","info");
        return;
    }
    location.href = url + "?machineId=" + gantryParams.machineId + "&createSiteCode=" + gantryParams.createSiteCode
        + "&createSiteName=" + encodeURIComponent(encodeURIComponent(gantryParams.createSiteName)) + "&startTime="
        + timeStampToDate(gantryParams.startTime) + "&endTime=" + timeStampToDate(gantryParams.endTime);
}

/**
 * 点击异常数据跳转到异常发货界面
 */
function toGantryExceptionPage(){
    var url = $("#contextPath").val() + "/gantryException/gantryExceptionList";
    if(gantryParams == undefined || gantryParams == null || gantryParams.machineId == null || gantryParams.machineId == 0){
        var machineId = $("#gantryDevice option:selected").val();
        var siteCode = $("#siteOrg option:selected").val();
        if(machineId != "" || machineId != 0){
            url += "?machineId=" + gantryParams.machineId;
        }
        if(siteCode != "" || siteCode != 0){
            url += "&siteCode=" + siteCode;
        }
        location.href = url;
        return;
    }
    location.href = url + "?machineId=" + gantryParams.machineId + "&siteCode=" + gantryParams.createSiteCode
        + "&startTime=" + timeStampToDate(gantryParams.startTime) + "&endTime=" + timeStampToDate(gantryParams.endTime);
}

/**
 * 将时间戳转换为时间对象 如2016-12-19 20:13:03
 * @param ts
 */
function timeStampToDate(ts){
    var date = new Date(ts);
    var Y = date.getFullYear() + "-";
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + "-";
    var D = date.getDate() < 10? "0"+date.getDate() : date.getDate();
    var h = date.getHours() + ":";
    var m = date.getMinutes() + ":";
    var s = date.getSeconds();
    var timeStr = Y + M + D + " " + h + m + s;
    return timeStr;
}

