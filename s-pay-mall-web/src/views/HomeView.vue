<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import CategoryCard from '@/components/CategoryCard.vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductGridSkeleton from '@/components/ProductGridSkeleton.vue'
import { productApi } from '@/api'
import { SPORT_CATEGORIES } from '@/constants/categories'
import type { Product } from '@/types'

const router = useRouter()
const featuredProducts = ref<Product[]>([])
const newestProducts = ref<Product[]>([])
const loading = ref(true)
const failed = ref(false)

const assurances = [
  { icon: '✓', title: '正品保障', text: '严选运动装备，品质透明可追溯' },
  { icon: '盾', title: '安全支付', text: '订单与支付状态全链路可靠保障' },
  { icon: '速', title: '快速发货', text: '现货商品及时处理，缩短等待时间' },
  { icon: '心', title: '售后服务', text: '清晰订单状态与贴心服务支持' }
]

async function loadProducts(): Promise<void> {
  loading.value = true
  failed.value = false
  try {
    const [featured, newest] = await Promise.all([
      productApi.list({ page: 1, pageSize: 8, sort: 'DEFAULT' }),
      productApi.list({ page: 1, pageSize: 4, sort: 'NEWEST' })
    ])
    featuredProducts.value = featured.data.data.records
    newestProducts.value = newest.data.data.records
  } catch {
    failed.value = true
  } finally {
    loading.value = false
  }
}

function goShopping(): void {
  router.push({ name: 'products' })
}

function scrollToFeatured(): void {
  document.querySelector('#featured-products')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(loadProducts)
</script>

<template>
  <div class="home-page">
    <section class="hero-section page-shell">
      <div class="hero-section__content">
        <span class="eyebrow">SPORTS EQUIPMENT STORE</span>
        <h1>为每一次热爱，<br><em>装备更好的自己</em></h1>
        <p>从跑步、球类到健身与户外，精选实用体育用品，让训练更专注，让突破更有底气。</p>
        <div class="hero-section__actions">
          <el-button type="primary" size="large" @click="goShopping">立即选购</el-button>
          <el-button size="large" @click="scrollToFeatured">查看热门商品</el-button>
        </div>
        <div class="hero-section__metrics">
          <div><strong>8</strong><span>运动分类</span></div>
          <div><strong>100%</strong><span>正品严选</span></div>
          <div><strong>30 min</strong><span>支付保护期</span></div>
        </div>
      </div>
      <div class="hero-section__visual" aria-hidden="true">
        <img src="/images/hero-sport.svg" alt="">
        <span class="hero-float hero-float--top">今日开练</span>
        <span class="hero-float hero-float--bottom">MOVE WITH PASSION</span>
      </div>
    </section>

    <section class="section page-shell">
      <div class="section-heading">
        <div>
          <span class="section-kicker">按运动探索</span>
          <h2>找到你的主场装备</h2>
        </div>
        <router-link class="text-link" :to="{ name: 'products' }">查看全部商品 <span>→</span></router-link>
      </div>
      <div class="category-grid">
        <CategoryCard v-for="category in SPORT_CATEGORIES.slice(0, 6)" :key="category.value" :category="category" />
      </div>
    </section>

    <section id="featured-products" class="section section--tinted">
      <div class="page-shell">
        <div class="section-heading">
          <div>
            <span class="section-kicker">人气精选</span>
            <h2>热门运动装备</h2>
            <p>从真实商品接口加载，为你的训练快速补齐装备。</p>
          </div>
          <router-link class="text-link" :to="{ name: 'products' }">更多好物 <span>→</span></router-link>
        </div>
        <ProductGridSkeleton v-if="loading" :count="8" />
        <div v-else-if="failed" class="state-panel">
          <strong>商品加载失败</strong>
          <p>网络连接异常，请稍后重试。</p>
          <el-button type="primary" @click="loadProducts">重新加载</el-button>
        </div>
        <el-empty v-else-if="!featuredProducts.length" description="暂时没有上架商品" />
        <div v-else class="product-grid">
          <ProductCard v-for="product in featuredProducts" :key="product.id" :product="product" />
        </div>
      </div>
    </section>

    <section class="section page-shell">
      <div class="section-heading">
        <div>
          <span class="section-kicker">NEW ARRIVALS</span>
          <h2>新品推荐</h2>
          <p>按商品创建时间展示最近上架的运动装备。</p>
        </div>
      </div>
      <ProductGridSkeleton v-if="loading" :count="4" />
      <div v-else-if="newestProducts.length" class="product-grid">
        <ProductCard v-for="product in newestProducts" :key="product.id" :product="product" />
      </div>
    </section>

    <section class="assurance-section">
      <div class="page-shell assurance-grid">
        <article v-for="item in assurances" :key="item.title" class="assurance-item">
          <span>{{ item.icon }}</span>
          <div><h3>{{ item.title }}</h3><p>{{ item.text }}</p></div>
        </article>
      </div>
    </section>
  </div>
</template>
