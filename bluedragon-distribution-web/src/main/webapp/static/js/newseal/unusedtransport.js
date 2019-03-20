$(function() {
  var queryUrl = '/newseal/preSealVehicle/getRemainTransportCode';
	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable({
                url : queryUrl, // 请求后台的URL（*）
                method : 'get', // 请求方式（*）
                toolbar : '#toolbar', // 工具按钮用哪个容器
                // queryParams : oTableInit.getSearchParams, // 查询参数（*）
                //height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId : "ID", // 每一行的唯一标识，一般为主键列
                pagination : true, // 是否显示分页（*）
                pageNumber : 1, // 初始化加载第一页，默认第一页
                pageSize : 10, // 每页的记录行数（*）
                pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
                cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination : "client", // 分页方式：client客户端分页，server服务端分页（*）
                striped : true, // 是否显示行间隔色
                showColumns : true, // 是否显示所有的列
                // sortable : true, // 是否启用排序
                // sortOrder : "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
//                 showRefresh : true, // 是否显示刷新按钮
//                 minimumCountColumns : 2, // 最少允许的列数
                clickToSelect : true, // 是否启用点击选中行
                // showToggle : false, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
//                 strictSearch : true,
                // icons: {refresh: "glyphicon-repeat", toggle:
                // "glyphicon-list-alt", columns: "glyphicon-list"},
                // search:false,
                // cardView: true, //是否显示详细视图
                // detailView: true, //是否显示父子表
                // showFooter:true,
                // paginationVAlign:'center',
                // singleSelect:true,
                responseHandler: function(data){
                    if(data.code == 200){
                        return data.data;
                    }else{
                        alert(data.message);
                        return [];
                    }
                },
                columns : oTableInit.tableColums,
                onLoadError: function(){  //加载失败时执行
                    alert("服务异常!");
                }
            });
		};

		oTableInit.tableColums = [ {
				checkbox : true
			}, {
				field : 'tranCode',
				title : '运力编码',
                formatter: function (value, row, index) {
                    return ' <div style="width:200px;">' + value + ' </div>';
                }
			}, {
				field : 'rname',
				title : '目的地',
                formatter: function (value, row, index) {
                    return ' <div style="width:300px;">' + value + ' </div>';
                }
			}, {
				field : 'sendTimeStr',
				title : '标准发车时间',
                formatter: function (value, row, index) {
                    return ' <div style="width:200px;">' + value + ' </div>';
                }
        	} ];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});

		};
		return oInit;
	};
	
	tableInit().init();
	pageInit().init();
});