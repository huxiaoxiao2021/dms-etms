var queryUrl = '/whiteList/listData';
var saveUrl = '/whiteList/save';
var deleteUrl = '/whiteList/deleteByIds';
$(function () {

    //todo 调试一下这两个有作用嘛！！！
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
            field: 'menu',
            title: '功能',
            align: 'center'
        }, {
            field: 'dimension',
            title: '维度',
            align: 'center'
        }, {
            field: 'siteName',
            title: '机构名称',
            align: 'center'
        }, {
            field: 'erp',
            title: 'ERP账号',
            align: 'center'
        },{
            field: 'createUser',
            title: '添加人',
            align: 'center'
        }, {
            field: 'createTime',
            title: '添加时间',
            align: 'center',
            formatter : function(value,row,index){
                return value=="0"?"空闲":value=="1"?"非空闲":value=="2"?"已满":"未知状态";
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        var postdata = {};
        oInit.init = function() {
            $('#dataEditDiv').hide();

            //查询
            $('#btn_query').click(function() {
                tableInit().refresh();
            });
            //新增
            $('#btn_add').click(function() {
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if(_k){
                        $(this).val('');
                    }
                });
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
                //$("#edit-form").data("bootstrapValidator").resetForm();
                initOrg();
            });
            // 删除
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
/*                        if (rows[i].collectGoodsPlaceType == 4) {
                            alert("不允许删除异常类型的集货位");
                            return;
                        }
                        if (rows[i].collectGoodsPlaceStatus != 0) {
                            alert("不允许删除非空闲的集货位");
                            return;
                        }*/
                    };
                    $.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
                        if(res&&res.succeed&&res.data){
                            alert('操作成功,删除'+res.data+'条。');
                            tableInit().refresh();
                        }else{
                            alert(res.message);
                        }
                    });
                }
            });

            //保存
            $('#btn_submit').click(function() {
                $('#btn_submit').attr("disabled",true);
                //先去校验
/*                if(!editValidator()){
                    $('#btn_submit').attr("disabled",false);
                    return;
                }*/

                var params = {};
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if(_k && _v){
                        if(_k == 'hintMark'){
                            params['hintMessage']=_v;
                        }else{
                            params[_k]=_v;
                        }
                    }
                });
                var url = saveUrl;
                $.ajaxHelper.doPostSync(url,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        Jd.alert('操作成功');
                        tableInit().refresh();
                    }else if(res){
                        Jd.alert(res.message);
                    }else{
                        Jd.alert('服务异常');
                    }
                    $('#btn_submit').attr("disabled",false);
                    $('#dataEditDiv').hide();
                    $('#dataTableDiv').show();

                });
            });
            //取消
            $('#btn_return').click(function() {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });
        };
        return oInit;
    };


    initOrg();
    tableInit().init();
    pageInit().init();

});

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
                    $("#edit-form #siteCode").val("");
                    var orgId = $("#site-group-select").val();
                    $("#edit-form #reviewOrgCode").val(orgId);
                    if (orgId) {
                        var siteListUrl = '/services/bases/dms/' + orgId;
                        findSite("#site-select", siteListUrl, "#edit-form #siteCode");
                    }

                });
            $("#site-select").on("change", function (e) {
                var _s = $("#site-select").val();
                $("#edit-form #siteCode").val(_s);
                var _n = $("#site-select").find("option:selected").text();
                $("#edit-form #siteName").val(_n);
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
