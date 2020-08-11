/*****************************************/
/*意见反馈*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/

    var appId = $('#appId').val();

    var saveInfoUrl = '/feedback/add';

    // 1M
    var fileMaxSize = 5 * 1024 * 1024;

    /*****************************************/
    /*组件*/
    /*****************************************/
    /**
     * 反馈内容字数提示
     */
    initTextArea();
    $.combobox.createNew('type-select', {
        placeholder: '请选择意见反馈类型'
    });

    /*表单验证*/
    $.formValidator.createNew('add-form', {
        excluded: [":disabled"],
        live: 'enabled',
        submitButtons: 'button[type="submit"]',
        message: '验证不通过',
        fields: {
            type: {
                validators: {
                    notEmpty: {
                        message: '意见反馈类型不能为空！'
                    }
                }
            },
            content: {
                validators: {
                    notEmpty: {
                        message: '反馈内容不能为空！'
                    },
                    stringLength: {
                        max: 200,
                        message: '反馈内容长度不能超过200个字！'
                    }
                }
            }
        }
    });

    /*****************************************/
    /*页面初始化*/
    /*****************************************/
    var initPageFunc = function () {
        var blocker = $.pageBlocker.block();
        $.combobox.clearAllSelected('type-select');
        $('#add-form').bootstrapValidator('resetForm', true);
        $.pageBlocker.close(blocker);
    };

    initPageFunc();

    var submitFormData = function (formData) {
        $.ajax({
            type: 'POST',
            data: formData,
            url: saveInfoUrl,
            async: false,
            cache: false,
            dataType: 'json',
            contentType: false,
            processData: false,
            error: function (XMLHttpRequest, status, errorThrown) {
                $.msg.error("提交意见反馈失败！", "");
                console.log('XMLHttpRequest:' + XMLHttpRequest);
                console.log('status:' + status);
                console.log('errorThrown:' + errorThrown);
                console.log('invoke failed');
            },
            success: function (res) {
                if (res != null && res.code == 200) {
                    $.msg.ok('提交意见反馈信息成功！', '', function () {
                        location.href = "/feedback/index?appId=" + appId + "&t=" + new Date().getTime();
                    });
                } else {
                    $.msg.error("提交意见反馈信息失败！", res.message);
                }
                return res;
            }
        });
    }

    /*****************************************/
    /*按钮动作*/
    /*****************************************/
    /*新增*/
    $('#btn_add').click(function () {
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('add-form');
        if (flag == true) {
            $.msg.confirm('确认提交意见反馈吗？', function () {
                var blocker = $.pageBlocker.block();
                var formData = new FormData();
                var image1 = $('#image1')[0].files;
                if (image1.length > 0) {
                    if (image1[0].size > fileMaxSize) {
                        $.pageBlocker.close(blocker);
                        $.msg.error("提交失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("images", image1[0]);
                }

                var image2 = $('#image2')[0].files;
                if (image2.length > 0) {
                    if (image2[0].size > fileMaxSize) {
                        $.pageBlocker.close(blocker);
                        $.msg.error("提交失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("images", image2[0]);
                }

                var image3 = $('#image3')[0].files;
                if (image3.length > 0) {
                    if (image3[0].size > fileMaxSize) {
                        $.pageBlocker.close(blocker);
                        $.msg.error("提交失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("images", image3[0]);
                }
                var image4 = $('#image4')[0].files;
                if (image4.length > 0) {
                    if (image4[0].size > fileMaxSize) {
                        $.pageBlocker.close(blocker);
                        $.msg.error("提交失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("images", image4[0]);
                }
                var image5 = $('#image5')[0].files;
                if (image5.length > 0) {
                    if (image5[0].size > fileMaxSize) {
                        $.pageBlocker.close(blocker);
                        $.msg.error("提交失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("images", image5[0]);
                }

                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');
                var formJson = JSON.stringify(formParams);
                console.info("formJson:" + formJson)
                formData.append("feedbackRequest", formJson);
                submitFormData(formData);
                $.pageBlocker.close(blocker);
            });
        }
    });

    /*取消*/
    $('#btn_cancel').click(function () {
        //window.history.back(-1);
        window.history.go(-1);
    });

});

function initTextArea() {
    // var text = $('#content-input').val();
    // var len = text.length;
    // $('#content-input').next().find('span').html(len);
    $('textarea').keyup(function () {
        var text = $(this).val();
        len = text.length;
        if (len > 200) {
            return false;
        }
        $(this).next().find('span').html(len);
    })
}