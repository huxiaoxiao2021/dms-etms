/*****************************************/
/*增加本地服务器信息*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*区域*/

    var saveInfoUrl = '/services/preSeal/vehicleMeasureInfo/save';

    var getCodesUrl = '/services/preSeal/vehicleInfo/';
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
            volume: {
                validators: {
                    notEmpty: {
                        message: '体积不能为空！'
                    },
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,3})?$/,
                        message:'数量为数字且最多三位小数'
                    }
                }
            },
            weight: {
                validators: {
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,3})?$/,
                        message:'数量为数字且最多三位小数'
                    }
                }
            }
        }
    });


    $.combobox.createNew('code-select',{
        width: '150',
        placeholder:'请选择车牌',
        data:[
        ]
    });

    /*****************************************/
    /*页面初始化*/
    /*****************************************/
    var initPageFunc = function () {
        var transportCode = $('#transportCode-input').val();
        var blocker = $.pageBlocker.block();

        $('#code-select').html("");
        $.ajax({
            type : "get",
            url : getCodesUrl+transportCode,
            data : {},
            async : false,
            success : function (res) {

                if(res.code != 200)
                {
                    $.msg.error(res.message);
                    return;
                }
                var list = res.data.vehicleMeasureInfoList;

                for(var i in list){
                    var option = $.combobox.appendData('code-select',list[i].vehicleNumber,list[i].vehicleNumber);
                    option.volume= list[i].volume;
                    option.weight= list[i].weight;
                }

                $.combobox.clearAllSelected('code-select');
            }
        });

        $.pageBlocker.close(blocker);
    };

    /**
     * 选中赋值
     */
    $('#code-select').on("select2:select", function (e) {
        var volume = e.params.data.element.volume;
        var weight = e.params.data.element.weight;

        $('#volume-input').val(volume);
        $('#weight-input').val(weight);
    });

    /*****************************************/
    /*按钮动作*/
    /*****************************************/
    /*新增*/
    $('#btn_add').click(function () {
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('add-form');
        if(flag == true)
        {
            $.msg.confirm('确认修改吗？',function () {

                var blocker = $.pageBlocker.block();
                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');

                var formJson = JSON.stringify(formParams);

                /*提交表单*/
                $.ajaxHelper.doPostSync(saveInfoUrl,formJson,function (res) {
                    if(res != null && res.code == 200){
                        $.msg.ok('修改信息成功！','',function () {
                            $('#btn_reload').click();
                            parent.$('#btn_query').click();
                        });

                    }else {
                        $.msg.error(res.message);
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
        var index = parent.layer.getFrameIndex('modifyVolumeFrame');
        parent.layer.close(index);
        parent.$('#btn_query').click();
    });

    /*重新加载*/
    $('#btn_reload').click(function () {
        initPageFunc();
        $('#volume-input').val('');
        $('#weight-input').val('');
    });

});