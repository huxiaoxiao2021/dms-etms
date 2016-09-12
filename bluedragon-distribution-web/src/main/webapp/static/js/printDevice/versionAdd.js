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
        if(params == -1){
            return;
        }
        printDeviceAdd(params);
    })
}

/**
 * 参数组装
 */
function getParams(){
    var params = {};
    var filePath = $.trim($("#uploadFile").val());
    params.versionId = $.trim($("#versionId").val());

    params.des = $.trim($("#des").val());
    params.state = $.trim($("#state").val());
    if (null == params.versionId || params.versionId == undefined || params.versionId.length <= 0){
        alert("请输入版本编号");
        return -1;
    }if (null == filePath || filePath == undefined || filePath.length <= 0){
        alert("请上传文件");
        return -1;
    }if(!CompWithIdAndFileName(filePath,params.versionId)){
        alert("请保持文件名与版本编号一致")
        return -1;
    }

    return params;
}

/**
 * 提交执行增加操作
 * @param params
 */
function printDeviceAdd(params) {
    var url = "" + "/version/add/?versionId=" + params.versionId + "&des=" + params.des + "&state=" + params.state;
    $.blockUI({message: "<span class='pl20 icon-loading'>正在处理，清稍等....</span>"});
    $("#form1").ajaxSubmit({
        url:url,
        data:$("#uploadFile").serialize(),
        type: "post",
        dataType: "json",
        async:true,//异步
        success:function(data){
            $.unblockUI();
            var data = eval(data);
            if(data == undefined || null == data){
                jQuery.messager.alert('提示:', "上传文件失败", 'info');
                return;
            }
            if(data.code == 200){
                jQuery.messager.alert('提示:', "版本上传成功", 'info');
                back_pager();
            }else{
                jQuery.messager.alert('提示:', data.message, 'info');
            }
        },
        error: function () {
            $.unblockUI();
            jQuery.messager.alert('提示:', "上传配置失败，稍后重试", 'info');
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
