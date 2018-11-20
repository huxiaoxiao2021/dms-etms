$(function () {
    var queryUrl = '/goodsPrint/listData';
    /*blockUI*/
    var lockPage = function () {
        $.blockUI({
            message: "<span class='pl20 icon-loading'>加载中-请您稍候</span>",
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: '#fff',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity: .5,
                color: '#000'
            }
        });
    };
    var unLockPage = function () {
        $.unblockUI();
    };
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                pagination: false, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 50000, // 每页的记录行数（*）
                cache: true, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "client", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                // pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                columns: oTableInit.tableColums
            });
        };
        oTableInit.tableColums = [ {
            field: 'sendCode',
            title: '发货批次号'
        }, {
            field: 'operateTime',
            title: '发货日期',
            formatter: function (value, row, index) {
               return $.dateHelper.formateDateOfTs(value);
            }
        }, {
            field: 'createSiteName',
            title: '始发网点'
        }, {
            field: 'receiveSiteName',
            title: '目的网点'
        }, {
            field: 'boxCode',
            title: '箱号'
        }, {
            field: 'vendorId',
            title: '订单号'
        }, {
            field: 'waybillCode',
            title: '运单号'
        }, {
            field: 'consignWare',
            title: '托寄物品名'
        }];
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            $('#dataTable').hide();
            $('#sendCode').val(null);
            $('#btn_query').click(function () {
                var params={};
                var v_sendCode=$('#sendCode').val();
                if(v_sendCode){
                    params.sendCode=v_sendCode;
                }else{
                    Jd.alert("请输入要查询的批次号");
                    return;
                }
                lockPage();
                $.ajaxHelper.doPostAsync(queryUrl, JSON.stringify(params), function (res) {

                    if (res&&res.code=='200') {
                        $('#dataTable').bootstrapTable("load",res.data);
                        $('#dataTable').show();
                    }else{
                        Jd.alert(res.message);
                    }
                    unLockPage();
                })
            });
            $('#btn_clear').click(function () {
                $('#sendCode').val(null);
                $('#dataTable').bootstrapTable("load",[]);
            });
            $('#btn_print').click(function () {
                window.print();
            });
            $("#btn_export").click(function () {

                var v_sendCode=$("#sendCode").val();
                if (!v_sendCode){
                    Jd.alert("无可导出内容");
                    return;
                }
                var url = "/goodsPrint/toExport";
                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action": url});

                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": "sendCode"});
                input.val(v_sendCode);
                form.append(input);
                form.appendTo(document.body);
                form.submit();
                document.body.removeChild(form[0]);
            });
        };
        return oInit;
    }
    tableInit().init();
    pageInit().init();
});