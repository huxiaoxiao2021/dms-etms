$(function() {
	var saveUrl = '/collect/collectGoodsDetail/save';
	var deleteUrl = '/collect/collectGoodsDetail/deleteByIds';
  var detailUrl = '/collect/collectGoodsDetail/detail/';
  var queryUrl = '/collect/collectGoodsDetail/listData';
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
            field : 'createSiteCode',
            title : '场地编码',
            visible: false
        },{
            field : 'createSiteName',
            title : '场地'
        }, {
            field : 'collectGoodsAreaCode',
            title : '集货区'
        }, {
            field : 'collectGoodsPlaceCode',
            title : '集货位'
        }, {
            field : 'collectGoodsPlaceType',
            title : '货位类型',
            formatter : function(value,row,index){
                return value=="1"?"小单":value=="2"?"中单":value=="3"?"大单":value=="4"?"异常":"未知类型";
            }
        }/*, {
            field : 'collectGoodsPlaceStatus',
            title : '状态',
            formatter : function(value,row,index){
                return value=="0"?"空闲":value=="1"?"非空闲":value=="2"?"已满":"未知状态";
            }
        }*/ , {
            field : 'waybillCode',
            title : '运单号'

        }, {
            field : 'packageCount',
            title : '总包裹数'

        }, {
            field : 'scanPackageCount',
            title : '实际包裹数',
            formatter : function(value,row,index){
                return '<a href="#" class="show-storage-view" onclick="showView(\''+row.createSiteCode+'\',\''+row.collectGoodsPlaceCode+'\',\''+row.waybillCode+'\',event)">'+value+'</a>';
            }
        }  , {
            field : 'createTime',
            title : '集货时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field : 'createUser',
            title : '集货人'
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

                var url = "/collect/collectGoodsDetail/toExport";
                var params = tableInit().getSearchCondition();

                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action":url});

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
    initOrg();
	tableInit().init();
	pageInit().init();
});

function showView(createSiteCode,placeCode,waybillCode,event){
    //获取明细
    var queryDUrl = '/collect/collectGoodsDetail/showViews';


    $("#collectGoodsDetailTbody").html("<tr><td colspan='5'>努力加载中...</td></tr>");

    $('#viewModal').modal("show");

    var scheduleTimeGteStr  = $("#scheduleTimeGteStr").val();
    var scheduleTimeLtStr  = $("#scheduleTimeLtStr").val();
	var param = {"collectGoodsPlaceCode":placeCode,"waybillCode":waybillCode,"createSiteCode":createSiteCode,
		"scheduleTimeGteStr":scheduleTimeGteStr,"scheduleTimeLtStr":scheduleTimeLtStr};


    $.ajaxHelper.doPostSync(queryDUrl,
		JSON.stringify(param),
			function(data){

        var tbodyHtml = "";
        if(data.code == 200){
            if(data.data.length == 0){
                tbodyHtml = "<tr><td colspan='5'>未获取到明细数据</td></tr>";
            }

            for(var i = 0 ; i<data.data.length ; i++ ){
                var pojo = data.data[i];
                tbodyHtml += "<tr><td>"+pojo.collectGoodsPlaceCode+"</td>";
                tbodyHtml += "<td>"+pojo.waybillCode+"</td>";
                tbodyHtml += "<td>"+pojo.packageCode+"</td>";
                tbodyHtml += "<td>"+pojo.createUser+"</td>";
                tbodyHtml += "<td>"+$.dateHelper.formateDateTimeOfTs(pojo.createTime)+"</td></tr>";

            }
        }else{
            tbodyHtml = "<tr><td colspan='6'>"+data.message+"</td></tr>";
        }

        $("#collectGoodsDetailTbody").html(tbodyHtml);



    });

    event.stopPropagation();

}

var initLogin = true;
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

            $('#site-group-select,#site-select').attr("disabled","disabled");
        }
    });

}