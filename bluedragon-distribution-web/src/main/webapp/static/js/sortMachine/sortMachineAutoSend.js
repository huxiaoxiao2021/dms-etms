var flashTime = 10000;//页面刷新时间

$(document).ready(function(){
    //初始化分拣机
    sortMachineInit();

    //查询按钮
    $('#queryBtn').click(function () {
        query();
    });
    //发货组改变时，加载关联的滑道
    $('#sendGroup').change(function () {
        var currentGroupId = $(this).val();
        $("tbody[id='chuteTb'] input[type='checkbox']").prop("checked",false);
        querySendGroupConfig(currentGroupId);
    });

    /** 异常数据点击事件 **/
    $("#exceptionNum").click(function(){
        if($("#sortMachine").val() !=null && $("#sortMachine").val()!= ""){
            toGantryExceptionPage();
        }
    });

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

    /**点击添加分组*/
    $("#newGroupbtn").click(function () {
        var sortMachineCode = $("#sortMachine").val();
        if(!sortMachineCode){
            jQuery.messager.alert("提示","请选择分拣机编号","error");
            return;
        }
        var checkedGroup = $("tbody[id='chuteTb'] input[type='checkbox']:checked");
        if(!checkedGroup.size()){
            jQuery.messager.alert("提示","请选择目的地记录！","error");
            return;
        }
        //清空内容
        $("#sendGroupName").val("");
        popUp('addSendGroupDiv',333,206);
    });
    /** 点击保存分组**/
    $("#addGroupSaveBtn").click(function () {
        var sendGroupName = $("#sendGroupName").val();
        var sortMachineCode = $("#sortMachine").val();
        if(!sortMachineCode){
            jQuery.messager.alert("提示","请选择分拣机编号","error");
            return;
        }
        if(!sendGroupName ){
            jQuery.messager.alert("提示","请填写组名","error");
            return;
        }
        var chuteCodes = getChuteCodes();
        if(chuteCodes == null || chuteCodes.length == 0){
            if(!sendGroupName ){
                jQuery.messager.alert("提示","请选择滑槽","error");
                return;
            }
        }
        addSendGroup(sendGroupName, sortMachineCode, chuteCodes);
    });

    /** 点击取消添加分组**/
    $("#addGroupCancelBtn").click(function () {
        popClose('addSendGroupDiv');//关闭弹出层
    });

    /**
     * 点击修改发货组
     */
    $("#modifyGroupbtn").click(function () {
        var sortMachineCode = $("#sortMachine").val();
        if(!sortMachineCode){
            jQuery.messager.alert("提示","请选择分拣机编号","error");
            return;
        }
        var currentSendGroup = $('#sendGroup').val();
        if(!currentSendGroup){
            if(!sortMachineCode){
                jQuery.messager.alert("提示","请选择发货组","error");
                return;
            }
        }
        var chuteCodes = getChuteCodes();
        if(chuteCodes == null || chuteCodes.length == 0){
            jQuery.messager.alert("提示","请选择目的地记录","error");
            return;
        }
        updateSendGroup(currentSendGroup, sortMachineCode, chuteCodes);
    });

    /**
     * 点击删除发货组
     */
    $("#delGroupbtn").click(function () {
        var currentSendGroup = $('#sendGroup').val();
        if(!currentSendGroup){
            jQuery.messager.alert("提示","请选择发货组","error");
            return;
        }
        if(!confirm("确定要删除该组么？")){
            return;
        }
        deleteSendGroup(currentSendGroup);
    });

    /**
     * 滑槽信息全选checkbox
     */
    $("#all").click(function(){
        $("tbody[id='chuteTb'] input[type='checkbox']").prop("checked", $("#all").prop("checked"));
    });


    /** 分拣机批次打印 **/
    $("#printBatch").click(function () {

        /** 获取要打印的批次数据 **/
        var list = [];
        $.each($("tbody[id='chuteTb'] input[type='checkbox']"), function(){
            if($(this).prop('checked')){
                var param = {};
                param.receiveSiteName = $(this).parents("tr").find("[name=sendSiteName]").text();
                param.sendCode = $(this).parents("tr").find("[name=sendCode]").text();
                param.packageSum = $(this).parents("tr").find("[name=packageSum]").text();
                list.push(param);
            }
        });
        if (list.length < 1) {
            jQuery.messager.alert("提示", "请选择要打印的批次");
        }

        /** 调用打印批次组件 **/
        labelPrint(list);

    });

    /** 分拣机批次打印调用组件*/
    labelPrint = function(list){

        for(var i=0;i<list.length;i++){

            var param=list[i];
            var labelPrintRequst = new Object();
            labelPrintRequst['systemCode'] = 'dms';
            labelPrintRequst['businessType'] = 'dms-sendBarcode';
            labelPrintRequst['siteCode'] =$("#createSiteCode").val();
            labelPrintRequst['siteName'] = $("#createSiteName").val();

            var labelParams=new Object();
            labelParams.SendCode=param.sendCode;
            labelParams.receiveSiteName=param.receiveSiteName;
            labelParams.createSiteName=param.createSiteName;
            labelParams.SumNum=param.packageSum;

            labelPrintRequst['labelParams']=labelParams;

            var formJson = JSON.stringify(labelPrintRequst);
            var labelPrintUrl = 'http://localhost:9099/services/label/print';
            /*提交表单*/
            CommonClient.asyncPost(labelPrintUrl,formJson,function (res) {
                if(res != null && res.status=== 200){
                    var result=$.parseJSON(res.responseText);
                    if (result.code===200) {
                        console.log("调用结果", "调用打印成功");
                    }else{
                        jQuery.messager.alert("提示", "请求发送成功但是调用打印组件失败", res.statusText.message);
                    }
                }else {
                    jQuery.messager.alert("提示", "服务器异常", res.statusText);
                }
            });
        }

    };
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
        $("tbody[id='chuteTb'] input[type='checkbox']:checked").each(function () {
            var param = {};
            param.machineId = $("#sortMachine").val();
            param.createSiteCode = $("#createSiteCode").val();
            param.receiveSiteCode = $(this).parents("tr").find("[name=sendSiteCode]").text();
            param.packageSum = $(this).parents("tr").find("[name=packageSum]").text();
            param.createTime = new Date($(this).parents("tr").find("[name=createTime]").text());
            //检查参数
            if(!checkGenerateSendCodeParam(param)){
                return false;
            }
            list.push(param);
        });

        if(list.length == 1){
            jQuery.messager.alert("提示","请选择要结束的目的地","info");
            return;
        }

        /** 第三步：打印，批次打印和汇总打印 **/
        printAndEndSendCodeBtn(list,printerNames);//打印事件

        /** 刷新当前页面 **/
        query();

    });

    /** 换批次按钮点击事件 **/
    $("#generateSendCodeBtn").click(function () {
        //得到勾选框的值
        var list = [];
        $("tbody[id='chuteTb'] input[type='checkbox']:checked").each(function () {
            var param = {};
            param.machineId = $("#sortMachine :selected").val();
            param.receiveSiteCode = $(this).parents("tr").find("[name=sendSiteCode]").text();
            param.sendCode = $(this).parents("tr").find("[name=sendCode]").text();
            param.createTime = new Date($(this).parents("tr").find("[name=createTime]").text());
            //检查参数
            if(!checkGenerateSendCodeParam(param)){
                return false;
            }
            list.push(param);
        });
        if(list.length == 0){
            jQuery.messager.alert("提示","请选择要结束的目的地","info");
            return;
        }
        generateSendCode(list);
    });
    /** 补打印按钮点击事件 **/
    $("#replenishPrint").click(function () {
        toReplenishPrintPage();
    });
});

function checkGenerateSendCodeParam(param) {
    var messageTemplet = "为空的记录，此记录无法更换批次，请不勾选此记录，然后重试！";
    if(!param.machineId){
        jQuery.messager.alert("提示","存在物理滑槽" + messageTemplet,"info");
        return false;
    }
    if(!param.receiveSiteCode){
        jQuery.messager.alert("提示","存在发货目的地代码" + messageTemplet,"info");
        return false;
    }
    if(!param.sendCode){
        jQuery.messager.alert("提示","存在批次号" + messageTemplet,"info");
        return false;
    }
    if(!param.createTime){
        jQuery.messager.alert("提示","存在批次开始时间" + messageTemplet,"info");
        return false;
    }
    return true;
}

function deleteSendGroup(groupId) {
    var param= {};
    param.groupId = groupId;
    var url = $("#contextPath").val() + "/sortMachineAutoSend/deleteSendGroup";
    CommonClient.ajax("POST",url,param,function (data){
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if ( data.code == 200) {
            jQuery.messager.alert("提示：","删除成功！","info");
            //刷新发货组
            sortMachineGroupInit($("#sortMachine").val());
            $("tbody[id='chuteTb'] input[type='checkbox']").prop("checked",false);
        }else if(data.code == 500) {
            jQuery.messager.alert("提示：",data.message,"error");
        }
    });
}

function updateSendGroup(groupId, sortMachineCode, chuteCodes) {
    var param= {};
    param.groupId = groupId;
    param.machineCode = sortMachineCode;
    param.chuteCodes = chuteCodes;
    var url = $("#contextPath").val() + "/sortMachineAutoSend/updateSendGroup";
    CommonClient.postJson(url,param,function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if ( data.code == 200) {
            jQuery.messager.alert("提示：","修改成功！","info");
            //刷新发货组
            // sortMachineGroupInit(sortMachineCode);
            // var currentGroupId = $(this).val();
            // $("tbody[id='chuteTb'] input[type='checkbox']").prop("checked",false);
            // querySendGroupConfig(currentGroupId);
            //
        }else if(data.code == 500) {
            jQuery.messager.alert("提示：",data.message,"error");
        }
    });
}
/**
 * 添加发货组
 * @param sendGroupName
 * @param sortMachineCode
 */
function addSendGroup(sendGroupName, sortMachineCode, chuteCodes) {
    var param= {};
    param.groupName = sendGroupName;
    param.machineCode = sortMachineCode;
    param.chuteCodes =  chuteCodes;
    var url = $("#contextPath").val() + "/sortMachineAutoSend/addSendGroup";
    CommonClient.postJson(url,param,function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
            return;
        }
        if ( data.code == 200) {
            popClose('addSendGroupDiv');
            jQuery.messager.alert("提示：","添加成功！","info");
            //刷新发货组
            sortMachineGroupInit(sortMachineCode);
            $("tbody[id='chuteTb'] input[type='checkbox']").prop("checked",false);
        }else{
            popClose('addSendGroupDiv');
            jQuery.messager.alert("提示：",data.message,"error");

        }
    });
}

/**
 * 取得发货组关联的滑槽号（实际上取得是发送目的地code）
 */
function getChuteCodes() {
    var chutecodes =  new Array();
    $.each($("tbody[id='chuteTb'] input[type='checkbox']"), function(index, chk){
        if($(this).prop('checked')){
            var chuteCode = $(this).parents("tr").find("[name=sendSiteCode]").text();
            if(chuteCode){
                chutecodes.push(chuteCode);
            }

        }
    });
    return chutecodes;
}
/**
 * 点击查询按钮
 */
function query() {
    var currentSortMachineCode = $("#sortMachine").val();
    //查询机器号下的滑道信息
    queryChuteBySortMachineCode(currentSortMachineCode);
    //初始化发货组
    sortMachineGroupInit(currentSortMachineCode);
    //查询异常信息
    queryExceptionNum();
}
/**
 * 查询该分拣机下的发货组
 * @param groupId
 */
function querySendGroupConfig(groupId) {
    if(groupId){
        var param= {};
        param.groupId = groupId;
        var url = $("#contextPath").val() + "/sortMachineAutoSend/findSendGroupConfigByGroupId";
        CommonClient.ajax("POST",url,param,function (data) {
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
    var url = $("#contextPath").val() + "/sortMachineAutoSend/findSortMachineByErp";
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
        var url = $("#contextPath").val() + "/sortMachineAutoSend/findSendGroupByMachineCode";
        CommonClient.ajax("POST",url,param,function (data) {
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
        $('#sendGroup').html(option);
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
            option = option + "<option value='" + sendGroup.id + "'>" + sendGroup.groupName + "</option>";
        });
        $('#sendGroup').html(option);
    }else {
        $('#sendGroup').html('<option value="">请选择发货组</option> ');
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
    $("#sortMachine").html(option);
}


/**
 * 加载发货分组关联的滑道
 * @param sendGroupConfigs
 */
function loadSendGroupConfigs(sendGroupConfigs) {
    if(sendGroupConfigs){
        $.each(sendGroupConfigs, function (index, groupConfig) {
            $("#ckbox"+ groupConfig.chuteCode).prop("checked",true);
        })
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
        var url = $("#contextPath").val() + "/sortMachineAutoSend/queryChuteBySortMachineCode";
        CommonClient.ajax("POST",url,param,function (data) {
            var chute = data.data;
            if (data == undefined || data == null) {
                jQuery.messager.alert('提示：', "HTTP请求无返回数据！", 'info');
                return;
            }
            if ( data.code == 200) {
               var chutes= groupBySendSite(chute);
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
function loadChutes(chutes) {
    $("#pagerTable tbody").html("");
        $.each(chutes, function (index, chute) {
                var url = $("#contextPath").val() + "/sortMachineAutoSend/summaryBySendCode";
                var packageSum = 0.00;//总数量
                var volumeSum = 0.00;//总体积
                if (chute.sendCode) {
                    CommonClient.ajax("POST", url, {"sendCode": chute.sendCode}, function (data) {
                        // CommonClient.syncPost(url,{"sendCode":chute.sendCode},function (data) {
                        if (data != undefined && data != null) {
                            var sum = data.data;
                            $(".packageSum" + sum.sendCode).text(sum.packageSum);
                            $(".volumeSum" + sum.sendCode).text(sum.volumeSum);
                        }
                    });
                }
                var chuteCode=chute.chuteCodes[0];
                for (var i=1;i<chute.chuteCodes.length;i++){
                    chuteCode=chuteCode+','+chute.chuteCodes[i];
                }
                var chuteCodeLength=chuteCode.length;
                var tr = '';
                tr += '<tr>';
                tr += '<td><input type="checkbox" id="ckbox' +chute.sendSiteCode + '"></td>';
                if (chuteCodeLength>42){
                tr += '<td width="230px" name="chuteCode"><div style="height:auto;width:230px;vertical-align:center;overflow-x:scroll;">' + chuteCode + '</div></td>';
                } else{
                tr += '<td width="230px" name="chuteCode">' + chuteCode + '</div></td>';
                }

                tr += '<td name="sendSiteCode">' + (chute.sendSiteCode || '') + '</td>';
                tr += '<td name="sendSiteName">' + (chute.sendSiteName || '') + '</td>';
                tr += '<td name="sendCode">' + chute.sendCode + '</td>';
                tr += '<td name="createTime">' + dateFormat(chute.sendCodeCreateTime) + '</td>';
                tr += '<td name="packageSum" class="packageSum' + chute.sendCode + '"></td>';
                tr += '<td name="volumeSum" class="volumeSum' + chute.sendCode + '"></td>';
                tr += '</tr>';
                $("#pagerTable tbody").append(tr);

        });

}

/**
 * 根据发货目的地不同对滑道信息分组
 * @param chutes
 * @returns {Array}
 */
function groupBySendSite(chutes) {
    var myArray=[];
       for(var i=0;i<chutes.length;i++){
        var sortSchemeDetail=chutes[i].sortSchemeDetail;
        var reval={chuteCode1:sortSchemeDetail.chuteCode1,sendSiteCode:sortSchemeDetail.sendSiteCode,
            sendSiteName:sortSchemeDetail.sendSiteName,sendCode:chutes[i].sendCode,sendCodeCreateTime:chutes[i].sendCodeCreateTime}
        myArray.push(reval);
    }
      var newArray=[];
      for(var j=0;j<myArray.length;j++){
          var index=-1;
          var sendSiteCode= myArray[j].sendSiteCode;
          var hasExists= newArray.some(function (value, index1) {
               if(sendSiteCode===value.sendSiteCode){
                   index=index1;
                   return true;
               }
           });
       if (!hasExists) {
               newArray.push({
               sendSiteCode: myArray[j].sendSiteCode,
               sendSiteName:myArray[j].sendSiteName,
               chuteCodes:[myArray[j].chuteCode1],
               sendCode:myArray[j].sendCode,
               sendCodeCreateTime:myArray[j].sendCodeCreateTime
           });
       } else {
           newArray[index].chuteCodes.push(myArray[j].chuteCode1);
       }
   }
      return newArray;
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
            jQuery.messager.alert("提示：","更换批次成功！","info");
            query();
        }else{
            jQuery.messager.alert("错误：",data.message,"error");
            return;
        }
    })
}



/**
 * 页面定时刷新函数(设置10秒刷新一次)
 */
function flashChuteInfo(){
    var machineId = $("#sortMachine option:selected").val();
    if(!machineId){
        return;
    }
    query();
}

/**
 * 获取发货异常数据总量
 */
function queryExceptionNum(){
    if(!$("#sortMachine :selected").val()){
        return;
    }
    var url = $("#contextPath").val() + "/sortMachineAutoSend/queryExceptionNum";
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
    CommonClient.postJson(url,params,function (data) {
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
 * 点击补打印跳转到补打印界面
 */
function toReplenishPrintPage(){
    var url = $("#contextPath").val() + "/sortMachineAutoSend/replenishPrintIndex";

    if(!$("#sortMachine").val()){
        jQuery.messager.alert("提示：","请选择分拣机编号","info");
        return;
    }
    var createSiteCode = $("#createSiteCode").val();
    var createSiteName = $("#createSiteName").val();
    location.href = url + "?machineId=" + $("#sortMachine").val()
        + "&createSiteCode=" + createSiteCode
        + "&createSiteName=" + encodeURIComponent(encodeURIComponent(createSiteName))
        // + "&startTime="
        // + timeStampToDate(gantryParams.startTime) + "&endTime=" + timeStampToDate(DateUtil.formatDateTime(new Date()));
}

/**
 * 点击异常数据跳转到异常发货界面
 */
function toGantryExceptionPage(){
    var url = $("#contextPath").val() + "/gantryException/autoMachineExceptionList";
    if(!$("#sortMachine").val()){
        return;
    }
    var createSiteCode = $("#createSiteCode").val();
    location.href = url + "?machineId=" + $("#sortMachine").val()
        + "&siteCode=" + createSiteCode
        // + "&startTime=" + timeStampToDate(gantryParams.startTime) + "&endTime=" + timeStampToDate(gantryParams.endTime);
}
function add0(m) {
    return m < 10 ? '0' + m : m;
}
function dateFormat(date) {
    if(date == null || date == ""){
        return "";
    }
    var time = new Date(date);
    var y = time.getFullYear();
    var m = time.getMonth() + 1;
    var d = time.getDate();
    var h = time.getHours();
    var mm = time.getMinutes();
    var s = time.getSeconds();

    return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm)
        + ':' + add0(s);
}