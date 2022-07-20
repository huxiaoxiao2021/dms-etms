/**********************************************************/
/* 运单称重  luyue  2017-12 */
/**************************************************************************************/
/*常量及变量*/
/**************************************************************************************/

    var WEIGHT_MAX = 999999.99;
    var VOLUME_MAX = 999.99;

    var CBM_DIV_KG_MIN_LIMIT = 168.0;
    var CBM_DIV_KG_MAX_LIMIT = 330.0;
    var waybill_weight_validate_url_package     = '/b2b/express/weightpackage/verifyWaybillReality';
    var waybill_weight_insert_url_package='/b2b/express/weightpackage/insertWaybillWeightPackage';
    var waybill_weight_improtbypackage_url ='/b2b/express/weightpackage/uploadExcelByPackage'; //包裹维度批量导入


    var SERVER_ERROR_CODE = 500;
    var ERROR_PARAM_RESULT_CODE = 400;
    var ERROR_HALF_RESULT_CODE = 600; //部分成功
    var SERVER_SUCCESS_CODE = 200;
    var INTERCEPT_CODE = 300;
    var INTERCEPT_MESSAGE = "根据重量体积信息已经转至C网进行后续操作，请操作【包裹补打】更换面单，否则无法操作建箱及发货";

    var VALID_EXISTS_STATUS_CODE = 10;
    var VALID_NOT_EXISTS_STATUS_CODE = 20;
    var NO_NEED_WEIGHT = 201; //不需要称重
    var WAYBILL_STATES_FINISHED=202; //
    var KAWAYBILL_NEEDPACKAGE_WEIGHT=203;//KA 需要包裹维度称重量方
    var JP_FORBID_WEIGHT = 204; // 集配场地揽收后禁止称重

    var forcedToSubmitCount = 0 ; //强制提交
    var errorData = []; //导入失败记录

    var CBM_DIV_KG_CODE = 10001; //批量导入 重泡比 校验失败码值

    var allForcedToSubmit = 0; // 批量强制提交。 0 代表否 1代表是
    var b2cCount = 0; //批量强制提交场景下需要转网的单的个数


/******************************************按包裹维度批量录入数据********************************************************/
   /*批量导入按钮*/
   $('#waybill-weight-improtbypackage-btn').linkbutton({
       iconCls: 'icon-add',
       onClick:function(){
           forcedToSubmitCount = 0 ; //初始化强制提交数量
           errorData = [] ; //初始化失败记录数据
           $("#waybill-weight-importbypackage-dialog").dialog('open');
       }
   });


   /*确定导入按钮*/
   $("#waybill-weight-improtbypackage-form-submit").click(function () {

       $("#waybill-weight-improtbypackage-form-submit").linkbutton('disable'); //锁住导入按钮
       $("#waybill-weight-improtbypackage-fail-message").html("");
       $("#waybill-weight-improtbypackage-fail-submit-message").html("");
       var fileInput = $("#importbyPackageExcelFile");
       if(!checkFileInput(fileInput)) { //其他操作
        $("#waybill-weight-improtbypackage-form-submit").linkbutton('enable'); //解锁导入按钮
        return;
       }
       $('#waybill-weight-importbypackage-form').form('submit', {
           url:waybill_weight_improtbypackage_url,
           success:function(data){
               $("#waybill-weight-improtbypackage-form-submit").linkbutton('enable'); //解锁导入按钮

               var data = eval('('+data+')');

               if(data.code==SERVER_SUCCESS_CODE){
                   $("#waybill-weight-importbypackage-dialog").dialog('close');
                   $.messager.alert('导入成功','全部导入成功,本次导入'+data.data.successCount+'条数据！');
                   showWarnDataPackage(data.data.warnList);
               }else if(data.code==ERROR_HALF_RESULT_CODE){
                  //部分成功
                   $("#waybill-weight-importbypackage-dialog").dialog('close');
                   $("#waybill-weight-improtbypackage-fail-message").html("部分导入成功，共导入"+data.data.count+"条数据，其中成功"+data.data.successCount+"条数据，失败"+data.data.errorCount+"条数据");
                   showWarnDataPackage(data.data.warnList);
                   showFailDataPackage(data.data.errorList);
               }else{
                   $.messager.alert('导入异常',data.message);
               }
               //展示成功数据
               if(data.data.successList && data.data.successList.length >0){
                   //
                   var successRows = data.data.successList;
                   for(var i in successRows){
                       $('#waybill-weight-success-datagrid').datagrid('appendRow',{
                           waybillCode:successRows[i].codeStr,
                           weight:successRows[i].weight,
                           volume:successRows[i].volume,
                           statusText:'按包裹维度批量导入',
                           memo:successRows[i].errorMessage
                       });
                   }
               }
           }
       });
   });


   $("#waybill-weight-improtbypackage-form-close").click(function () {
       $("#waybill-weight-importbypackage-dialog").dialog('close');
   });
    /******************************************按包裹维度批量录入数据********************************************************/
    /******************************************按包裹维度批量录入数据-强制提交操作********************************************************/
    /**
         * 强制提交 批量导出失败后的触发方法
         * @param waybillCode
         * @param weight
         * @param volume
         * @param index
         */
        function forcedToSubmitPackage(waybillCode,weight,volume,index,packageLength,packageWidth,packageHigh,status){
            //存在标识获取
            var isExist = status==VALID_EXISTS_STATUS_CODE;
            //组装提交数据
            var insertParam = {
                codeStr:waybillCode,
                weight:weight,
                volume:volume,
                packageLength:packageLength,
                packageWidth:packageWidth,
                packageHigh:packageHigh,
                status:status
            };

            if(allForcedToSubmit == 1){
                // 批量强制提交 调用的时候
                if(isExist){
                    //是真正意义上的存在
                    existSubmitPackage(insertParam,removeFailDataFuncPackage,index);
                }else{
                    noExistSubmitPackage(insertParam,removeFailDataFuncPackage,index);
                }

            }else{
                // 单独点击强制提交 调用的时候
                var messageBodyStr = "包裹号："+waybillCode+" 重量："+weight+" 体积："+volume;
                $.messager.confirm('请您仔细确认',messageBodyStr
                    ,function(confirmFlag){
                        if(confirmFlag == true){

                            if(isExist){
                                //此处返回的存在并不是真正意义上的存在
                                doWaybillWeightPackage(insertParam,removeFailDataFuncPackage,index);
                            }else{
                                noExistSubmitPackage(insertParam,removeFailDataFuncPackage,index);
                            }
                            //会自动把errorData的数据移除掉一条

                            /*提交业务流程*/
                            $.messager.alert('提交成功','提交成功');
                            return;
                        }else {
                            return;
                        }
                    }
                );

            }
        }

        /**
         * 运单存在调用逻辑 抽象出来供批量导入使用
         * @param insertParam
         */
        function existSubmitPackage(insertParam,removeFailData,removeIndex){

                involkPostSync(waybill_weight_insert_url_package,insertParam,function(res){
                    // console.log(res);
                    if(res.code == ERROR_PARAM_RESULT_CODE)
                    {
                        $.messager.alert('错误','重量或体积参数输入有误，达到最大值，或者重量体积录入值为0！','error');
                        $('#waybill-weight-btn').linkbutton('enable');
                    } else if(res.data == false)
                    {
                        /*录入失败*/
                        $.messager.alert('运单录入结果','称重信息录入失败,请稍后重试 （错误：' + res.message + ')','info');
                        $('#waybill-weight-btn').linkbutton('enable');
                    } else if(res.code == SERVER_ERROR_CODE && res.message == "toTask")
                    {
                        /*MQ服务不可用时，转为task重试*/
                       /* $.messager.alert('包裹录入结果','进行称重量方信息录入时存在运单信息，但消息发送失败，已转为离线录入','info');

                        insertParam.statusText = '离线录入';
                        insertParam.memo = '进行称重量方信息录入时存在运单信息，但消息发送失败，已转为离线录入';
                        involkPostSync(waybill_weight_convert_url,{codeStr:insertParam.codeStr},function(res){
                            insertParam.waybillCode = res.data;
                        });

                        $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                        $('#waybill-weight-btn').linkbutton('enable');

                        *//*为方便输入 清空输入内容*//*
                        clearInputContentsFunc();*/
                    } else if(res.code == INTERCEPT_CODE)
                    {
                        /*录入成功*/
                        insertParam.statusText = '按包裹维度录入';
                        insertParam.memo = res.message;
                        insertParam.waybillCode = insertParam.codeStr;
                        /*involkPostSync(waybill_weight_convert_url,{codeStr:insertParam.codeStr},function(res){
                            insertParam.waybillCode = res.data;
                        });*/

                        $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);
                        if(allForcedToSubmit == 0){
                            $.messager.alert('运单录入结果',res.message);
                        }
                        else if(allForcedToSubmit == 1){
                            b2cCount=b2cCount+1;
                        }
                        $('#waybill-weight-btn').linkbutton('enable');

                        /*为方便输入 清空输入内容*/
                        clearInputContentsFunc();
                    }else
                    {
                        /*录入成功*/
                        insertParam.statusText = '在线录入';
                        insertParam.memo = '进行称重量方信息录入时存在运单信息，正常录入';
                        insertParam.waybillCode = insertParam.codeStr;
                        /*involkPostSync(waybill_weight_convert_url,{codeStr:insertParam.codeStr},function(res){
                            insertParam.waybillCode = res.data;
                        });*/

                        $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);
                        if(allForcedToSubmit == 0){
                            $.messager.alert('运单录入结果','运单称重量方记录已录入成功！','info');
                        }
                        $('#waybill-weight-btn').linkbutton('enable');

                        /*为方便输入 清空输入内容*/
                        clearInputContentsFunc();
                    }
                    removeFailData(removeIndex);
                });
            }

            /**
             * 不存在运单强行提交 抽取出来供批量使用
             * @param insertParam
             */
            function noExistSubmitPackage(insertParam,removeFailData,removeIndex){
                involkPostSync(waybill_weight_insert_url_package,insertParam,function(res){
                    if(res.code == ERROR_PARAM_RESULT_CODE)
                    {
                        $.messager.alert('错误','重量或体积参数输入有误，超过最大值或输入的重量体积为0！','error');
                    }
                    else if(res.data == false || res.data == undefined)
                    {
                        /*录入失败*/
                        $.messager.alert('运单录入结果','称重信息录入失败,请稍后重试 （错误：' + res.message + ')','info');
                    } else if(res.code == SERVER_ERROR_CODE && res.message == "toTask")
                    {
                       /* *//*MQ服务不可用时，转为task重试*//*
                        $.messager.alert('运单录入结果','进行称重量方信息录入不存在运单信息，且消息发送失败，已转为离线录入','info');

                        insertParam.statusText = '离线录入';
                        insertParam.memo = '进行称重量方信息录入不存在运单信息，且消息发送失败，已转为离线录入';
                        involkPostSync(waybill_weight_convert_url,{codeStr:insertParam.codeStr},function(res){
                            insertParam.waybillCode = res.data;
                        });

                        $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                        $('#waybill-weight-btn').linkbutton('enable');

                        *//*为方便输入 清空输入内容*//*
                        clearInputContentsFunc();*/
                    }
                    else
                    {
                        /*录入成功*/
                        if(allForcedToSubmit == 0){
                            $.messager.alert('运单录入结果','运单相关信息不存在，已转为离线录入','info');
                        }
                        insertParam.statusText = '离线录入';
                        insertParam.memo = '进行称重量方信息录入时无运单信息，转为离线录入';
                        /*involkPostSync(waybill_weight_convert_url,{codeStr:insertParam.codeStr},function(res){
                            insertParam.waybillCode = res.data;
                        });*/
                        $('#waybill-weight-success-datagrid').datagrid('appendRow',insertParam);

                        /*为方便输入 清空输入内容*/
                        clearInputContentsFunc();
                    }

                    removeFailData(removeIndex);
                });
            }
            /**
             * 拦截提示-和showWarnData一样
             * @param data
             */
            function showWarnDataPackage(warnList){
                    //提示转网运单数据
                    if(warnList && warnList.length >0){
                        var warnMessage = "";
                        var count = warnList.length;

                        if(count > 3){
                            count = 3;
                        }
                        for(var i= 0;i<count;i ++){
                            warnMessage = warnMessage + warnList[i].codeStr + ",";
                        }

                        if(warnMessage.length > 0){
                            warnMessage = warnMessage.substr(0,warnMessage.length-1);
                            if(warnMessage.length > 0){
                                if(count>3){
                                    warnMessage = "包裹号" + warnMessage +"……共" + warnList.length + "单" + INTERCEPT_MESSAGE;
                                }else{
                                    warnMessage = "包裹号" + warnMessage + INTERCEPT_MESSAGE;
                                }
                                $.messager.alert('运单录入结果',warnMessage);
                            }
                        }
                    }
                }


            /**
             * 导出
             */
            $("#waybill-weight-improtbypackage-export").click(function(){

                var url = "/b2b/express/weightpackage/toExportPackage";
                var form = $("<form method='post'></form>"),input;
                form.attr({"action":url});
                input = $("<input type='hidden' name='json'>");
                input.val(JSON.stringify(errorData));
                form.append(input);
                form.appendTo(document.body);
                form.submit();
                document.body.removeChild(form[0]);

            });

            function showFailDataPackage(data){
                    $("#waybill-weight-importbypackage-dialog").dialog('close');
                    $("#waybill-weight-improtbypackage-fail-dialog").dialog('open');
                    //组装展示页面
                    if(data&&data.length>0){

                        $('#waybill-weight-improtbypackage-fail-datagrid').datagrid({
                            singleSelect:true,
                            columns:[[
                                {field:'codeStr',title:'包裹号',width:150},
                                {field:'weight',title:'总重量/千克',width:100},
                                {field:'volume',title:'总体积/立方米',width:100},
                                {field:'packageLength',title:'长(cm)',width:100,hidden:true},
                                {field:'packageWidth',title:'宽(cm)',width:100,hidden:true},
                                {field:'packageHigh',title:'高(cm)',width:100,hidden:true},
                                {field:'errorMessage',title:'失败原因',width:330},
                                {field:'operate',title:'操作',align:'center',width:100,
                                    formatter:function(value, row, index){
                                        var str = '';
                                        if(row.canSubmit == 1){
                                            if(row.errorCode && CBM_DIV_KG_CODE == row.errorCode){
                                                str = '<a href="javascript:eval(\'forcedToSubmitPackage(\\\'' +  row.codeStr + '\\\',\\\''+row.weight+'\\\',\\\''+row.volume+'\\\',\\\''+row.key+'\\\',\\\''+row.packageLength+'\\\',\\\''+row.packageWidth+'\\\',\\\''+row.packageHigh+'\\\',\\\''+row.status+'\\\')\');" name="opera" class="easyui-linkbutton all-submit-data" ></a>';

                                            }else{
                                                str = '<a href="javascript:eval(\'forcedToSubmitPackage(\\\'' +  row.codeStr + '\\\',\\\''+row.weight+'\\\',\\\''+row.volume+'\\\',\\\''+row.key+'\\\',\\\''+row.packageLength+'\\\',\\\''+row.packageWidth+'\\\',\\\''+row.packageHigh+'\\\',\\\''+row.status+'\\\')\');" name="opera" class="easyui-linkbutton" ></a>';

                                            }
                                        }
                                        return str;
                                    }
                                }
                            ]],
                            data:data,
                            onLoadSuccess:function(data){
                               $("a[name='opera']").linkbutton({text:'强制提交',plain:true,iconCls:'icon-save'});
                            },

                        });
                        errorData = data.slice(0);
                    }

                }

                $("#waybill-weight-improtbypackage-export-close").click(function(){
                    $.messager.confirm('提示','关闭后将无法导出数据，是否关闭？'
                        ,function(confirmFlag){
                            if(confirmFlag){
                                $("#waybill-weight-improtbypackage-fail-dialog").dialog('close');
                            }
                        }
                    );

                });

                /**
                 * 抽取 称重逻辑供 批量使用
                 * @param insertParam
                 */
                function doWaybillWeightPackage(insertParam,removeFailData,removeIndex){
                        /*调用验证方法验证单号是否合法、是否存在*/
                        involkPostSync(waybill_weight_validate_url_package,{codeStr:insertParam.codeStr},function (res) {

                            var isExists = res.data;

                            if(isExists)
                            {
                                /*******************************************************************************/
                                /*运单存在*/
                                /*******************************************************************************/
                                $('#waybill-weight-btn').linkbutton('disable');

                                insertParam.status = VALID_EXISTS_STATUS_CODE;

                                existSubmitPackage(insertParam,removeFailData,removeIndex);

                                /*******************************************************************************/
                            }else
                            {
                                /*******************************************************************************/
                                /*运单不存在 或校验不通过*/
                                /*******************************************************************************/

                                /*单号不合法*/
                                if(res.code == ERROR_PARAM_RESULT_CODE)
                                {
                                    $.messager.alert('单号格式有误','快运外单单号输入有误，请您检查单号！','error');
                                    return;
                                }

                                if(res.code == NO_NEED_WEIGHT){
                                    $.messager.alert('提示',res.message,'warning');
                                    return ;
                                }
                                //KA运单
                                if(res.code == KAWAYBILL_NEEDPACKAGE_WEIGHT){
                                    $.messager.alert('提示',res.message,'error');
                                    $('#waybill-weight-btn').linkbutton('disable');
                                    $('#waybill-weight-import-btn').linkbutton('disable');
                                    return ;
                                }
                                // 集配场地揽收后禁止称重
                                if(res.code === JP_FORBID_WEIGHT){
                                    $.messager.alert('提示', res.message,'error');
                                    return ;
                                }
                                if(res.code == WAYBILL_STATES_FINISHED){
                                    $.messager.alert('提示',res.message,'error');
                                    return ;
                                }
                                /*单号通过正则校验、但单号对应运单不存在*/
                                $.messager.confirm('无运单信息','您输入的运单号/包裹号无相关运单信息，' + '请问您确认要录入吗？'
                                    ,function(confirmFlag){
                                        if(confirmFlag)
                                        {
                                            insertParam.status = VALID_NOT_EXISTS_STATUS_CODE;
                                            noExistSubmitPackage(insertParam,removeFailData,removeIndex);

                                        }
                                    }
                                );

                                /*******************************************************************************/
                            }
                        });
                    }

                    function removeFailDataFuncPackage(key){
                            //防止删除后 数组索引发生变化  原函数索引未改变
                        if(key || key >= 0){
                            for(var index = 0 ; index < errorData.length ; index++ ){
                                if(errorData[index].key == key){
                                    $("#waybill-weight-improtbypackage-fail-submit-message").html("强制提交"+(++forcedToSubmitCount)+"条数据");
                                    $('#waybill-weight-improtbypackage-fail-datagrid').datagrid('deleteRow',index);
                                    errorData.splice(index,1);
                                }
                            }
                        }
                    }

                    $("#waybill-weight-improtbypackage-all-submit").click(function(){
                        $.messager.confirm('提示','是否强制提交重泡比校验未通过的所有数据？'
                            ,function(confirmFlag){
                                if(confirmFlag){
                                    allForcedToSubmit = 1; //控制提示语 执行的所有请求全部为同步 才可以这么做
                                    allSubmitRemovePackage();
                                    allForcedToSubmit = 0;
                                    if(b2cCount>0){
                                        $.messager.alert('运单录入结果',"共有"+b2cCount + "单" + INTERCEPT_MESSAGE);
                                    }
                                }
                            }
                        );

                    });

                    function allSubmitRemovePackage(){

                        for(var i = 0 ; i < errorData.length ; i++){
                            var myRow = errorData[i];
                            if(myRow.errorCode && CBM_DIV_KG_CODE == myRow.errorCode){
                                forcedToSubmitPackage(myRow.codeStr ,myRow.weight ,myRow.volume ,myRow.key ,myRow.packageLength,myRow.packageWidth,myRow.packageHigh,myRow.status);
                                allSubmitRemovePackage();
                                break;
                            }
                        }
                    }


