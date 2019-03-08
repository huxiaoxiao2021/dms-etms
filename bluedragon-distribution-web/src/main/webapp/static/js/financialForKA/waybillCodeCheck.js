$(function() {

    //成功提示音地址
    var sucessBeep = "/static/music/success.wav";
    //失败提示音地址
    var failBeep= "/static/music/fail.wav";

    //条码相同提示语隐藏
    $("#successMessage").hide();

    //焦点聚集在输入框1
    $('#barCodeOfOne').focus();
    //输入框1回车事件后焦点聚集在输入框2
    $('#barCodeOfOne').keydown(function (event) {
        if(event.keyCode == 13){
            $("#successMessage").hide();
            $('#barCodeOfTwo').focus();
        };
    });

    //输入框2回车事件后校验输入的条码
    $('#barCodeOfTwo').keydown(function (event) {
        if(event.keyCode == 13){
            var params={};
            var barCodeOfOne=$('#barCodeOfOne').val();
            var barCodeOfTwo=$('#barCodeOfTwo').val();
            if(barCodeOfOne && barCodeOfTwo){
                params.barCodeOfOne=barCodeOfOne;
                params.barCodeOfTwo=barCodeOfTwo;
            }else{
                Jd.alert("请输入条码!");
                return;
            }
            var url = '/waybillCodeCheckForKA/check';
            $.ajaxHelper.doPostAsync(url, JSON.stringify(params), function (res) {
                if (res && res.code !=200) {
                    //条码不同，提示音错误警告
                    playSound(sucessBeep);
                    //弹框提示
                    var sss = confirm(res.message);
                    if(sss){
                        $('#barCodeOfOne').val("");
                        $('#barCodeOfTwo').val("");
                        $('#barCodeOfOne').focus();
                    }
                }else{
                    //条码相同提示语隐藏
                    $("#successMessage").show();
                    //条码相同，提示音成功
                    playSound(failBeep);
                    //光标聚集在输入框1并清空
                    $('#barCodeOfOne').val("");
                    $('#barCodeOfTwo').val("");
                    $('#barCodeOfOne').focus();
                }
            });
        };
    });

    //报警提示音
    function playSound(src) {
        var auto = $("#auto");
        auto.attr("src",src);
    }

});
