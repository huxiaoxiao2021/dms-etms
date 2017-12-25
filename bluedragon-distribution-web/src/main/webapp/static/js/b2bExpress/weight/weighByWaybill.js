/**********************************************************/
/* 运单称重  luyue  2017-12 */
/**********************************************************/
$(function () {

/**************************************************************************************/
/*常量及变量*/
/**************************************************************************************/

    var WEIGHT_MAX = 999999.99;
    var VOLUME_MAX = 999.99;

    var waybill_weight_validate_url     = '/b2b/express/weight/verifyWaybillReality';
    var waybill_weight_insert_url       = '/b2b/express/weight//insertWaybillWeight';
    var waybill_weight_convert_url      = '/b2b/express/weight/convertCodeToWaybillCode';

    var SERVER_ERROR_CODE = 500;
    var ERROR_PARAM_RESULT_CODE = 400;

    var VALID_EXISTS_STATUS_CODE = 10;
    var VALID_NOT_EXISTS_STATUS_CODE = 20;


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
                console.log('invoke failded');
            },
            success:function(res){
                successFunc(res);
                return res;
            }
        });
    };

    /*验证运单号存在性*/
    var validateCodeFunc = function(codeStr,successFunc){
        if(codeStr == '' || codeStr == null)
        {
            $.messager.alert('运单号输入有误','运单号或包裹号为空');
            return;
        }

        var param = {codeStr:codeStr};
        involkPostSync(waybill_weight_validate_url,param,successFunc);

    };

    var clearInputContentsFunc = function () {
        $('#waybill-weight-code-input').textbox('clear');
        $('#waybill-weight-kg-input').numberbox('clear');
        $('#waybill-weight-cbm-input').numberbox('clear');
        $("input",$("#waybill-weight-code-input").next("span")).focus();
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
                console.log(res);
                if(isExists)
                {
                    $.messager.alert('运单验证结果','存在运单相关信息，可进行录入操作','info');
                }else
                {
                    if(res.code == ERROR_PARAM_RESULT_CODE)
                    {
                        $.messager.alert('单号格式有误','快运外单单号输入有误，请您检查单号！','error');
                    }else if(res.code == SERVER_ERROR_CODE)
                    {
                        $.messager.alert('运单验证结果','运单查询服务暂不可用，请操作人员务必确认运单真实性再进行录入操作','warning');
                    } else{
                        $.messager.alert('运单验证结果','不存在运单相关信息，请确认运单真实性再录入操作','warning');
                    }

                }
            };

            validateCodeFunc(codeStr,successFunc);
        }
    });

    /*重量输入*/
    $('#waybill-weight-kg-input').numberbox({
        value:null,
        required: true,
        missingMessage:'请您输入该运单总体重量 单位：千克',
        min:0,
        // max:WEIGHT_MAX,
        precision:2,
        suffix:'千克'
    });

    /*体积输入*/
    $('#waybill-weight-cbm-input').numberbox({
        value:null,
        required: true,
        missingMessage:'请您输入该运单总体体积 单位：立方米',
        min:0,
        // max:VOLUME_MAX,
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
            {field:'memo',title:'备注说明',width:500}
        ]]
    });


/**************************************************************************************/
/*组件扩展绑定事件*/
/**************************************************************************************/

    $("input",$("#waybill-weight-code-input").next("span")).focus();
    $("input",$("#waybill-weight-code-input").next("span")).keyup(function(e){
        var currKey=0,e=e||event;
        currKey=e.keyCode||e.which||e.charCode;
        // var keyName = String.fromCharCode(currKey);
        // console.log('按键码: ' + currKey + ' 字符: ' + keyName);
        if(currKey == 13)
        {
            $("input",$("#waybill-weight-kg-input").next("span")).focus();
        }
    });

    $("input",$("#waybill-weight-kg-input").next("span")).keyup(function(e){
        var currKey=0,e=e||event;
        currKey=e.keyCode||e.which||e.charCode;

        if(currKey == 13)
        {
            $("input",$("#waybill-weight-cbm-input").next("span")).focus();
        }
    });

    $("input",$("#waybill-weight-cbm-input").next("span")).keyup(function(e){
        var currKey=0,e=e||event;
        currKey=e.keyCode||e.which||e.charCode;

        if(currKey == 13)
        {
            $('#waybill-weight-btn').click();
        }

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

                    if(isExists)
                    {
                        /*******************************************************************************/
                        /*运单存在*/
                        /*******************************************************************************/
                        $('#waybill-weight-btn').linkbutton('disable');

                        insertParam.status = VALID_EXISTS_STATUS_CODE;

                        involkPostSync(waybill_weight_insert_url,insertParam,function(res){
                            console.log(res);
                            if(res.code == ERROR_PARAM_RESULT_CODE)
                            {
                                $.messager.alert('错误','重量或体积参数输入有误，达到最大值！','error');
                                $('#waybill-weight-btn').linkbutton('enable');
                            } else if(res.data == false)
                            {
                                /*录入失败*/
                                $.messager.alert('运单录入结果','称重信息录入失败,请稍后重试 （错误：' + res.message + ')','info');
                                $('#waybill-weight-btn').linkbutton('enable');
                            } else if(res.code == SERVER_ERROR_CODE && res.message == "toTask")
                            {
                                /*MQ服务不可用时，转为task重试*/
                                $.messager.alert('运单录入结果','进行称重量方信息录入时存在运单信息，但消息发送失败，已转为离线录入','info');

                                insertParam.statusText = '离线录入';
                                insertParam.memo = '进行称重量方信息录入时存在运单信息，但消息发送失败，已转为离线录入';
                                involkPostSync(waybill_weight_convert_url,param,function(res){
                                    insertParam.waybillCode = res.data;
                                });

                                $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                                $('#waybill-weight-btn').linkbutton('enable');

                                /*为方便输入 清空输入内容*/
                                clearInputContentsFunc();
                            } else
                            {
                                /*录入成功*/
                                insertParam.statusText = '在线录入';
                                insertParam.memo = '进行称重量方信息录入时存在运单信息，正常录入';

                                involkPostSync(waybill_weight_convert_url,param,function(res){
                                    insertParam.waybillCode = res.data;
                                });

                                $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);
                                $.messager.alert('运单录入结果','运单称重量方记录已录入成功！','info');
                                $('#waybill-weight-btn').linkbutton('enable');

                                /*为方便输入 清空输入内容*/
                                clearInputContentsFunc();
                            }
                        });
                        /*******************************************************************************/
                    }else
                    {
                        /*******************************************************************************/
                        /*运单不存在*/
                        /*******************************************************************************/

                        /*单号不合法*/
                        if(res.code == ERROR_PARAM_RESULT_CODE)
                        {
                            $.messager.alert('单号格式有误','快运外单单号输入有误，请您检查单号！','error');
                            return;
                        }

                        /*单号通过正则校验、但单号对应运单不存在*/
                        $.messager.confirm('无运单信息','您输入的运单号/包裹号无相关运单信息，' + '请问您确认要录入吗？'
                            ,function(confirmFlag){
                                if(confirmFlag)
                                {
                                    insertParam.status = VALID_NOT_EXISTS_STATUS_CODE;
                                    involkPostSync(waybill_weight_insert_url,insertParam,function(res){
                                        if(res.code == ERROR_PARAM_RESULT_CODE)
                                        {
                                            $.messager.alert('错误','重量或体积参数输入有误，超过最大值！','error');
                                        }
                                        else if(res.data == false || res.data == undefined)
                                        {
                                            /*录入失败*/
                                            $.messager.alert('运单录入结果','称重信息录入失败,请稍后重试 （错误：' + res.message + ')','info');
                                        } else if(res.code == SERVER_ERROR_CODE && res.message == "toTask")
                                        {
                                            /*MQ服务不可用时，转为task重试*/
                                            $.messager.alert('运单录入结果','进行称重量方信息录入不存在运单信息，且消息发送失败，已转为离线录入','info');

                                            insertParam.statusText = '离线录入';
                                            insertParam.memo = '进行称重量方信息录入不存在运单信息，且消息发送失败，已转为离线录入';
                                            involkPostSync(waybill_weight_convert_url,param,function(res){
                                                insertParam.waybillCode = res.data;
                                            });

                                            $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                                            $('#waybill-weight-btn').linkbutton('enable');

                                            /*为方便输入 清空输入内容*/
                                            clearInputContentsFunc();
                                        }
                                        else
                                        {
                                            /*录入成功*/
                                            $.messager.alert('运单录入结果','运单相关信息不存在，已转为离线录入','info');
                                            insertParam.statusText = '离线录入';
                                            insertParam.memo = '进行称重量方信息录入时无运单信息，转为离线录入';
                                            involkPostSync(waybill_weight_convert_url,param,function(res){
                                                insertParam.waybillCode = res.data;
                                            });
                                            $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                                            /*为方便输入 清空输入内容*/
                                            clearInputContentsFunc();
                                        }
                                    });
                                }
                            }
                        );
                        /*******************************************************************************/
                    }
                });
            }

        }

    });

/**************************************************************************************/





});