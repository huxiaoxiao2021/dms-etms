var getMenuUrl = '/funcSwitchConfig/getMenu';
var getDimensionUrl = '/funcSwitchConfig/getDimension';
var queryUrl = '/funcSwitchConfig/listData';
var saveUrl = '/funcSwitchConfig/save';
var updateUrl = '/funcSwitchConfig/update';
var deleteUrl = '/funcSwitchConfig/deleteByIds';
var uploadExcelUrl = '/funcSwitchConfig/uploadExcel';
$(function () {

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                showColumns: true, // 是否显示所有的列
                sortable: true, // 是否启用排序
                sortOrder: "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
                strictSearch: true,
                // icons: {refresh: "glyphicon-repeat", toggle:
                // "glyphicon-list-alt", columns: "glyphicon-list"},
                // search:false,
                // cardView: true, //是否显示详细视图
                // detailView: true, //是否显示父子表
                // showFooter:true,
                // paginationVAlign:'center',
                // singleSelect:true,
                columns: oTableInit.tableColums
            });
        };
        oTableInit.getSearchParams = function (params) {
            var temp = oTableInit.getSearchCondition();
            if (!temp) {
                temp = {};
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
                if(_k && (_v != null && _v != '')){
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'menuCode',
            title: '功能编码',
            align: 'center',
            visible: false
        }, {
            field: 'menuName',
            title: '功能',
            align: 'center'
        }, {
            field: 'dimensionCode',
            title: '维度编码',
            align: 'center',
            visible: false
        }, {
            field: 'dimensionName',
            title: '维度',
            align: 'center'
        }, {
            field: 'orgId',
            title: '区域ID',
            align: 'center'
        },{
            field: 'orgName',
            title: '区域名称',
            align: 'center'
        }, {
            field: 'siteCode',
            title: '站点ID',
            align: 'center'
        }, {
            field: 'siteName',
            title: '站点名称',
            align: 'center'
        }, {
            field: 'createErp',
            title: '添加人ERP',
            align: 'center',
            visible: false
        }, {
            field: 'operateErp',
            title: '操作人ERP',
            align: 'center'
        }, {
            field: 'createUser',
            title: '添加人',
            align: 'center'
        }, {
            field: 'yn',
            title: '是否有效',
            align: 'center',
            formatter : function(value,row,index){
                return value == 1 ? '有效' : '无效';
            }
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'updateTime',
            title: '变更时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        oInit.init = function() {
            $('#dataEditDiv').hide();

            //查询
            $('#btn_query').click(function() {
                tableInit().refresh();
            });
            //新增
            $('#btn_add').click(function() {
                $("#siteCode-EG").empty();
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if(_k){
                        $(this).val('');
                    }
                });
                $('#btn_add_submit').show();
                $('#btn_update_submit').hide();

                $('#dataTableDiv').hide();
                $('#operateErp-EG').val('');
                $('#dataEditDiv').show();
                initMenu("#menuCode-EG");
                initDimension("#dimensionCode-EG");
                initOrg("#orgId-EG","#siteCode-EG");
                initYn("#yn-EG");
            });
            // 逻辑变更
            $('#btn_update').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length != 1) {
                    $.msg.warn("请选中一条数据操作");
                    return;
                }
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();

                $('#btn_add_submit').hide();
                $('#btn_update_submit').show();

                initMenu("#menuCode-EG");
                initDimension("#dimensionCode-EG");
                initOrg("#orgId-EG","#siteCode-EG");
                initYn("#yn-EG");

                $('#menuCode-EG').val(rows[0].menuCode).trigger('change');
                $('#dimensionCode-EG').val(rows[0].dimensionCode).trigger('change');
                $('#orgId-EG').val(rows[0].orgId).trigger('change');
                $('#siteCode-EG').val(rows[0].siteCode).trigger('change');
                $('#operateErp-EG').val(rows[0].operateErp);

                $('#menuCode-EG').attr("disabled",true);
                $('#dimensionCode-EG').attr("disabled",true);
                $('#orgId-EG').attr("disabled",true);
                $('#siteCode-EG').attr("disabled",true);
            });
            // 逻辑删除
            $('#btn_delete').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    $.msg.warn("请选中数据后操作");
                    return;
                }
                var params = [];
                for(var i in rows){
                    var data = {};
                    data["id"] = rows[i].id;
                    data["menuCode"] = rows[i].menuCode;
                    data["siteCode"] = rows[i].siteCode;
                    params.push(data);
                };
                $.msg.confirm('是否将这些数据置为无效？', function () {
                    $.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
                        if(res != null && res.code == 200){
                            $.msg.ok(res.message);
                            tableInit().refresh();
                        }else if (res != null && res.code == 400){
                            $.msg.warn(res.message);
                            tableInit().refresh();
                        }else {
                            $.msg.error('操作异常！');
                        }
                    });
                });
            });

            // 新增
            $('#btn_add_submit').click(function() {
                $('#btn_add_submit').attr("disabled",true);
                var param = convert2param();
                if(param["dimensionCode"]=='3'&&(param["menuCode"] == null || param["dimensionCode"] == null || param['yn'] == null)){
                    $.msg.warn("带 * 号选项必填!");
                    $('#btn_add_submit').attr("disabled",false);
                    return;
                }else if(param["dimensionCode"]!='3'&&(param["menuCode"] == null || param["dimensionCode"] == null || param["orgId"] == null
                    || param['siteCode'] == null || param['yn'] == null)){
                    $.msg.warn("带 * 号选项必填!");
                    $('#btn_add_submit').attr("disabled",false);
                    return;
                }
                if(param['dimensionName'] == '个人'
                    && (param['operateErp'] == null || param['operateErp'] == '')){
                    $.msg.warn("个人维度则操作人ERP必填!");
                    $('#btn_add_submit').attr("disabled",false);
                    return;
                }
                var url = saveUrl;
                $.ajaxHelper.doPostSync(url,JSON.stringify(param),function(res){
                    if(res&&res.succeed){
                        $.msg.ok('操作成功');
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();
                        //重置状态
                        resetStatus();
                        tableInit().refresh();
                    }else if(res){
                        $.msg.error(res.message);
                    }else{
                        $.msg.error('服务异常');
                    }
                    $('#btn_add_submit').attr("disabled",false);
                });
            });

            // 变更
            $('#btn_update_submit').click(function() {
                $('#btn_update_submit').attr("disabled",true);
                debugger;
                var param = convert2param();
                if(param["menuCode"] == null || param["dimensionCode"] == null || param["orgId"] == null
                    || param['siteCode'] == null || param['yn'] == null){
                    $.msg.warn("带 * 号选项必填!");
                    $('#btn_update_submit').attr("disabled",false);
                    return;
                }
                if(param['dimensionName'] == '个人'
                    && (param['operateErp'] == null || param['operateErp'] == '')){
                    $.msg.warn("个人维度则操作人ERP必填!");
                    $('#btn_update_submit').attr("disabled",false);
                    return;
                }
                var url = updateUrl;
                $.ajaxHelper.doPostSync(url,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        $.msg.ok('操作成功');
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();
                        //重置状态
                        resetStatus();
                        tableInit().refresh();
                    }else if(res){
                        $.msg.error(res.message);
                    }else{
                        $.msg.error('服务异常');
                    }
                    $('#btn_add_submit').attr("disabled",false);
                });

            });

            //取消
            $('#btn_return').click(function() {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
                // 重置状态
                resetStatus();
            });

            //导入
            $('#btn_import').click(function(){
                $('#importExcelFile').val(null);
                $('#import_modal').modal('show');
            });
        };
        return oInit;
    };

    initMenu("#menuCode");
    initYn("#yn");
    initDimension("#dimensionCode");
    initOrg("#orgId","#siteCode");
    tableInit().init();
    pageInit().init();
    initImportExcel();
    initSelect();

});

// 重置状态
function resetStatus(){
    $('#menuCode-EG').attr("disabled",false);
    $('#dimensionCode-EG').attr("disabled",false);
    $('#orgId-EG').attr("disabled",false);
    $('#siteCode-EG').attr("disabled",false);
}

function initSelect(){

    $("#menuCode").select2({
        width: '100%',
        placeholder:'请选择功能',
        allowClear:true
    });
    $("#dimensionCode").select2({
        width: '100%',
        placeholder:'请选择维度',
        allowClear:true
    });
    $("#orgId").select2({
        width: '100%',
        placeholder:'请选择机构',
        allowClear:true
    });
    $("#siteCode").select2({
        width: '100%',
        placeholder:'请选择分拣中心',
        allowClear:true
    });
    $("#menuCode-EG").select2({
        width: '100%',
        placeholder:'请选择功能',
        allowClear:true
    });
    $("#orgId-EG").select2({
        width: '100%',
        placeholder:'请选择机构',
        allowClear:true
    });
    $("#siteCode-EG").select2({
        width: '100%',
        placeholder:'请选择分拣中心',
        allowClear:true
    });
    $("#yn-EG").select2({
        width: '100%',
        placeholder:'请选择是否有效',
        allowClear:true
    });
}

// 初始化功能下拉框
function initMenu(menu) {
    var param = {};
    $.ajax({
        type: "get",
        url: getMenuUrl,
        data: param,
        async: false,
        success: function (data) {
            var result = [];
            for (var item in data) {
                result.push({id: item, text: data[item]});
            }
            $(menu).select2({
                width: '100%',
                placeholder: '请选择功能',
                allowClear: true,
                data: result
            });
            $(menu).val(null).trigger('change');
        }
    });
}
// 初始化维度下拉框
function initDimension(menu) {
    var param = {};
    $.ajax({
        type: "get",
        url: getDimensionUrl,
        data: param,
        async: false,
        success: function (data) {
            var result = [];
            for (var item in data) {
                result.push({id: item, text: data[item]});
            }
            $(menu).select2({
                width: '100%',
                placeholder: '请选择维度',
                allowClear: true,
                data: result
            });
            $(menu).on("change", function(e) {
                //选择全国时,将区域和分拣中心置灰不可选
                if($(menu).val()=='3'){
                    $('#orgId-EG').attr("disabled","disabled").css("background-color","#EEEEEE;");
                    $('#siteCode-EG').attr("disabled","disabled").css("background-color","#EEEEEE;");
                    $('#orgId-EG').val(null).trigger('change');
                    $('#siteCode-EG').val(null).trigger('change');
                }else{
                    $('#orgId-EG').attr("disabled",false).css("background-color","#FFFFFF;");
                    $('#siteCode-EG').attr("disabled",false).css("background-color","#FFFFFF;");
                }
            });
            $(menu).val(null).trigger('change');
        }
    });
}

// 初始化是否有效下拉框
function initYn(yn) {
    var result = [];
    result.push({id: 1, text: '有效'});
    result.push({id: 0, text: '无效'});
    $(yn).select2({
        width: '100%',
        placeholder: '请选择是否有效',
        allowClear: true,
        data: result
    });
    $(yn).val(null).trigger('change');
}

//初始化区域、分拣中心
function initOrg(orgSelect,siteSelect) {
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
            $(orgSelect).select2({
                width: '100%',
                placeholder:'请选择机构',
                allowClear:true,
                data:result
            });
            $(orgSelect).val(null).trigger('change');
            $(orgSelect)
                .on("change", function(e) {
                    var orgId = $(orgSelect).val();
                    if(orgId){
                        var siteListUrl = '/services/bases/dms/'+orgId;
                        findSite(siteSelect,siteListUrl);
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

//上传按钮
function initImportExcel(){
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);
        var form = document.getElementById('upload_excel_form'),
            formData = new FormData(form);
        $.ajax({
            url:uploadExcelUrl,
            type:"post",
            data:formData,
            processData:false,
            contentType:false,
            success:function(res){
                if(res && res.code == 200){
                    $.msg.ok("上传成功!");
                    $('#import_modal').modal('hide');
                    $('#dataTable').bootstrapTable('refresh');
                }else{
                    $.msg.error("上传失败!" + res.message);
                }
                $('#btn_upload').attr("disabled",false);
            },
            error:function(err){
                $.msg.error("网络连接失败,稍后重试");
                $('#btn_upload').attr("disabled",false);
            }
        });
    });
}

// 提交
function convert2param(){
    var params = {};
    params["id"] = $('#id').val();
    params["menuCode"] = $('#menuCode-EG').val();
    params["menuName"] = $('#menuCode-EG option:selected').text();
    params["dimensionCode"] = $('#dimensionCode-EG').val();
    params["dimensionName"] = $('#dimensionCode-EG option:selected').text();
    params["orgId"] = $('#orgId-EG').val();
    params["orgName"] = $('#orgId-EG option:selected').text();
    params["siteCode"] = $('#siteCode-EG').val();
    params["siteName"] = $('#siteCode-EG option:selected').text();
    params["operateErp"] = $('#operateErp-EG').val();
    params["yn"] = $('#yn-EG option:selected').val();
    return params;
};