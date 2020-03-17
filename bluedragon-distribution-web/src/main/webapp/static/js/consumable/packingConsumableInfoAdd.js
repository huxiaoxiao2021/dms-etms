/*****************************************/
/*增加本地服务器信息*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*区域*/

    var saveInfoUrl = '/consumable/packingConsumableInfo/save';

    var allTypeUrl = '/consumable/packingConsumableInfo/getAllPackingType';

    // 分拣物资类型
    let type_sorting_material = 'TY010';

    /*****************************************/
    /*组件*/
    /*****************************************/

    $.combobox.createNew('type-select',{
        width: '200',
        placeholder:'请选择耗材类型',
        data:[
        ]
    });

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
            },
            length: {
                validators: {
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,2})?$/,
                        message:'长为数字且最多两位小数'
                    }
                }

            },
            with: {
                validators: {
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,2})?$/,
                        message:'宽为数字且最多两位小数'
                    }
                }

            },
            height: {
                validators: {
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,2})?$/,
                        message:'高为数字且最多两位小数'
                    }
                }

            },
            volumeCoefficient: {
                validators: {
                    regexp:{
                        regexp:/^[0-9]+(.[0-9]{1,2})?$/,
                        message:'体积系数为数字且最多两位小数'
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
        $.ajaxHelper.doPostSync(allTypeUrl,null,function(res){
            if(res != null && res.code != 200) {
                $.msg.error("获取类型列表失败")
                return;
            }
            for(i in res.data){
                var dataArr = res.data[i].split(",");
                var option = $.combobox.appendData('type-select',i,dataArr[0]);
                option.typeName = dataArr[0];
                if (dataArr.length > 1) {
                    option.unit = dataArr[1];
                }
                else {
                    option.unit = null;
                }
            }
        });
        $.combobox.clearAllSelected('type-select');
        $.pageBlocker.close(blocker);
    };

    initPageFunc();

    $('#type-select').on("select2:select", function (e){
        var unitInput = $('#unit-value-input');

        var unit = e.params.data.element.unit;
        if (unit != null) {
            unitInput.val(unit);
        } else {
            unitInput.val(null);
            unitInput.prop("readonly", false);
        }

        $('#type-name-value-input').val(e.params.data.element.typeName);

        let type =  $('#type-select').val();
        if (type == type_sorting_material) {
            $('#volumeCoefficientDiv').hide();
            $('#unitDiv').hide();
            $('#weightDiv').show();
        }
        else {
            $('#weightDiv').hide();
        }

    });
    /*****************************************/
    /*按钮动作*/
    /*****************************************/
    /*新增*/
    $('#btn_add').click(function () {

        // 分拣物资类型不需要录入单位，跳过表单验证
        if (formParams.type == type_sorting_material) {
            $('#unit-value-input').val("");
            $('#volume-coefficient-value-input').val(1);
        }

        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('add-form');
        if(flag == true)
        {
            $.msg.confirm('确认新增吗？',function () {

                var blocker = $.pageBlocker.block();

                var withValue = $('#with-value-input').val();
                var lengthValue = $('#length-value-input').val();
                var heightValue =  $('#height-value-input').val();

                /*获取参数*/
                var formParams = $.formHelper.serialize('add-form');
                formParams.name = formParams.name.trim();
                formParams.type = formParams.type.trim();
                formParams.volumeCoefficient = formParams.volumeCoefficient.trim();

                // 分拣物资特殊逻辑
                if (formParams.type == type_sorting_material) {
                    if (formParams.weight == undefined || formParams.weight == null) {
                        $.msg.warn('类型为【' + formParams.typeName + ']必须录入重量！');
                        $.pageBlocker.close(blocker);
                        return;
                    }
                    else {
                        let regex = new RegExp("^[0-9]*$");
                        if (!regex.test(formParams.weight)) {
                            $.msg.warn('重量必须为数字！');
                            $.pageBlocker.close(blocker);
                            return;
                        }
                    }
                }

                if (withValue != null && withValue !="" && lengthValue != null && lengthValue !="" && heightValue != null && heightValue != "") {
                    formParams.volume = withValue * lengthValue * heightValue;
                    formParams.specification = lengthValue + "*" + withValue + "*" + heightValue;
                } else {
                    if (formParams.type == "TY001" || formParams.type == type_sorting_material) {
                        $.msg.warn('类型为【' + formParams.typeName + ']需要计算标准体积' + '必须录入长、宽、高！');
                        $.pageBlocker.close(blocker);
                        return;
                    }
                }
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