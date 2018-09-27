$(function() {
    var queryUrl = '/jinpeng/performance/query';
    //将打印按钮置灰
    document.getElementById("btn_print").setAttribute("disabled", true);
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
            field : 'performanceCode',
            title : '履约单号',
            width:200,
            class:'min_120'
        }, {
            field : 'poNo',
            title : 'PO单号',
            width:180,
            class:'min_180'
        }, {
            field : 'waybillCode',
            title : '运单号'
        }, {
            field : 'packageCode',
            title : '包裹号',
        } , {
            field : 'goodName',
            title : '商品名称'
        }, {
            field : 'goodNumber',
            title : '商品件数',
            width:200,
            class:'min_120'
        } ];
        oTableInit.refresh = function() {
            // $('#dataTable').bootstrapTable('refreshOptions',{pageNumber:1}); //更改刷新时回到第一页
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        var postdata = {};
        oInit.init = function() {
            $('#btn_query').click(function() {
                tableInit().refresh();
                isCanPrint();
            });
            // 打印
            $('#btn_print').click(function(){
                // window.location.href="/jinpeng/performance/print?performanceCode="+$('#performanceCode').val()+"&waybillorPackCode="+$('#waybillorPackCode').val();
                window.open("/jinpeng/performance/print?performanceCode="+$('#performanceCode').val()+"&waybillorPackCode="+$('#waybillorPackCode').val());
            });


        };
        return oInit;
    };

    function isCanPrint(){
        //判断是否可以打印
        jQuery.ajax({
            type: 'Post',
            contentType : 'application/json',
            url : '/jinpeng/performance/isCanPrint',
            dataType : 'json',
            data:JSON.stringify(getParams()),
            async:true,
            success : function(data) {
                if (data==1) {
                    document.getElementById("isPrint").style.display = "";
                    document.getElementById("noPrint").style.display = "none";
                    //可以打印的时候将打印按钮置为正常
                    document.getElementById("btn_print").removeAttribute("disabled");
                } else if(data==2) {
                    document.getElementById("noPrint").style.display = "";
                    document.getElementById("isPrint").style.display = "none";
                    //将打印按钮置灰
                    document.getElementById("btn_print").setAttribute("disabled", true);
                }else{
                    document.getElementById("isPrint").style.display = "none";
                    document.getElementById("noPrint").style.display = "none";
                    //将打印按钮置灰
                    document.getElementById("btn_print").setAttribute("disabled", true);
                }

            }
        });
    };

    function getParams(){
        var params = {};
        params.performanceCode=$('#performanceCode').val();
        params.waybillorPackCode=$('#waybillorPackCode').val();
        return params;
    }

    tableInit().init();
    pageInit().init();

});