
/**
 * Created by wuzuxiang on 2016/10/14.
 */
var queryParam = {}//用于缓存用户的查询条件，便于进行明细查询
$(document).ready(function(){
    var multiSelectTemp = multiSelectShow();//下拉列表多选

    /** 初始化分拣中心 **/
    siteOrgInit();

    /** EXP_TYPE控件隐藏与显示 **/
    $("#tableName").bind("change",function () {
        $("#type").html("<input type='text' style='visibility: hidden'>");

        /** 联动效果 **/
        if($("#tableName option:selected").val() == "scan_lists"){
            $("#type").html(multiSelectTemp);
            $("#expTypeList").niceForm({
                eventType:'click',  //默认为单击事件
                change:function(){} //下拉菜单发生change事件时触发的回调函数
            });
        }

    });

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
    queryParam = params;//每次点击查询的时候，对查询条件进行缓存
    goQuery(params);
}

function goQuery(params){
    var url = $("#contextPath").val() + "/sortingCenter/query";
    CommonClient.postJson(url, params, function (data){
        var titleTemp = sqlSyntax(params);/** 拼接sql **/
        if(data.code == 200){
            var countNum = data.data;
            var temp = "<tr title='" + titleTemp + "'><td><a onclick='doQueryDetail(1)'>" + countNum + "</a></td></tr>";
            $("#numTable tbody").html(temp);
        }else{
            $("#numTable tbody").html("<tr title='" + titleTemp + "'><td>查询数据失败</td></tr>");
            jQuery.messager.alert("提示：",data.message,'info');
        }
    })
}

function doQueryDetail(pageNo){
    var params = queryParam ;//每次点击查询的时候，对查询条件进行缓存
    params.pageNo = pageNo;
    goQueryDetail(params);
}

function goQueryDetail(params){
    var url = $("#contextPath").val() + "/sortingCenter/queryDetail";
    CommonClient.postJson(url, params, function (page){
        var data = page.data;
        if(undefined != data && null != data){
            var tableHtml = "";
            if (undefined == params.tableName || null == params.tableName || "" == params.tableName){
                return;
            } else if (params.tableName == "box"){
                tableHtml = getBoxDetailHtml(data);
            } else if (params.tableName == "sorting"){
                tableHtml = getSortingDetailHtml(data);
            } else if (params.tableName == "scan_lists"){
                tableHtml = getScanDetailHtml(data);
            }
            var totalRow = $("#numTable tbody a").text();
            var totalPage = Math.ceil(totalRow/10)
            $("#detailPopUpTable").html(tableHtml);
            $("#detailPopUp #pager").html(PageBar.getHtml("doQueryDetail",totalRow,page.pageNo,totalPage));
            popUp("detailPopUp",1500,650);
        }else{
            jQuery.messager.alert("提示：","查询无数据",'info');
        }
    })
}

/** 获得查询参数 **/
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

/** 复选框下拉列表 **/
function multiSelectShow(){
    var temp = "";
    temp += "<select name='expTypeList' id='expTypeList' multiple='multiple' >";
    temp += "<option value=''>选择EXP_TYPE</option>";
    temp += "<option value='AT'>AT</option>";
    // temp += "<option value='MB'>MB</option>";
    temp += "<option value='MD'>MD</option>";
    // temp += "<option value='MJ'>MJ</option>";

    temp += "<option value='CD'>CD</option>";
    temp += "<option value='CF'>CF</option>";
    temp += "<option value='DD'>DD</option>";
    temp += "<option value='DF'>DF</option>";
    temp += "<option value='DJ'>DJ</option>";
    temp += "<option value='OF'>OF</option>";
    temp += "<option value='SP'>SP</option>";

    temp += "<option value='DE'>DE</option>";
    temp += "<option value='ID'>ID</option>";
    temp += "<option value='MB'>MB</option>";
    temp += "<option value='MJ'>MJ</option>";
    temp += "<option value='MR'>MR</option>";
    temp += "<option value='MT'>MT</option>";
    temp += "<option value='NR'>NR</option>";
    temp += "<option value='UP'>UP</option>";

    temp += "<option value='SC'>SC</option>";
    temp += "</select>";
    return temp;
}

/** 拼写sql语句 **/
function sqlSyntax(params){
    var temp = "select count(*) from " + params.tableName + " where 1=1";
    if(params.startTime != ""){
        temp += " AND create_time > ";
        temp += params.startTime;
    }
    if(params.endTime != ""){
        temp += " AND create_time < ";
        temp += params.endTime;
    }
    if(params.expTypeList.length > 0){
        var plist = params.expTypeList;
        var length = plist.length;
        temp += " AND exp_type IN (";
        for (var i = 0;i < length-1;i++){
            temp += "&quot;"+ plist[i] +"&quot;,";
        }
        temp += "&quot;" + plist[length-1] + "&quot;)";
    }
    return temp;
}

/** 组装box详细表的html **/
function getBoxDetailHtml(boxes){
    var temp = "";
    temp += "<tr>";
    temp += "<td>序号</td>";
    temp += "<td>箱号(boxCode)</td>";
    temp += "<td>箱号类型(boxType)</td>";
    temp += "<td>操作码(opCode)</td>";
    temp += "<td>站点编号(siteNo)</td>";
    temp += "<td>分拣机代码(machineCode)</td>";
    temp += "<td>创建时间(createTime)</td>";
    temp += "<td>站点编号(siteCode)</td>";
    temp += "<td>建包数量(mailCnt)</td>";
    temp += "<td>格口编号(chuteCode)</td>";
    temp += "<td>接受标识(receFlag)</td>";
    temp += "<td>接受时间(receTime)</td>";
    temp += "<td>数据库时间(ts)</td>";
    temp += "<td>逻辑删除标识(yn)</td>";
    temp += "</tr>";
    if (null != boxes && undefined != boxes && boxes.length > 0){

        boxes.forEach(function (item,index,array) {
            temp += "<tr>";
            temp += "<td>" + index + "</td>";
            temp += "<td>" + item.boxCode + "</td>";
            temp += "<td>" + item.boxType + "</td>";
            temp += "<td>" + item.opCode + "</td>";
            temp += "<td>" + item.siteNo + "</td>";
            temp += "<td>" + item.machineCode + "</td>";
            temp += "<td>" + item.createTime + "</td>";
            temp += "<td>" + item.siteCode + "</td>";
            temp += "<td>" + item.mailCnt + "</td>";
            temp += "<td>" + item.chuteCode + "</td>";
            temp += "<td>" + item.receFlag + "</td>";
            temp += "<td>" + item.receTime + "</td>";
            temp += "<td>" + item.ts + "</td>";
            temp += "<td>" + item.yn + "</td>";
            temp += "</tr>";
        },this)
    }

    return temp;
}
/** 组装sorting详细表的html **/
function getSortingDetailHtml(sortings){
    var temp = "";
    temp += "<tr>";
    temp += "<td>序号</td>";
    temp += "<td>操作码(opCode)</td>";
    temp += "<td>站点编号(siteNo)</td>";
    temp += "<td>分拣机代码(machineCode)</td>";
    temp += "<td>箱号(boxCode)</td>";
    temp += "<td>运单号(waybillCode)</td>";
    temp += "<td>包裹号(packageCode)</td>";
    temp += "<td>创建时间(createTime)</td>";
    temp += "<td>目的站点编号(siteCode)</td>";
    temp += "<td>重量(weight)</td>";
    temp += "<td>长度(length)</td>";
    temp += "<td>宽度(width)</td>";
    temp += "<td>高度(height)</td>";
    temp += "<td>接受标识(receFlag)</td>";
    temp += "<td>成功标识(successFlag)</td>";
    temp += "<td>逻辑删除标识(yn)</td>";
    temp += "</tr>";
    if (null != sortings && undefined != sortings && sortings.length > 0){

        sortings.forEach(function (item,index,array) {
            temp += "<tr>";
            temp += "<td>" + index + "</td>";
            temp += "<td>" + item.opCode + "</td>";
            temp += "<td>" + item.siteNo + "</td>";
            temp += "<td>" + item.machineCode + "</td>";
            temp += "<td>" + item.boxCode + "</td>";
            temp += "<td>" + item.waybillCode + "</td>";
            temp += "<td>" + item.packageCode + "</td>";
            temp += "<td>" + item.createTime + "</td>";
            temp += "<td>" + item.siteCode + "</td>";
            temp += "<td>" + item.weight + "</td>";
            temp += "<td>" + item.length + "</td>";
            temp += "<td>" + item.width + "</td>";
            temp += "<td>" + item.height + "</td>";
            temp += "<td>" + item.receFlag + "</td>";
            temp += "<td>" + item.successFlag + "</td>";
            temp += "<td>" + item.yn + "</td>";
            temp += "</tr>";
        },this)
    }

    return temp;
}
/** 组装scan_Lists详细表的html **/
function getScanDetailHtml(scanLists){
    var temp = "";
    temp += "<tr>";
    // temp += "<td>序号</td>";
    // temp += "<td>小车ID(cardNO)</td>";
    // temp += "<td>快件追踪ID(transId)</td>";
    temp += "<td>扫描码(scanNO)</td>";
    // temp += "<td>供件台(supplyNo)</td>";
    temp += "<td>支持类型(supplyType)</td>";
    temp += "<td>格口号(chuteCode)</td>";
    // temp += "<td>站点编号(siteNo)</td>";
    temp += "<td>分拣机代码(machineCode)</td>";
    temp += "<td>运单号(waybillCode)</td>";
    temp += "<td>包裹号(packageCode)</td>";
    temp += "<td>操作时间(operateTime)</td>";
    temp += "<td>目的站点(siteCode)</td>";
    temp += "<td>重量(weight)</td>";
    temp += "<td>长度(length)</td>";
    temp += "<td>宽度(width)</td>";
    temp += "<td>高度(height)</td>";
    temp += "<td>异常类型(expType)</td>";
    temp += "<td>接受标识(receFlag)</td>";
    temp += "<td>接受时间(receTime)</td>";
    temp += "<td>逻辑删除标识(yn)</td>";
    temp += "</tr>";
    if (null != scanLists && undefined != scanLists && scanLists.length > 0){

        scanLists.forEach(function (item,index,array) {
            temp += "<tr>";
            // temp += "<td>" + index + "</td>";
            // temp += "<td>" + item.cardNO + "</td>";
            // temp += "<td>" + item.transId + "</td>";
            temp += "<td>" + item.scanNO + "</td>";
            // temp += "<td>" + item.supplyNo + "</td>";
            temp += "<td>" + item.supplyType + "</td>";
            temp += "<td>" + item.chuteCode + "</td>";
            // temp += "<td>" + item.siteNo + "</td>";
            temp += "<td>" + item.machineCode + "</td>";
            temp += "<td>" + item.waybillCode + "</td>";
            temp += "<td>" + item.packageCode + "</td>";
            temp += "<td>" + item.operateTime + "</td>";
            temp += "<td>" + item.siteCode + "</td>";
            temp += "<td>" + item.weight + "</td>";
            temp += "<td>" + item.length + "</td>";
            temp += "<td>" + item.width + "</td>";
            temp += "<td>" + item.height + "</td>";
            temp += "<td>" + item.expType + "</td>";
            temp += "<td>" + item.receFlag + "</td>";
            temp += "<td>" + item.receTime + "</td>";
            temp += "<td>" + item.yn + "</td>";
            temp += "</tr>";
        },this)
    }

    return temp;
}
