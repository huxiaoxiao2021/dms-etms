function main() {




    $("#compare").click(function () {
        $.ajax({
            type : "post",
            url : '/dataCompare/compare',
            data :getFormData(),
            async : false,
            success : function (data) {
                $("#paperTable THEAD tr").html('');
                $("#paperTable TBODY").html('');
                if(data.code == 200){
                    var headTh = '<th>旧库or新库</th>>';
                    $.each(data.data.fields,function(index,value){
                        headTh += '<th>'+value+'</th>';
                    });
                    $("#paperTable THEAD tr").prepend(headTh);

                    var bodyTr = '';
                    $.each(data.data.mergeList,function(recordIndex,value){
                        var td = '';
                        var isNewDbRecord = (parseInt(recordIndex)+1) % 2 == 0;
                        if(isNewDbRecord){
                            td +='<td>新库</td>';
                        }else {
                            td +='<td>旧库</td>';
                        }
                        $.each(data.data.mergeList[recordIndex],function(fieldIndex,value){
                            if(isNewDbRecord){
                                var previousRecord = data.data.mergeList[recordIndex - 1];
                                if(previousRecord[fieldIndex] != value){
                                    td += '<td style="color: #ffffff;background-color: #b81900;">'+value+'</td>';
                                }else{
                                    td += '<td>'+value+'</td>';
                                }
                            }else{
                                td += '<td>'+value+'</td>';
                            }
                        });
                        bodyTr +='<tr>'+td+'</tr>';
                    });
                    $("#paperTable TBODY").prepend(bodyTr);
                }else{
                    $('#errorMsg').html(data.message)
                }

            }
        });
    });
}

function getFormData() {
    var d = {};
    var t = $('#dataForm').serializeArray();
    $.each(t, function() {
        d[this.name] = this.value;
    });
    return d;
}
$(document).ready(function(){
    main();
});