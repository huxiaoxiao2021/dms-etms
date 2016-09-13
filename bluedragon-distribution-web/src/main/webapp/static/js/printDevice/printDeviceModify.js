/**
 * Created by wuzuxiang on 2016/8/29.
 */
$(document).ready(init);

/**
 * 页面加载
 */
function init(){



    $("#modifyBtn").click(function () {
        var params = getParams();
        if(params == -1){
            return;
        }
        printDeviceModify(params);
    })

}

/**
 * 参数组装
 */
function getParams(){
    var params = {};
    params.printDeviceId = $.trim($("#printDeviceId").val());
    params.versionId = $.trim($("#versionId").val());
    params.des = $.trim($("#Des").val());
    params.state = $.trim($("#state").val());
    params.createTime = $.trim($("#createTime").val());
    if (null == params.printDeviceId || params.printDeviceId == undefined || params.printDeviceId.length <= 0){
        alert("请输入isvId的值");
        return -1;
    }if (null == params.versionId || params.versionId == undefined || params.versionId.length <= 0){
        alert("请输入版本编号的值");
        return -1;
    }
    return params;
}

/**
 * 提交执行修改操作
 * @param params
 */
function printDeviceModify(params) {
    var url = "" + "/printDevice/modify";
    CommonClient.postJson(url,params,function(data){
        if(data.code == 200){
            jQuery.messager.alert('提示：',data.message,'info');
            back_pager();
        }else{
            alert(data.message);
        }
    })
}

function back_pager(){
    window.location.href = "index";
}
