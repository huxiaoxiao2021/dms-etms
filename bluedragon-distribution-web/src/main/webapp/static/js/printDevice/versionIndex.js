/**
 * Created by wuzuxiang on 2016/8/26.
 */

$(document).ready(init);

/**
 * 页面加载执行
 **/
function init() {


    $("#search").click(function () {
        //查询按钮
        versionQuery();
    })

    $("#addBtn").click(function () {
        //新增按钮
        toVersionAddPager();
    })

    $("#modifyBtn").click(function () {
        //修改按钮
        versionModify();
    })

    $("#deleteBtn").click(function () {
        //删除按钮
        var list = [];
        var printDeviceIdList = {};
        $("input[name=subcheckbox]:checked").each(function () {
            list.push($(this).parents(".tr").find("#id").text());
        })
        printDeviceIdList.list = list;
        if(null == printDeviceIdList.list){
            return;
        }
        versionDelete(printDeviceIdList);
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
 */
function versionQuery() {
    var params = getParams();
    doQuery(params);
}

/**
 * 组装参数
 * @returns {{}}
 */
function getParams(){
    var params = {};
    params.versionId = $("#versionId").val();
    var state = $("#state").val();
    if(state == "请选择"){
        params.state = "";
    }else{
        params.state = state;
    }
    return params;
}

/**
 * 查询
 */
function doQuery(params) {
    var url = $("#contextPath").val() + "/version/query";
    CommonClient.postJson(url,params,function(data) {
        if(data == undefined || null == data){
            alert("查询失败");
            return;
        }
        if(data.code == 200){
            var versionList = data.data;
            var temp = "";
            for(var i = 0;i<versionList.length;i++){
                temp += "<tr class='a2 tr'>";
                temp += "<td><input type='checkbox' name='subcheckbox' ></td>";
                temp += "<td id='stateId'>" + (versionList[i].state) + "</td>";
                // temp += "<td></td>";
                temp += "<td id='id'>" + (null == versionList[i].versionId? "" : versionList[i].versionId) + "</td>";
                temp += "<td>" + (null == versionList[i].des? "" : versionList[i].des) + "</td>";
                temp += "<td>" + (null == versionList[i].createTime? "" : versionList[i].createTime) + "</td>";
                temp += "<td>" + (null == versionList[i].updateTime? "" : versionList[i].updateTime) + "</td>";
                temp += "</tr>";
            }
            $(".opening table tbody").html(temp);
        }

    })
}


/**
 * 保证选择的是一个进行修改
 */
function versionModify() {
    var checked = 0;
    var versionId = null;
    $("input[name=subcheckbox]:checked").each(function () {
        checked += 1;
        versionId = $(this).parents(".tr").find("#id").text();
    })
    if (checked != 1) {
        alert("请选择一个有效的版本");
        return;
    } else {
        toVersionModifyPager(versionId);
    }
}

/**
 * 跳转新增页面
 */
function toVersionAddPager(){
    window.location.href = "toAddPager";
}

/**
 * 修改页面跳转
 */
function toVersionModifyPager(versionId){
    window.location.href = "toModifyPager?versionId="+versionId;
}


/**
 * 删除相关的version
 */
function versionDelete(params){

    var url = $("#contextPath").val() + "/version/delete";
    CommonClient.postJson(url,params,function(data){
        if(data == undefined || null == data){
            alert("删除版本信息失败");
            return;
        }if(data.code == 200){
            jQuery.messager.alert('提示:', "删除成功", 'info');
            window.location.href = "index";
        }if(data.code == 10000){
            jQuery.messager.alert('提示:', "删除失败", 'info');
            window.location.href = "index";
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
        params.versionId = $(this).parents(".tr").find("#id").text();
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
    var url = $("#contextPath").val() + "/version/stateChange";
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
