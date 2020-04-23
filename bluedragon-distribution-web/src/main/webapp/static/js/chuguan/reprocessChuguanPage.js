$(document).ready(function() {

    $("#doSubmmit").click(function() {
        var data = {orderIds:$('#orderIds').val()};
        $.ajax({
            type : "post",
            url : '/kuGuan/reprocessChuguan',
            data : data,
            async : false,
            success : function (data) {
                $('#message').html(data)
            }
        });
    });
});