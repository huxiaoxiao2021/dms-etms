function main() {




    $("#compare").click(function () {
        $.ajax({
            type : "post",
            url : '/dataCompare/compare',
            data :getFormData(),
            async : false,
            success : function (data) {


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