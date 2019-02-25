$(function() {
	var saveUrl = '/reverse/part/reversePartDetail/save';
	var deleteUrl = '/reverse/part/reversePartDetail/deleteByIds';
  var detailUrl = '/reverse/part/reversePartDetail/detail/';
  var queryUrl = '/reverse/part/reversePartDetail/listData';
  var exportUrl = '/reverse/part/reversePartDetail/toExport';
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
				checkbox : true
			}, {
				field : 'waybillCode',
				title : '运单号'
			}, {
				field : 'createSiteName',
				title : '操作分拣中心'
			}, {
				field : 'allSendPackSum',
				title : '累计发货包裹数',
				formatter : function(value,row,index){
					return "<a href='javascript:void(0);' onclick='allSendPackSumClick(event,\""+row.waybillCode+"\",\""+row.createSiteCode+"\")'>"+value+"</a>";
				}
			}, {
				field : 'noSendPackSum',
				title : '未退包裹数',
				formatter : function(value,row,index){
					return "<a href='javascript:void(0);' onclick='noSendPackSumClick(event,\""+row.waybillCode+"\",\""+row.createSiteCode+"\")'>"+value+"</a>";
				}
			}, {
				field : 'sendTime',
				title : '发货时间',
				formatter : function(value,row,index){
					return $.dateHelper.formateDateTimeOfTs(value);
				}

			}, {
				field : 'receiveTime',
				title : '仓储收货时间',
				formatter : function(value,row,index){
					return $.dateHelper.formateDateTimeOfTs(value);
				}
			}, {
				field : 'createSiteCode',
				title : '操作分拣中ID',
				visible: false
			} , {
				field : 'reverseSiteCode',
				title : '目的地ID',
				visible: false
			}];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {

            $.datePicker.createNew({
                elem: '#sendTimeGEStr',
                theme: '#3f92ea',
                type: 'datetime',
                //btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });

            $.datePicker.createNew({
                elem: '#sendTimeLEStr',
                theme: '#3f92ea',
                type: 'datetime',
                //btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });

			$('#dataEditDiv').hide();		
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});
		    //导出
		    $('#btn_export').click(function(){

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

    initOrg();

	tableInit().init();
	pageInit().init();
});

/**
 * 累计发货明细
 * @param waybillCode
 * @param createSiteCode
 */
function allSendPackSumClick(event,waybillCode,createSiteCode){

    event.stopPropagation();


    var url = "/reverse/part/reversePartDetail/allSendPack/"+createSiteCode+"/"+waybillCode;

	$.get(url,function(data){

		if(data.code != 200){
			alert(data.message);
		}else{

			var htmlStr = "";
			for(var sd in data.data){

                htmlStr += "<tr><td>"+data.data[sd].sendCode+"</td><td>"+data.data[sd].receiveSiteName+"</td><td>"+data.data[sd].packNo+"</th> <td>"+$.dateHelper.formateDateTimeOfTs(data.data[sd].sendTime)+"</td></tr>";
			}

            $("#allPackSendModalTbody").html(htmlStr);
            $("#allPackSendModal").modal('show');
		}

	},'json');


	return false;
}

/**
 * 未退包裹明细
 * @param waybillCode
 * @param createSiteCode
 */
function noSendPackSumClick(event,waybillCode,createSiteCode){

    event.stopPropagation();


    var url = "/reverse/part/reversePartDetail/noSendPack/"+createSiteCode+"/"+waybillCode;

    $.get(url,function(data){

        if(data.code != 200){
            alert(data.message);
        }else{

            var htmlStr = "";
            for(var sd in data.data){
                htmlStr += "<tr><td>"+data.data[sd]+"</td></tr>";
            }

            $("#noPackSendModalTbody").html(htmlStr);
            $("#noPackSendModal").modal('show');
        }

    },'json');


    return false;
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