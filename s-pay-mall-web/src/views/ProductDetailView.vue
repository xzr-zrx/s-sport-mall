<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductGridSkeleton from '@/components/ProductGridSkeleton.vue'
import { productApi } from '@/api'
import { useCartStore } from '@/stores/cart'
import type { Product } from '@/types'
import {
  formatPrice,
  getCategoryLabel,
  productStockClass,
  productStockText
} from '@/composables/useProductPresentation'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref<Product | null>(null)
const relatedProducts = ref<Product[]>([])
const quantity = ref(1)
const loading = ref(true)
const failed = ref(false)
const adding = ref(false)

const maxQuantity = computed(() => Math.max(Math.min(product.value?.stock ?? 1, 99), 1))
const canBuy = computed(() => Boolean(product.value && product.value.status === 'ON_SALE' && product.value.stock > 0))

async function loadProduct(): Promise<void> {
  loading.value = true
  failed.value = false
  product.value = null
  quantity.value = 1
  try {
    const detail = await productApi.detail(Number(route.params.id))
    product.value = detail.data.data
    const related = await productApi.list({
      category: product.value.category,
      page: 1,
      pageSize: 5,
      sort: 'DEFAULT'
    })
    relatedProducts.value = related.data.data.records.filter((item) => item.id !== product.value?.id).slice(0, 4)
  } catch {
    failed.value = true
  } finally {
    loading.value = false
  }
}

async function addToCart(): Promise<void> {
  if (!product.value || !canBuy.value) return
  if (!localStorage.getItem('token')) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  adding.value = true
  try {
    await cartStore.addProduct(product.value.id, quantity.value)
    ElMessage.success(`已将 ${quantity.value} 件商品加入购物车`)
  } finally {
    adding.value = false
  }
}

async function buyNow(): Promise<void> {
  if (!product.value || !canBuy.value) return
  if (!localStorage.getItem('token')) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await router.push({
    name: 'checkout',
    query: { source: 'BUY_NOW', productId: String(product.value.id), quantity: String(quantity.value) }
  })
}

function normalizeQuantity(): void {
  const value = Number(quantity.value)
  if (!Number.isFinite(value) || value < 1) quantity.value = 1
  if (value > maxQuantity.value) quantity.value = maxQuantity.value
}

watch(() => route.params.id, loadProduct)
onMounted(loadProduct)
</script>

<template>
  <div class="product-detail-page page-shell page-section">
    <el-breadcrumb class="breadcrumb" separator="/">
      <el-breadcrumb-item :to="{ name: 'home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ name: 'products' }">全部商品</el-breadcrumb-item>
      <el-breadcrumb-item v-if="product">{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="loading" class="detail-skeleton">
      <el-skeleton animated>
        <template #template>
          <div class="detail-skeleton__grid">
            <el-skeleton-item variant="image" class="detail-skeleton__image" />
            <div class="detail-skeleton__content">
              <el-skeleton-item variant="h1" style="width: 74%" />
              <el-skeleton-item variant="text" style="width: 100%" />
              <el-skeleton-item variant="text" style="width: 86%" />
              <el-skeleton-item variant="h1" style="width: 36%; margin-top: 30px" />
              <el-skeleton-item variant="button" style="width: 100%; height: 48px; margin-top: 36px" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="failed || !product" class="state-panel state-panel--empty">
      <span class="state-panel__icon">📦</span>
      <strong>商品不存在或暂时无法访问</strong>
      <p>商品可能已经下架，也可能是网络连接出现问题。</p>
      <el-button type="primary" @click="router.push({ name: 'products' })">返回商品列表</el-button>
    </div>

    <template v-else>
      <section class="product-detail-card">
        <div class="product-detail-card__media">
          <AppImage :src="product.coverUrl" :alt="product.name" :lazy="false" />
          <span class="product-detail-card__badge">{{ getCategoryLabel(product.category) }}</span>
        </div>
        <div class="product-detail-card__content">
          <span class="section-kicker">{{ getCategoryLabel(product.category) }}</span>
          <h1>{{ product.name }}</h1>
          <p class="product-detail-card__description">{{ product.description }}</p>
          <div class="product-detail-card__price"><small>¥</small>{{ formatPrice(product.price) }}</div>
          <div class="product-detail-card__benefits">
            <span>✓ 正品保障</span><span>✓ 安全支付</span><span>✓ 库存实时校验</span>
          </div>
          <div class="product-detail-card__stock">
            <span>库存状态</span>
            <strong :class="productStockClass(product.stock)">{{ productStockText(product.stock) }}</strong>
            <small v-if="product.stock > 0">可售 {{ product.stock }} 件</small>
          </div>
          <div class="product-detail-card__purchase">
            <div class="quantity-field">
              <label for="product-quantity">购买数量</label>
              <el-input-number
                id="product-quantity"
                v-model="quantity"
                :min="1"
                :max="maxQuantity"
                :disabled="!canBuy"
                @blur="normalizeQuantity"
              />
            </div>
            <div class="product-detail-card__actions">
              <el-button size="large" :loading="adding" :disabled="!canBuy" @click="addToCart">加入购物车</el-button>
              <el-button type="primary" size="large" :disabled="!canBuy" @click="buyNow">立即购买</el-button>
            </div>
          </div>
          <p v-if="!canBuy" class="product-detail-card__sold-out">该商品当前不可购买，请浏览其他运动装备。</p>
        </div>
      </section>

      <section class="detail-info-panel">
        <div><span>01</span><h3>严选商品</h3><p>精选运动装备，品质透明可追溯，让每次购买都值得信赖。</p></div>
        <div><span>02</span><h3>库存保护</h3><p>下单时实时校验库存，确保订单可执行，减少等待烦恼。</p></div>
        <div><span>03</span><h3>可靠支付</h3><p>多重支付状态校验，订单状态全程可追溯，购物更安心。</p></div>
      </section>

      <section v-if="relatedProducts.length" class="section related-section">
        <div class="section-heading">
          <div><span class="section-kicker">YOU MAY ALSO LIKE</span><h2>同类装备推荐</h2></div>
        </div>
        <div class="product-grid">
          <ProductCard v-for="item in relatedProducts" :key="item.id" :product="item" />
        </div>
      </section>
      <ProductGridSkeleton v-else-if="loading" :count="4" />
    </template>
  </div>
</template>
