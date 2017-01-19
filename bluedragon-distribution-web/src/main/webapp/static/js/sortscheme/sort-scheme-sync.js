/**
 * Created by wuzuxiang on 2017/1/13.
 */
$(document).ready(function(){
    $("#syncBtn").click(function () {
        var siteCode = $("#siteOrg").val();
        if(siteCode == ""){
            return;
        }
        syncFunc(siteCode);
    })
})

/**
 * 提交同步逻辑
 **/
function syncFunc(siteCode){
    var url = $("#contextPath").val() + "/sortSchemeSync/sync";
    var param = {"siteCode":siteCode};
    CommonClient.post(url,param,function (data) {
        if(undefined == data || null == data){
            jQuery.messager.alert("警告","HTTP请求无响应","warning");
            return;
        }
        if(200 == data.code){
            jQuery.messager.alert("提示","分拣中心：" + siteCode + "分拣方案同步成功","info");
        }else{
            jQuery.messager.alert("错误",data.message,"error");
        }
    })
}
