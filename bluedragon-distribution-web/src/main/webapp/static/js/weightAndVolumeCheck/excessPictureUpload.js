$(function () {

    var uploadUrl = "/weightAndVolumeCheck/uploadExcessPicture";

    //提示语隐藏
    $("#successMessage").hide();
    $("#failMessage").hide();

    //浏览
    $('#btn_browse').click(function () {
        $('#fileField').click();
    });

    //上传图片
    $('#btn_upload').click(function () {
        var fileName = $('#pictureField').val().trim();
        var index1 = fileName.lastIndexOf(".");
        var index2 = fileName.length;
        var suffixName = fileName.substring(index1+1,index2);
        var arr = ['jpg','jpeg','gif','png','bmp'];
        if(fileName == ''){
            Jd.alert('请选择图片在上传!');
            return;
        }
        if(!arr.includes(suffixName)){
            Jd.alert('上传图片的格式不正确,请检查后在上传!');
            return;
        }

        var formData = new FormData();
        formData.append('image',$('#fileField')[0].files[0]);
        formData.append('packageCode',$('#packageCode').val());

        $.ajax({
            url : uploadUrl,
            type : 'POST',
            data : formData,
            // 告诉jQuery不要去处理发送的数据
            processData : false,
            // 告诉jQuery不要去设置Content-Type请求头
            contentType : false,
            // mimeType:"multipart/form-data",
            // dataType:'json',
            async : false,
            success : function(data) {
                if(data && data.code == 200){
                    $("#successMessage").show();
                }else{
                    Jd.alert(data.message);
                    $("#failMessage").show();
                }
            }
        });



    });








    //返回
    $('#btn_return').click(function () {
        var index = parent.layer.getFrameIndex('upExcessPicture');
        parent.layer.close(index);
    });

});

/*function upload(){

    var fileName = $('#pictureField').val().trim();
    var index1 = fileName.lastIndexOf(".");
    var index2 = fileName.length;
    var suffixName = fileName.substring(index1+1,index2);
    var arr = ['jpg','jpeg','gif','png','bmp'];
    if(fileName == ''){
        Jd.alert('请选择图片在上传!');
        return false;
    }
    if(!arr.includes(suffixName)){
        Jd.alert('上传图片的格式不正确,请检查后在上传!');
        return false;
    }
    return true;
}*/
