$(function () {

/**************************************************************************************/
/*常量及变量*/
/**************************************************************************************/

    var WEIGHT_MAX = 999999.99;
    var VOLUME_MAX = 999.99;

    var waybill_weight_validate_url     = '/b2b/express/weight/verifyWaybillReality';
    var waybill_weight_insert_url       = '/b2b/express/weight//insertWaybillWeight';
    var waybill_weight_convert_url      = '/b2b/express/weight/convertCodeToWaybillCode';

/**************************************************************************************/
/*扩展方法*/
/**************************************************************************************/
    $.extend($.fn.validatebox.defaults.rules, {
        equals: {
            validator: function(value,param){
                return value == $(param[0]).val();
            },
            message: 'Field do not match.'
        },
        onlyNum:{
            validator:function(value,param){
                var reg = /^\d+$/g;
                return reg.test(value);
            },
            message:  '只能输入数字！'
        }
    });

    // validType:'onlyNum',

/**************************************************************************************/
/*公共方法*/
/**************************************************************************************/

    var involkPostSync = function(apiUrl,paramObject,successFunc){
        $.ajax({
            type:'post',
            data:paramObject,
            url:apiUrl,
            async:false,
            dataType:'json',
            error:function(XMLHttpRequest, textStatus, errorThrown){
                console.log('involk failded');
            },
            success:function(res){
                successFunc(res);
                return res;
            }
        });
    };

    /*验证运单号存在性*/
    var validateCodeFunc = function(codeStr,successFunc){
        if(codeStr == '')
        {
            $.messager.alert('运单号输入有误','运单号或包裹号为空');
            return;
        }

        var param = {codeStr:codeStr};
        involkPostSync(waybill_weight_validate_url,param,successFunc);

    };

/**************************************************************************************/
/*组件初始化*/
/**************************************************************************************/

    /*表单*/
    $('#waybill-weight-form').form({});

    /*运单号输入*/
    $('#waybill-weight-code-input').textbox({
        value:'',
        required: true,
        missingMessage:'请您输入运单号',
        buttonText:'查询此运单信息是否存在',
        width:300,
        onClickButton:function(){
            var codeStr = $(this).textbox('getValue').trim();

            var successFunc = function(res){
                var isExists = res.data;
                if(isExists)
                {
                    $.messager.alert('运单验证结果','存在运单相关信息，可进行录入操作','info');
                }else
                {
                    if(res.code == 400)
                    {
                        $.messager.alert('单号格式有误','单号输入有误，请您检查单号！','error');
                    }else{
                        $.messager.alert('运单验证结果','不存在运单相关信息，请确认运单真实性再录入操作','warning');
                    }

                }
            };

            validateCodeFunc(codeStr,successFunc);

        }
    })

    /*重量输入*/
    $('#waybill-weight-kg-input').numberbox({
        value:null,
        required: true,
        missingMessage:'请您输入该运单总体重量 单位：千克',
        min:0,
        max:WEIGHT_MAX,
        precision:2,
        suffix:'千克'
    });

    /*体积输入*/
    $('#waybill-weight-cbm-input').numberbox({
        value:null,
        required: true,
        missingMessage:'请您输入该运单总体体积 单位：立方米',
        min:0,
        max:VOLUME_MAX,
        precision:2,
        suffix:'立方米'

    });

    /*成功记录datagrid*/
    $('#waybill-weight-success-datagrid').datagrid({
        singleSelect:true,
        columns:[[
            {field:'waybillCode',title:'运单号',width:300},
            {field:'weight',title:'总重量/千克',width:200},
            {field:'volume',title:'总体积/立方米',width:200},
            {field:'statusText',title:'录入方式',width:200},
            {field:'memo',title:'备注说明',width:300},
        ]]
    });

/**************************************************************************************/
/*提交操作*/
/**************************************************************************************/

    /*提交按钮*/
    $('#waybill-weight-btn').linkbutton({
        iconCls: 'icon-search',
        onClick:function(){

            /*验证参数非空*/
            var isValid = $('#waybill-weight-form').form('validate');
            if(isValid)
            {
                var codeStr = $('#waybill-weight-code-input').textbox('getValue').trim();
                var weight = $('#waybill-weight-kg-input').numberbox('getValue');
                var cbm = $('#waybill-weight-cbm-input').numberbox('getValue');

                var insertParam = {
                    codeStr:codeStr,
                    weight:weight,
                    volume:cbm
                };

                var param = {codeStr:codeStr};
                /*调用验证方法验证单号是否合法、是否存在*/
                involkPostSync(waybill_weight_validate_url,param,function (res) {
                    var isExists = res.data;

                    /*运单存在*/
                    if(isExists)
                    {
                        // $('#waybill-weight-success-datagrid').datagrid('appendRow',{
                        //     weight:weight
                        // });
                        $.messager.alert('运单录入结果','运单称重量方记录已成功录入','info');
                    }else
                    {
                        /*单号不合法*/
                        if(res.code == 400)
                        {
                            insertParam.status = 20;
                            $.messager.alert('单号格式有误','单号输入有误，请您检查单号！','error');
                            return;
                        }

                        /*单号不存在*/
                        $.messager.confirm('无运单信息','您输入的运单号/包裹号无相关运单信息，' + '请问您确认要录入吗？'
                            ,function(confirmFlag){
                                if(confirmFlag)
                                {
                                    insertParam.status = 20;

                                    involkPostSync(waybill_weight_insert_url,insertParam,function(res){
                                        console.log(res);
                                    });

                                    $.messager.alert('运单录入结果','运单相关信息不存在，已转为离线录入','info');
                                    // waybill_weight_convert_url

                                    insertParam.statusText = '离线录入';
                                    insertParam.memo = '进行称重量方信息录入时无运单信息，转为离线录入';
                                    involkPostSync(waybill_weight_convert_url,param,function(res){
                                        insertParam.waybillCode = res.data;
                                    });


                                    $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                                }
                            });
                    }
                });
            }

        }

    });

/**************************************************************************************/





});