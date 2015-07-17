// JavaScript Document

//全局加载事件
$(function () {
	globalEvent.btnOver();
	globalEvent.eachEditor();
	globalEvent.gridRows();
	globalEvent.dateTimePicker();
	globalEvent.dateTimePicker2();
});

/*===============================================*/
/*加事件*/
var globalEvent = {
	/*自适应按钮效果*/
    btnOver: function () {
        $(".btn").live('mouseover',
        function () {
            $(this).addClass("btnOver");
        }).live('mouseout',
        function () {
            $(this).removeClass("btnOver");
        });
    },
    /*inupt输入框换背景*/
    eachEditor: function () {
        $("input:text,input:password,textarea").focus(function () {
            $(this).addClass("editBgFocus");
        }).blur(function () {
            $(this).removeClass("editBgFocus");
        });
    },
 /*日期时间选择器*/
    dateTimePicker: function () {
		if($(".dateSet").length>0||$(".timeSet").length>0||$(".YMSet").length>0||$(".dateTimeSet").length>0){
        /*日期*/
        $(".dateSet").dynDateTime();
        /*时间*/
        $(".timeSet").dynDateTime({
            showsTime: true,
            ifFormat: "%H:%M"
        });
        /*年月*/
        $(".YMSet").dynDateTime({
            ifFormat: "%Y-%m"
        });
        /*日期+时间*/
        $(".dateTimeSet").dynDateTime({
            showsTime: true,
            ifFormat: "%Y-%m-%d %H:%M"
        });}
    },
    dateTimePicker2: function(){
    	if($(".dateSet2").length>0){
    		$('.dateSet2').datePicker({clickInput:true,dateFormat:'yy-mm-dd'});
    	}
    },	
    gridRows:function(){
    	$('.grid').each(function(){
    		$('tbody tr:odd',this).addClass("trOdd");
    		$('tbody tr:even',this).addClass("trEven");
    		$('tbody tr',this).removeClass("trLast").last().addClass("trLast");
    	});
		$('.grid tbody tr').not('tr.thead')
		.mouseover(function(){
			$(this).addClass('trHover');
		})
		.mouseleave(function(){
			$(this).removeClass('trHover');
		});
	}
}