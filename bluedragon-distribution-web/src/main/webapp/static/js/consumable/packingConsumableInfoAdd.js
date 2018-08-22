/*****************************************/
/*增加本地服务器信息*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*区域*/

    var saveInfoUrl = '/consumable/packingConsumableInfo/save';
    /*****************************************/
    /*组件*/
    /*****************************************/

    /*表单验证*/
    $.formValidator.createNew('add-form',{
        excluded:[":disabled"],
        live: 'enabled',
        submitButtons: 'button[type="submit"]',
        message: '验证不通过',
        fields: {
            name: {
                validators: {
                    notEmpty: {
                        message: '名称不能为空！'
                    }
                }
            },
            type: {
                validators: {
                    notEmpty: {
                        message: '类型不能为空！'
                    }
                }
            },
            unit: {
                validators: {
                    notEmpty: {
                        message: '单位不能为空！'
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

        $.pageBlocker.close(blocker);
    };

    initPageFunc();

    /*****************************************/
    /*按钮动作*/
    /*****************************************/
    /*修改*/
    $('#btn_add').click(function () {
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('add-form');
        if(flag == true)
        {
            $.msg.confirm('确认修改吗？',function () {

                var blocker = $.pageBlocker.block();
                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');
                formParams.name = formParams.name.trim();
                formParams.type = formParams.type.trim();
                formParams.volume = formParams.volume.trim();
                formParams.volumeCoefficient = formParams.volumeCoefficient.trim();
                formParams.specification = formParams.specification.trim();
                formParams.unit = formParams.unit.trim();

                var formJson = JSON.stringify(formParams);

                /*提交表单*/
                $.ajaxHelper.doPostSync(saveInfoUrl,formJson,function (res) {
                    if(res != null && res.code == 200){
                        $.msg.ok('新增信息成功！','',function () {
                            $('#btn_cancel').click();
                            parent.$('#btn_query').click();
                        });

                    }else {
                        $.msg.error("新增信息失败！",res.statusMessage);
                    }
                },'json',function (XMLHttpRequest, textStatus, errorThrown) {
                    $.msg.error("新增信息成功失败！","");
                    console.log('XMLHttpRequest:' + XMLHttpRequest);
                    console.log('status:' + status);
                    console.log('errorThrown:' + errorThrown);
                });
                $.pageBlocker.close(blocker);
            });
        }else{
            $.msg.warn('参数有误','请您检查服务器信息是否有误');
        }
    });

    /*取消*/
    $('#btn_cancel').click(function () {
        var index = parent.layer.getFrameIndex('addInfoFrame');
        parent.layer.close(index);
    });

});