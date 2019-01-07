function editFormValidator(){
    $("#edit-form").bootstrapValidator({
        /**
         *  指定不验证的情况
         *  值可设置为以下三种类型：
         *  1、String  ':disabled, :hidden, :not(:visible)'
         *  2、Array  默认值  [':disabled', ':hidden', ':not(:visible)']
         *  3、带回调函数
         [':disabled', ':hidden', function($field, validator) {
            // $field 当前验证字段dom节点
            // validator 验证实例对象
            // 可以再次自定义不要验证的规则
            // 必须要return，return true or false;
            return !$field.is(':visible');
        }]
         */
        excluded: [':disabled', ':hidden', ':not(:visible)'],
        /**
         * 指定验证后验证字段的提示字体图标。（默认是bootstrap风格）
         * Bootstrap 版本 >= 3.1.0
         * 也可以使用任何自定义风格，只要引入好相关的字体文件即可
         * 默认样式
         .form-horizontal .has-feedback .form-control-feedback {
            top: 0;
            right: 15px;
        }
         * 自定义该样式覆盖默认样式
         .form-horizontal .has-feedback .form-control-feedback {
            top: 0;
            right: -15px;
        }
         .form-horizontal .has-feedback .input-group .form-control-feedback {
            top: 0;
            right: -30px;
        }
         */
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        /**
         * 生效规则（三选一）
         * enabled 字段值有变化就触发验证
         * disabled,submitted 当点击提交时验证并展示错误信息
         */
        live: 'enabled',
        /**
         * 为每个字段指定通用错误提示语
         */
        message: 'This value is not valid',
        /**
         * 指定提交的按钮，例如：'.submitBtn' '#submitBtn'
         * 当表单验证不通过时，该按钮为disabled
         */
        submitButtons: 'button[type="submit"]',
        /**
         * submitHandler: function(validator, form, submitButton) {
    *   //validator: 表单验证实例对象
    *   //form  jq对象  指定表单对象
    *   //submitButton  jq对象  指定提交按钮的对象
    * }
         * 在ajax提交表单时很实用
         *   submitHandler: function(validator, form, submitButton) {
            // 实用ajax提交表单
            $.post(form.attr('action'), form.serialize(), function(result) {
                // .自定义回调逻辑
            }, 'json');
         }
         *
         */
        submitHandler: null,
        /**
         * 为每个字段设置统一触发验证方式（也可在fields中为每个字段单独定义），默认是live配置的方式，数据改变就改变
         * 也可以指定一个或多个（多个空格隔开） 'focus blur keyup'
         */
        trigger: null,
        /**
         * Number类型  为每个字段设置统一的开始验证情况，当输入字符大于等于设置的数值后才实时触发验证
         */
        threshold: null,
        /**
         * 表单域配置
         */
        fields: {
            //多个重复
            excpTimeStr: {
                //隐藏或显示 该字段的验证

                //错误提示信息
                message: '异常日期不能为空',

                validators: {

                    notEmpty: {

                    }
                }
            },
            excpNode:{
                message: '异常节点不能为空',
                validators: {
                    notEmpty: {
                    }
                }
            },
            transportName:{
                validators: {
                    stringLength: {
                        max: 50,
                        message: '不得超过50'
                    }
                }
            },
            siteOrder:{
                message: '铁路站序不能为空',
                validators: {
                    stringLength: {
                        max: 50,
                        message: '不得超过50'
                    }/*,
                    callback: {
                        message: '请填写铁路站序和航空单号中的一个字段',
                        callback:function(value, validator,$field){
                            if(value && $("#edit-form #orderCode").val()){
                                return false;
                            }else if(!(value || $("#edit-form #orderCode").val() )){
                                return false;
                            }
                            return true;
                        }
                    }*/
                }
            },
            orderCode:{
                message: '航空单号不能为空',
                validators: {
                    stringLength: {
                        max: 50,
                        message: '不得超过50'
                    }/*,
                    callback: {
                        message: '请填写铁路站序和航空单号中的一个字段',
                        callback:function(value, validator,$field){
                            if(value && $("#edit-form #siteOrder").val()){
                                return false;
                            }else if(!(value || $("#edit-form #siteOrder").val() )){
                                return false;
                            }
                            return true;
                        }
                    }*/
                }
            },
            excpTypeEdit:{
                message:'异常类型不能为空',
                validators: {
                    notEmpty: {
                    }
                }

            },
            excpReasonEdit:{
                message:'异常原因不能为空',
                validators: {
                    notEmpty: {
                    }
                }

            },
            excpResultEdit:{
                message:'异常结果不能为空',
                validators: {
                    notEmpty: {
                    }
                }
            },
            remark:{

                validators: {

                    stringLength: {
                        max: 500,
                        message: '不得超过10位'
                    }
                }
            },
            excpCity:{
                message: '发现异常城市不能为空',
                validators: {
                    notEmpty: {
                    },
                    stringLength: {
                        max: 20,
                        message: '不得超过20'
                    }
                }
            },
            startCityName:{
                //message: '起飞城市不能为空',
                validators: {

                    stringLength: {
                        max: 20,
                        message: '不得超过20位'
                    }
                }
            },
            endCityName:{
                //message: '落地城市不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    stringLength: {
                        max: 20,
                        message: '不得超过20位'
                    }
                }
            },
            planStartTimeStr:{
                //message: '起飞时间不能为空',
                validators: {
                    /*notEmpty: {
                    }*/
                }
            },
            planEndTimeStr:{
                //message: '落地时间不能为空',
                validators: {
                    /*notEmpty: {
                    }*/
                }
            },
            sendCode:{
                //message: '发货批次号不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    stringLength: {
                        max: 50,
                        message: '不得超过50位'
                    }
                }
            },
            excpNum:{
                //message: '异常件数不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    digits: {
                        message: '该值只能包含数字'
                    },
                    stringLength: {
                        max: 10,
                        message: '不得超过10位'
                    }
                }
            },
            excpPackageNum:{
                //message: '异常包裹数不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    digits: {
                        message: '该值只能包含数字'
                    },
                    stringLength: {
                        max: 10,
                        message: '不得超过10位'
                    }
                }
            },
            operatorErp:{
                //message: '现场操作人不能为空',
                validators: {
                   /* notEmpty: {
                    },*/
                    stringLength: {
                        max: 50,
                        message: '不得超过50位'
                    }
                }
            }
        }
    });
}

function editValidator(){
    $("#edit-form").data('bootstrapValidator').validate();

    return $("#edit-form").data("bootstrapValidator").isValid();
}

function resetFieldValidator(value,fieldName){

    //$("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'NOT_VALIDATED').validateField(fieldName);
    if(value){
        $("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'VALID');
    }else{
        $("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'NOT_VALIDATED','notEmpty');
        $("#edit-form").data("bootstrapValidator").validateField(fieldName);
    }

}