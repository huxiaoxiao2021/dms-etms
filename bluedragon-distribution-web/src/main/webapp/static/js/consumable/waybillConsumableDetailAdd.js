/*****************************************/
/*增加本地服务器信息*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*区域*/

    var saveInfoUrl = '/consumable/waybillConsumableRelation/save';

    var getCodesUrl = '/consumable/dmsConsumableRelation/getConsumableInfoList';
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
            confirmQuantity: {
                validators: {
                    notEmpty: {
                        message: '确认数量不能为空！'
                    },
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,3})?$/,
                        message:'数量为数字且最多三位小数'
                    }
                }
            },
            packUserErp: {
                validators: {
                    notEmpty: {
                        message: '打包人ERP不能为空！'
                    }
                }
            }
        }
    });

    /*日志类型选择*/
    $.combobox.createNew('code-select',{
        width: '150',
        placeholder:'请选择耗材编号',
        data:[
        ]
    });
    /*****************************************/
    /*页面初始化*/
    /*****************************************/
    var initPageFunc = function () {
        var blocker = $.pageBlocker.block();
        $.ajaxHelper.doGetSync(getCodesUrl,{},function(res){
            if(res.code != 200)
            {
                $.msg.error("获取耗材编号列表失败");
                $.pageBlocker.close(blocker);
                return;
            }
            for(var i in res.data){
                var data = res.data;
                var option = $.combobox.appendData('code-select',data[i].packingCode,data[i].packingCode);
                option.name= data[i].packingName;
                option.type= data[i].packingType;
                option.typeName= data[i].packingTypeName;
                option.volume= data[i].packingVolume;
                option.volumeCoefficient= data[i].volumeCoefficient;
                option.specification= data[i].packingSpecification;
                option.unit= data[i].packingUnit;

                $.combobox.clearAllSelected('code-select');
            }
        });
        $.pageBlocker.close(blocker);
    };

    initPageFunc();

    /**
     * 选中赋值
     */
    $('#code-select').on("select2:select", function (e) {

        var name = e.params.data.element.name;
        var type = e.params.data.element.type;
        var typeName = e.params.data.element.typeName;
        var volume = e.params.data.element.volume;
        var volumeCoefficient = e.params.data.element.volumeCoefficient;
        var specification = e.params.data.element.specification;
        var unit = e.params.data.element.unit;

        $('#name-value-input').val(name);
        $('#type-value-input').val(typeName);
        $('#volume-value-input').val(volume);
        $('#volume-coefficient-value-input').val(volumeCoefficient);
        $('#specification-value-input').val(specification);
        $('#unit-value-input').val(unit);
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
            $.msg.confirm('确认新增吗？',function () {

                var blocker = $.pageBlocker.block();
                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');

                var formJson = JSON.stringify(formParams);

                /*提交表单*/
                $.ajaxHelper.doPostSync(saveInfoUrl,formJson,function (res) {
                    if(res != null && res.code == 200){
                        $.msg.ok('新增信息成功！','',function () {
                            $('#btn_cancel').click();
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
        var index = parent.layer.getFrameIndex('addInfoFrame');
        parent.layer.close(index);
    });

});