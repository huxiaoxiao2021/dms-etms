/**
 * Created by wuzuxiang on 2016/8/29.
 */
$(document).ready(init);

/**
 * 页面加载
 */
function init(){

    $("#addBtn").click(function(){
        var params = getParams();
        printDeviceAdd(params);
    })
}

/**
 * 参数组装
 */
function getParams(){
    var params = {};
    params.printDeviceId = $.trim($("#printDeviceId").val());
    params.versionId = $.trim($("#versionId").val());
    params.des = $.trim($("#versionDes").val());
    params.state = $.trim($("#state").val());
    if (null == params.printDeviceId || params.printDeviceId == undefined || params.printDeviceId.length <= 0){
        alert("请输入ISVID的值");
        return;
    }if (null == params.versionId || params.versionId == undefined || params.versionId.length <= 0){
        alert("请输入版本编号的值");
        return;
    }
    return params;
}

/**
 * 提交执行增加操作
 * @param params
 */
function printDeviceAdd(params) {
    var url = $("#contextPath").val() + "/printDevice/add";
    CommonClient.postJson(url,params,function(data){
        if(data == undefined || null == data){
            alert("新增ISV失败")
            return;
        }
        if(data.code == 200){
            alert(data.message);
            back_pager();
        }else{
            alert(data.message);
        }
    })
}

function back_pager(){
    window.location.href = $("#contextPath").val() + "/printDevice/index";
}
