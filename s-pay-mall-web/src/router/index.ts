import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  },
  routes: [
    { path: '/', name: 'home', component: HomeView, meta: { title: '首页' } },
    { path: '/products', name: 'products', component: () => import('@/views/ProductListView.vue'), meta: { title: '全部商品' } },
    { path: '/products/:id', name: 'product-detail', component: () => import('@/views/ProductDetailView.vue'), meta: { title: '商品详情' } },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { title: '登录' } },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue'), meta: { title: '注册' } },
    { path: '/cart', name: 'cart', component: () => import('@/views/CartView.vue'), meta: { auth: true, title: '购物车' } },
    { path: '/checkout', name: 'checkout', component: () => import('@/views/CheckoutView.vue'), meta: { auth: true, title: '确认订单' } },
    { path: '/orders', name: 'orders', component: () => import('@/views/OrdersView.vue'), meta: { auth: true, title: '我的订单' } },
    { path: '/orders/:orderNo', name: 'order-detail', component: () => import('@/views/OrderDetailView.vue'), meta: { auth: true, title: '订单详情' } },
    { path: '/payment/:orderNo', name: 'payment', component: () => import('@/views/PaymentView.vue'), meta: { auth: true, title: '订单支付' } },
    { path: '/payment-result', name: 'payment-result', component: () => import('@/views/PaymentResultView.vue'), meta: { auth: true, title: '支付结果' } },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue'), meta: { title: '页面不存在' } }
  ]
})

router.beforeEach((to) => {
  document.title = `${String(to.meta.title || '商城')} - S-SPORT` 
  if (to.meta.auth && !localStorage.getItem('token')) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
