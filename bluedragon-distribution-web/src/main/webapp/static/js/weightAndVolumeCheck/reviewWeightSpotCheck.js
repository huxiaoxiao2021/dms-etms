var queryUrl = '/reviewWeightSpotCheck/listData';
var importUrl = '/reviewWeightSpotCheck/toImport';
var exportUrl = '/reviewWeightSpotCheck/toExport';
var exportSpotUrl = '/reviewWeightSpotCheck/toExportSpot';
$(function () {

    $('#fileField').hide();
    //浏览
    $('#btn_browse').click(function () {
        $('#fileField').click();
    });

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
                pageSize: 500, // 每页的记录行数（*）
                pageList: [200, 500], // 可供选择的每页的行数（*）
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
            field: 'reviewDate',
            title: '复核日期',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateOfTs(value);
            }
        }, {
            field: 'reviewOrgName',
            title: '复核区域',
            align: 'center'
        },{
            field: 'reviewMechanismType',
            title: '机构类型',
            align: 'center',
            formatter: function (value, row, index) {
                return (value == null || value == -1) ? "" : ((value == 0) ? "分拣中心" : "转运中心");
            }
        }, {
            field: 'reviewSiteName',
            title: '机构名称',
            align: 'center'
        }, {
            field: 'spotCheckType',
            title: '业务类型',
            align: 'center',
            formatter: function (value, row, index) {
                return (value != null && value == "1") ? "B网" : "C网";
            }
        }, {
            field: 'normalPackageNum',
            title: '普通应抽查运单数',
            align: 'center'
        },{
            field: 'normalPackageNumOfActual',
            title: '普通实际抽查运单数',
            align: 'center'
        }, {
            field: 'normalCheckRate',
            title: '普通抽查率',
            align: 'center'
        },{
            field: 'normalPackageNumOfDiff',
            title: '普通抽查差异运单数',
            align: 'center'
        },{
            field: 'normalCheckRateOfDiff',
            title: '普通抽查差异率',
            align: 'center'
        },{
            field: 'trustPackageNum',
            title: '信任商家应抽查运单数',
            align: 'center'
        },{
            field: 'trustPackageNumOfActual',
            title: '信任商家实际抽查运单数',
            align: 'center'
        },{
            field: 'trustCheckRate',
            title: '信任商家抽查率',
            align: 'center'
        },{
            field: 'trustPackageNumOfDiff',
            title: '信任商家抽查差异运单数',
            align: 'center'
        },{
            field: 'trustCheckRateOfDiff',
            title: '信任商家抽查差异率',
            align: 'center'
        },{
            field: 'totalCheckRate',
            title: '总抽查率',
            align: 'center'
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {

            /*起始时间*/
            /*截止时间*/
            $.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            //查询
            $('#btn_query').click(function () {
                var days = getDaysByDateString($('#startTime').val(),$('#endTime').val());
                if(days > 1){
                    Jd.alert("查询时间不能超过1天，请缩小时间范围!");
                    return;
                }
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

    //导出抽查任务
    $('#btn_export_spot').click(function () {

        var form = $("<form method='post'></form>");
        form.attr({"action": exportSpotUrl});
        form.appendTo(document.body);
        form.submit();
        document.body.removeChild(form[0]);

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

    initSelect();
    initOrg();
    initDateQuery();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());
    initImportExcel();

});

function initSelect() {
    var defualt = $("#query-form #spotCheckTypeSelect").val();
    if(defualt != 2){
        $("#query-form #spotCheckType").val(defualt);
    }
    $("#query-form #spotCheckTypeSelect").on('change', function (e) {
        var v = $("#query-form #spotCheckTypeSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #spotCheckType").val(v);
        } else {
            $("#query-form #spotCheckType").val(null);
        }
    });
}

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#startTime").val(v+" 00:00:00");
    $("#endTime").val(v+" 23:59:59");
}

function  getDaysByDateString(dateString1,dateString2) {
    var startDate = Date.parse(dateString1.replace('/-/g', '/'));
    var endDate = Date.parse(dateString2.replace('/-/g', '/'));
    var days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
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
            $(selectId).val(null).trigger('change');
        }
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
            $('#site-group-select').select2({
                width: '100%',
                placeholder: '请选择机构',
                allowClear: true,
                data: result
            });
            $("#site-group-select")
                .on("change", function (e) {
                    $("#query-form #createSiteCode").val("");
                    var orgId = $("#site-group-select").val();
                    $("#query-form #reviewOrgCode").val(orgId);
                    if (orgId) {
                        var siteListUrl = '/services/bases/dms/' + orgId;
                        findSite("#site-select", siteListUrl, "#query-form #createSiteCode");
                    }

                });
            $("#site-select").on("change", function (e) {
                var _s = $("#site-select").val();
                $("#query-form #createSiteCode").val(_s);
            });

            if ($("#loginUserOrgId").val() != -1) {
                //登录人大区
                $('#site-group-select').val($("#loginUserOrgId").val()).trigger('change');
            } else {
                $('#site-group-select').val(null).trigger('change');
            }
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
        if(suffixName != 'xlsx'){
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
