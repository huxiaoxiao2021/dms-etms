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
            transportName: {
                //隐藏或显示 该字段的验证

                //错误提示信息
                message: 'This value is not valid',

                // 定义每个验证规则
                validators: {
                    //多个重复
                    //官方默认验证参照  http://bv.doc.javake.cn/validators/
                    // 注：使用默认前提是引入了bootstrapValidator-all.js
                    // 若引入bootstrapValidator.js没有提供常用验证规则，需自定义验证规则哦
                    //<validatorName>: <validatorOptions>
                    notEmpty: {
                        message: '运力名称不能为空'
                    },
                    stringLength: {
                        max: 50,
                        message: '运力名称不得超过50位'
                    }
                }
            },
            planStartDateStr:{
                message: '预计起飞日期不能为空',
                validators: {
                    notEmpty: {

                    }
                }
            },
            transportType:{
                message: '运力类型不能为空',
                validators: {
                    notEmpty: {
                    }
                }
            },
            planStartTimeStr:{
                message: '预计起飞时间不能为空',
                validators: {
                    notEmpty: {

                    }
                }
            },
            planEndTimeStr:{
                message: '预计落地时间不能为空',
                validators: {
                    notEmpty: {
                    }
                }
            },
            remark:{
                validators: {
                    stringLength: {
                        max: 500,
                        message: '备注长度不得超过500'
                    }
                }

            },
            priority:{
                validators: {
                   /* notEmpty: {
                    },*/
                    stringLength: {
                        max: 20,
                        message: '优先级长度不能超过20'
                    }
                }
            },
            gainSpace:{
                message: '可获取舱位不能为空',
                validators: {
                    notEmpty: {
                    },
                    numeric: {
                        message: '只能输入数字'
                    },
                    between: {
                        min: 0.01,
                        max: 9999999999.99,
                        message: '必须在0.01到9999999999.99之间'
                    },
                    regexp: {
                        regexp: /^\d+(\.\d{2})?$/,
                        message: '小数点后仅可保留两位小数'
                    }
                }
            },
            planSpace:{
                message: '计划订舱位不能为空',
                validators: {
                    notEmpty: {
                    },
                    numeric: {
                        message: '只能输入数字'
                    },
                    between: {
                        min: 0.01,
                        max: 9999999999.99,
                        message: '必须在0.01到9999999999.99之间'
                    },
                    regexp: {
                        regexp: /^\d+(\.\d{2})?$/,
                        message: '小数点后仅可保留两位小数'
                    }
                }
            },
            realSpace:{
                message: '实际订舱位不能为空',
                validators: {
                    notEmpty: {
                    },
                    numeric: {
                        message: '只能输入数字'
                    },
                    between: {
                        min: 0.01,
                        max: 9999999999.99,
                        message: '必须在0.01到9999999999.99之间'
                    },
                    regexp: {
                        regexp: /^\d+(\.\d{2})?$/,
                        message: '小数点后仅可保留两位小数'
                    }
                }
            },
            bookingSpaceTimeStr:{
                message: '订舱日期不能为空',
                validators: {
                    notEmpty: {
                    }
                }
            },
            supplierName:{
                //message: '供应商名称不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    stringLength: {
                        max: 50,
                        message: '不得超过50位'
                    }
                }
            },
            phone:{
                //message: '联系电话不能为空',
                validators: {
                    /*notEmpty: {
                    },*/
                    stringLength: {
                        max: 20,
                        message: '不得超过20位'
                    },
                    regexp: {
                        regexp: /^[\d-]*$/,
                        message: '只能数字和-组成'
                    }
                }
            },
            startCityName:{
                message: '起飞城市不能为空',
                validators: {
                    notEmpty: {
                    },
                    stringLength: {
                        max: 20,
                        message: '不得超过20位'
                    }
                }
            },
            endCityName:{
                message: '落地城市不能为空',
                validators: {
                    notEmpty: {
                    },
                    stringLength: {
                        max: 20,
                        message: '不得超过20位'
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
   // $("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'NOT_VALIDATED').validateField(fieldName);

    //$("#edit-form").data("bootstrapValidator").validateField(fieldName);
    if(value){
        $("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'VALID');
    }else{
        $("#edit-form").data("bootstrapValidator").updateStatus(fieldName, 'NOT_VALIDATED','notEmpty');
        $("#edit-form").data("bootstrapValidator").validateField(fieldName);
    }

}