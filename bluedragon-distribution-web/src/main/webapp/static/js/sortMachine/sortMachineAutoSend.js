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
    });
    //发货组改变时，加载关联的滑道
    $(sendGroupSelect).change(function () {
        var currentGroupId = $(this).val();
        querySendGroupConfig(currentGroupId);
    });
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
    $("#gantryDevice").html(temp);//清空分拣机信息
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
 * 查询分拣机下关联的滑道
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

            }else if(data.code == 500) {
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
        $.each(chutes, function (index, chuteInfo) {
            var tr = '';
            tr += '<tr>';
            tr += '<td><input type="checkbox" id="ckbox' + chuteInfo.chuteCode + '"></td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '<td>' + chuteInfo. + '</td>';
            tr += '</tr>';
            $("#pagerTable tbody").append(tr);
        });

    }
}

