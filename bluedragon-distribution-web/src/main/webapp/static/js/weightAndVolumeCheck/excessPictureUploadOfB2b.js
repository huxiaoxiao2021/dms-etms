$(function () {

    var uploadUrl = "/weightAndVolumeCheckOfB2b/uploadExcessPicture";


    //浏览
    $('#btn_browse1').click(function () {
        $('#fileField1').click();
    });
    $('#btn_browse2').click(function () {
        $('#fileField2').click();
    });
    $('#btn_browse3').click(function () {
        $('#fileField3').click();
    });
    $('#btn_browse4').click(function () {
        $('#fileField4').click();
    });
    $('#btn_browse5').click(function () {
        $('#fileField5').click();
    });
    //上传
    $('#btn_upload1').click(function () {
        var upSuccess1 = '#upSuccess1';
        var upFail1 = '#upFail1';
        var upIsSuccessFlage1 = '#upIsSuccessFlage1';
        uplload ($('#pictureField1').val().trim(),$('#fileField1')[0].files[0],upSuccess1,upFail1,upIsSuccessFlage1);
    });
    $('#btn_upload2').click(function () {
        var upSuccess2 = '#upSuccess2';
        var upFail2 = '#upFail2';
        var upIsSuccessFlage2 = '#upIsSuccessFlage2';
        uplload ($('#pictureField2').val().trim(),$('#fileField2')[0].files[0],upSuccess2,upFail2,upIsSuccessFlage2);
    });
    $('#btn_upload3').click(function () {
        var upSuccess3 = '#upSuccess3';
        var upFail3 = '#upFail3';
        var upIsSuccessFlage3 = '#upIsSuccessFlage3';
        uplload ($('#pictureField3').val().trim(),$('#fileField3')[0].files[0],upSuccess3,upFail3,upIsSuccessFlage3);
    });
    $('#btn_upload4').click(function () {
        var upSuccess4 = '#upSuccess4';
        var upFail4 = '#upFail4';
        var upIsSuccessFlage4 = '#upIsSuccessFlage4';
        uplload ($('#pictureField4').val().trim(),$('#fileField4')[0].files[0],upSuccess4,upFail4,upIsSuccessFlage4);
    });
    $('#btn_upload5').click(function () {
        var upSuccess5 = '#upSuccess5';
        var upFail5 = '#upFail5';
        var upIsSuccessFlage5 = '#upIsSuccessFlage5';
        uplload ($('#pictureField5').val().trim(),$('#fileField5')[0].files[0],upSuccess5,upFail5,upIsSuccessFlage5);
    });

    //上传事件
    function uplload (param1,param2,param3,param4,param5) {
        var fileName = param1;
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
        debugger;
        var formData = new FormData();
        formData.append('image',param2);
        formData.append('waybillCode',$('#waybillCode').val());
        // formData.append('reviewDate',$('#reviewDate').val());

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
                    $(param3).css("display","block");
                    $(param5).val(1);
                }else{
                    $(param4).css("display","block");
                    $(param5).val(0);
                }
            }
        });



    };

    //保存
    $('#btn_saved').click(function () {
        if($('#upIsSuccessFlage1').val()==1 && $('#upIsSuccessFlage2').val()==1
            && $('#upIsSuccessFlage3').val()==1 && $('#upIsSuccessFlage4').val()==1
            && $('#upIsSuccessFlage5').val()==1){

            if($('#isWaybill').val() == 1){
                parent.$('#waybillDataTable')[0].rows[$('#rowIndex').val()].cells[6].innerHTML = 5;
            }else {
                parent.$('#packageDataTable')[0].rows[$('#rowIndex').val()].cells[6].innerHTML = 5;
            }
            //全部上传成功，显示记录
            var index = parent.layer.getFrameIndex('upExcessPicture');
            parent.layer.close(index);
        }else {
            Jd.alert('保存失败,至少上传5张图片!');
        }

    });

    //返回
    $('#btn_return').click(function () {
        if($('#upIsSuccessFlage1').val()==1
            && $('#upIsSuccessFlage2').val()==1
            && $('#upIsSuccessFlage3').val()==1
            && $('#upIsSuccessFlage4').val()==1
            && $('#upIsSuccessFlage5').val()==1){
            //全部上传成功，显示记录
            var index = parent.layer.getFrameIndex('upExcessPicture');
            parent.layer.close(index);
        }else {
            Jd.alert('全部上传成功后再返回!');
        }
    });

});
