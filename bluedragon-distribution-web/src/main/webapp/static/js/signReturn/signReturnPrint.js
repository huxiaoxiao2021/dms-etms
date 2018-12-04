$(function() {
    var queryUrl = '/signReturn/query';
    var queryListUrl = '/signReturn/listData';
    var exportUrl = '/signReturn/toExport';
    var printUrl = '/signReturn/toPrint';

	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable({
				url : queryUrl, // 请求后台的URL（*）
				method : 'post', // 请求方式（*）
				toolbar : '#toolbar', // 工具按钮用哪个容器
				queryParams : oTableInit.getSearchParams, // 查询参数（*）
				// height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
				// uniqueId : "ID", // 每一行的唯一标识，一般为主键列
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
		        if(_k && (_v != null && _v != '')){
		        	params[_k] = _v;
		        }
		    });
		    return params;
		};
		oTableInit.tableColums = [
			{
				field : 'newWaybillCode',
				title : '签单返回合单运单号',
                width:200,
                class:'min_120'
			}, {
				field : 'busiId',
				title : '商家编码',
                width:120,
                class:'min_120'
			}, {
				field : 'busiName',
				title : '商家名称',
                width:120,
                class:'min_120'
			}, {
				field : 'returnCycle',
				title : '返单周期',
                width:120,
                class:'min_120'
			}, {
				field : 'operateTime',
				title : '合单操作日期',
                formatter : function(value,row,index){
                    return $.dateHelper.formateDateOfTs(value);
                },
                width:120,
                class:'min_120'
			}, {
				field : 'orgId',
				title : '合单操作机构',
                width:120,
                class:'min_120'
			}, {
				field : 'operateUser',
				title : '合单操作人',
                width:120,
                class:'min_120'
			}, {
                field : 'mergeCount',
                title : '合单运单数',
                width:120,
                class:'min_120'
            }];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};

    var dataTableListInit = function() {
        var oTableInit = new Object();
        oTableInit.init = function() {
            $('#dataTableList').bootstrapTable({
                url : queryListUrl, // 请求后台的URL（*）
                method : 'post', // 请求方式（*）
                queryParams : oTableInit.getSearchParams, // 查询参数（*）
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
                minimumCountColumns : 2, // 最少允许的列数
                strictSearch : true,
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
        oTableInit.tableColums = [
            {
                field : 'waybillCode',
                title : '运单号',
                width:200,
                class:'min_120'
            }, {
                field : 'deliveredTime',
                title : '妥投时间',
                formatter : function(value,row,index){
                    return $.dateHelper.formateDateTimeOfTs(value);
                },
                width:200,
                class:'min_120'
            }];
        oTableInit.refresh = function() {
            $('#dataTableList').bootstrapTable('refresh');
        };
        return oTableInit;
    };
    var refreshCount = 0;
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {

            $('#dataTable').on('load-success.bs.table', function (e, data) {
                debugger;
                if( data && data.rows){
                    if( refreshCount <1){
                        refreshCount++;
                        dataTableListInit().init();
                    }
                }

            });

			//1.查询
		    $('#btn_query').click(function() {

		    	tableInit().refresh();
                dataTableListInit().refresh();

			});

            //2.导出
			$('#btn_export').click(function() {
                //TODO
                var newWaybillCode = $('#newWaybillCode').val();
                var waybillCode = $('#waybillCode').val();
                if(!newWaybillCode && !waybillCode){
                    Jd.alert("无可导出内容");
                    return;
                }
                // 提交表单
                var newWaybillCode=$("#newWaybillCode").val();
                var waybillCode = $("#waybillCode").val();
                var url = "/signReturn/toExport";
                var form = $("<form method='post'></form>"),
                    input1,input2;
                form.attr({"action": url});

                input1 = $("<input type='hidden' id='input1' class='search-param'>");
                input1.attr({"name": "newWaybillCode"});
                input1.val(newWaybillCode);
                input2 = $("<input type='hidden' id='input2' class='search-param'>");
                input2.attr({"name": "waybillCode"});
                input2.val(waybillCode);
                form.append(input1).append(input2);
                form.appendTo(document.body);
                form.submit();
                document.body.removeChild(form[0]);

			});

			//4.打印
			$('#btn_print').click(function() {
				//TODO
                var newWaybillCode = $('#newWaybillCode').val();
                var waybillCode = $('#waybillCode').val();
                if(!newWaybillCode && !waybillCode){
                    Jd.alert("无可打印内容");
                    return;
                }
                window.open("/signReturn/toPrint?newWaybillCode="+$('#newWaybillCode').val()+"&waybillCode="+$('#waybillCode').val());
			});
		};
		return oInit;
	};


	tableInit().init();
	pageInit().init();

});
