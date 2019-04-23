/*****************************************/
/*通知栏管理*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/

    var saveInfoUrl = '/notice/add';

    // 5M
    var fileMaxSize = 5 * 1024 * 1024;

    /*****************************************/
    /*组件*/
    /*****************************************/

    $.combobox.createNew('type-select', {
        width: '200',
        placeholder: '请选择通知类型'
    });

    $.combobox.createNew('level-select', {
        width: '200',
        placeholder: '请选择通知级别'
    });

    $.combobox.createNew('top-display-select', {
        width: '200',
        placeholder: '请选择是否置顶'
    });

    /*表单验证*/
    $.formValidator.createNew('add-form', {
        excluded: [":disabled"],
        live: 'enabled',
        submitButtons: 'button[type="submit"]',
        message: '验证不通过',
        fields: {
            theme: {
                validators: {
                    notEmpty: {
                        message: '主题不能为空！'
                    },
                    stringLength: {
                        max: 200,
                        message: '主题长度不能超过200个字'
                    }
                }
            },
            type: {
                validators: {
                    notEmpty: {
                        message: '通知类型不能为空！'
                    }
                }
            },
            level: {
                validators: {
                    notEmpty: {
                        message: '通知级别不能为空！'
                    }
                }
            },
            isTopDisplay: {
                validators: {
                    notEmpty: {
                        message: '是否置顶不能为空！'
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
        $.combobox.clearAllSelected('level-select');
        $.combobox.clearAllSelected('top-display-select');
        $('#add-form').bootstrapValidator('resetForm', true);
        $.pageBlocker.close(blocker);
    };

    initPageFunc();

    var submitFormData = function (formData, blocker) {
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
                $.msg.error("新增通知失败！", "");
                console.log('XMLHttpRequest:' + XMLHttpRequest);
                console.log('status:' + status);
                console.log('errorThrown:' + errorThrown);
                console.log('involk failded');
            },
            success: function (res) {
                if (res != null && res.code == 200) {
                    $.msg.ok('新增信息成功！', '', function () {
                        $('#btn_cancel').click();
                        parent.$('#btn_query').click();
                    });
                } else {
                    $.msg.error("新增通知失败！", res.message);
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
        var blocker = $.pageBlocker.block();
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('add-form');
        if (flag == true) {
            $.msg.confirm('确认新增吗？', function () {
                var formData = new FormData();
                var files = $('#importFiles')[0].files;
                if (files.length > 5) {
                    $.msg.error("新增失败！", "附件数量不能超过5个");
                    return;
                }

                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    if (file.size > fileMaxSize) {
                        $.msg.error("新增失败！", "每个附件大小不能超过5M");
                        return;
                    }
                    formData.append("files", files[i]);
                }
                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');
                var formJson = JSON.stringify(formParams);
                formData.append("noticeRequest", formJson);
                submitFormData(formData, blocker);
            });
            $.pageBlocker.close(blocker);
        } else {
            $.msg.warn('参数有误', '请您检查服务器信息是否有误');
        }
    });

    /*取消*/
    $('#btn_cancel').click(function () {
        var index = parent.layer.getFrameIndex('addFrame');
        parent.layer.close(index);
    });

});