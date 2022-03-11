$(function () {

    var uploadUrl = "/weightAndVolumeCheckOfB2b/uploadExcessPicture";

    if($('#excessType').val() === '1'){
        // 1表示重量超标
        document.getElementById('picDimension').innerHTML = "重量";
    }else if($('#excessType').val() === '2'){
        // 2表示体积超标
        document.getElementById('picDimension').innerHTML = "全景";
    }

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
        let picType = 0;
        if($('#excessType').val() === '2'){
            picType = 0;
        }else {
            picType = 1;
        }
        upload ($('#pictureField1').val().trim(),$('#fileField1')[0].files[0],upSuccess1,upFail1,upIsSuccessFlage1, picType);
    });
    $('#btn_upload2').click(function () {
        var upSuccess2 = '#upSuccess2';
        var upFail2 = '#upFail2';
        var upIsSuccessFlage2 = '#upIsSuccessFlage2';
        upload ($('#pictureField2').val().trim(),$('#fileField2')[0].files[0],upSuccess2,upFail2,upIsSuccessFlage2,2);
    });
    $('#btn_upload3').click(function () {
        var upSuccess3 = '#upSuccess3';
        var upFail3 = '#upFail3';
        var upIsSuccessFlage3 = '#upIsSuccessFlage3';
        upload ($('#pictureField3').val().trim(),$('#fileField3')[0].files[0],upSuccess3,upFail3,upIsSuccessFlage3,3);
    });
    $('#btn_upload4').click(function () {
        var upSuccess4 = '#upSuccess4';
        var upFail4 = '#upFail4';
        var upIsSuccessFlage4 = '#upIsSuccessFlage4';
        upload ($('#pictureField4').val().trim(),$('#fileField4')[0].files[0],upSuccess4,upFail4,upIsSuccessFlage4,4);
    });
    $('#btn_upload5').click(function () {
        var upSuccess5 = '#upSuccess5';
        var upFail5 = '#upFail5';
        var upIsSuccessFlage5 = '#upIsSuccessFlage5';
        upload ($('#pictureField5').val().trim(),$('#fileField5')[0].files[0],upSuccess5,upFail5,upIsSuccessFlage5,5);
    });

    // 图片上传失败两次后，第三次可强制上传
    let weightOrPanoramaUploadCount = 0;
    let faceUploadCount = 0;
    let lengthUploadCount = 0;
    let widthUploadCount = 0;
    let heightUploadCount = 0;

    //上传事件
    function upload (param1,param2,param3,param4,param5,picType) {
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
        var formData = new FormData();
        formData.append('image',param2);
        formData.append('waybillOrPackageCode',$('#waybillOrPackageCode').val());
        formData.append('createSiteCode',$('#createSiteCode').val());
        formData.append('weight',$('#weight').val());
        formData.append('uploadPicType',picType); // 上传的图片类型：重量/全景（1）、面单（2）、长（3）、宽（4）、高（5）
        formData.append('excessType',$('#excessType').val());
        formData.append('isMultiPack',$('#isMultiPack').val());

        $.ajax({
            url : uploadUrl,
            type : 'POST',
            data : formData,
            processData : false,
            contentType : false,
            async : false,
            success : function(data) {
                if(data && data.code === 200){
                    $(param3).css("display","block");
                    $(param4).css("display","none");
                    $(param5).val(1);
                    setPicUrl(picType, data.data);
                    resetUploadCount(picType);
                }else if(data.code === 40001){
                    // 后台自定义的AI图片识别的错误编码：40001
                    let count;
                    if(picType === 1 || picType === 0){
                        count = weightOrPanoramaUploadCount ++;
                    }
                    if(picType === 2) {
                        count = faceUploadCount ++;
                    }
                    if(picType === 3){
                        count = lengthUploadCount ++;
                    }
                    if(picType === 4){
                        count = widthUploadCount ++;
                    }
                    if(picType === 5){
                        count = heightUploadCount ++;
                    }
                    if(count >= 1){
                        $.msg.confirm('您上传的照片已多次未通过系统校验，是否强制提交?',function () {
                            // 设置强制提交
                            formData.append('isForce',true);
                            $.ajax({
                                url: uploadUrl,
                                type: 'POST',
                                data: formData,
                                processData: false,
                                contentType: false,
                                async: false,
                                success: function (forceData) {
                                    if(forceData && forceData.code === 200){
                                        setPicUrl(picType, forceData.data);
                                        resetUploadCount(picType);
                                        $(param3).css("display","block");
                                        $(param4).css("display","none");
                                        $(param5).val(1);
                                    }else {
                                        $(param4).css("display","block");
                                        $(param3).css("display","none");
                                        $(param5).val(0);
                                        Jd.alert(data.message);
                                    }
                                }
                            })
                        }, function (){
                            $(param4).css("display","block");
                            $(param3).css("display","none");
                            $(param5).val(0);
                        });
                    }else {
                        $(param4).css("display","block");
                        $(param3).css("display","none");
                        $(param5).val(0);
                        Jd.alert(data.message);
                    }
                }else {
                    $(param4).css("display","block");
                    $(param3).css("display","none");
                    $(param5).val(0);
                    Jd.alert(data.message);
                }
            }
        });
    }

    function setPicUrl(picType, picUrl) {
        if(picType === 1 || picType === 0){
            parent.$('#excessPicWeightOrPanorama').val(picUrl);
        }
        if(picType === 2) {
            parent.$('#excessPicFace').val(picUrl);
        }
        if(picType === 3){
            parent.$('#excessPicLength').val(picUrl);
        }
        if(picType === 4){
            parent.$('#excessPicWidth').val(picUrl);
        }
        if(picType === 5){
            parent.$('#excessPicHeight').val(picUrl);
        }
    }

    function resetUploadCount(picType) {
        if(picType === 1 || picType === 0){
            weightOrPanoramaUploadCount = 0;
        }
        if(picType === 2){
            faceUploadCount = 0;
        }
        if(picType === 3){
            lengthUploadCount = 0;
        }
        if(picType === 4){
            widthUploadCount = 0;
        }
        if(picType === 5){
            heightUploadCount = 0;
        }
    }

    //保存
    $('#btn_saved').click(function () {
        if($('#upIsSuccessFlage1').val()==1 && $('#upIsSuccessFlage2').val()==1
            && $('#upIsSuccessFlage3').val()==1 && $('#upIsSuccessFlage4').val()==1
            && $('#upIsSuccessFlage5').val()==1){
            // 设置父页面图片数量为：5
            parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = 5;
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
