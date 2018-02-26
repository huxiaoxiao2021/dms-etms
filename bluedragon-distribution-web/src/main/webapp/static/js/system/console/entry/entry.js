define(function (require) {
    /*加载各组件*/
    require('bootstrap');
    require('metismenu');
    var Vue = require('vue2');
    var ApiRouter = require('ApiRouter');

    $(function () {

        /********************************************************************************************/
        /*界面尺寸初始化*/
        /********************************************************************************************/
        var height = $(window).height();
        $('.container').height(height);


        $(window).resize(function() {
            var height = $(window).height();
            $('.container').height(height);
        });

        $('#automatic-main-iframe').prop('src','/static/pages/system/empty.html');
        /********************************************************************************************/
        /*二级菜单app*/
        /********************************************************************************************/
        var secondLevelMenuApp = new Vue({
            el: '#automatic-left-nav',
            data: {
                values:{}
            },
            methods:{
                secondLevelMenuOnClick:function(event){
                    var secondMenu = event.currentTarget;
                    var thirdMenu = $(secondMenu).siblings();
                    var isHidden = thirdMenu.is(":hidden");
                    if(isHidden == true) {
                        thirdMenu.show(100);
                        $("icon", $(secondMenu)).prop('class','glyphicon glyphicon-menu-down');
                    } else {
                        thirdMenu.hide(80);
                        $("icon", $(secondMenu)).prop('class','glyphicon glyphicon-menu-left');
                    }
                },
                thirdLevelMenuOnClick:function(menu){
                    var url = menu.url;
                    var random = Math.random();
                    url = url + '?noCache=' + random;
                    $('#automatic-main-iframe').prop('src',url);
                }
            }
        });

        /********************************************************************************************/
        /*一级菜单app*/
        /********************************************************************************************/
        var firstLevelMenuApp = new Vue({
            el: '#automatic-first-level-menus-zone',
            data: {
                values:{}
            },
            methods:{
                loadSecondMenus:function(menu)
                {
                    var menuId = menu.id;

                    ApiRouter.involker.involkGetAsync(ApiRouter.secondLevelMenusUrl,{menuId:menuId},
                        function (res){
                            secondLevelMenuApp.values = res;
                        }
                    );
                }
            }
        });

        /********************************************************************************************/
        /*初始化一级菜单*/
        /********************************************************************************************/
        ApiRouter.involker.involkGetAsync(ApiRouter.firstLevelMenusUrl,null,
            function (res){
                firstLevelMenuApp.values = res;
            }
        );

    });

});