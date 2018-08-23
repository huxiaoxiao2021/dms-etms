/*****************************************/
/*增加本地服务器信息*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*区域*/

    var saveInfoUrl = '/consumable/waybillConsumableRelation/save';
    /*****************************************/
    /*组件*/
    /*****************************************/

    /*表单验证*/
    $.formValidator.createNew('modify-form',{
        excluded:[":disabled"],
        live: 'enabled',
        submitButtons: 'button[type="submit"]',
        message: '验证不通过',
        fields: {
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
    $('#btn_modify').click(function () {
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('modify-form');
        if(flag == true)
        {
            $.msg.confirm('确认修改吗？',function () {

                var blocker = $.pageBlocker.block();
                /*获取参数*/
                var formParams = $.formHelper.serialize('modify-form');

                var formJson = JSON.stringify(formParams);

                /*提交表单*/
                $.ajaxHelper.doPostSync(saveInfoUrl,formJson,function (res) {
                    if(res != null && res.code == 200){
                        $.msg.ok('修改信息成功！','',function () {
                            $('#btn_cancel').click();
                            parent.$('#btn_query').click();
                        });

                    }else {
                        $.msg.error("修改信息失败！",res.statusMessage);
                    }
                },'json',function (XMLHttpRequest, textStatus, errorThrown) {
                    $.msg.error("修改信息成功失败！","");
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
        var index = parent.layer.getFrameIndex('modifyInfoFrame');
        parent.layer.close(index);
    });

});