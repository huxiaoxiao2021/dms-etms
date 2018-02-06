/**********************************************************/
/* requirejs 全局模块配置  @author:luyue5 */
/**********************************************************/
var context_path = "/static/";

var imports_path = context_path + '/imports/'

require.config({
	baseUrl: context_path ,
	//  baseUrl: './',
	paths: {
        /**********************************************************/
		/* 基本框架 */
        /**********************************************************/
		jquery: 'imports/jquery/v1.11.3/jquery.min',
		vue2: 'imports/vue/v2.0.3/vue.min',
		html5shiv: 'imports/html5shiv/html5shiv.min',
		respond: 'imports/html5shiv/respond.min',

        /**********************************************************/
		/* UI库 */
        /**********************************************************/
		easyui: 'imports/easyui/v1.4.5/jquery.easyui.min',
		bootstrap: 'imports/bootstrap/v3.3.7/js/bootstrap.min',
        /**********************************************************/
		/* 插件 */
        /**********************************************************/
        metismenu:'imports/plugin/metismenu/metisMenu.min',
		// toastr: 'lib/import/plugin/toastr/toastr.min',

        /**********************************************************/
		/* 入口  */
        /**********************************************************/
		IndexEntry: 'js/system/console/entry/entry',

        /**********************************************************/
		/* 模块  */
        /**********************************************************/

		/* 数据路由模块 */
		ApiRouter:'js/system/console/modules/common/ApiRouter',

	},
	map: {
		'*': {
			/* css less 模块化 */
			'css': 'imports/requirejs/component/css.min',
			'less': 'imports/requirejs/component/less/less'
		}
	},
	shim: {
		
		/**********************************************
		 *  业务组件依赖
		 */
		IndexEntry:{
			deps: [
                // 'less!assets/system/less/main.less',
                'css!assets/system/css/main.css'
			]
		},
		/**********************************************
		 *  显示组件依赖
		 */
		easyui: {
			deps: [
				'jquery',
				'css!imports/easyui/v1.4.5/easyui.css',
				'css!imports/easyui/v1.4.5/icon.css',
				'css!imports/easyui/v1.4.5/color.css'
			]

		},
		bootstrap: {
			deps: [
				'jquery',
				'html5shiv',
				'respond',
				'css!imports/bootstrap/v3.3.7/css/bootstrap.min.css',
				'css!imports/bootstrap/v3.3.7/css/bootstrap-theme.min.css',
			]
		},
        metismenu:{
            deps: [
                'jquery',
                'css!imports/plugin/metismenu/metisMenu.min.css'

            ]
		},
		toastr: {
			deps: [
				'css!lib/import/plugin/toastr/toastr.min.css'
			]
		}
	}
});