$(function () {

    var uploadUrl = "/weightAndVolumeCheckOfB2b/uploadExcessPicture";
    var uploadVideoUrl = "/weightAndVolumeCheckOfB2b/uploadExcessVideo";
    var getVideoUploadUrl = "/weightAndVolumeCheckOfB2b/getVideoUploadUrl";

    if($('#excessType').val() === '1'){
        // 1表示重量超标
        document.getElementById('picDimension').innerHTML = "重量";
        // 由于重量超标只下发面单和重量2张图片，所以隐藏长、宽、高
        $('#lengthDiv').css("display", "none");
        $('#widthDiv').css("display", "none");
        $('#heightDiv').css("display", "none");
    }else if($('#excessType').val() === '2'){
        // 2表示体积超标
        document.getElementById('picDimension').innerHTML = "全景";
        $('#lengthDiv').css("display", "block");
        $('#widthDiv').css("display", "block");
        $('#heightDiv').css("display", "block");
    }

    // 单选框上传图片或视频选择
    $('input[type="radio"]').change(function(){
        // 如果选择超标图片上传单选框，则隐藏视频相关区域
        if ($('#picRadio').attr("checked")) {
            $('#videoDiv').css("display", "none");
            $('#weightDiv').css("display", "block");
            $('#faceDiv').css("display", "block");
            // 由于重量超标只下发面单和重量2张图片，所以隐藏长、宽、高
            if ($('#excessType').val() === '2') {
                $('#lengthDiv').css("display", "block");
                $('#widthDiv').css("display", "block");
                $('#heightDiv').css("display", "block");
            }
            $('#picFormat').css("display", "block");
            $('#videoFormat').css("display", "none");
        }
        // 如果选择超标视频上传单选框，则隐藏图片相关区域
        if ($('#videoRadio').attr("checked")) {
            $('#videoDiv').css("display", "block");
            $('#weightDiv').css("display", "none");
            $('#faceDiv').css("display", "none");
            $('#lengthDiv').css("display", "none");
            $('#widthDiv').css("display", "none");
            $('#heightDiv').css("display", "none");
            $('#formatDiv').css("display", "none");
            $('#picFormat').css("display", "none");
            $('#videoFormat').css("display", "block");
        }
    });

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
    $('#btn_browse6').click(function () {
        $('#fileField6').click();
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
    $('#btn_upload6').click(function () {
        var upSuccess6 = '#upSuccess6';
        var upFail6 = '#upFail6';
        var upIsSuccessFlage6 = '#upIsSuccessFlage6';
        uploadVideo($('#pictureField6').val().trim(),$('#fileField6')[0].files[0],upSuccess6,upFail6,upIsSuccessFlage6,6);
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
        formData.append('uploadPicType',picType); // 上传的图片类型：重量/全景（1）、面单（2）、长（3）、宽（4）、高（5）、视频(6)
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

    // 上传视频事件
    function uploadVideo (param1,param2,param3,param4,param5,picType) {
        let fileName = param1;
        let index1 = fileName.lastIndexOf(".");
        let index2 = fileName.length;
        let suffixName = fileName.substring(index1 + 1, index2);
        let arr = ['mp4'];
        if (fileName === '') {
            Jd.alert('请选择视频文件再上传!');
            return;
        }
        if (!arr.includes(suffixName)) {
            Jd.alert('上传视频的格式不正确,请检查后再上传!');
            return;
        }
        let formData = new FormData();
        formData.append('video', param2);
        formData.append('waybillOrPackageCode', $('#waybillOrPackageCode').val());
        formData.append('createSiteCode', $('#createSiteCode').val());
        formData.append('weight', $('#weight').val());
        formData.append('uploadType', picType); // 上传类型：重量/全景（1）、面单（2）、长（3）、宽（4）、高（5）、视频(6)
        formData.append('excessType', $('#excessType').val());
        formData.append('isMultiPack', $('#isMultiPack').val());

        $.ajax({
            url : uploadVideoUrl,
            type : 'POST',
            data : formData,
            processData : false,
            contentType : false,
            async : false,
            success : function(data) {
                if (data && data.code === 200){
                    $(param3).css("display","block");
                    $(param4).css("display","none");
                    $(param5).val(1);
                    setPicUrl(picType, data.data);
                    resetUploadCount(picType);
                } else if (data.code === 40001) {
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

    $("#fileField6").change(function () {
        let filePath = $(this).val();
        if (!filePath) {
            Jd.alert('请选择视频文件再上传!');
            return;
        }
        let startIndex = filePath.lastIndexOf(".");
        let suffixName = filePath.substring(startIndex + 1, filePath.length);
        let arr = ["mp4", "avi", "wmv", "flv", "mpg", ".mpeg", "mkv", "mov", "3gp", "rmvb"];
        if (!arr.includes(suffixName)) {
            Jd.alert('上传视频的格式不正确,请检查后再上传!');
            return;
        }
        document.getElementById('pictureField6').value = filePath;
        // let formData = new FormData();
        // formData.append('videoName', $('#waybillOrPackageCode').val());
        // formData.append('fileSize', $(this)[0].files[0].size);
        // formData.append('operateErp', parent.$('#loginErp').val());
        // $.ajax({
        //     url : getVideoUploadUrl,
        //     type : 'POST',
        //     data : formData,
        //     processData : false,
        //     contentType : false,
        //     async : false,
        //     success : function(data) {
        //         if (data && data.code === 200) {
        //             if (!data.data) {
        //                 Jd.alert('获取视频上传地址失败');
        //                 return;
        //             }
        //             $('#uploadVideoUrl').val(data.data.uploadUrl);
        //             $("#videoUploadForm").attr("action", data.data.uploadUrl);
        //             $('#playUrl').val(data.data.playUrl);
        //             $('#videoId').val(data.data.videoId);
        //         } else {
        //             Jd.alert(data.message);
        //         }
        //     }
        // })
    });

    // 上传视频事件
    function uploadVideoNew (param1,param2,param3,param4,param5,picType) {
        let uploadAddress = $('#uploadVideoUrl').val();
        if (!uploadAddress) {
            Jd.alert('获取视频上传地址失败');
            return;
        }
        $("#videoUploadForm").submit();
        console.log('视频上传表单已提交');
    }

    $("#videoCallback").load(function(){
        console.log('触发视频上传回调事件');
        let str = $("#videoCallback").contents().text();
        console.log('videoCallback=' + str);
        let result = JSON.parse(str);
        console.log('result=' + result);
        console.log('result.code=' + result.code + 'result.message=' + result.message);
        if (result.code === 200) {
            $('#upSuccess6').css("display","block");
            $('#upFail6').css("display","none");
            $('#upIsSuccessFlage6').val(1);
            setPicUrl(picType, $('#playUrl').val());
        } else {
            $('#upSuccess6').css("display","none");
            $('#upFail6').css("display","block");
            $('#upIsSuccessFlage6').val(0);
            Jd.alert(data.message);
        }
    });

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
        if(picType === 6){
            parent.$('#excessVideo').val(picUrl);
            parent.$('#excessVideoId').val($('#videoId').val());
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
        // 上传对话框frame对象
        let index = parent.layer.getFrameIndex('upExcessPicture');
        let picNum = 0;
        if ($('#upIsSuccessFlage1').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage2').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage3').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage4').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage5').val() === '1') {
            picNum = picNum + 1;
        }

        // 设置父页面图片数量
        if (picNum > 0) {
            // 如果视频也上传了，设置父页面图片数量为：照片数量+视频数量
            if ($('#upIsSuccessFlage6').val() === '1') {
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = picNum + 1;
            } else {
                let excessType = $('#excessType').val();
                if (excessType === '1' && picNum < 2) {
                    Jd.alert('保存失败,重量超标至少上传2张图片!');
                    return;
                }
                if (excessType === '2' && picNum < 5) {
                    Jd.alert('保存失败,体积超标至少上传5张图片!');
                    return;
                }
                // 如果只上传照片，那么设置父页面图片数量为：照片数量
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = picNum;
            }
            // 关闭上传对话框
            parent.layer.close(index);
        } else {
            // 如果只上传视频，设置父页面图片数量为：1
            if ($('#upIsSuccessFlage6').val() === '1') {
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = 1;
                // 关闭上传对话框
                parent.layer.close(index);
            } else {
                Jd.alert('请先上传超标图片或视频!');
            }
        }

    });

    //返回
    $('#btn_return').click(function () {
        // 上传对话框frame对象
        let index = parent.layer.getFrameIndex('upExcessPicture');
        let picNum = 0;
        if ($('#upIsSuccessFlage1').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage2').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage3').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage4').val() === '1') {
            picNum = picNum + 1;
        }
        if ($('#upIsSuccessFlage5').val() === '1') {
            picNum = picNum + 1;
        }

        // 设置父页面图片数量
        if (picNum > 0) {
            // 如果视频也上传了，设置父页面图片数量为：照片数量+视频数量
            if ($('#upIsSuccessFlage6').val() === '1') {
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = picNum + 1;
            } else {
                let excessType = $('#excessType').val();
                if (excessType === '1' && picNum < 2) {
                    Jd.alert('请2张图片全部上传完成后再返回!');
                    return;
                }
                if (excessType === '2' && picNum < 5) {
                    Jd.alert('请5张图片全部上传完成后再返回!');
                    return;
                }
                // 如果只上传照片，那么设置父页面图片数量为：照片数量
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = picNum;
            }
            // 关闭上传对话框
            parent.layer.close(index);
        } else {
            // 如果只上传视频，设置父页面图片数量为：1
            if ($('#upIsSuccessFlage6').val() === '1') {
                parent.$('#waybillDataTable')[0].rows[1].cells[5].innerHTML = 1;
                // 关闭上传对话框
                parent.layer.close(index);
            } else {
                Jd.alert('请先上传超标图片或视频再返回!');
            }
        }
    });

});
