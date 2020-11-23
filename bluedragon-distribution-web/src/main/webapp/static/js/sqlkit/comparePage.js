function oneTableClick(tableName){
    $("#countTable tbody").html('');
    if(tableName){
        $("#tableName").val(tableName);
    }
    $.ajax({
        type : "post",
        url : '/dataCompare/compare',
        data :getFormData(),
        async : false,
        success : function (data) {
            $('#errorMsg').html('');
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
}

function main() {
    $("#compareCount").click(function(){
        $.ajax({
            type : "post",
            url : '/dataCompare/findTables',
            data :getFormData(),
            async : false,
            success : function (data) {
                $('#errorMsg').html('');
                $("#allTable").html('');
                $("#countTable tbody").html('');
                if(data.code == 200){
                    $.each(data.data,function(index,value){
                        $("#tableName").val(value);
                        var postData = getFormData();
                        $.ajax({
                            type : "post",
                            url : '/dataCompare/compareCount',
                            data :postData,
                            async : true,
                            success : function (data2) {
                                $('#errorMsg').html('');

                                if(data2.code == 200){
                                    $("#countTable tbody").append("<tr><td>"+data2.data+"</td></tr>")
                                }else{
                                    console.error(data2.message);
                                }

                            }});
                    });
                    $("#allTable").html(alltablesBtn);
                }else{
                    $('#errorMsg').html(data.message);
                }

            }});

    });

    $("#findAllTables").click(function (){
        $("#countTable tbody").html('');
        $.ajax({
            type : "post",
            url : '/dataCompare/findTables',
            data :getFormData(),
            async : false,
            success : function (data) {
                $('#errorMsg').html('');

                if(data.code == 200){
                    var alltablesBtn = "";
                    $.each(data.data,function(index,value){
                        alltablesBtn += "<button onclick=\"oneTableClick('"+value+"')\" class=\"btn_c\">"+value+"</button>";
                    });
                    $("#allTable").html(alltablesBtn);
                }else{
                    $('#errorMsg').html(data.message);
                }

            }});
    });

    $("#compare").click(function () {
        oneTableClick();
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