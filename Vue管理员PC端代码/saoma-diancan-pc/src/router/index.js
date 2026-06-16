import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
	{
		path: '/',
		name: 'login',
		component: resolve => require(['@/page/login/login'], resolve),
	},
	{
		path: '/index',
		name: 'index',
		component: resolve => require(['@/page/index/index'], resolve),
		redirect:'/analysis',
		children:[
			{
			path:'/analysis',
			name:'analysis',
			component:resolve => require(['@/page/analysis/analysis'],resolve)
			},
			{
			path:'/order',
			name:'order',
			component:resolve => require(['@/page/order/order'],resolve)
			},
			{
			path:'/dishes',
			name:'dishes',
			component:resolve => require(['@/page/dish-management/dishes-list'],resolve)
			},
			{
			path:'/table',
			name:'table',
			component:resolve => require(['@/page/table-number/table'],resolve)
			},
			{
			path:'/waiter',
			name:'waiter',
			component:resolve => require(['@/page/waiter/waiter-management'],resolve)
			},
			{
			path:'/upload',
			name:'upload',
			component:resolve => require(['@/page/dish-management/upload-dishes'],resolve)
			},
			{
			path:'/category',
			name:'category',
			component:resolve => require(['@/page/category/category'],resolve)
			},
			{
			path:'/set-up',
			name:'set-up',
			component:resolve => require(['@/page/set-up/set-up'],resolve)
			},
			{
			path:'/review',
			name:'review',
			component:resolve => require(['@/page/review/review-management'],resolve)
			}
		]
	},
	{
		path: '/waiter-index',
		name: 'waiter-index',
		component: resolve => require(['@/page/waiter/index'], resolve)
	},
	{
		path: '/waiter-table',
		name: 'waiter-table',
		component: resolve => require(['@/page/waiter/table-management'], resolve)
	},
	{
		path: '/waiter-order',
		name: 'waiter-order',
		component: resolve => require(['@/page/waiter/order-management'], resolve)
	},
	{
		path: '/waiter-service',
		name: 'waiter-service',
		component: resolve => require(['@/page/waiter/service-management'], resolve)
	},
	{
		path: '/waiter-dish',
		name: 'waiter-dish',
		component: resolve => require(['@/page/waiter/dish-management'], resolve)
	},
	{
		path: '/waiter-feedback',
		name: 'waiter-feedback',
		component: resolve => require(['@/page/waiter/feedback-management'], resolve)
	},
	{
		path: '/waiter-ordering',
		name: 'waiter-ordering',
		component: resolve => require(['@/page/waiter/waiter-ordering'], resolve)
	}
]

const router = new VueRouter({
	routes
})

export default router
