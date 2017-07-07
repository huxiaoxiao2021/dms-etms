var flashTime = 10000;//页面刷新时间
//发货组
var sendGroupSelect = $('#sendGroup');
//机器码
var sortMachineSelect = $("#sortMachine");

$(document).ready(function(){
    //初始化分拣机
    sortMachineInit();

    //查询按钮
    $('#queryBtn').click(function () {
        var currentSortMachineCode = $(this).val();
        //查询机器号下的滑道信息
        queryChuteBySortMachineCode(currentSortMachineCode);
        //初始化发货组
        sortMachineGroupInit(currentSortMachineCode);
        //查询异常信息
        queryExceptionNum();
    });
    //发货组改变时，加载关联的滑道
    $(sendGroupSelect).change(function () {
        var currentGroupId = $(this).val();
        querySendGroupConfig(currentGroupId);
    });

    /** 异常数据点击事件 **/
    $("#exceptionNum").click(function(){
        if($(sortMachineSelect).val() !=null && $(sortMachineSelect).val()!= ""){
            toGantryExceptionPage();
        }
    })
});
/**
 * 查询该分拣机下的发货组
 * @param groupId
 */
function querySendGroupConfig(groupId) {
    if(groupId){
        var param= {};
        param.groupId = groupId;
        var url = $("#contextPath").val() + "/services/sortMachineAutoSend/findSendGroupConfigByGroupId";
        CommonClient.postJson(url,param,function (data) {
            var sendGroupConfigs = data.data;
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
                return;
            }
            if ( data.code == 200) {
                loadSendGroupConfigs(sendGroupConfigs);
            }else if(data.code == 500) {
                jQuery.messager.alert("提示：",data.message,"error");
            }
        });
    }
}

/**
 * 初始化分拣机
 */
function sortMachineInit() {
    var temp = "<option value=''>请选择分拣机</option>";
    $("#sortMachine").html(temp);//清空分拣机信息
    var param= {};
    var url = $("#contextPath").val() + "/services/sortMachineAutoSend/findSortMachineByErp";
    CommonClient.postJson(url,param,function (data) {
        var machineCodes = data.data;
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if (machineCodes!= null && data.code == 200) {
            loadMachineCodes(machineCodes);
        }else{
            jQuery.messager.alert("提示：",data.message,"error");
        }
    });
}

/**
 * 初始化发货组
 * @param machineCode 分拣机编号
 */
function sortMachineGroupInit(machineCode) {

    if(machineCode){
        var param= {};
        param.machineCode = machineCode;
        var url = $("#contextPath").val() + "/services/sortMachineAutoSend/findSendGroupByMachineCode";
        CommonClient.postJson(url,param,function (data) {
            var sendGroups = data.data;
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
                return;
            }
            if ( data.code == 200) {
                loadSendGroups(sendGroups);
            }else if(data.code == 500) {
                jQuery.messager.alert("提示：",data.message,"error");
            }
        });

    }else {
        var option = '<option value="">无发货组</option>';
        $(sendGroupSelect).html(option);
    }
}
/**
 * 加载发货组
 * @param sendGroups
 */
function loadSendGroups(sendGroups) {
    if(sendGroups){
        var option = '<option value="">请选择发货组</option>';
        $.each(sendGroups, function (index, sendGroup) {
            option = option + "<option value='" + id + "'>" + groupName + "</option>";
        });
        $(sendGroupSelect).html(option);
    }else {
        $(sendGroupSelect).html('<option value="">无发货组</option> ');
    }


}
/**
 * 加载分拣机编号
 * @param machineCodes
 */
function loadMachineCodes(machineCodes) {

    var option = '<option value="">请选择分拣机</option>';
    $.each(machineCodes, function (index, machineCode) {
        option = option + "<option value='" + machineCode + "'>" + machineCode + "</option>";
    });
    $(sortMachineSelect).html(option);
}


/**
 * 加载发货分组关联的滑道
 * @param sendGroupConfigs
 */
function loadSendGroupConfigs(sendGroupConfigs) {
    //todo
    if(sendGroupConfigs){

    }
}

/**
 * 查询分拣机下分拣计划的滑道
 * @param currentSortMachineCode
 */
function queryChuteBySortMachineCode(currentSortMachineCode) {
    if(currentSortMachineCode){
        var param= {};
        param.machineCode = currentSortMachineCode;
        var url = $("#contextPath").val() + "/services/sortMachineAutoSend/queryChuteBySortMachineCode";
        CommonClient.postJson(url,param,function (data) {
            var chutes = data.data;
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
                return;
            }
            if ( data.code == 200) {
                loadChutes(chutes);
                //加载发货组

            }else {
                jQuery.messager.alert("提示：",data.message,"error");
            }
        });
    }else {
        jQuery.messager.alert("提示：","所选分拣机编号不存在！","error");
        return;
    }
}
/**
 * 加载滑道信息
 * @param chutes
 */
//todo
function loadChutes(chutes) {
    $("#pagerTable tbody").html("");
    if(chutes){
        $.each(chutes, function (index, chute) {

            var sortSchemeDetail = chute.sortSchemeDetail;
            var url = $("#contextPath").val() + "/gantryAutoSend/summaryBySendCode";
            var packageSum = 0.00;//总数量
            var volumeSum = 0.00;//总体积
            CommonClient.syncPost(url,{"sendCode":chute.sendCode},function (data) {
                if (data != undefined && data != null){
                    var sum = data.data;
                    if(sum.packageSum != null && sum.volumeSum != null){
                        packageSum = sum.packageSum;
                        volumeSum = sum.volumeSum;
                    }
                }
            });
            var tr = '';
            tr += '<tr>';
            tr += '<td><input type="checkbox" id="ckbox' + sortSchemeDetail.chuteCode + '"></td>';
            tr += '<td name="sendSiteCode">' + sortSchemeDetail.sendSiteCode + '</td>';
            tr += '<td name="sendSiteName">' + sortSchemeDetail.sendSiteName + '</td>';
            tr += '<td name="sendCode">' + chute.sendCode + '</td>';
            tr += '<td name="createTime">' + chute.createTime + '</td>';
            tr += '<td name="packageSum">' + packageSum+ '</td>';
            tr += '<td name="volumeSum">' + volumeSum + '</td>';
            tr += '</tr>';
            $("#pagerTable tbody").append(tr);
        });

    }
}


/**
 * 设置打印机弹出层事件
 */
function printSettingPopUp() {
    printerShow();
    popUp('printSettingPopUp',333,206);
}
function getPrintersCallBack(printerNames){
    var labelPrinterHtml = document.getElementById("labelPrinter");
    var listPrinterHtml = document.getElementById("listPrinter");
    var temp = "";
    for(var i=0;i<printerNames.length;i++){
        temp += "<option value='" + printerNames[i] + "'>" + (i+1) + "." + printerNames[i] + "</option>";
    }
    labelPrinterHtml.innerHTML = temp;
    listPrinterHtml.innerHTML = temp;
}
/**
 * 打印机列表展示
 */
function printerShow(){
    var labelPrinterValue = $("#labelPrinter option:selected").val();
    var listPrinterValue = $("#listPrinter option:selected").val();
    if(labelPrinterValue == null || listPrinterValue == null || labelPrinterValue == "" || listPrinterValue == ""){
        getPrinters(getPrintersCallBack);
    }
}


/**
 * 弹出层打印机设置保存
 */
function printSettingSave(){
    var labelPrinterValue = $("#labelPrinter option:selected").val();//标签打印机参数
    var listPrinterValue = $("#listPrinter option:selected").val();//清单打印机参数
    $.cookie(
        "labelPrinterValue",
        labelPrinterValue,
        {expires:1,path:"/"}//设置一天的保存时间
    );
    $.cookie(
        "listPrinterValue",
        listPrinterValue,
        {expires:1,path:"/"}//设置一天的保存时间
    );
    popClose('printSettingPopUp');//关闭弹出层
}

/** 设置打印机的保存点击事件 **/
$("#printSettingSaveBtn").click(function () {
    printSettingSave();
});

/** 打印并完结批次点击事件 **/
$("#printAndEndSend").click(function () {
    var machineId = $("#sortMachine :selected").val();
    if(machineId == undefined || machineId == "" || machineId == 0 ){
        return;
    }

    /** 第一步：读取需要打印的方式(逻辑与)：1.打批次号 2.打汇总单 3.both**/
    var type = 0;
    $("input[name=printStyle]:checked").each(function () {
        type |= $(this).val();
    });

    /** 读取cookie中设置的打印机的值 **/
    var labelPrinterValue = $.cookie("labelPrinterValue");
    var listPrinterValue = $.cookie("listPrinterValue");
    if(type&1 == 1 && labelPrinterValue == null){
        jQuery.messager.alert("提示","没有设置标签打印机，请前往设置","info");
        return;
    }
    if(type&2 == 2 && listPrinterValue == null){
        jQuery.messager.alert("提示","没有设置清单打印机，请前往设置","info");
        return;
    }
    var printerNames = {"labelPrinter":labelPrinterValue,"listPrinter":listPrinterValue};

    /** 第二步判断是否有选中单个进行打印并完结的事件 **/
    var list = [];
    list.push({"machineId":$("#sortMachine :selected").val(),
        "printType": type
        // ,
        // "createSiteCode":$("#siteOrg :selected").val()
    });
    $("input[name=item]:checked").each(function () {
        var param = {};
        param.machineId = $("#sortMachine :selected").val();
        // param.createSiteCode = $("#siteOrg :selected").val();
        param.receiveSiteCode = $(this).parents("tr").find("[name=sendSiteCode]").text();
        param.packageSum = $(this).parents("tr").find("[name=packageSum]").text();
        param.createTime = new Date($(this).parents("tr").find("[name=createTime]").text());
        list.push(param);
    });

    if(list.length == 1){
        if(!confirm("您将要打印并完结当前所有批次，是否继续？")){
            return;
        }
    }

    /** 第三步：打印，批次打印和汇总打印 **/
    printAndEndSendCodeBtn(list,printerNames);//打印事件

    /** 刷新当前页面 **/
    // var currentPage = $(".current").text();
    // queryBatchSendSub(currentPage);

});

/**
 * 打印并完结批次事件
 */
function printAndEndSendCodeBtn(param,printerNames){
    var width = 200;
    var height = 100;
    var url = $("#contextPath").val() + "/sortMachineAutoSend/sendEndAndPrint";
    CommonClient.syncPostJson(url,param,function (data) {
        if(data == undefined && data == null){
            jQuery.messager.alert("提示：","获取打印内容异常，请稍后再试","info");
            return;
        }
        var responseList = data.data;
        if(data.code==200){
            $.blockUI({ message:"<span class='pl20 icon-loading'>正在处理打印,请不要关闭页面...</span>"});
            for(var i = 0;i<responseList.length;i++){
                var printerName = "";
                var imageStr = responseList[i].sendCodeImgStr;
                if(responseList[i].printType == 1){
                    printerName = printerNames.labelPrinter;
                }else if(responseList[i].printType == 2){
                    printerName = printerNames.listPrinter;
                }
                printPic(printerName,imageStr,width,height);
            }
            $.unblockUI();
        }else if(data.code == 300){
            jQuery.messager.alert("警告：",data.message,"warning");
        }else{
            jQuery.messager.alert("警告：","打印完结批次失败","warning");
        }
    })
}


/** 换批次按钮点击事件 **/
$("#generateSendCodeBtn").click(function () {
    //得到勾选框的值
    var list = [];
    $("input[name=item]:checked").each(function () {
        var param = {};
        param.machineId = $("#sortMachine :selected").val();
        param.receiveSiteCode = $(this).parents("tr").find("[name=sendSiteCode]").text();
        param.sendCode = $(this).parents("tr").find("[name=sendCode]").text();
        param.createTime = new Date($(this).parents("tr").find("[name=createTime]").text());
        list.push(param);
    });
    if(list.length == 0){
        return;
    }
    generateSendCode(list);
});


/**
 * 换批次点击事件
 */
function generateSendCode(list) {
    var url = $("#contextPath").val() + "/sortMachineAutoSend/generateSendCode";
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
}

/** 定时刷新数据 **/
var flashT;
$("#flash").click(function () {
    if($(this).is(":checked")){
        // $(function() {
        flashT = setInterval("flashChuteInfo()",flashTime);
        // })
    }else{
        flashT = window.clearInterval(flashT);
    }
})

/**
 * 页面定时刷新函数(设置10秒刷新一次)
 */
function flashChuteInfo(){
    var machineId = $("#sortMachine option:selected").val();
    if(!machineId){
        return;
    }
    //模拟点击查询按钮
    document.getElementById("queryBtn").onclick();
}

/**
 * 获取发货异常数据总量
 */
function queryExceptionNum(){
    if(!$("#sortMachine :selected").val()){
        return;
    }
    var url = $("#contextPath").val() + "/gantryAutoSend/queryExceptionNum";
    var params = {};
    // if(gantryParams != undefined && gantryParams != null ){
    //     params.machineId = gantryParams.machineId;
    //     params.startTime = new Date(gantryParams.startTime);
    //     if(null != gantryParams.endTime){
    //         params.endTime = new Date(gantryParams.endTime);
    //     }else{
    //         params.endTime = new Date();
    //     }
    // }
    params.machineId = $("#sortMachine :selected").val();
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

/** 补打印按钮点击事件 **/
$("#replenishPrint").click(function () {
    toReplenishPrintPage();
})

/**
 * 点击补打印跳转到补打印界面
 */
//todo 参数
function toReplenishPrintPage(){
    var url = $("#contextPath").val() + "/GantryBatchSendReplenishPrint/index";
    var param = {};
    if(gantryParams == undefined || gantryParams == null || gantryParams.machineId == null){
        jQuery.messager.alert("提示：","请选择有效的补打信息","info");
        return;
    }
    location.href = url + "?machineId=" + gantryParams.machineId + "&createSiteCode=" + gantryParams.createSiteCode
        + "&createSiteName=" + encodeURIComponent(encodeURIComponent(gantryParams.createSiteName)) + "&startTime="
        + timeStampToDate(gantryParams.startTime) + "&endTime=" + timeStampToDate(DateUtil.formatDateTime(new Date()));
}

/**
 * 点击异常数据跳转到异常发货界面
 */
//todo ???
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