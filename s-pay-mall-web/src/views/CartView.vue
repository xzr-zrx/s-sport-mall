<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { cartApi } from '@/api'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/composables/useProductPresentation'
import type { CartItem } from '@/types'

const cartStore = useCartStore()
const selected = ref<number[]>([])
const updatingIds = ref<Set<number>>(new Set())
const router = useRouter()
const items = computed(() => cartStore.items)
const availableItems = computed(() => items.value.filter((item) => item.stock > 0))
const allSelected = computed(() => availableItems.value.length > 0 && availableItems.value.every((item) => selected.value.includes(item.id)))
const selectedItems = computed(() => items.value.filter((item) => selected.value.includes(item.id)))
const selectedCount = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))
const total = computed(() => selectedItems.value.reduce((sum, item) => sum + Number(item.unitPrice) * item.quantity, 0))
const hasInvalidSelection = computed(() => selectedItems.value.some((item) => item.stock < item.quantity || item.stock <= 0))

async function load(): Promise<void> {
  await cartStore.load()
  selected.value = availableItems.value.map((item) => item.id)
}

function toggle(id: number, value: unknown): void {
  const checked = Boolean(value)
  const index = selected.value.indexOf(id)
  if (checked && index < 0) selected.value.push(id)
  if (!checked && index >= 0) selected.value.splice(index, 1)
}

function toggleAll(value: unknown): void {
  selected.value = Boolean(value) ? availableItems.value.map((item) => item.id) : []
}

async function update(item: CartItem): Promise<void> {
  if (item.quantity > item.stock) {
    item.quantity = Math.max(item.stock, 1)
    ElMessage.warning('购买数量不能超过当前库存')
  }
  updatingIds.value.add(item.id)
  try {
    await cartApi.update(item.id, item.quantity)
    await cartStore.load()
    ElMessage.success('数量已更新')
  } finally {
    updatingIds.value.delete(item.id)
  }
}

async function remove(id: number): Promise<void> {
  await ElMessageBox.confirm('确定从购物车删除这件商品吗？', '删除商品', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '保留' })
  await cartApi.remove(id)
  selected.value = selected.value.filter((itemId) => itemId !== id)
  await cartStore.load()
  ElMessage.success('商品已删除')
}

async function checkout(): Promise<void> {
  if (!selected.value.length) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  if (hasInvalidSelection.value) {
    ElMessage.warning('选中商品存在库存不足，请先调整数量')
    return
  }
  await router.push({ name: 'checkout', query: { source: 'CART', cartItemIds: selected.value.join(',') } })
}

onMounted(load)
</script>

<template>
  <div class="container cart-page">
    <div class="page-heading-row"><div><span class="section-kicker">SHOPPING CART</span><h1 class="page-title">购物车</h1><p>确认数量与库存后，选择商品进入订单确认。</p></div><router-link class="text-link" :to="{ name: 'products' }">继续选购 →</router-link></div>
    <el-skeleton v-if="cartStore.loading" :rows="6" animated />
    <el-empty v-else-if="!items.length" description="购物车还是空的">
      <router-link :to="{ name: 'products' }"><el-button type="primary">去逛体育装备</el-button></router-link>
    </el-empty>
    <template v-else>
      <section class="cart-list panel-card">
        <div class="cart-list__head"><el-checkbox :model-value="allSelected" @change="toggleAll">全选</el-checkbox><span>商品信息</span><span>单价</span><span>数量</span><span>小计</span><span>操作</span></div>
        <article v-for="item in items" :key="item.id" :class="['cart-row', { 'is-invalid': item.stock <= 0 || item.quantity > item.stock }]">
          <el-checkbox :model-value="selected.includes(item.id)" :disabled="item.stock <= 0" @change="toggle(item.id, $event)" />
          <div class="cart-product"><AppImage :src="item.productCover" :alt="item.productName" /><div><router-link :to="{ name: 'product-detail', params: { id: item.productId } }">{{ item.productName }}</router-link><p v-if="item.stock > 0">库存 {{ item.stock }} 件</p><p v-else class="danger-text">商品已售罄</p></div></div>
          <strong>¥{{ formatPrice(item.unitPrice) }}</strong>
          <el-input-number v-model="item.quantity" :min="1" :max="Math.max(Math.min(item.stock, 99), 1)" size="small" :disabled="item.stock <= 0 || updatingIds.has(item.id)" @change="update(item)" />
          <strong class="price">¥{{ formatPrice(Number(item.unitPrice) * item.quantity) }}</strong>
          <el-button text type="danger" @click="remove(item.id)">删除</el-button>
        </article>
      </section>
      <div class="cart-settlement panel-card"><div><el-checkbox :model-value="allSelected" @change="toggleAll">全选</el-checkbox><span>已选 <b>{{ selectedCount }}</b> 件商品</span></div><div class="cart-settlement__amount"><span>合计</span><strong>¥{{ formatPrice(total) }}</strong><small>不含配送优惠</small></div><el-button type="primary" size="large" :disabled="!selected.length || hasInvalidSelection" @click="checkout">去结算</el-button></div>
    </template>
  </div>
</template>
