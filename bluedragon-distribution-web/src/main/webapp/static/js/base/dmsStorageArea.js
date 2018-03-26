$(function() {
    var saveUrl = '/base/dmsStorageArea/save';
    var deleteUrl = '/base/dmsStorageArea/deleteByIds';
    var detailUrl = '/base/dmsStorageArea/detail/';
    var queryUrl = '/base/dmsStorageArea/listData';
    var tableInit = function() {
        var oTableInit = new Object();
        $("#dataEditDiv").hide();
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
            field : 'dmsSiteName',
            title : '操作机构名称',
            width:200,
            class:'min_120'
        }, {
            field : 'desProvinceName',
            title : '收件省',
            width:180,
            class:'min_180'
        }, {
            field : 'desCityName',
            title : '收件市'
        }, {
            field : 'storageCode',
            title : '库位号',
        } , {
            field : 'updateUserName',
            title : '操作人'
        }, {
            field : 'updateTime',
            title : '操作时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            },
            width:200,
            class:'min_120'
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
            $('#btn_query').click(function() {
                tableInit().refresh();
            });
            $('#btn_add').click(function() {
                $("#cityEG").empty();
                initProAndCity("#provinceEG","#cityEG");
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
                $('#dataEditDiv').show();
                initProAndCity("#provinceEG","#cityEG");
                var pro = "";
                var city = "";
                var editId = "";
                $.ajax({
                    type : "get",
                    url : detailUrl+rows[0].id,
                    data : {},
                    async : false,
                    success : function (data) {
                        if(data&&data.code == 400){
                            alert('操作异常');
                            return;
                        }else{
                            editId = data.data.id;
                            pro = data.data.desProvinceCode;
                            city = data.data.desCityCode;
                            $("#storageCodeEG").val(data.data.storageCode);
                        }
                    }
                });
                $("#editId").val(editId);
                $('#provinceEG').val(pro).trigger('change');
                $('#cityEG').val(city).trigger('change');
                $('#dataTableDiv').hide();
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
                $('.eidt-param').each(function () {
                    var _k = this.name;
                    var _v = $(this).val();
                    if(_k ){
                        params[_k]=_v;
                    }
                });
                params["desProvinceName"] = $('#provinceEG option:selected').text();
                params["desCityName"] = $('#cityEG option:selected').text();
                params["storageCode"] = $('#storageCodeEG').val();
                var s= JSON.stringify(params);
                $.post(saveUrl,params,function(res){
                        if(res&&res.succeed){
                            alert('操作成功');
                            tableInit().refresh();
                        }else if(res&& res.succeed == 400){
                            alert(res.message);
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
    initProAndCity("#desProvinceCode","#desCityCode");

    tableInit().init();
    pageInit().init();

    initSiteSelect();
    editFormValidator();
    initImportExcel();



});

var initLogin = true;

function initSiteSelect(){

    $("#dmsArea").select2({
        width: '100%',
        placeholder:'请选择机构',
        allowClear:true
    });
    $("#dmsSiteCode").select2({
        width: '100%',
        placeholder:'请选择分拣中心',
        allowClear:true
    });
    $("#desProvinceCode").select2({
        width: '100%',
        placeholder:'请选择收件省',
        allowClear:true
    });
    $("#desCityCode").select2({
        width: '100%',
        placeholder:'请选择收件市',
        allowClear:true
    });
    $("#provinceEG").select2({
        width: '100%',
        placeholder:'请选择收件省',
        allowClear:true
    });
    $("#cityEG").select2({
        width: '100%',
        placeholder:'请选择收件市',
        allowClear:true
    });
}

function findCity(selectId,cityListUrl,orgId){
   $(selectId).empty();
    $.ajax({
        type : "post",
        url : cityListUrl,
        data : { provinceId : orgId },
        async : false,
        success : function (data) {
            var result = [];
            for(var i in data){
                if(data[i].assortName && data[i].assortName != ""){
                    result.push({id:data[i].assortCode,text:data[i].assortName});
                }
            }
            $(selectId).select2({
                width: '100%',
                placeholder:'请选择收件市',
                allowClear:true,
                data:result
            });
        }
    });
}


// 初始化收件省、收件市下拉框
function initProAndCity(comboxProvinceCondition,comboxCityCondition) {

    var url = "/base/dmsStorageArea/getProvinceList";
    var param = {};
    $.ajax({
        type : "post",
        url : url,
        data : param,
        async : false,
        success : function (data) {
            var result = [];
            for(var i in data){
                if(data[i].id && data[i].id != ""){
                    result.push({id:data[i].id,text:data[i].name});
                }
            }

            $(comboxProvinceCondition).select2({
                width: '100%',
                placeholder:'请选择收件省',
                allowClear:true,
                data:result
            });
            $(comboxProvinceCondition).val(null).trigger('change');
            $(comboxProvinceCondition)
                .on("change", function(e) {
                    var orgId = $(comboxProvinceCondition).val();
                    if(orgId){
                        var siteListUrl = '/base/dmsStorageArea/getCityList';
                        findCity(comboxCityCondition ,siteListUrl , Number($(comboxProvinceCondition).val()));
                    }
                });
        }
    });
}

function findSite(selectId,siteListUrl){
    $(selectId).empty();
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
            $(selectId).select2({
                width: '100%',
                placeholder:'请选择分拣中心',
                allowClear:true,
                data:result
            });
        }
    });
}
//初始化区域、分拣中心
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
            $('#dmsArea').select2({
                width: '100%',
                placeholder:'请选择机构',
                allowClear:true,
                data:result
            });
            $("#dmsArea").val(null).trigger('change');
            $("#dmsArea")
                .on("change", function(e) {
                    var orgId = $("#dmsArea").val();
                    if(orgId){
                        var siteListUrl = '/services/bases/dms/'+orgId;
                        findSite("#dmsSiteCode",siteListUrl);
                    }
                });
        }
    });
}

function initImportExcel(){
    //上传按钮
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);
        var form =  $("#upload_excel_form");
        var options = {
            url:'/base/dmsStorageArea/uploadExcel',
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
