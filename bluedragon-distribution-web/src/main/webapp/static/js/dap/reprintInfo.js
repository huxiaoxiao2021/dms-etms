$(function() {

	var queryUrl = '/dap/info/listPddReprintData';
	var exportUrl = '/dap/info/toPddReprintExport';

	$.combobox.createNew('dateGap',{
		width: '150',
		placeholder:'请选择',
		data:[
			{id:'0',text:'今天'},
			{id:'1',text:'昨天'},
			{id:'2',text:'前天'},
			{id:'3',text:'3天前'},
			{id:'4',text:'4天前'},
			{id:'5',text:'5天前'},
			{id:'6',text:'6天前'}
		]
	});

	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable({
				url : queryUrl, // 请求后台的URL（*）
				method : 'get', // 请求方式（*）
				toolbar : '#toolbar', // 工具按钮用哪个容器
				queryParams : oTableInit.getSearchParams, // 查询参数（*）
				uniqueId : "ID", // 每一行的唯一标识，一般为主键列
				pagination : false, // 是否显示分页（*）
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
			var data = $.combobox.getSelected('dateGap');
			if (data != null && data.length > 0) {
				return {'dateGap' : data[0].id};
			}
			return {'dateGap' : -1};
		};

		oTableInit.tableColums = [
			{
				title: "序号",
				formatter: function(value, row, index) {
					// 显示行号
					return index + 1;
				},
				align : "center",
				width: '80px',
			},
			{
				field: 'space1',
				title: '补打条码'
			},
			{
				field: 'space2',
				title: '操作单位'
			},{
				field: 'space3',
				title: '操作人'
			},
			{
				field: 'space4',
				title: '操作时间'
			}
			];

		oTableInit.refresh = function () {
			$('#dataTable').bootstrapTable('refresh');
		};

		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
			$.combobox.clearAllSelected('dateGap');

			$('#btn_query').click(function() {
				tableInit().refresh();
			});
		};
		return oInit;
	};

	//导出
	function initExport(tableInit) {
		$('#btn_export').click(function () {
			var params = tableInit.getSearchParams();
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

	pageInit().init();
	tableInit().init();
	initExport(tableInit());

});