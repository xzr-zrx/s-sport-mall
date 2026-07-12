<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { useCartStore } from '@/stores/cart'
import type { Product } from '@/types'
import {
  formatPrice,
  getCategoryLabel,
  productStockClass,
  productStockText
} from '@/composables/useProductPresentation'

const props = defineProps<{ product: Product }>()
const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const adding = ref(false)

function openDetail(): void {
  router.push({ name: 'product-detail', params: { id: props.product.id } })
}

async function addToCart(): Promise<void> {
  if (!localStorage.getItem('token')) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  adding.value = true
  try {
    await cartStore.addProduct(props.product.id, 1)
    ElMessage.success('已加入购物车')
  } finally {
    adding.value = false
  }
}

async function buyNow(): Promise<void> {
  if (props.product.stock <= 0) return
  if (!localStorage.getItem('token')) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await router.push({
    name: 'checkout',
    query: { source: 'BUY_NOW', productId: String(props.product.id), quantity: '1' }
  })
}
</script>

<template>
  <article class="product-card" tabindex="0" @click="openDetail" @keyup.enter="openDetail">
    <div class="product-card__media">
      <AppImage :src="product.coverUrl" :alt="product.name" />
      <span class="product-card__category">{{ getCategoryLabel(product.category) }}</span>
    </div>
    <div class="product-card__body">
      <h3 class="product-card__title">{{ product.name }}</h3>
      <p class="product-card__description">{{ product.description || '精选运动装备，为日常训练带来可靠体验。' }}</p>
      <div class="product-card__meta">
        <strong class="product-card__price"><small>¥</small>{{ formatPrice(product.price) }}</strong>
        <span class="stock-pill" :class="productStockClass(product.stock)">{{ productStockText(product.stock) }}</span>
      </div>
      <div class="product-card__actions">
        <el-button
          class="product-card__cart"
          :loading="adding"
          :disabled="product.stock <= 0"
          @click.stop="addToCart"
        >
          加入购物车
        </el-button>
        <el-button
          class="product-card__buy"
          type="primary"
          :disabled="product.stock <= 0"
          @click.stop="buyNow"
        >
          立即购买
        </el-button>
      </div>
    </div>
  </article>
</template>
