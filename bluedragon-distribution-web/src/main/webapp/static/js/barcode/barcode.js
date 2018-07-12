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
        oTableInit.tableColums = [{
            checkbox: true
        }, {
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
            $('#btn_query').click(function () {
                var params={};
                $.ajaxHelper.doPostSync(queryUrl, JSON.stringify(params), function (res) {
                    if (res) {
                        $('#dataTable').bootstrapTable("load",res);
                        $('#dataTable').show();
                    }
                })
            });
            $("#btn_export").click(function () {

                var url = "/barcode/toExport";
                var params ={};
                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action": url});

                $.each(params, function (key, value) {

                    input = $("<input type='hidden' class='search-param'>");
                    input.attr({"name": key});
                    if (key == 'startTime' || key == 'endTime') {
                        input.val(new Date(value));
                    } else {
                        input.val(value);
                    }
                    form.append(input);
                });
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