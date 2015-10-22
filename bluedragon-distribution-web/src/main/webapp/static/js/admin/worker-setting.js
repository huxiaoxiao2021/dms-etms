function Class_User(){
    this.name;/*实例变量*/
    this.id;
    this.version;
    this.change=function(){

    };

    var type;/*私有变量*/
}

/*静态变量*/
Class_User.Parent=2;
Class_User.ParentChange=function(){

};

/*prototype*/
Class_User.prototype.shareData=[];
Class_User.prototype.shareMethod=function(){
    Object.extend();

};

(function(){})();/*定义并执行*/

(function(window,undefined){
    var _jsQuery=(function(){
        var _jsQuery=function(selector,context){
            return new _jsQuery.prototype.init(selector,context);
        };
        _jsQuery.prototype.init=function(selector,context){
            return this;
        };
        return _jsQuery;

    })();
    window.JSQuery=_jsQuery;
})();