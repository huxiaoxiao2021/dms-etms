/**
 * Created by wuzuxiang on 2016/8/29.
 */
$(document).ready(init);

/**
 * 页面加载
 */
function init(){

    $("#modifyBtn").click(function(){
        var params = getParams();
        if(params == -1){
            return;
        }
        versionModify(params);
    })
}

/**
 * 参数组装
 */
function getParams(){
    var params = {};

    params.versionId = $.trim($("#versionId").val());

    params.des = $.trim($("#versionDes").val());
    params.state = $.trim($("#state").val());
    params.createTime = $.trim($("#createTime").val());
    if (null == params.versionId || params.versionId == undefined || params.versionId.length <= 0){
        alert("请输入版本编号");
        return -1;
    }
    return params;
}

/**
 * 提交执行修改操作
 * @param params
 */
function versionModify(params) {
    var url = "" + "/version/modify";
    $.blockUI({message: "<span class='pl20 icon-loading'>正在处理，清稍等....</span>"});
    CommonClient.postJson(url,params,function(data){
        $.unblockUI();
        var data = eval(data);
        if(data == undefined || null == data){
            jQuery.messager.alert('提示:', "修改失败", 'info');
            return;
        }
        if(data.code == 200){
            jQuery.messager.alert('提示:', "版本修改成功", 'info');
            back_pager();
        }else{
            jQuery.messager.alert('提示:', data.message, 'info');
        }
    })
}

/**
 * 返回主页
 */
function back_pager(){
    window.location.href = "/version/index";
}

/**
 * 比较版本编号与文件名是否一致
 * @param fileName 文件路径
 * @param versionId 版本编号
 * return 相同返回ture 不同返回false;
 */
 function CompWithIdAndFileName( fileName , versionId){
    var i = fileName.lastIndexOf("\\");
    var j = fileName.lastIndexOf(".");
    if(fileName.substring(i+1,j) == versionId){
        return true;
    }else{
        return false;
    }
}
