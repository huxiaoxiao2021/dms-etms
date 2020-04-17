$(function () {
    var queryUrl = '/feedback/listData';
    var exportUrl = '/inventoryTask/toExport';

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                // toolbar: '#toolbar', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped: false, // 是否显示行间隔色
                showColumns: false, // 是否显示所有的列
                // sortable: true, // 是否启用排序
                // sortOrder: "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh: false, // 是否显示刷新按钮
//                 minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: false, // 是否启用点击选中行
                showToggle: false, // 是否显示详细视图和列表视图的切换按钮
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
            temp.limit = params.limit;
            // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            return temp;
        };
        /**
         * 获取查询参数
         * @param _selector 选择器（默认为'.search-param'）
         */
        oTableInit.getSearchCondition = function (_selector) {
            var params = {};
            return params;
        };
        oTableInit.tableColums = [
            {
                field: 'viewDataJsonStr',
                title: '我的建议',
                align: 'left',
                formatter: function (value, row, index) {
                    if (typeof value == 'string') {
                        try {
                            var obj = JSON.parse(value);
                            console.log(value);
                            var html = [];
                            html.push('<div class="row" style="margin-left: 10px;margin-top: 5px">【');
                            html.push(obj.typeName)
                            html.push('】');
                            html.push(' 提交时间：');
                            if ($.trim(obj.createTime) != '') {
                                html.push($.dateHelper.formateDateTimeOfTs(obj.createTime))
                            }
                            if (obj.status == 1) {
                                html.push('<span class="feedBack replySpan" >');
                                html.push('已回复');
                                html.push('</span>')
                            } else if (obj.status == 0) {
                                html.push('<span class="feedBack noReplySpan">');
                                html.push('未回复');
                                html.push('</span>')
                            } else {
                                html.push("");
                            }
                            html.push('</label></div>');
                            // html.push('<div class="row" style="margin-left: 18px;margin-top: -7px;color:#989888">');
                            //
                            // html.push('  </div>' );

                            html.push('<div class="row" style="margin-left: 10px;margin-top: 5px">');
                            html.push('【问题描述】');
                            html.push(obj.content);
                            html.push('</div>');
                            html.push(' <div class="row" style="margin-left: 5px;margin-top: 10px">');
                            if (obj.attachmentList != undefined && obj.attachmentList != null) {
                                for (let i = 0; i < obj.attachmentList.length; i++) {
                                    var pic = obj.attachmentList[i];
                                    html.push('  <div class="col-xs-2 departImg" style="max-height: 100px;max-width: 100px">')
                                    html.push(' <a href="' + pic + '" target="_blank">')
                                    html.push('  <label class="control-label"><img width="100%" src="' + pic + '"></label></a>')
                                    html.push(' </div>');
                                }
                            }
                            html.push('</div>');
                            if (obj.replys != undefined && obj.replys != null) {
                                for (let i = 0; i < obj.replys.length; i++) {
                                    let reply = obj.replys[i];
                                    html.push('  <div class="row" style="margin-left: 10px;margin-top: 5px;background: #E6E6E6">');
                                    html.push('  <div class="row" style="margin-left: 10px;margin-top: 5px;background: #E6E6E6">');
                                    html.push('  【产品回复】');
                                    html.push('  </div>');
                                    html.push('    <div class="row" style="margin-left: 17px;margin-top: 5px;background: #E6E6E6">');
                                    html.push(reply.content);
                                    html.push('  </div>');
                                    html.push('   <div class="row" style="margin-left: 3px;margin-top: 10px">');
                                    if (reply.imgs != null && reply.imgs != undefined) {
                                        for (let j = 0; j <reply.imgs.length ; j++) {
                                            let img = reply.imgs[i];
                                            html.push('   <div class="col-xs-2 departImg" style="max-height: 100px;max-width: 100px">');
                                            html.push('  <a href="'+img+'" target="_blank">');
                                            html.push('  <label class="control-label"><img width="100%" src="'+img+'"></label></a>');
                                            html.push(' </div>');
                                        }
                                    }
                                    html.push(' </div>');
                                    html.push(' </div>');

                                }

                            }

                            return html.join("");
                            return value;
                        } catch (e) {

                        }
                    }


                }
            }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            //查询
            $('#btn_query').click(function () {
                tableInit().refresh();
            });

        };

        return oInit;
    };

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            var params = tableInit.getSearchCondition();
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


    tableInit().init();
    pageInit().init();
    $("#btn_add").click(function () {
        location.href = "/feedback/addView?t=" + new Date().getTime();
    })

});


function initDateQuery() {
    var v = $.dateHelper.formatDate(new Date());
    $("#createStartTime").val(v + " 00:00:00");
    $("#createEndTime").val(v + " 23:59:59");
    $("#completeStartTime").val(v + " 00:00:00");
    $("#completeEndTime").val(v + " 23:59:59");

}

function initSelect() {
    var defualt = $("#query-form #isExcessSelect").val();
    $("#query-form #isExcess").val(defualt);
    $("#query-form #isExcessSelect").on('change', function (e) {
        var v = $("#query-form #isExcessSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #isExcess").val(v);
        } else {
            $("#query-form #isExcess").val(null);
        }
    });
}

function findSite(selectId, siteListUrl, initIdSelectId) {
    $(selectId).html("");
    $.ajax({
        type: "get",
        url: siteListUrl,
        data: {},
        async: false,
        success: function (data) {
            var result = [];
            if (data[0].code = 200) {
                for (var i in data) {
                    if (data[i].siteCode && data[i].siteCode != "") {
                        result.push({id: data[i].siteCode, text: data[i].siteName});
                    }
                }
            }

            $(selectId).select2({
                placeholder: '请选择分拣中心',
                allowClear: true,
                data: result
            });

            $.combobox.clearAllSelected('site-select');
        }
    });
}

function findDirection(selectId, directionListUrl, initIdSelectId) {
    $(selectId).html("");
    $.ajax({
        type: "get",
        url: directionListUrl,
        data: {},
        async: false,
        success: function (data) {
            list = data.data;
            var result = [];
            if (data.code == 200 && list.length > 0) {
                for (var i in list) {
                    if (list[i].code && list[i].name != "") {
                        result.push({id: list[i].code, text: list[i].name});
                    }
                }
                $(selectId).select2({
                    placeholder: '请选择下游场地',
                    allowClear: true,
                    data: result
                });

            } else {
                result.push({id: -99, text: "无下游目的地列表"})
                $(selectId).select2({
                    placeholder: '无下游目的地列表',
                    allowClear: true,
                    data: result
                });

            }
            $.combobox.clearAllSelected('direction-select');
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

            $('#org-select').select2({
                placeholder: '请选择机构',
                allowClear: true,
                data: result
            });

            $("#org-select")
                .on("change", function (e) {
                    $("#query-form #createSiteCode").val("");
                    var orgId = $("#org-select").val();
                    $("#query-form #orgId").val(orgId);
                    if (orgId) {
                        var siteListUrl = '/services/bases/dms/' + orgId;
                        findSite("#site-select", siteListUrl, "#query-form #createSiteCode");
                    }

                });

            $("#site-select").on("change", function (e) {
                var createSiteCode = $("#site-select").val();
                $("#query-form #createSiteCode").val(createSiteCode);

                if (createSiteCode) {
                    var directionListUrl = '/services/inventory/getDirectionList/' + createSiteCode;
                    findDirection("#direction-select", directionListUrl, "#query-form #directionCode");
                }
            });
            $("#direction-select").on("change", function (e) {
                var directionCode = $("#direction-select").val();
                if (directionCode != -99) {
                    $("#query-form #directionCode").val(directionCode);
                }
            });
            $.combobox.clearAllSelected('org-select');
            $.combobox.clearAllSelected('site-select');
            $.combobox.clearAllSelected('direction-select');
        }
    });

}
