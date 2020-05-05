$(function() {
	var saveUrl = '/collect/collectGoodsDetail/save';
	var deleteUrl = '/collect/collectGoodsDetail/deleteByIds';
  var detailUrl = '/collect/collectGoodsDetail/detail/';
  var queryUrl = '/schedule/dmsScheduleInfo/queryEdnPickingList';
  var exportUrl = '/schedule/dmsScheduleInfo/exportEdnPickingList';
	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable({
				url : queryUrl, // 请求后台的URL（*）
				method : 'post', // 请求方式（*）
				toolbar : '#toolbar', // 工具按钮用哪个容器
				queryParams : oTableInit.getSearchParams, // 查询参数（*）
				height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
		        if(_k && (_v != null && _v != '')){
		        	params[_k] = _v;
		        }
		    });
		    return params;
		};
        oTableInit.tableColums = [ {
            field : 'scheduleBillCode',
            title : '操作',
            formatter : function(value,row,index){
                return '<a href="#" class="show-storage-view" onclick="printEdnPickingList(\''+row.scheduleBillCode+'\',event)">'+'打印拣货单</a>'
                      +'<br/>'+'<a href="#" class="show-storage-view" onclick="printEdnDeliveryReceipt(\''+row.scheduleBillCode+'\',event)">'+'打印配送单</a>';
            }
        },{
            field : 'scheduleBillCode',
            title : '调度单号',
            formatter : function(value,row,index){
                return '<a href="#" class="show-storage-view" onclick="showView(\''+row.scheduleBillCode+'\',event)">'+value+'</a>';
            }
        },{
            field : 'carrierName',
            title : '承运商名称'
        }, {
            field : 'scheduleTime',
            title : '下发时间',
            formatter: function (value, row, index) {
                if (value == null) {
                    return null;
                } else {
                    return $.dateHelper.formatDateTime(new Date(value));
                }
            }
        }];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
			$('#dataEditDiv').hide();


            $.datePicker.createNew({
                elem: '#scheduleTimeGteStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/


                }
            });
            $.datePicker.createNew({
                elem: '#scheduleTimeLtStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});
			$('#btn_add').click(function() {
			    $('.edit-param').each(function () {
			    	var _k = this.id;
			        if(_k){
			        	$(this).val('');
			        }
			    });
			    $('#edit-form #typeGroup').val(null).trigger('change');
			    $('#edit-form #parentId').val(null).trigger('change');
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
			});
			// 修改操作
			$('#btn_edit').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length > 1) {
					alert("修改操作，只能选择一条数据");
					return;
				}
				if (rows.length == 0) {
					alert("请选择一条数据");
					return;
				}
			    $.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
			    	if(res&&res.succeed&&res.data){
					    $('.edit-param').each(function () {
					    	var _k = this.id;
					        var _v = res.data[_k];
					        if(_k){
					        	if(_v != null && _v != undefined){
						        	$(this).val(_v);
						        }else{
						        	$(this).val('');
						        }
					        } 
					    });
			    	}
			    });
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
			});

			// 删
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
				    };
					$.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
				    	if(res&&res.succeed&&res.data){
				    		alert('操作成功,删除'+res.data+'条。');
				    		tableInit().refresh();
				    	}else{
				    		alert('操作异常！');
				    	}
				    });
				}
			});

            $("#btn_export").on("click",function(e){

                var params = tableInit().getSearchCondition();

                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action":exportUrl});

                $.each(params,function(key,value){

                    input = $("<input type='hidden' class='search-param'>");
                    input.attr({"name":key});
                    input.val(value);
                    form.append(input);
                });
                form.appendTo(document.body);
                form.submit();
                document.body.removeChild(form[0]);

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
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});	
			$('#btn_return').click(function() {
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});		
		};
		return oInit;
	};
	tableInit().init();
	pageInit().init();
});
function printPdf(pdfUrl){
	$("#pdfIfram").attr('src', pdfUrl);
	$("#pdfIfram").contentWindow.print();
}
function printEdnPickingList(scheduleBillCode,event){
    //获取明细
    var queryDUrl = '/schedule/dmsScheduleInfo/printEdnPickingList/'+scheduleBillCode;
	var param = {};
    $.ajaxHelper.doPostSync(queryDUrl,
		JSON.stringify(param),
			function(data){
        if(data.code == 200){
        	printPdf(data.data);
        }else{
            alert(data.message);
        }        
    });
    event.stopPropagation();
}
function printEdnDeliveryReceipt(scheduleBillCode,event){
    //获取明细
    var queryDUrl = '/schedule/dmsScheduleInfo/printEdnDeliveryReceipt/'+scheduleBillCode;
    $("#deliveryReceiptDetailTbody").html("<tr><td colspan='5'>努力加载中...</td></tr>");
    $('#printEdnDeliveryReceipt').modal("show");
	var param = {};
    $.ajaxHelper.doPostSync(queryDUrl,
		JSON.stringify(param),
			function(data){
        var tlogBodyHtml = "";
        if(data.code == 200){
            if(data.data.length == 0){
            	tlogBodyHtml = "<tr><td colspan='5'>未获取到明细数据</td></tr>";
            }
            for(var i = 0 ; i<data.data.length ; i++ ){
                var pojo = data.data[i];
                tlogBodyHtml += "<tr><td width=\"25%\">"+pojo.ednBatchNum+"</td>";
                tlogBodyHtml += "<td width=\"25%\">"+'<a href="#" class="show-storage-view" onclick="printPdf(\''+pojo.deliveryReceiptUrl+'\',event)">'+'打印</a>'+"</td>";
                tlogBodyHtml += "</tr>";

            }
        }else{
            tbodyHtml = "<tr><td colspan='6'>"+data.message+"</td></tr>";
        }        
        $("#deliveryReceiptDetailTbody").html(tlogBodyHtml);
    });
    event.stopPropagation();
}
function showView(scheduleBillCode,event){
    //获取明细
    var queryDUrl = '/schedule/dmsScheduleInfo/queryEdnPickingVo/'+scheduleBillCode;
	$("#scheduleBillCodeView").text('');
	$("#carrierNameView").text('');
	$("#scheduleTimeView").text('');
    $("#scheduleInfoTbody").html("<tr><td colspan='5'>努力加载中...</td></tr>");
    $("#operateLogTbody").html("<tr><td colspan='5'>努力加载中...</td></tr>");
    $('#viewModal').modal("show");
	var param = {};
    $.ajaxHelper.doPostSync(queryDUrl,
		JSON.stringify(param),
			function(data){

        var tbodyHtml = "";
        var tlogBodyHtml = "";
        if(data.code == 200){
        	
        	$("#scheduleBillCodeView").text(data.data.scheduleBillCode);
        	$("#carrierNameView").text(data.data.carrierName);
        	$("#scheduleTimeView").text($.dateHelper.formatDateTime(new Date(data.data.scheduleTime)));
            if(data.data.dmsScheduleInfoList.length == 0){
                tbodyHtml = "<tr><td colspan='5'>未获取到明细数据</td></tr>";
            }
            for(var i = 0 ; i<data.data.dmsScheduleInfoList.length ; i++ ){
                var pojo = data.data.dmsScheduleInfoList[i];
                tbodyHtml += "<tr><td width=\"25%\">"+pojo.rowNum+"</td>";
                tbodyHtml += "<td width=\"25%\">"+pojo.businessBatchCode+"</td>";
                tbodyHtml += "<td width=\"50%\">"+pojo.waybillCode+"</td>";
                tbodyHtml += "</tr>";

            }
            if(data.data.dmsEdnOperateLogList.length == 0){
            	tlogBodyHtml = "<tr><td colspan='5'>未获取到明细数据</td></tr>";
            }
            for(var i = 0 ; i<data.data.dmsEdnOperateLogList.length ; i++ ){
                var pojo = data.data.dmsEdnOperateLogList[i];
                tlogBodyHtml += "<tr><td width=\"25%\">"+pojo.operateTime+"</td>";
                tlogBodyHtml += "<td width=\"25%\">"+pojo.operateUser+"</td>";
                tlogBodyHtml += "<td width=\"50%\">"+pojo.operateContent+"</td>";
                tlogBodyHtml += "</tr>";

            }
        }else{
            tbodyHtml = "<tr><td colspan='6'>"+data.message+"</td></tr>";
        }

        $("#scheduleInfoTbody").html(tbodyHtml);
        
        $("#operateLogTbody").html(tlogBodyHtml);
        
    });
    event.stopPropagation();
}