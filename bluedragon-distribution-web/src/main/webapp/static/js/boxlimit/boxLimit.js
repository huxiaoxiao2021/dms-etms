var queryUrl = '/boxlimit/listData';
var saveUrl = '/boxlimit/save';
var deleteUrl = '/boxlimit/delete';
var importUrl = '/boxlimit/toImport';
var siteNameUrl = '/boxlimit/getSiteNameById';
$(function () {

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                // height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 20, // 每页的记录行数（*）
                pageList: [20, 50, 100, 500], // 可供选择的每页的行数（*）
                smartDisplay:false,
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
            temp.pageSize = params.limit;
            // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            return temp;
        };
        /**
         * 获取查询参数
         */
        oTableInit.getSearchCondition = function() {
            var params = {};

            $('#query-form input').each(function () {
                var _k = this.name;
                var _v = $(this).val();
                if(_k && (_v != null && _v !== '')){
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            title: '序号',
            align: 'center',
            formatter: function (value,row,index) {
                return index + 1;
            }
        }, {
            field: 'siteName',
            title: '机构名称',
            align: 'center'
        }, {
            field: 'siteId',
            title: '机构ID',
            align: 'center'
        }, {
            field: 'limitNum',
            title: '建箱包裹数',
            align: 'center'
        }, {
            field: 'operatorErp',
            title: '操作人ERP',
            align: 'center'
        },{
            field: 'operatingTime',
            title: '操作时间',
            align: 'center'
        }, {
            field: 'operatorSiteName',
            title: '操作机构',
            align: 'center'
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
                $('#importExcelFile').val(null);
                $('#box_limit_add_form').resetForm();
                $('#edit_title').text('新增');
                $('#edit_modal').modal('show');
            });
            //修改
            $('#btn_update').click(function() {
                $('#importExcelFile').val(null);
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    Jd.alert("错误，未选中数据");
                    return;
                }
                if (rows.length > 1) {
                    Jd.alert("错误，仅可修改一条数据");
                    return;
                }
                $('#edit_title').text('修改');
                $('#site_name_span').text(rows[0].siteName);
                $('#site_name_for_update').val(rows[0].siteName);
                $('#limit_num_for_update').val(rows[0].limitNum);
                $('#site_id_for_update').val(rows[0].siteId);
                $('#id_for_update').val(rows[0].id);
                $('#edit_modal').modal('show');
            });
            // 删除
            $('#btn_delete').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    Jd.alert("错误，未选中数据");
                    return;
                }
                var flag = confirm("是否删除这些数据?");
                if (flag == true) {
                    var deleteIds = [];
                    for(var i in rows){
                        deleteIds.push(rows[i].id);
                    };
                    $.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(deleteIds),function(res){
                        if(res.succeed){
                            Jd.alert('操作成功,删除'+rows.length+'条。');
                            tableInit().refresh();
                        }else{
                            Jd.alert(res.message);
                        }
                    });
                }
            });

            //保存
            $('#btn_submit').click(function() {
                $('#btn_submit').attr("disabled",true);
                var params = {};
                $('#box_limit_add_form input').each(function () {
                    var _k = this.name;
                    var _v = $(this).val();
                    if(_k && _v){
                        if(_k == 'hintMark'){
                            params['hintMessage']=_v;
                        }else{
                            params[_k]=_v;
                        }
                    }
                });
                $.ajaxHelper.doPostSync(saveUrl,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        Jd.alert('操作成功');
                        tableInit().refresh();
                        $('#edit_modal').modal('hide');
                    }else if(res){
                        Jd.alert(res.message);
                    }else{
                        Jd.alert('服务异常');
                    }
                    $('#btn_submit').attr("disabled",false);

                });
            });
            //取消
            $('#btn_return').click(function() {
                // $('#dataEditDiv').hide();
                // $('#dataTableDiv').show();
            });
        };
        return oInit;
    };
    tableInit().init();
    pageInit().init();

    // 根据机构ID查询机构名称
    $('#site_id_for_update').change(function () {
        var querySiteNameParam = {};
        querySiteNameParam['siteId'] = $('#site_id_for_update').val();
        $.ajaxHelper.doGetSync(siteNameUrl, querySiteNameParam, function (res) {
            var spanEle = $('#site_name_span');
            if (res.succeed) {
                spanEle.text(res.data);
                $('#site_name_for_update').val(res.data);
            } else {
                spanEle.text('机构不存在!');
            }
        });
    });

    //上传按钮
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);

        var inputValue = $('#importExcelFile').val().trim();
        var index1 = inputValue.lastIndexOf(".");
        var index2 = inputValue.length;
        var suffixName = inputValue.substring(index1+1,index2);
        if(inputValue === ''){
            Jd.alert('请先浏览文件在上传!');
            $('#btn_upload').attr("disabled",false);
            return;
        }
        if(suffixName !== 'xlsx'){
            Jd.alert('请上传指定Excel文件!');
            $('#btn_upload').attr("disabled",false);
            return;
        }

        var form = document.getElementById('import_excel_file_form'), formData = new FormData(form);
        $.ajax({
            url:importUrl,
            type:"post",
            data:formData,
            processData:false,
            contentType:false,
            success:function(res){
                if(res.succeed){
                    Jd.alert("上传成功!");
                    tableInit().refresh();
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
});
