$(function() {
	var saveUrl = '/half/packageHalf/save';
	var deleteUrl = '/half/packageHalf/deleteByIds';
  var detailUrl = '/half/packageHalf/detail/';
  var queryUrl = '/half/packageHalf/listData';
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
		oTableInit.tableColums = [/* {
				checkbox : true
				}, */{
					field : 'waybillCode',
					title : '运单号'
				}, {
					field : 'halfType',
					title : '半收类型',
					formatter : function(value,row,index){
						return value==1?'包裹半收':value==2?'明细半收':'未知类型';
					}
				} , {
					field : 'operateSiteName',
					title : '操作机构'
				} , {
					field : 'createUser',
					title : '提交人'
				}, {
					field : 'createTime',
					title : '提交时间',
					formatter : function(value,row,index){
						return $.dateHelper.formateDateTimeOfTs(value);
					}
				}
			];
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
                elem: '#createTimeLE',
                theme: '#3f92ea',
                //btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#createTimeGE',
                theme: '#3f92ea',
                //btns: ['clear','now'],
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

	//绑定 页面按钮事件
	$("#add-query-btn").on('click',loadPackage);
    $("#add-delievered-btn").on('click',delievered);
    $("#add-submit-btn").on('click',addSubmit);
	$("#add-waybill-code").on('keydown',function(event){

		if (event.keyCode == "13") {

            loadPackage();
		}
	});

    // 检查是否URL上是否有运单号
    var urlParam = $.getQueryString("waybillCode");
    if(urlParam){
    	//存在直接跳转
        $('#dataTableDiv').hide();
        $('#dataEditDiv').show();
        $('#add-waybill-code').val(urlParam);
        loadPackage();
	}

});

/**
 * 加载包裹信息
 */
function loadPackage(){
    var waybillCode = $("#add-waybill-code").val().trim();
    if(waybillCode!=null && waybillCode != ""){
        //查询运单数据
        addWaybillCodeTemp = waybillCode;
        var getWaybillUrl = "/half/packageHalfDetail/getPackageStatus";
        $.post(getWaybillUrl,{waybillCode:addWaybillCodeTemp},function (data) {
            //组装页面
			if(data.code == 200){
				//包裹操作列表
                makeTableHtml(data.data);
				//提示语
                $("#load-message-p").html(data.message);
			}else{
				alert(data.message);
			}

        });



    }else{
        alert('运单号不能为空');
        $("#add-waybill-code").focus();
    }
}



function makeTableHtml(data){
	if(data==null || data.length == 0){
		alert("无包裹信息");
	}
    var myRowHtml = "";
    for(var i in data){
        var myRow = data[i];

        if( myRow.resultType == 1){
        	//妥投
            myRowHtml += "<tr class='success' id='tr-"+myRow.packageCode+"' package-ope-type='8'>";
            myRowHtml += "<td></td><td>"+myRow.waybillCode+"</td><td >"+myRow.packageCode+"</td><td >妥投</td><td ></td>";
        }else if ( myRow.resultType == 2){
        	//拒收
            myRowHtml += "<tr class='danger' id='tr-"+myRow.packageCode+"' package-ope-type='19'>";
            myRowHtml += "<td></td><td>"+myRow.waybillCode+"</td><td >"+myRow.packageCode+"</td><td>拒收</td>";
            if(myRow.reasonType && rejectReasonData[myRow.reasonType]){
                myRowHtml += "<td>"+rejectReasonData[myRow.reasonType]+"</td>";
            }else{
                myRowHtml += "<td></td>";
			}

        }else {
            myRowHtml += "<tr class='need-submit' id='tr-"+myRow.packageCode+"'>";
            myRowHtml+="<td><input type='checkbox' name='package-check' value='"+myRow.packageCode+"'/></td>";
            myRowHtml += "<td>"+myRow.waybillCode+"</td><td submit-value='"+myRow.packageCode+"'>"+myRow.packageCode+"</td><td id='"+myRow.packageCode+"-result'></td><td id='"+myRow.packageCode+"-reason'></td>";

        }
        myRowHtml += "</tr>";
    }
    $("#package-list-tbody").html(myRowHtml);

}

function getSelectTr(){
	return $("input[name='package-check']:checked");
}

/**
 * 妥投
 */
function delievered(){
	//变更页面状态
	var trs = getSelectTr();
	$(trs).each(function(){
		var packageCode = $(this).val();
		$("#"+packageCode+"-result").html(resultData[1]);
        $("#"+packageCode+"-result").attr("submit-value","1");
        $("#"+packageCode+"-reason").html("");
        $("#"+packageCode+"-reason").attr("submit-value","");
        $("#tr-"+packageCode).attr("package-ope-type","8");

    });
}

var resultData = {
	1:"妥投",
	2:"拒收"
}

var rejectReasonData = {
	1:"破损",
    2:"丢失",
	3:"报废",
	4:"客户原因",
	5:"其他"
}
/**
 * 拒收
 * @param type
 */
function rejectReason(type){
	var reasonName = rejectReasonData[type];
    //变更页面状态
    var trs = getSelectTr();
    $(trs).each(function(){
        var packageCode = $(this).val();
        $("#"+packageCode+"-result").html(resultData[2]);
        $("#"+packageCode+"-result").attr("submit-value","2");
        $("#"+packageCode+"-reason").html(reasonName);
        $("#"+packageCode+"-reason").attr("submit-value",type);
        $("#tr-"+packageCode).attr("package-ope-type","19");
    });
}


var addWaybillCodeTemp = "";
var submitUrl = "/half/packageHalf/save";

/**
 * 确认提交
 */
function addSubmit(){
    if($("#package-list-tbody input[type='checkbox']").length == 0){
        alert("无可操作包裹！");
        return false;
    }

	//校验是否所有包裹都有状态
    if(checkAllPackageHasResult()){
        disableBtn();
		//显示一下拒收的包裹数，方便现场人员确认
        //组装对象
        var param = {};
        var packageList = [];
        param["halfType"] = 1; //先默认包裹半收
		//拒收包裹数量
        param["rejectPackageCount"] = $("#package-list-tbody tr[package-ope-type='19']").length;

        $("#package-list-tbody .need-submit").each(function(){
            var packageVo = {};
            packageVo["packageCode"] = $(this).find("td:eq(2)").attr("submit-value");
            packageVo["resultType"] = $(this).find("td:eq(3)").attr("submit-value");
            packageVo["reasonType"] = $(this).find("td:eq(4)").attr("submit-value");
            packageList.push(packageVo);
        });
        param["packageList"] = packageList;
        param["waybillCode"] = addWaybillCodeTemp;
        param["operateTime"] = $.dateHelper.formatDateTime(new Date());
		//计算当前运单状态
		var waybillOpeType;
		var loopIndex = 1;
		$("#package-list-tbody tr").each(function(){
			if(loopIndex == 1){
                waybillOpeType = $(this).attr("package-ope-type");
			}else{
                waybillOpeType = $(this).attr("package-ope-type") == waybillOpeType ? waybillOpeType : "7100";
			}
			if(waybillOpeType == "7100"){
				return false;
			}
            loopIndex++;
		});
        param["waybillOpeType"] = waybillOpeType;

        $.ajaxHelper.doPostAsync(submitUrl,JSON.stringify(param),function(data){
        	if(data.code == 200 && data.data){
                alert('提交成功');
                //
				$("input[name='package-check']").remove();
			}else{
                alert(data.message?data.message:"提交失败");
			}
            enableBtn();
        });


	}else{
    	alert("所有包裹都有配送结果后才能提交！");
	}


}



/**
 * 检查所有包裹是否都有状态
 */
function checkAllPackageHasResult(){
	var checkResult = true;

    $("#package-list-tbody td[id $= 'result']").each(function(){
    	if($(this).html()=='' || $(this).html()==null){
            checkResult = false;
    		return false;
		}
	});
	return checkResult;

}


/**
 * 全选
 */
function allSelect(){

    $("input[name='package-check']").prop("checked",true)

}

/**
 * 反选
 */
function turnSelect(){
    $("input[name='package-check']").each(function(){
        $(this).prop("checked", !$(this).prop("checked"));
    });

}

function disableBtn(){

    $("#add-query-btn,#add-delievered-btn,#add-submit-btn,#add-reject-btn,#add-waybill-code").attr("disabled",true);

}

function enableBtn(){
    $("#add-query-btn,#add-delievered-btn,#add-submit-btn,#add-reject-btn,#add-waybill-code").attr("disabled",false);
}
