/**
 * @fileoverview 基于jQuery的表格批量操作，提供全选,不选功能
 * @author 360BUY UED Fengwp
 * @version 1.0
 * @example
 * $('#tableId').tableCheck(),用自定义被选中的样式selected
 */
/*global jQuery:true */
(function ($) {
    $.fn.extend({
        "tableCheck": function (options) {
            //设置默认值
            options = $.extend({
                selected: "selected",
				checkControl:".checkControl"
            }, options);

            //全选

            $("thead tr :checkbox,tr.thead :checkbox", this).click(function () {
                $(this).parents("table").find('tbody tr :checkbox,tfoot tr :checkbox,tr.tfoot :checkbox').not('::disabled').not('tr.thead :checkbox,tr.tfoot :checkbox')
				.attr("checked", this.checked).parents("tr")[this.checked ? "addClass" : "removeClass"](options.selected); //2011-5-31 fengweiping edited
            });
            $("tfoot tr :checkbox,tr.tfoot :checkbox", this).click(function () {
                $(this).parents("table").find('tbody tr :checkbox,thead tr :checkbox,tr.thead :checkbox').not('::disabled').not('tr.thead :checkbox,tr.tfoot :checkbox')
				.attr("checked", this.checked).parents("tr")[this.checked ? "addClass" : "removeClass"](options.selected); //2011-5-31 fengweiping edited
            });
            //单选
            $('tbody tr :checkbox', this).not('tr.thead :checkbox').click(function () {
                var hasSelected = $(this).parents("tr").hasClass(options.selected);
                //如果选中，则移出selected类，否则就加上selected类
                $(this).parents("tr")[hasSelected ? "removeClass" : "addClass"](options.selected);
                //定义一个临时变量，避免重复使用同一个选择器选择页面中的元素，提升程序效率
                var $tmp = $(this).parents("table").find('tbody tr :checkbox').not('::disabled').not('tr.thead :checkbox');
                //用filter方法筛选出选中的复选框。并直接给CheckedAll赋值
                $(this).parents("table").find('thead tr :checkbox,tr.thead :checkbox,tfoot tr :checkbox,tr.tfoot :checkbox')
				.attr('checked', $tmp.length == $tmp.filter(':checked').length);

            });

            // 如果复选框默认情况下是选择的，则高色
            $('tbody>tr:has(:checkbox:checked)', this).addClass(options.selected);

        }
    });
})(jQuery);




