<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const auth = useAuthStore()
const cart = useCartStore()
const route = useRoute()
const router = useRouter()
const mobileMenuOpen = ref(false)

async function bootstrap(): Promise<void> {
  try {
    await auth.loadUser()
  } catch {
    auth.logout()
  }
  if (auth.loggedIn) await cart.load().catch(() => undefined)
}

function logout(): void {
  auth.logout()
  cart.reset()
  mobileMenuOpen.value = false
  router.push({ name: 'home' })
}

watch(() => route.fullPath, () => {
  mobileMenuOpen.value = false
})

watch(() => auth.loggedIn, async (loggedIn) => {
  if (loggedIn) await cart.load().catch(() => undefined)
  else cart.reset()
})

onMounted(bootstrap)
</script>

<template>
  <div class="site-layout">
    <div class="announcement-bar">
      <div class="page-shell announcement-bar__inner">
        <span>为热爱出发 · 综合体育用品商城</span>
        <span class="announcement-bar__right">正品严选 · 安全支付 · 订单状态可靠保障</span>
      </div>
    </div>

    <header class="site-header">
      <div class="page-shell site-header__inner">
        <router-link class="brand" :to="{ name: 'home' }" aria-label="S-SPORT 商城首页">
          <span class="brand__mark">S</span>
          <span class="brand__text"><strong>S-SPORT</strong><small>运动装备商城</small></span>
        </router-link>

        <nav class="desktop-nav" aria-label="主导航">
          <router-link :to="{ name: 'home' }">首页</router-link>
          <router-link
            :class="{ 'is-active': route.name === 'products' && !route.query.category }"
            active-class=""
            exact-active-class=""
            :to="{ name: 'products' }"
          >全部商品</router-link>
          <router-link
            :class="{ 'is-active': route.name === 'products' && route.query.category === 'RUNNING' }"
            active-class=""
            exact-active-class=""
            :to="{ name: 'products', query: { category: 'RUNNING' } }"
          >跑步装备</router-link>
          <router-link
            :class="{ 'is-active': route.name === 'products' && ['BASKETBALL', 'FOOTBALL', 'BADMINTON'].includes(String(route.query.category || '')) }"
            active-class=""
            exact-active-class=""
            :to="{ name: 'products', query: { category: 'BASKETBALL' } }"
          >球类运动</router-link>
          <router-link v-if="auth.loggedIn" :to="{ name: 'orders' }">我的订单</router-link>
        </nav>

        <div class="header-actions">
          <router-link v-if="auth.loggedIn" class="cart-link" :to="{ name: 'cart' }" aria-label="购物车">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 4h2l2.1 10.1a2 2 0 0 0 2 1.6h7.8a2 2 0 0 0 2-1.6L20.3 8H6.1M9.5 20a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm7 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"/></svg>
            <span>购物车</span>
            <b v-if="cart.count">{{ cart.count > 99 ? '99+' : cart.count }}</b>
          </router-link>

          <div v-if="auth.loggedIn" class="account-actions">
            <span class="account-avatar">{{ (auth.user?.nickname || auth.user?.username || 'U').slice(0, 1).toUpperCase() }}</span>
            <span class="account-name">{{ auth.user?.nickname || auth.user?.username }}</span>
            <button type="button" @click="logout">退出</button>
          </div>
          <div v-else class="guest-actions">
            <router-link :to="{ name: 'login', query: { redirect: route.fullPath } }">登录</router-link>
            <router-link class="guest-actions__register" :to="{ name: 'register' }">注册</router-link>
          </div>

          <button
            class="mobile-menu-button"
            type="button"
            aria-label="打开导航菜单"
            :aria-expanded="mobileMenuOpen"
            @click="mobileMenuOpen = !mobileMenuOpen"
          >
            <span></span><span></span><span></span>
          </button>
        </div>
      </div>

      <Transition name="menu-slide">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <nav class="page-shell">
            <router-link :to="{ name: 'home' }">首页</router-link>
            <router-link :to="{ name: 'products' }">全部商品</router-link>
            <router-link :to="{ name: 'products', query: { category: 'RUNNING' } }">跑步装备</router-link>
            <router-link :to="{ name: 'products', query: { category: 'BASKETBALL' } }">篮球装备</router-link>
            <router-link :to="{ name: 'products', query: { category: 'FITNESS' } }">健身训练</router-link>
            <router-link v-if="auth.loggedIn" :to="{ name: 'cart' }">购物车（{{ cart.count }}）</router-link>
            <router-link v-if="auth.loggedIn" :to="{ name: 'orders' }">我的订单</router-link>
            <template v-if="!auth.loggedIn">
              <router-link :to="{ name: 'login', query: { redirect: route.fullPath } }">登录</router-link>
              <router-link :to="{ name: 'register' }">注册</router-link>
            </template>
            <button v-else type="button" @click="logout">退出登录</button>
          </nav>
        </div>
      </Transition>
    </header>

    <main class="site-main">
      <router-view />
    </main>

    <footer class="site-footer">
      <div class="page-shell site-footer__grid">
        <div class="site-footer__brand">
          <div class="brand brand--footer">
            <span class="brand__mark">S</span>
            <span class="brand__text"><strong>S-SPORT</strong><small>运动装备商城</small></span>
          </div>
          <p>S-SPORT 专注于跑步、球类、健身与户外运动装备，为运动爱好者提供实用、可靠的体育用品。</p>
        </div>
        <div><h3>商城服务</h3><router-link :to="{ name: 'products' }">全部商品</router-link><router-link :to="{ name: 'products', query: { category: 'RUNNING' } }">运动分类</router-link><router-link :to="{ name: 'cart' }">购物车</router-link><router-link :to="{ name: 'orders' }">我的订单</router-link></div>
        <div><h3>服务保障</h3><span>正品保障</span><span>安全支付</span><span>快速发货</span><span>售后服务</span></div>
        <div><h3>用户帮助</h3><span>购物指南</span><span>支付说明</span><span>订单查询</span><span>联系客服</span></div>
      </div>
      <div class="page-shell site-footer__bottom">© 2026 S-SPORT 体育用品商城<br>商品图片及信息仅用于商城展示</div>
    </footer>
  </div>
</template>
