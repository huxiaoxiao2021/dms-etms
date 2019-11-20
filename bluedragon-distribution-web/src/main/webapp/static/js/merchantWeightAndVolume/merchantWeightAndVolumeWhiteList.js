var queryUrl = '/merchantWeightAndVolume/whiteList/listData';
var importUrl = '/merchantWeightAndVolume/whiteList/toImport';
var exportUrl = '/merchantWeightAndVolume/whiteList/toExport';
$(function () {

    $('#fileField').hide();
    //浏览
    $('#btn_browse').click(function () {
        $('#fileField').click();
    });

    var tableInit;
    tableInit = function () {
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
        oTableInit.getSearchCondition = function (_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param";
            }
            $(_selector).each(function () {
                var _k = this.id;
                var _v = $(this).val();
                if (_k && (_v != null && _v != '')) {
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'merchantId',
            title: '商家配送ID',
            align: 'center',
            visible: false
        }, {
            field: 'merchantCode',
            title: '商家配送编码',
            align: 'center'
        }, {
            field: 'merchantName',
            title: '商家名称',
            align: 'center'
        }, {
            field: 'operateOrgCode',
            title: '操作区域ID',
            align: 'center'
        }, {
            field: 'operateOrgName',
            title: '操作区域名称',
            align: 'center'
        }, {
            field: 'operateSiteCode',
            title: '操作场站ID',
            align: 'center'
        }, {
            field: 'operateSiteName',
            title: '操作场站',
            align: 'center'
        }, {
            field: 'createErp',
            title: '创建人ERP',
            align: 'center',
            visible: true
        }, {
            field: 'createUserName',
            title: '创建人',
            align: 'center'
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'operateType',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return '<a class="delete" href="javascript:void(0)" ><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;删除&nbsp;</a>';
            },
            events: {
                'click .delete': function (e, value, row, index) {
                    confirm('确定删除此商家？',function () {
                        var param = {};
                        param.merchantId = row.merchantId;
                        param.merchantCode = row.merchantCode;
                        param.operateSiteCode = row.operateSiteCode;
                        param.createErp = row.createErp;
                        jQuery.ajax({
                            type: 'post',
                            url: '/merchantWeightAndVolume/whiteList/delete',
                            dataType: "json",//必须json
                            contentType: "application/json", // 指定这个协议很重要
                            data: JSON.stringify(param),
                            async: false,
                            success: function (result) {
                                if(result && result.code == 200){
                                    tableInit().refresh();
                                }else {
                                    Jd.alert("删除失败!");
                                }
                            }
                        });
                    }, function () {});
                }
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            //查询
            $('#btn_query').click(function () {
                tableInit().refresh();
            });

        };

        return oInit;
    };

    //导入
    $('#btn_improt').click(function(){
        $('#importExcelFile').val(null);
        $('#improt_modal').modal('show');
    });

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            var params = tableInit.getSearchCondition();
            var form = $("<form method='post'></form>"),
                input;
            form.attr({"action": exportUrl});

            $.each(params, function (key, value) {
                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": key});
                input.val(value);
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        });
    }
    initOrg();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());
    initImportExcel();

});

//自定义确认框
function confirm(msg,okFunc,cancelFunc){
    var cancelFunc = arguments[2] ? arguments[2] : function () {};
    layer.confirm( '<span style="margin-left:40px;margin-right:40px;font-size: 18px;font-weight: bold;font-family:"Arial","Microsoft YaHei","黑体","宋体",sans-serif;">'
        + msg+ '</span>', {
        title:'请仔细确认',
        closeBtn: 0,
        shade: 0.7,
        icon: 3,
        anim: 1,
        area: ['330px', '190px'],
        btn: ['确定','取消']
    },function (index) {
        okFunc();
        layer.close(index);
    },function () {
        cancelFunc();
    });

}

// 初始化大区下拉框
function initOrg() {
    var url = "/services/bases/allorgs";
    var param = {};
    $.ajax({
        type: "get",
        url: url,
        data: param,
        async: false,
        success: function (data) {
            var result = [];
            for (var i in data) {
                if (data[i].orgId && data[i].orgId != "") {
                    result.push({id: data[i].orgId, text: data[i].orgName});
                }
            }
            $('#org-select').select2({
                width: '100%',
                placeholder: '请选择机构',
                allowClear: true,
                data: result
            });
            $("#org-select").on("change", function (e) {
                $("#query-form #orgCode").val($("#org-select").val());
                $("#query-form #siteCode").val("");
                var orgId = $("#org-select").val();
                if (orgId) {
                    var siteListUrl = '/services/bases/dms/' + orgId;
                    findSite("#site-select", siteListUrl, "#query-form #siteCode");
                }
            });
            $("#site-select").on("change", function (e) {
                var _s = $("#site-select").val();
                $("#query-form #siteCode").val(_s);
            });
            if ($("#loginUserOrgId").val() != -1) {
                //登录人大区
                $('#org-select').val($("#loginUserOrgId").val()).trigger('change');
            } else {
                $('#org-select').val(null).trigger('change');
            }
        }
    });

}
var initLogin = true;
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

function initImportExcel(){
    //上传按钮
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);
        var inputValue = $('#importExcelFile').val().trim();
        var index1 = inputValue.lastIndexOf(".");
        var index2 = inputValue.length;
        var suffixName = inputValue.substring(index1+1,index2);
        if(inputValue == ''){
            Jd.alert('请先浏览文件在上传!');
            $('#btn_upload').attr("disabled",false);
            return;
        }
        if(suffixName != 'xls'){
            Jd.alert('请上传指定Excel文件!');
            $('#btn_upload').attr("disabled",false);
            return;
        }
        var form = document.getElementById('upload_excel_form'),
            formData = new FormData(form);
        $.ajax({
            url:importUrl,
            type:"post",
            data:formData,
            processData:false,
            contentType:false,
            success:function(res){
                if(res && res.code == 200){
                    Jd.alert("上传成功!");
                }else{
                    Jd.alert("上传失败!" + res.message);
                }
                $('#btn_upload').attr("disabled",false);
            },
            error:function(err){
                Jd.alert("网络连接失败,稍后重试");
                $('#btn_upload').attr("disabled",false);
            }
        });
    });
}
