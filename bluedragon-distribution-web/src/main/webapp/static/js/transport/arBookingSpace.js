$(function() {
    var saveUrl = '/transport/arBookingSpace/save';
    var deleteUrl = '/transport/arBookingSpace/deleteByIds';
    var detailUrl = '/transport/arBookingSpace/detail/';
    var queryUrl = '/transport/arBookingSpace/listData';
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
            field : 'planStartDate',
            title : '预计起飞日期',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateOfTs(value);
            },
            width:120,
            class:'min_120'
        }, {
            field : 'createSiteName',
            title : '分拣中心名称',
            width:180,
            class:'min_180'
        }, {
            field : 'transportName',
            title : '运力名称'
        }, {
            field : 'transportType',
            title : '运力类型',
            formatter : function(value,row,index){
                return value==1?'散航':value==2?'全货机':value==3?'铁路':'未知类型';
            }
        } , {
            field : 'startCityName',
            title : '起飞城市'
        } , {
            field : 'endCityName',
            title : '落地城市'
        } , {
            field : 'planStartTime',
            title : '预计起飞时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateTimeNossOfTs(value);
            }
        } , {
            field : 'planEndTime',
            title : '预计落地时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateTimeNossOfTs(value);
            }
        } , {
            field : 'priority',
            title : '航班优先级'
        } , {
            field : 'gainSpace',
            title : '可获取舱位kg'
        } , {
            field : 'planSpace',
            title : '计划订舱位kg'
        } , {
            field : 'realSpace',
            title : '实际订舱位kg'
        } , {
            field : 'bookingSpaceTime',
            title : '订舱日期',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateOfTs(value);
            },
            width:120,
            class:'min_120'
        } , {
            field : 'supplierName',
            title : '供应商名称'
        } , {
            field : 'phone',
            title : '联系电话'
        } , {
            field : 'remark',
            title : '备注',
            width:180,
            class:'min_180'
        } , {
            field : 'operateTime',
            title : '操作时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            },
            width:180,
            class:'min_180'
        } , {
            field : 'operatorErp',
            title : '操作人'
        } ];
        oTableInit.refresh = function() {
            $('#dataTable').bootstrapTable('refreshOptions',{pageNumber:1}); //更改刷新时回到第一页
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
                elem: '#planStartDateLEStr',
                theme: '#3f92ea',
               btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#planStartDateGEStr',
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
                                if(_k=='planStartDate' || _k=='bookingSpaceTime'){
                                    _v =$.dateHelper.formateDateOfTs(_v);
                                }else if(_k=='planStartTime' || _k=='planEndTime'){
                                    _v =$.dateHelper.formateTimeOfTs(_v);
                                }
                                $(this).val(_v);
                            }
                        });
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
            //导入
            $('#btn_improt').click(function(){
                $('#importExcelFile').val(null);
                $('#improt_modal').modal('show');
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
                    if(_k ){
                        params[_k]=_v;
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
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();

                    },'json');


            });
            $('#btn_return').click(function() {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });
        };
        return oInit;
    };

    initOrg();

    initDateQuery();
    tableInit().init();
    pageInit().init();

    initSiteSelect();
    initEditPage();
    editFormValidator();
    initExport(tableInit());
    initImportExcel();



});


//初始化导出按钮
function initExport(tableInit){
    $("#btn_export").on("click",function(e){

        var url = "/transport/arBookingSpace/toExport";
        var params = tableInit.getSearchCondition();

        var form = $("<form method='post'></form>"),
            input;
        form.attr({"action":url});

        $.each(params,function(key,value){

            input = $("<input type='hidden' class='search-param'>");
            input.attr({"name":key});
            input.val(value);
            form.append(input);
        });
        form.appendTo(document.body);
        form.submit();
        document.body.removeChild(form[0]);
        /*var index = 1;
        $.each(params,function(key,value){
            if(index == 1){
                url+='?';
            }else{
                url+='&';
            }
            url+=key+"="+value;
            index++;
        });

        window.location.href = url;*/

    });
}

var initLogin = true;

function initSiteSelect(){
    $("#site-select").select2({
        width: '100%',
        placeholder:'请选择分拣中心',
        allowClear:true

    });
    $("#transportType").select2({
        width: '100%',
        placeholder:'请选运力类型',
        allowClear:true
    });
}

function initEditPage(){
    /*新增修改涉及的时间*/
    $.datePicker.createNew({
        elem: '#planStartDateStr',
        theme: '#3f92ea',
        btns: ['clear','now'],
        done: function(value, date, endDate){

            resetFieldValidator(value,"planStartDateStr");
        }
    });
    $.datePicker.createNew({
        elem: '#bookingSpaceTimeStr',
        theme: '#3f92ea',
        btns: ['clear','now'],
        done: function(value, date, endDate){
            resetFieldValidator(value,"bookingSpaceTimeStr");
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


function findSite(selectId,siteListUrl,initIdSelectId){
   $(selectId).html("");
    $.ajax({
        type : "get",
        url : siteListUrl,
        data : {},
        async : false,
        success : function (data) {


            var result = [];
            if(data.length==1 && data[0].code!="200"){


                result.push({id:"-999",text:data[0].message});

            }else{
                for(var i in data){
                    if(data[i].siteCode && data[i].siteCode != ""){
                        result.push({id:data[i].siteCode,text:data[i].siteName});
                    }
                }

            }
            if(initIdSelectId && result[0].id!="-999"){
                $(initIdSelectId).val(result[0].id);
            }

            $(selectId).select2({
                width: '100%',
                placeholder:'请选择分拣中心',
                allowClear:true,
                data:result
            });

            if(initLogin){
                //第一次登录 初始化登录人分拣中心
                if($("#loginUserCreateSiteCode").val() != -1){
                    //登录人大区
                    $(selectId).val($("#loginUserCreateSiteCode").val()).trigger('change');
                }
            }
            initLogin = false;

        }
    });
}


// 初始化大区下拉框
function initOrg() {



    var url = "/services/bases/allorgs";
    var param = {};
    $.ajax({
                type : "get",
                url : url,
                data : param,
                async : false,
                success : function (data) {

                    var result = [];
                    for(var i in data){
                        if(data[i].orgId && data[i].orgId != ""){
                            result.push({id:data[i].orgId,text:data[i].orgName});

                        }

                    }

                    $('#site-group-select').select2({
                        width: '100%',
                        placeholder:'请选择机构',
                        allowClear:true,
                        data:result
                    });

                    $("#site-group-select")
                        .on("change", function(e) {
                            $("#query-form #createSiteCode").val("");
                            var orgId = $("#site-group-select").val();
                            if(orgId){
                                var siteListUrl = '/services/bases/dms/'+orgId;
                                findSite("#site-select",siteListUrl,"#query-form #createSiteCode");
                            }

                        });

                    $("#site-select").on("change",function(e){
                        var _s = $("#site-select").val();
                        $("#query-form #createSiteCode").val(_s);
                    });


                    if($("#loginUserOrgId").val() != -1){
                        //登录人大区
                        $('#site-group-select').val($("#loginUserOrgId").val()).trigger('change');
                    }else{
                        $('#site-group-select').val(null).trigger('change');
                    }








                }
      });







}


function initImportExcel(){
    //上传按钮
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);
        var form =  $("#upload_excel_form");
        var options = {
            url:'/transport/arBookingSpace/uploadExcel',
            type:'post',
            success:function(data){
                var code = data.code;
                if(code == '200'){
                    alert("导入成功！");
                    $('#improt_modal').modal('hide');
                    $('#dataTable').bootstrapTable('refresh');
                }else{
                    alert(data.message);
                }

                $('#importExcelFile').val(null);
                $('#btn_upload').attr("disabled",false);
            },
            error:function(XmlHttpRequest,textStatus,errorThrown){
                console.log(XmlHttpRequest);
                console.log(textStatus);
                console.log(errorThrown);
            }

        };
        form.ajaxSubmit(options);


    });




}


function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());

    $("#planStartDateGEStr").val(v);
    $("#planStartDateLEStr").val(v);
}





