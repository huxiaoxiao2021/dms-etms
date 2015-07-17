/*
 * jqModal - Minimalist Modaling with jQuery
 * (http://dev.iceburg.net/jquery/jqModal/)
 *
 * Copyright (c) 2007,2008 Brice Burgess <bhb@iceburg.net>
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 * 
 * $Version: 03/01/2009 +r14
 */
 /**
 * @fileoverview 基于jQuery的模态框插件 
 * @author Brice Burgess
 * @version 03/01/2009 +r14
 * @example
 * $("#demo").jqm();
 */
/*global jQuery:true */
(function($) {
$.fn.jqm=function(o){
var p={
overlay: 20,
overlayClass: 'whiteOverlay',
closeClass: 'jqmClose',
trigger: '.jqModal',
ajax: F,
ajaxText: '',
target: F,
modal: true,
toTop: F,
onShow: F,
onHide: F,
onLoad: F
};
return this.each(function(){if(this._jqm)return H[this._jqm].c=$.extend({},H[this._jqm].c,o);s++;this._jqm=s;
H[s]={c:$.extend(p,$.jqm.params,o),a:F,w:$(this).addClass('jqmID'+s),s:s};
if(p.trigger)$(this).jqmAddTrigger(p.trigger);
});};

$.fn.jqmAddClose=function(e){return hs(this,e,'jqmHide');};
$.fn.jqmAddTrigger=function(e){return hs(this,e,'jqmShow');};
$.fn.jqmShow=function(t){return this.each(function(){
	t=t||window.event;
	/*自适应模态框*/
	$(this).css({'margin-left':-$(this).width()/2,'width':$(this).width(),'left':'50%'});
	if(!$(this).find('.move').length)
	$(this).jqDrag('.tl,.modalFooter').find('.tl,.modalFooter').addClass('move');
	$.jqm.open(this._jqm,t);});};
$.fn.jqmHide=function(t){return this.each(function(){t=t||window.event;$.jqm.close(this._jqm,t)});};

$.jqm = {
hash:{},
open:function(s,t){var h=H[s],c=h.c,cc='.'+c.closeClass,z=(parseInt(h.w.css('z-index'))),z=(z>0)?z:3000,o=$('<div></div>').css({height:'100%',width:'100%',position:'fixed',left:0,top:0,'z-index':z-1,opacity:c.overlay/100});if(h.a)return F;h.t=t;h.a=true;h.w.css('z-index',z);
 if(c.modal) {if(!A[0])L('bind');A.push(s);}
 else if(c.overlay >0){ h.w.jqmAddClose(o);/*背景不添加关闭触发器dai2011.5.4,注释此语句或设置参数modal:true;*/}
 else o=F;

 h.o=(o)?o.addClass(c.overlayClass).prependTo('body'):F;
 if(ie6){$('html,body').css({height:'100%',width:'100%'});if(o){o=o.css({position:'absolute'})[0];for(var y in {Top:1,Left:1})o.style.setExpression(y.toLowerCase(),"(_=(document.documentElement.scroll"+y+" || document.body.scroll"+y+"))+'px'");}}

 if(c.ajax) {var r=c.target||h.w,u=c.ajax,r=(typeof r == 'string')?$(r,h.w):$(r),u=(u.substr(0,1) == '@')?$(t).attr(u.substring(1)):u;
  r.html(c.ajaxText).load(u,function(){if(c.onLoad)c.onLoad.call(this,h);if(cc)h.w.jqmAddClose($(cc,h.w));e(h);});}
 else if(cc)h.w.jqmAddClose($(cc,h.w));

 if(c.toTop&&h.o)h.w.before('<span id="jqmP'+h.w[0]._jqm+'"></span>').insertAfter(h.o);	
 (c.onShow)?c.onShow(h):h.w.show();e(h);
 return F;
},
close:function(s){var h=H[s];if(!h.a)return F;h.a=F;
 if(A[0]){A.pop();if(!A[0])L('unbind');}
 if(h.c.toTop&&h.o)$('#jqmP'+h.w[0]._jqm).after(h.w).remove();
 if(h.c.onHide)h.c.onHide(h);else{h.w.hide();if(h.o)h.o.remove();} return F;
},
params:{}};
var s=0,H=$.jqm.hash,A=[],ie6=$.browser.msie&&($.browser.version == "6.0"),F=false,
i=$('<iframe src="javascript:false;document.write(\'\');" class="jqm"></iframe>').css({opacity:0}),
e=function(h){if(ie6)if(h.o)h.o.html('<p style="width:100%;height:100%"/>').prepend(i);else if(!$('iframe.jqm',h.w)[0])h.w.prepend(i); f(h);},
f=function(h){try{$(':input:visible',h.w)[0].focus();}catch(_){}},
L=function(t){$()[t]("keypress",m)[t]("keydown",m)[t]("mousedown",m);},
m=function(e){var h=H[A[A.length-1]],r=(!$(e.target).parents('.jqmID'+h.s)[0]);if(r)f(h);return !r;},
hs=function(w,t,c){return w.each(function(){var s=this._jqm;$(t).each(function() {
 if(!this[c]){this[c]=[];$(this).click(function(){for(var i in {jqmShow:1,jqmHide:1})for(var s in this[i])if(H[this[i][s]])H[this[i][s]].w[i](this);return F;});}this[c].push(s);});});};
})(jQuery);

if (typeof (jqm) == "undefined") {
    jqm = new Object();
}else{
	}
jqm.confirm = function(o){
		
		var op={
			w:370,
			self:null,
			title:'确认',
			content:'内容',
			type:'alert',//警示alert ,提示attention,公告notice,疑问question
			onConfirm:null,
			overlay: 20,
			overlayClass: 'whiteOverlay',
			closeClass: 'jqmClose'/*,
			trigger: '.jqModal',
			ajax: F,
			ajaxText: '',
			target: F,
			modal: F,
			toTop: F,
			onShow: F,
			onHide: F,
			onLoad: F*/
		};
		op.w = o.w?o.w:op.w;
		op.self = o.self?o.self:op.self;
		op.title = o.title?o.title:op.title;
		op.content = o.content?o.content:op.content;
		op.type = o.type?o.type:op.type;
		op.onConfirm = o.onConfirm?o.onConfirm:op.onConfirm;
		op.overlay = o.overlay?o.overlay:op.overlay;
		op.overlayClass = o.overlayClass?o.overlayClass:op.overlayClass;
		op.jqmClose = o.jqmClose?o.closeClass:op.closeClass;
		var jqmOp={
			overlay: op.overlay,
			overlayClass: op.overlayClass,
			closeClass: op.closeClass/*,
			trigger: op.trigger,
			ajax: op.ajax,
			ajaxText: op.ajaxText,
			target: op.target,
			modal: op.modal,
			toTop: op.toTop,
			onShow: op.onShow,
			onHide: op.onHide,
			onLoad: op.onLoad	*/
		};
		if($('#jqmConfirm').length){
			$('#jqmConfirm').remove();	
		}
		var jqmConfirm = '<div id="jqmConfirm" style="width:'+op.w+'px;"class="modal"><h1 class="tl"><span class="tr"><span class="tit">'
						+op.title+'</span><span class="modalClose jqmClose">关闭</span></span></h1><div class="moadalCon"><div class="clearfix fakeMsg '
						+op.type+'"><i class="ico"></i><div class="conText">'+op.content+'</div></div></div><div class="modalFooter pb20"><input type="submit" class="btn_c mr25" id="jqmConfirmBtn" value="确定" name=""><input type="submit" class="btn_c '+jqmOp.closeClass+'" value="取消" id="" name=""></div><div class="bl"><div class="br"></div></div></div>';
		$('body').append(jqmConfirm);
		$('#jqmConfirm').jqm(jqmOp);
		$('#jqmConfirm').jqmShow();
		$('#jqmConfirmBtn').click(function(){
			if(op.onConfirm){
				//op.onConfirm();
				$('#jqmConfirm').jqmHide().remove();
				op.onConfirm.call(op.self);
			}else{
				return;	
			}
			
			});
			
}
jqm.alert= function(o){
		
		var op={
			w:200,
			self:null,
			title:'确认',
			content:'内容',
			type:'success',
			onConfirm:null,
			overlay: 20,
			overlayClass: 'whiteOverlay',
			closeClass: 'jqmClose',
			onHide: function(){return true;}/*,
			trigger: '.jqModal',
			ajax: F,
			ajaxText: '',
			target: F,
			modal: F,
			toTop: F,
			onShow: F,
			onHide: F,
			onLoad: F*/
		};
		op.w = o.w?o.w:op.w;
		op.self = o.self?o.self:op.self;
		op.title = o.title?o.title:op.title;
		op.content = o.content?o.content:op.content;
		op.type = o.type?o.type:op.type;
		op.onConfirm = o.onConfirm?o.onConfirm:op.onConfirm;
		op.overlay = o.overlay?o.overlay:op.overlay;
		op.overlayClass = o.overlayClass?o.overlayClass:op.overlayClass;
		op.jqmClose = o.jqmClose?o.closeClass:op.closeClass;
		op.onHide = o.onHide?o.onHide:op.onHide;
		var jqmOp={
			overlay: op.overlay,
			overlayClass: op.overlayClass,
			closeClass: op.closeClass/*,
			onHide: op.onHide,
			trigger: op.trigger,
			ajax: op.ajax,
			ajaxText: op.ajaxText,
			target: op.target,
			modal: op.modal,
			toTop: op.toTop,
			onShow: op.onShow,
			onHide: op.onHide,
			onLoad: op.onLoad	*/
		};
		if($('#jqmConfirm').length){
			$('#jqmConfirm').remove();	
		}
		var jqmAlert = '<div id="jqmAlert" style="width:'+op.w+'px;"class="modal"><h1 class="tl"><span class="tr"><span class="tit">'
						+op.title+'</span><span class="modalClose '+jqmOp.closeClass+'">关闭</span></span></h1><div class="moadalCon"><div class="clearfix fakeMsg '
						+op.type+'"><i class="ico"></i><div class="conText">'+op.content+'</div></div></div><div class="modalFooter pb20"><input type="submit" id="jqmAlertBtn" class="btn_c '+jqmOp.closeClass+'" value="关&emsp;闭" id="" name=""></div><div class="bl"><div class="br"></div></div></div>';
		$('body').append(jqmAlert);
		$('#jqmAlert').jqm(jqmOp);
		$('#jqmAlert').jqmShow();
		$('#jqmAlert .'+jqmOp.closeClass+'').click(function(){
			op.onHide();
			$('#jqmAlert').jqmHide().remove();
		});
}

jqm.loading=function(o){
		var op={
			content:'',
			size:32,//等待图标大小，有三种大小：16px,24px,32px
			overlay: 10,
			overlayClass: 'whiteOverlay'
			/*
			trigger: '.jqModal',
			ajax: F,
			ajaxText: '',
			target: F,
			modal: F,
			toTop: F,
			onShow: F,
			onHide: F,
			onLoad: F*/
		};
			op.content = o.content?o.content:op.content;
			op.size = o.size?o.size:op.size;
			op.overlay = o.overlay?o.overlay:op.overlay;
			op.overlayClass = o.overlayClass?o.overlayClass:op.overlayClass;
		var jqmOp={
			overlay: op.overlay,
			overlayClass: op.overlayClass,
			closeClass: op.closeClass/*,
			trigger: op.trigger,
			ajax: op.ajax,
			ajaxText: op.ajaxText,
			target: op.target,
			modal: op.modal,
			toTop: op.toTop,
			onShow: op.onShow,
			onHide: op.onHide,
			onLoad: op.onLoad	*/
		};
		if($('#jqmLoading').length){
			$('#jqmLoading').remove();	
		}
		var jqmLoading = '<div id="jqmLoading" class="jqmLoading hide"><span class="loading loading_'+op.size+'"></span><span style="line-height:'+op.size+'px;" class="ml10 font1">'+op.content+'</span></div>';
		$('body').append(jqmLoading);
		$('#jqmLoading').jqm(jqmOp).jqDrag('.jqDrag');;
		$('#jqmLoading').jqmShow();				

}