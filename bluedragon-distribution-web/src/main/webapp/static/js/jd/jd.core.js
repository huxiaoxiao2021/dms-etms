/**
 * core
 */
(function($) {
    $.fn.getPosition = function() {
        var left = 0,top = 0,obj = this[0];
        while (obj.offsetParent) {
            left += obj.offsetLeft;
            top += obj.offsetTop;
            obj = obj.offsetParent;
        }
        left += obj.offsetLeft;
        top += obj.offsetTop;
        return { x : left,y : top };
    }
})(jQuery)

(function($) {
    var opt = {
        observer:false,
        ob_freq:300,
        fix_position:false,
        pos:'e',
        //align:'center',
        valign:'middle',
        closeOnClick:false,
        padding:'5px'
    },A = [],W = window;

    $.fn.tip = function(o) {
        (o = $.extend({}, opt, o)) && this.data('ctx', o);
        _setTip.call(this);
        o.observer && A.push(this);
    }

    W.setInterval(function() {
        for (var i in A) {
            if (A[i].data('tip_clear')) {
                continue;
            } else if (A[i].css('display') == 'none'
                    || A[i].css('disable') == true
                    || A[i].css('enable') == false
                    || A[i].css('visible') == false) {
                A[i].clearTip();
            } else {
                var offset = A[i].getPosition();
                var size = {x:A[i].outerWidth(true),y:A[i].outerHeight(true)}
                if (offset.x < 1 || offset.y < 1 || size.x < 1 || size.y < 1) {
                    A[i].clearTip();
                } else {
                    var _offset = A[i].data('offset'),  _size = A[i].data('size');
                    if (offset.x != _offset.x || offset.y != _offset.y || size.x != _size.x || size.y != _size.y) {
                        A[i].data('offset', offset),A[i].data('size', size);
                        _resetPos.call(A[i], {x:size.x - _size.x,y:size.y - _size.y}, {x:offset.x - _offset.x,y:offset.y - _offset.y});
                    }
                }
            }
        }
    }, opt.ob_freq);

    function _setTip() {
        var size = {x:this.outerWidth(true),y:this.outerHeight(true)},offset = this.getPosition();
        this.data('size', size) && this.data('offset', offset);
        var c = this.data('dom'),z = false;
        if (!c) {
            c = $('<div></div>').css({position:'absolute',padding:opt.padding}).appendTo('body');
            this.data('dom', c);
            z = true;
        }
        c.html('').append(this.data('ctx').innerDom).data('obj',this);
        (z || !opt.fix_position) && _setPos.call(this, c);  //init tip position
        c.show();
        opt.closeOnClick && c.click(function(){this.data('obj').clearTip()});
    }

    function _setPos($obj) {
        var ctx = this.data('ctx'),offset = this.data('offset'), xo = $obj.outerWidth(true),
                yo = $obj.outerHeight(true),size = this.data('size'),_x = offset.x,_y = offset.y;
        if (typeof ctx.pos == 'object') {
            _y += parseInt(ctx.pos.y, 10),_x += parseInt(ctx.pos.x, 10);
        } else {
            _x += ctx.pos.indexOf('e') != -1 ? size.x : ctx.pos.indexOf('w') != -1 ? -xo : ctx.align == 'center' ? (size.x - xo) / 2 : ctx.align == 'right' ? (size.x - xo) : 0;
            _y += ctx.pos.indexOf('s') != -1 ? size.y : ctx.pos.indexOf('n') != -1 ? -yo : ctx.valign == 'middle' ? (size.y - yo) / 2 : ctx.align == 'bottom' ? (size.x - xo) : 0;
        }
        this.css({top : _y + 'px',left : _x + 'px'});
    }

    function _resetPos(size, offset) {
        var ctx = this.data('ctx'),_x = offset.x,_y = offset.y;
        if (typeof ctx.pos != 'object') {
            _x += ctx.pos.indexOf('e') != -1 ? size.x : ctx.pos.indexOf('w') ? 0 : ctx.align == 'right' ? size.x : ctx.align == 'center' ? size.x / 2 : 0;
            _y += ctx.pos.indexOf('s') != -1 ? size.y : ctx.pos.indexOf('n') ? 0 : ctx.align == 'bottom' ? size.y : ctx.align == 'middle' ? size.y / 2 : 0;
        }
        var dom = this.data('dom')[0];
        dom.style.top += _y,dom.style.left += _x;
    }

    $.fn.clearTip = function() {
        this.data('tip_clear', true) && this.data('dom').hide();
    }
})(jQuery)