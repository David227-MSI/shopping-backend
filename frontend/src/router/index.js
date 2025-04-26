import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import CartPage from '@/pages/CartPage.vue'
import CheckoutPage from '@/pages/CheckoutPage.vue'
import OrderCompletePage from '@/pages/OrderCompletePage.vue'
import OrdersPage from '@/pages/OrdersPage.vue'

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/cart', name: 'cart', component: CartPage },
  { path: '/checkout', name: 'checkout', component: CheckoutPage }, // 填寫付款資料
  { path: '/order/complete', name: 'order-complete', component: OrderCompletePage }, // 完成付款後顯示訂單資訊
  { path: '/member/orders', name: 'orders', component: OrdersPage }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
