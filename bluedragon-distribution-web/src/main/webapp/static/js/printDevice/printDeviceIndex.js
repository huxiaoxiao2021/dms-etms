/**
 * Created by wuzuxiang on 2016/8/25.
 */
$(document).ready(init);

/**
 * 页面加载执行
 **/
function init() {


    $("#search").click(function () {
        //查询按钮
        printDeviceQuery();
    })

    $("#modifyBtn").click(function () {
        //修改按钮
        printDeviceModify();
    })

    $("#deleteBtn").click(function () {
        //删除按钮
        var list = [];
        var printDeviceIdList = {};
        $("input[name=subcheckbox]:checked").each(function () {
            list.push($(this).parents(".tr").find("#id").text());
        })
        printDeviceIdList.list = list;
        printDeviceDelete(printDeviceIdList);
    })

    $("#enableBtn").click(function () {
        //启用按钮
        changeState("true");
    })

    $("#disableBtn").click(function () {
        //停用按钮
        changeState("false");
    })

    /**
     * 全选/取消
     */
    $("#checkAll").click(function () {
        if($(this).prop("checked")){
            $("input[name=subcheckbox]").each(function(){
                $(this).prop("checked",true);
            })
        }else{
            $("input[name=subcheckbox]:checked").each(function(){
                $(this).prop("checked",false);
            })
        }
    })
}

/**
 * 执行查询
 * @constructor
 */
function printDeviceQuery() {
    var params = getParams();
    doQuery(params);

}

/**
 * 组装参数
 * @returns {{}}
 */
function getParams() {
    var params = {};
    params.printDeviceId = $.trim($("#printDeviceId").val());
    params.versionId = $.trim($("#versionId").val());
    return params;
}

/**
 * 请求查询
 * @param params
 * @constructor
 */
function doQuery(params) {
    // var url = $("#contextPath").val() + "/printDevice/query";
    var url = "" + "/printDevice/query";
    CommonClient.postJson(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("没有符合条件的ISV插件");
            return;
        }
        if (data.code == 200) {
            var printDeviceList = data.data;
            var temp = "";
            for (var i = 0; i < printDeviceList.length; i++) {
                temp += "<tr class='a2 tr' style=''>";
                temp += "<td><input type='checkbox' name='subcheckbox'/></td>"
                temp += "<td id='stateId'>" + (printDeviceList[i].state) + "</td>";
                temp += "<td id='id'>" + (printDeviceList[i].printDeviceId) + "</td>";
                temp += "<td>" + (null == printDeviceList[i].versionId ? "" : printDeviceList[i].versionId) + "</td>";
                temp += "<td>" + (null == printDeviceList[i].des ? "" : printDeviceList[i].des) + "</td>";
                temp += "<td>" + (printDeviceList[i].createTime) + "</td>";
                temp += "<td>" + (printDeviceList[i].updateTime) + "</td>";
                temp += "</tr>";
            }
            $(".opening table tbody").html(temp);
        }
    })
}

/**
 * 格式化时间
 * @param millis
 * @returns {string}
 */
function getDateString(millis) {
    if (null == millis) {
        return "";
    }
    var date = new Date();
    date.setTime(millis);
    return date.format('yyyy-MM-dd HH:mm:ss');
}

/**
 * 保证选择的是一个进行修改
 */
function printDeviceModify() {
    var checked = 0;
    var printDeviceId = null;
    $("input[name=subcheckbox]:checked").each(function () {
        checked += 1;
        printDeviceId = $(this).parents(".tr").find("#id").text();
    })
    if (checked != 1) {
        alert("请选择一个有效的ISV");
        return;
    } else {
        toModifyPager(printDeviceId);
    }
}

/**
 * 修改跳转页面
 */
function toModifyPager(printDeviceId) {

    window.location.href = "toModifyPager?printDeviceId="+printDeviceId;

}

/**
 * 删除ISV
 */
function printDeviceDelete(printDeviceIdList) {

    var url = "" + "/printDevice/delete";
    CommonClient.postJson(url,printDeviceIdList,function(data){
        if(data == undefined || null == data){
            alert("删除ISV插件失败");
            return;
        }if(data.code == 200){
            window.location.href = "index";
        }
        if(data.code == 20000){
            jQuery.messager.alert('提示：',data.message,'info');
        }
    })
}


/**
 * 状态改变
 * state==true表示启用按钮：state==false表示停用按钮
 */
function changeState(state){

    var params = {};
    var num = 0;
    $("input[name=subcheckbox]:checked").each(function () {
        num += 1;
        params.printDeviceId = $(this).parents(".tr").find("#id").text();
        params.state =  $(this).parents(".tr").find("#stateId").text();
    });
    if(num != 1){
        jQuery.messager.alert('提示：','请选择一个进行修改','info');
        return;
    }
    if(state == params.state){
        jQuery.messager.alert('提示：','状态不需要改变','Info');
        return;
    }
    var url = "" + "/printDevice/stateChange";
    CommonClient.postJson(url,params,function(data){
        if(data == undefined || null == data){
            alert("操作失败");
            return;
        }if(data.code == 200){
            jQuery.messager.alert('提示:', "状态修改成功", 'info');
            window.location.href = "index";
        }
    })
}

