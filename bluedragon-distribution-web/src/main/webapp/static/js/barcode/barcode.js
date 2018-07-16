$(function () {
    var queryUrl = '/barcode/listData';
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                width:200,
                columns: oTableInit.tableColums
            });
        };
        oTableInit.tableColums = [ {
            field: 'barcode',
            title: '69码'
        }, {
            field: 'skuId',
            title: 'SKU'
        }, {
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
                $.ajaxHelper.doPostSync(queryUrl, JSON.stringify(params), function (res) {
                    if (res) {
                        $('#dataTable').bootstrapTable("load",res);
                        $('#dataTable').show();
                    }
                })
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