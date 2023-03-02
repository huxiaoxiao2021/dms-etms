var uploadExcelUrl = '/abnormal/abnormalWaybillDiff/uploadExcel';
$(function () {

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {

        };
        oTableInit.getSearchParams = function (params) {

        };
        oTableInit.getSearchCondition = function(_selector) {

        };
        oTableInit.tableColums = [];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        oInit.init = function() {
            $('#dataEditDiv').hide();

            //取消
            $('#btn_return').click(function() {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
                // 重置状态
                resetStatus();
            });

            //导入
            $('#btn_import').click(function(){
                $('#importExcelFile').val(null);
                $('#import_modal').modal('show');
            });
        };
        return oInit;
    };

    initImportExcel();
});

//上传按钮
function initImportExcel(){
    $('#btn_upload').on('click',function(e){
        $('#btn_upload').attr("disabled",true);
        var form = document.getElementById('upload_excel_form'),
            formData = new FormData(form);
        $.ajax({
            url:uploadExcelUrl,
            type:"post",
            data:formData,
            processData:false,
            contentType:false,
            success:function(res){
                if(res && res.code == 200){
                    $.msg.ok("上传成功!");
                    $('#import_modal').modal('hide');
                    $('#dataTable').bootstrapTable('refresh');
                }else{
                    $.msg.error("上传失败!" + res.message);
                }
                $('#btn_upload').attr("disabled",false);
            },
            error:function(err){
                $.msg.error("网络连接失败,稍后重试");
                $('#btn_upload').attr("disabled",false);
            }
        });
    });
}

// 提交
function convert2param(){
    var params = {};
    params["id"] = $('#id').val();
    params["menuCode"] = $('#menuCode-EG').val();
    params["menuName"] = $('#menuCode-EG option:selected').text();
    params["dimensionCode"] = $('#dimensionCode-EG').val();
    params["dimensionName"] = $('#dimensionCode-EG option:selected').text();
    params["orgId"] = $('#orgId-EG').val();
    params["orgName"] = $('#orgId-EG option:selected').text();
    params["siteCode"] = $('#siteCode-EG').val();
    params["siteName"] = $('#siteCode-EG option:selected').text();
    params["operateErp"] = $('#operateErp-EG').val();
    params["yn"] = $('#yn-EG option:selected').val();
    return params;
};