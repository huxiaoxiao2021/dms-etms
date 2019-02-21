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
    $.formValidator.createNew('modify-form',{
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
                        message:'体积为数字且最多两位小数'
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

        $.pageBlocker.close(blocker);
    };


    $('#type-select').on("select2:select", function (e){
        var type =  $('#type-select').val();
        var unitInput = $('#unit-value-input');
        var volumeCoefficientInput =  $('#volume-coefficient-value-input');
        var withInput = $('#with-value-input');
        var lengthInput = $('#length-value-input');
        var heightInput =  $('#height-value-input');

        // unitInput.prop("readonly", true);
        // withInput.prop("readonly", true);
        // lengthInput.prop("readonly", true);
        // heightInput.prop("readonly", true);
        // volumeCoefficientInput.prop("readonly", true);
        // withInput.val(null);
        // lengthInput.val(null);
        // heightInput.val(null);
        // volumeCoefficientInput.val(null);
        // if (type == "TY003" || type == "TY004" || type == "TY005") {
        //     withInput.prop("readonly", false);
        //     lengthInput.prop("readonly", false);
        //     heightInput.prop("readonly", false);
        //     volumeCoefficientInput.prop("readonly", false);
        // }
        var unit = e.params.data.element.unit;
        if (unit != null) {
            unitInput.val(unit);
        } else {
            unitInput.val(null);
            unitInput.prop("readonly", false);
        }

        $('#type-name-value-input').val(e.params.data.element.typeName);

    });

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

                var withValue = $('#with-value-input').val();
                var lengthValue = $('#length-value-input').val();
                var heightValue =  $('#height-value-input').val();

                /*获取参数*/
                var formParams = $.formHelper.serialize('modify-form');
                formParams.name = formParams.name.trim();
                formParams.type = formParams.type.trim();
                formParams.volumeCoefficient = formParams.volumeCoefficient.trim();

                // if (formParams.volumeCoefficient == null || formParams.volumeCoefficient == "") {
                //     $.msg.warn('类型为' + formParams.typeName + '必须录入体积系数！');
                //     $.pageBlocker.close(blocker);
                //     return;
                // }
                if (withValue != null && withValue !="" && lengthValue != null && lengthValue !="" && heightValue != null && heightValue != "") {
                    formParams.volume = withValue * lengthValue * heightValue;
                    formParams.specification = lengthValue + "*" + withValue + "*" + heightValue;
                } else {
                    if (formParams.type == "TY001") {
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