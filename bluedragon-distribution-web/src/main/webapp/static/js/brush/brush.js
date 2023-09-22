var importBrushUrl = '/brush/toImportBrush';
$(function () {

    // 刷数导入
    $('#btn_import_brush').click(function(){
        $('#importExcelFile_brush').val(null);
        $('#import_modal_brush').modal('show');
    });

    initImportBrushExcel();
});
function initImportBrushExcel(){
    //上传按钮
    $('#btn_upload_brush').on('click',function(e){
        $('#btn_upload_brush').attr("disabled",true);
        var inputValue = $('#importExcelFile_brush').val().trim();
        var index1 = inputValue.lastIndexOf(".");
        var index2 = inputValue.length;
        var suffixName = inputValue.substring(index1+1,index2);
        if(inputValue == ''){
            Jd.alert('请先浏览文件在上传!');
            $('#btn_upload_brush').attr("disabled",false);
            return;
        }
        if(suffixName != 'xls'){
            Jd.alert('请上传指定Excel文件!');
            $('#btn_upload').attr("disabled",false);
            return;
        }
        var form = document.getElementById('brush_upload_excel_form'),
            formData = new FormData(form);
        $.ajax({
            url:importBrushUrl,
            type:"post",
            data:formData,
            processData:false,
            contentType:false,
            success:function(res){
                if(res && res.code == 200){
                    Jd.alert("上传成功!");
                }else{
                    Jd.alert("上传失败!" + res.message);
                }
                $('#btn_upload_brush').attr("disabled",false);
            },
            error:function(err){
                Jd.alert("网络连接失败,稍后重试");
                $('#btn_upload_brush').attr("disabled",false);
            }
        });
    });
}
