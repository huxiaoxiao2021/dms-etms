$(function() {
	var batchSealUrl = '/newseal/preSealVehicle/batchSeal';
    var queryUrl = '/newseal/preSealVehicle/queryPreSeals';

    $.combobox.createNew('hourRange',{
        width: '150',
        placeholder:'查询时间范围',
        allowClear:true,
        data:[
            {id:'12',text:'12小时'},
            {id:'24',text:'24小时'},
            {id:'48',text:'48小时'},
            {id:'72',text:'72小时'},
        ]
    });

	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable({
				url : queryUrl, // 请求后台的URL（*）
				method : 'post', // 请求方式（*）
				toolbar : '#toolbar', // 工具按钮用哪个容器
                toolbarAlign : 'right',
				queryParams : oTableInit.getSearchParams, // 查询参数（*）
				// height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
				uniqueId : "ID", // 每一行的唯一标识，一般为主键列
				pagination : false, // 是否显示分页（*）
				pageNumber : 1, // 初始化加载第一页，默认第一页
				pageSize : 10000, // 每页的记录行数（*）
				pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
				cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				sidePagination : "client", // 分页方式：client客户端分页，server服务端分页（*）
				striped : true, // 是否显示行间隔色
				// showColumns : true, // 是否显示所有的列
				// sortable : true, // 是否启用排序
				// sortOrder : "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
				showRefresh : false, // 是否显示刷新按钮
				// minimumCountColumns : 2, // 最少允许的列数
				clickToSelect : false, // 是否启用点击选中行
				// showToggle : true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
// 				strictSearch : true,
				// icons: {refresh: "glyphicon-repeat", toggle:
				// "glyphicon-list-alt", columns: "glyphicon-list"},
				// search:false,
				// cardView: true, //是否显示详细视图
				detailView: true, //是否显示父子表
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
		        if(_k && (_v != null && _v != '')){
		        	params[_k] = _v;
		        }
		    });
		    return params;
		};
        oTableInit.tableColums = [{
            checkbox : true,
            checked:true,
            width: '50px',
            formatter : stateFormatter
        },{
            title: "序号",
            formatter: function(value, row, index) {
                // 显示行号
                return index + 1;
            },
            align: 'center',
            width: '50px'
        }, {
            field : 'id',
            title : 'ID',
            visible:false
        }, {
            field : 'preSealUuid',
            title : 'preSealUuid',
            visible:false
        }, {
            field : 'createSiteCode',
            title : 'createSiteCode',
            visible:false
        }, {
            field : 'createSiteName',
            title : 'createSiteName',
            visible:false
        }, {
            field : 'receiveSiteCode',
            title : 'receiveSiteCode',
            visible:false
        }, {
            field : 'receiveSiteName',
            title : 'receiveSiteName',
            visible:false
        }, {
            field : 'sendCodes',
            title : 'sendCodes',
            visible:false
        }, {
            field : 'vehicleNumbers',
            title : 'vehicleNumbers',
            visible:false
        }, {
            field : 'receiveSiteName',
            title : '目的地'
        }, {
            field : 'sendCodes',
            title : '批次数量',
            formatter: function (value, row, index) {
                return value.length;
            }
        }, {
            field : 'transportCode',
            title : '运力编码'
        }, {
            field : 'sendCarTime',
            title : '标准发车时间',
            formatter: function (value, row, index) {
                return value.substring(value.length-5);
            }
        }, {
            field : 'sealCodes',
            title : '封签号'
        } ];
        function stateFormatter(value, row, index) {
            if (row.sendCodes.length > 0) {
                return {
                    checked: true//设置选中
                };
            } else {
                return {
                    checked: false//设置选中
                };
            }
        };
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};

		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
		    $('#btn_query_transport').click(function() {
                window.open("/newseal/preSealVehicle/unusedtransport");
			});

		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});

			$('#btn_submit').click(function() {
				var params = {};
				$('.edit-param').each(function () {
			    	var _k = this.id;
			        var _v = $(this).val();
			        if(_k && _v){
			        	params[_k]=_v;
			        }
			    });
				$.ajaxHelper.doPostSync(saveUrl,JSON.stringify(params),function(res){
			    	if(res&&res.succeed){
			    		alert('操作成功');
			    		tableInit().refresh();
			    	}else{
			    		alert('操作异常');
			    	}
			    });
			});

		};
		return oInit;
	};

	tableInit().init();
	pageInit().init();
});