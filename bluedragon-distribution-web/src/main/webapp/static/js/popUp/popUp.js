/**
 * Created by wuzuxiang on 2016/12/12.
 */
/**
 * 弹出浮动图层
 *
 * @param id
 *            图层id
 * @param width
 *            宽度
 * @param height
 *            高度
 */
function popUp(id, width, height) {
    var popup = $('#' + id); // 浮动图层
    popup.fadeIn(200).css({
        'width' : width,
        'height' : height
    });
    popup.prepend('<div class="close"></div>'); // 将关闭按钮图层加入到弹出图层里面
    var close = popup.parent().find(".close"); // 关闭按钮图层
    /*
     * 移动关闭按钮图层的位置
     */
    close.css({
        "position" : "relative",
        "left" : "-8px",
        "top" : "8px"
    });
    close.append("<img src='../static/images/close_pop.png' class='btn_close'/>");// 关闭图片加入到关闭按按钮图层
    close.find(".btn_close").css({
        "border" : "none"
    });
    /*
     * 弹出图层居中显示
     */
    var popMargTop = (popup.height() + 80) / 2;
    var popMargLeft = (popup.width() + 80) / 2;
    popup.css({
        'margin-top' : -popMargTop,
        'margin-left' : -popMargLeft
    });
    $('body').append('<div id="fade"></div>');
    var fade = $('#fade');
    fade.css({
        "background-color" : "#343843"

    }).fadeIn(200);

    close.click(function() {
        $('#fade , .popup_block').fadeOut(200, function() {
            fade.remove();
            close.remove();
        });
    });

    /*
     * 弹出层禁止鼠标右键
     */
    popup.bind("contextmenu", function() {
        return false;
    });
    fade.bind("contextmenu", function() {
        return false;
    });

    return close;
}
/**
 * 关闭弹出图层(仅限于非引用iframe的页面使用)
 *
 * @param id
 */
function popClose(id) {
    var popup = $('#' + id); // 浮动图层
    var close = popup.parent().find(".close"); // 关闭按钮图层
    close.click();
}

/**
 * 父框架弹出浮动图层
 *
 * @param id
 *            图层id
 * @param width
 *            宽度
 * @param height
 *            高度
 */
function popUpPage(id, width, height) {
    var object = $(window.parent.document).find("body"); // index.html页面
    var old = $("#" + id); // 当前页的浮动图层

    object.append(old.clone()); // 将弹出的对话框HTML标签加入到index.html页面

    var popup = object.find('#' + id); // 浮动图层
    popup.fadeIn(200).css({
        'width' : width,
        'height' : height
    });
    popup.prepend("<div class='close'></div>"); // 将关闭按钮图层加入到弹出图层里面
    var close = popup.find(".close"); // 关闭按钮图层
    /*
     * 移动关闭按钮图层的位置
     */
    close.css({
        "position" : "relative",
        "left" : "-8px",
        "top" : "8px"
    });
    close.append("<img src='../static/images/close_pop.png' class='btn_close' />"); // 关闭图片加入到关闭按按钮图层
    close.find(".btn_close").css({
        "border" : "none"
    });
    /*
     * 弹出图层居中显示
     */
    var popMargTop = (popup.height() + 80) / 2;
    var popMargLeft = (popup.width() + 80) / 2;
    popup.css({
        'margin-top' : -popMargTop,
        'margin-left' : -popMargLeft
    });
    /*
     * 深色的背景图层
     */
    object.append('<div id="fade"></div>');
    var fade = object.find("#fade");
    fade.css({
        "background-color" : "#343843"
    }).fadeIn(200);
    /*
     * 点击关闭图片就关闭弹出图层
     */
    close.click(function() {
        /*
         * 弹出层消失后，会出现原来禁止右键菜单的地方又能用了。为了避免这种情况，需要重新禁止鼠标右键
         */
        $(document).bind("contextmenu", function() {
            return false;
        });
        object.find('#fade , .popup_block').fadeOut(200, function() {
            fade.remove();
            close.remove();
        });
        popup.remove();
        /*
         * 弹出图层消失之后，index.html页面的焦点依旧落在触发事件的html页面上。
         * 如果不这样做，浮动图层消失之后，触发页面的快捷键全都不好用，必须用鼠标点击网页，获得焦点以后才能使用键盘快捷键
         */
        object.find("#mainFrame").focus();
    });

    /*
     * 禁止鼠标右键
     */
    popup.bind("contextmenu", function() {
        return false;
    });
    fade.bind("contextmenu", function() {
        return false;
    });
}
function closePopUp(id) {
    var object = $(window.parent.document).find("body"); // index.html页面
    var popup = object.find("#" + id);
    var close = popup.find(".close");
    close.click();
}
