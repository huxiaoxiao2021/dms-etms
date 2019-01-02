$(function() {
    var saveUrl = '/transport/arExcpRegister/save';
    var deleteUrl = '/transport/arExcpRegister/deleteByIds';
    var detailUrl = '/transport/arExcpRegister/detail/';
    var queryUrl = '/transport/arExcpRegister/listData';
    var tableInit = function() {
        var oTableInit = new Object();
        oTableInit.init = function() {
            $('#dataTable').bootstrapTable({
                url : queryUrl, // 请求后台的URL（*）
                method : 'post', // 请求方式（*）
                toolbar : '#toolbar', // 工具按钮用哪个容器
                queryParams : oTableInit.getSearchParams, // 查询参数（*）
                //height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId : "ID", // 每一行的唯一标识，一般为主键列
                pagination : true, // 是否显示分页（*）
                pageNumber : 1, // 初始化加载第一页，默认第一页
                pageSize : 10, // 每页的记录行数（*）
                pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
                cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped : true, // 是否显示行间隔色
                showColumns : true, // 是否显示所有的列
                sortable : true, // 是否启用排序
                sortOrder : "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh : true, // 是否显示刷新按钮
                minimumCountColumns : 2, // 最少允许的列数
                clickToSelect : true, // 是否启用点击选中行
                showToggle : true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
                strictSearch : true,
                // icons: {refresh: "glyphicon-repeat", toggle:
                // "glyphicon-list-alt", columns: "glyphicon-list"},
                // search:false,
                // cardView: true, //是否显示详细视图
                // detailView: true, //是否显示父子表
                // showFooter:true,
                // paginationVAlign:'center',
                // singleSelect:true,
                columns : oTableInit.tableColums
            });
        };
        oTableInit.getSearchParams = function(params) {

            var temp = oTableInit.getSearchCondition();
            if(!temp){
                temp={};
            }
            temp.offset = params.offset;
            temp.limit = params.limit;
            // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            return temp;
        };
        /**
         * 获取查询参数
         * @param _selector 选择器（默认为'.search-param'）
         */
        oTableInit.getSearchCondition = function(_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param";
            }
            $(_selector).each(function () {
                var _k = this.id;
                var _v = $(this).val();
                if(_k && _v){
                    params[_k] = _v;
                }
            });
            return params;
        };


        oTableInit.tableColums = [ {
            checkbox : true
        }, {
            field : 'excpTime',
            title : '异常日期',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateOfTs(value);
            },
            width:120,
            class:'min_120'
        }, {
            field : 'excpNode',
            title : '异常节点',
            formatter : function(value,row,index){
                return value==1?'发货异常':value==2?'提货异常':'未知异常';

            }
        }, {
            field : 'transportName',
            title : '运力名称'
        }, {
            field : 'siteOrder',
            title : '铁路站序'

        } , {
            field : 'orderCode',
            title : '航空单号'
        } , {
            field : 'startCityName',
            title : '起飞城市'
        } , {
            field : 'endCityName',
            title : '落地城市'
        } , {
            field : 'planStartTime',
            title : '起飞时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateTimeNossOfTs(value);
            }
        } , {
            field : 'planEndTime',
            title : '落地时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateTimeNossOfTs(value);
            }
        } , {
            field : 'excpTypeName',
            title : '异常类型'
        } , {
            field : 'excpReasonName',
            title : '异常原因'
        } , {
            field : 'excpResultName',
            title : '异常结果'
        } , {
            field : 'excpCity',
            title : '发现异常城市'
        }  , {
            field : 'sendCode',
            title : '发货批次号'
        } , {
            field : 'excpNum',
            title : '异常件数'
        } , {
            field : 'excpPackageNum',
            title : '异常包裹数'
        } , {
            field : 'operatorErp',
            title : '现场操作人'
        } , {
            field : 'remark',
            title : '备注',
            width:180,
            class:'min_180'
        } ];
        oTableInit.refresh = function() {
            $('#dataTable').bootstrapTable('refreshOptions',{pageNumber:1});
            //$('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        var postdata = {};
        oInit.init = function() {
            $('#dataEditDiv').hide();
            /*起始时间*/ /*截止时间*/
            $.datePicker.createNew({
                elem: '#excpTimeGEStr',
                theme: '#3f92ea',
                btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#excpTimeLEStr',
                theme: '#3f92ea',
                btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });


            $('#btn_query').click(function() {
                tableInit().refresh();
            });
            $('#btn_add').click(function() {
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if(_k){
                        $(this).val('');
                    }
                });
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
                $("#edit-form").data("bootstrapValidator").resetForm();
            });
            // 初始化页面上面的按钮事件
            // 改
            $('#btn_edit').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length > 1) {
                    alert("修改操作，只能选择一条数据");
                    return;
                }
                if (rows.length == 0) {
                    alert("请选择一条数据");
                    return;
                }
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if(_k){
                        $(this).val('');
                    }
                });
                $("#edit-form").data("bootstrapValidator").resetForm();
                $.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
                    if(res&&res.succeed&&res.data){
                        $('.edit-param').each(function () {
                            var _k = this.id;
                            var _v = res.data[_k];
                            if(_k && _v){
                                if(_k=='excpTime' ){
                                    _v =$.dateHelper.formateDateOfTs(_v);
                                }else if(_k=='planStartTime' || _k=='planEndTime'){
                                    _v =$.dateHelper.formateTimeOfTs(_v);
                                }
                                $(this).val(_v);
                            }
                        });
                        //保证数据赋值后 按照 异常类型 原因 结果 先后顺序执行 防止触发错乱
                        //并且需要等待对应下拉框的change事件执行完毕后才触发
                        var v1 = $('#edit-form #excpType').val();

                        if(v1){
                            $('#excpTypeEdit').val(v1).trigger('change');

                        }
                        var v2= $('#edit-form #excpReason').val();


                        var v3 = $('#edit-form #excpResult').val();

                        if(v2 && v3){
                            changeExcpTypeEdit(v2,v3);
                        }



                    }
                });
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });

            // 删
            $('#btn_delete').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    alert("错误，未选中数据");
                    return;
                }
                var flag = confirm("是否删除这些数据?");
                if (flag == true) {
                    var params = [];
                    for(var i in rows){
                        params.push(rows[i].id);
                    };
                    $.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
                        if(res&&res.succeed&&res.data){
                            alert('操作成功,删除'+res.data+'条。');
                            tableInit().refresh();
                        }else{
                            alert('操作异常！');
                        }
                    });
                }
            });

            $('#btn_submit').click(function() {
                $('#btn_submit').attr("disabled",true);
                //先去校验
                if(!editValidator()){
                    $('#btn_submit').attr("disabled",false);
                    return;
                }

                var params = {};
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if(_k){
                        //数字类型 如果是空 给0
                        if(_k == 'excpNum' || _k =='excpPackageNum'){
                            if(_v){
                                params[_k]= _v;
                            }else{
                                params[_k]= 0;
                            }
                        }else{
                            params[_k]=_v;
                        }

                    }
                });
                $.post(saveUrl,params,function(res){
                        if(res&&res.succeed){
                            alert('操作成功');
                            tableInit().refresh();
                        }else{
                            alert('操作异常');
                        }

                        $('#btn_submit').attr("disabled",false);
                        //还原异常信息下拉框
                        $('#excpTypeEdit').val(null).trigger('change');
                        $("#excpReasonEdit").html("");
                        $("#excpResultEdit").html("");
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();

                    },'json');


            });
            $('#btn_return').click(function() {
                //还原异常信息下拉框
                $('#excpTypeEdit').val(null).trigger('change');
                $("#excpReasonEdit").html("");
                $("#excpResultEdit").html("");

                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });
        };
        return oInit;
    };


    initDateQuery();
    tableInit().init();
    pageInit().init();
    initSelect();
    initExcpType();
    initEditPage();
    editFormValidator();
    initEditSelect();



});





function initSelect(){
    $("#query-form #excpNodeSelect").select2({
        width: '100%',
        placeholder:'请选择异常节点',
        allowClear:true

    });
    $("#query-form #excpNodeSelect").val(null).trigger('change');
    //ID 冲突。。select2插件有问题
    $("#query-form #excpNodeSelect").on('change',function(e){
        var v = $("#query-form #excpNodeSelect").val();

        $("#query-form #excpNode").val(v);


    });

}

function initEditPage(){
    /*新增修改涉及的时间*/
    $.datePicker.createNew({
        elem: '#excpTimeStr',
        theme: '#3f92ea',
        btns: ['clear','now'],
        done: function(value, date, endDate){
            resetFieldValidator(value,"excpTimeStr");
        }
    });

    $.datePicker.createNew({
        elem: '#planStartTimeStr',
        theme: '#3f92ea',
        type: 'time',
        //btns: ['clear','now'],
        done: function(value, date, endDate){
            resetFieldValidator(value,"planStartTimeStr");

        }
    });
    $.datePicker.createNew({
        elem: '#planEndTimeStr',
        theme: '#3f92ea',
        type: 'time',
        //btns: ['clear','now'],
        done: function(value, date, endDate){
            resetFieldValidator(value,"planEndTimeStr");

        }
    });

}




var excpTypeData={};
var excpReasonData={};
var excpResultData={};

// 初始化异常类型下拉框
function initExcpType() {


    var url = "/base/dmsBaseDict/airRailwayExceptionType";
    var param = {};
    $.get(url, param, function (myData) {
        var data = myData.data;

        var result = [];
        for(var i in data){
            if(data[i].id && data[i].typeName){
                result.push({id:data[i].id,text:data[i].typeName});

                excpTypeData[data[i].id] = data[i]; //缓存一份
            }

        }

        $('#query-form #excpType').select2({
            width: '100%',
            placeholder:'请选择异常类型',
            allowClear:true,
            data:result
        });

        $('#excpTypeEdit').select2({
            width: '100%',
            placeholder:'请选择异常类型',
            allowClear:true,
            data:result
        });
        $('#query-form #excpType').val(null).trigger('change');

        $('#excpTypeEdit').val(null).trigger('change');





    }, "json");




}


function initEditSelect(){
    $("#excpReasonEdit").select2({
        width: '100%',
        placeholder:'请先选择异常类型',
        allowClear:true
    });
    $("#excpResultEdit").select2({
        width: '100%',
        placeholder:'请先选择异常原因',
        allowClear:true
    });

    //监听异常类型发生变化
    $("#excpTypeEdit").on('change',function(e){

        changeExcpTypeEdit();

    });

    //监听异常原因发生变化
    $("#excpReasonEdit").on('change',function(e){
        changeExcpReasonEdit();

    });
    $("#excpResultEdit").on('change',function(e){
        var p = $("#excpResultEdit").val();
        if(!p){
            return false;
        }
        var v = excpResultData[p]['typeName'];
        if(v){
            $("#edit-form #excpResult").val(p);
            $("#edit-form #excpResultName").val(v);

        }

    });
}

/**
 * 异常类型 发生变化 处理逻辑
 * 如果传入参数 ，则改变异常原因的值  为了修改反向勾选用
 * @param selectVal
 * @returns {boolean}
 */
function changeExcpTypeEdit(selectVal1,selectVal2){

    var p = $("#excpTypeEdit").val();
    var _p = $("#edit-form #excpType").val();
    if((!p || p ==_p) && !selectVal1){ //防止 空值 或 修改的时候反选触发change事件 在多走一遍
        return false;
    }
    var k = excpTypeData[p]['typeGroup'];
    var v = excpTypeData[p]['typeName'];
    if(k){
        $("#edit-form #excpType").val(p);
        $("#edit-form #excpTypeName").val(v);
        var url = "/base/dmsBaseDict/airRailwayExceptionReason/"+k


        $.get(url, {}, function (myData) {
            $("#excpReasonEdit").html("");
            $("#excpResultEdit").html("");
            var data = myData.data;

            var result = [];
            for(var i in data){
                if(data[i].id && data[i].typeName){
                    result.push({id:data[i].id,text:data[i].typeName});
                    excpReasonData[data[i].id] = data[i]; //缓存一份
                }
            }

            $("#excpReasonEdit").select2({
                width: '100%',
                placeholder:'请先选择异常类型',
                allowClear:true,
                data:result
            });
            if(selectVal1 && selectVal2){

                $("#excpReasonEdit").val(selectVal1).trigger('change');
                changeExcpReasonEdit(selectVal2);

            }else{
                $("#excpReasonEdit").val(null).trigger('change');
                $("#excpResultEdit").val(null).trigger('change');
                $("#edit-form #excpReason").val("");
            }



        }, "json");

    }
}
/**
 * 异常原因 发生变化 处理逻辑
 * 如果传入参数 ，则改变异常结果的值  为了修改反向勾选用
 * @param selectVal
 * @returns {boolean}
 */
function changeExcpReasonEdit(selectVal){

    var p = $("#excpReasonEdit").val();
    var _p = $("#edit-form #excpReason").val();
    if((!p || p ==_p) && !selectVal){ //防止 空值 或 修改的时候反选触发change事件 在多走一遍
        return false;
    }
    var k = excpReasonData[p]['typeGroup'];
    var v = excpReasonData[p]['typeName'];
    if(k){
        $("#edit-form #excpReason").val(p);
        $("#edit-form #excpReasonName").val(v);
        var url = "/base/dmsBaseDict/airRailwayExceptionResult/"+k

        $.get(url, {}, function (myData) {

            $("#excpResultEdit").html("");

            var data = myData.data;

            var result = [];
            for(var i in data){
                if(data[i].id && data[i].typeName){
                    result.push({id:data[i].id,text:data[i].typeName});
                    excpResultData[data[i].id] = data[i]; //缓存一份
                }
            }

            $("#excpResultEdit").select2({
                width: '100%',
                placeholder:'请先选择异常原因',
                allowClear:true,
                data:result
            });
            if(selectVal){
                $("#excpResultEdit").val(selectVal).trigger('change');

            }else {
                $("#excpResultEdit").val(null).trigger('change');
            }

        }, "json");
    }
}

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());

    $("#excpTimeGEStr").val(v);
    $("#excpTimeLEStr").val(v);
}




