$(function () {
    var queryUrl = '/barcode/listData';
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
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                cache: true, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "client", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                columns: oTableInit.tableColums
            });
        };
        oTableInit.tableColums = [ {
            field: 'skuId',
            title: 'SKU'
        },{
            field: 'barcode',
            title: '69码'
        },{
            field: 'productName',
            title: '商品名称'
        }];
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            $('#dataTable').hide();
            $('#barcode').val(null);
            $('#btn_query').click(function () {
                var params={};
                var v_barcode=$('#barcode').val();
                if(v_barcode){
                    params.barcode=v_barcode;
                }else{
                    Jd.alert("请输入要查询的商品编码");
                    return;
                }
                lockPage();
                $.ajaxHelper.doPostAsync(queryUrl, JSON.stringify(params), function (res) {
                    if (res) {
                        $('#dataTable').bootstrapTable("load",res);
                        $('#dataTable').show();
                        unLockPage();
                    }
                })
            });
            $('#btn_clear').click(function () {
                $('#barcode').val(null);
                $('#dataTable').bootstrapTable("load",[]);
            });
            $("#btn_export").click(function () {

                var v_barcode=$("#barcode").val();
                if (!v_barcode){
                    Jd.alert("无可导出内容");
                    return;
                }
                var url = "/barcode/toExport";
                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action": url});

                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": "barcode"});
                input.val(v_barcode);
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