<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { cartApi, orderApi, productApi } from '@/api'
import { formatPrice } from '@/composables/useProductPresentation'
import { useCartStore } from '@/stores/cart'
import type { CheckoutDisplayItem, CreateOrderPayload, OrderSource } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const loading = ref(true)
const failed = ref(false)
const submitting = ref(false)
const items = ref<CheckoutDisplayItem[]>([])
const formRef = ref<FormInstance>()
const source = computed<OrderSource>(() => route.query.source === 'BUY_NOW' ? 'BUY_NOW' : 'CART')

const savedDelivery = (() => {
  try { return JSON.parse(localStorage.getItem('checkoutDelivery') || '{}') as Record<string, string> } catch { return {} }
})()
const form = reactive({
  recipientName: savedDelivery.recipientName || '',
  recipientPhone: savedDelivery.recipientPhone || '',
  shippingAddress: savedDelivery.shippingAddress || ''
})
const rules: FormRules = {
  recipientName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  recipientPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^[0-9+\- ]{6,24}$/, message: '联系电话格式不正确', trigger: 'blur' }
  ],
  shippingAddress: [
    { required: true, message: '请输入详细收货地址', trigger: 'blur' },
    { min: 5, message: '收货地址至少 5 个字符', trigger: 'blur' }
  ]
}

const total = computed(() => items.value.reduce((sum, item) => sum + Number(item.unitPrice) * item.quantity, 0))
const invalidItems = computed(() => items.value.filter((item) => item.stock < item.quantity || item.stock <= 0))

async function load(): Promise<void> {
  loading.value = true
  failed.value = false
  try {
    if (source.value === 'BUY_NOW') {
      const productId = Number(route.query.productId)
      const quantity = Math.max(1, Math.min(Number(route.query.quantity || 1), 99))
      if (!Number.isInteger(productId) || productId <= 0) throw new Error('商品参数无效')
      const product = (await productApi.detail(productId)).data.data
      items.value = [{
        key: `product-${product.id}`,
        productId: product.id,
        productName: product.name,
        productCover: product.coverUrl,
        unitPrice: product.price,
        quantity,
        stock: product.stock
      }]
    } else {
      const ids = String(route.query.cartItemIds || '').split(',').map(Number).filter((id) => Number.isInteger(id) && id > 0)
      if (!ids.length) throw new Error('没有选择购物车商品')
      const cartItems = (await cartApi.list()).data.data
      const idSet = new Set(ids)
      items.value = cartItems.filter((item) => idSet.has(item.id)).map((item) => ({
        key: `cart-${item.id}`,
        productId: item.productId,
        productName: item.productName,
        productCover: item.productCover,
        unitPrice: item.unitPrice,
        quantity: item.quantity,
        stock: item.stock
      }))
      if (items.value.length !== idSet.size) throw new Error('部分购物车商品已失效，请返回购物车重试')
    }
  } catch (error) {
    failed.value = true
    ElMessage.error(error instanceof Error ? error.message : '结算信息加载失败')
  } finally {
    loading.value = false
  }
}

async function submit(): Promise<void> {
  if (!formRef.value || !(await formRef.value.validate().catch(() => false))) return
  if (!items.value.length || invalidItems.value.length) {
    ElMessage.warning('存在库存不足或失效商品，请返回检查')
    return
  }
  submitting.value = true
  try {
    localStorage.setItem('checkoutDelivery', JSON.stringify(form))
    const payload: CreateOrderPayload = {
      source: source.value,
      recipientName: form.recipientName.trim(),
      recipientPhone: form.recipientPhone.trim(),
      shippingAddress: form.shippingAddress.trim()
    }
    if (source.value === 'CART') {
      payload.cartItemIds = String(route.query.cartItemIds).split(',').map(Number)
    } else {
      payload.items = items.value.map((item) => ({ productId: item.productId, quantity: item.quantity }))
    }
    const order = (await orderApi.create(payload)).data.data
    await cartStore.load().catch(() => undefined)
    ElMessage.success('订单创建成功，请在 30 分钟内完成支付')
    await router.replace({ name: 'payment', params: { orderNo: order.orderNo } })
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="container checkout-page">
    <div class="flow-steps"><span class="is-done">1 选择商品</span><span class="is-active">2 确认订单</span><span>3 完成支付</span></div>
    <div class="page-heading-row"><div><span class="section-kicker">CHECKOUT</span><h1 class="page-title">确认订单</h1><p>提交订单时将以最新价格与库存为准，请确认商品信息无误。</p></div></div>
    <el-skeleton v-if="loading" :rows="8" animated />
    <div v-else-if="failed" class="state-panel"><strong>结算信息无法加载</strong><p>商品可能已经失效，建议返回重新选择。</p><el-button type="primary" @click="$router.back()">返回上一页</el-button></div>
    <div v-else class="checkout-layout">
      <div class="checkout-main">
        <section class="panel-card">
          <div class="panel-card__header"><h2>收货信息</h2><span>用于本次体育用品配送</span></div>
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="delivery-form">
            <div class="delivery-form__row">
              <el-form-item label="收货人" prop="recipientName"><el-input v-model="form.recipientName" maxlength="64" placeholder="请输入姓名" /></el-form-item>
              <el-form-item label="联系电话" prop="recipientPhone"><el-input v-model="form.recipientPhone" maxlength="24" placeholder="手机号或联系电话" /></el-form-item>
            </div>
            <el-form-item label="详细收货地址" prop="shippingAddress"><el-input v-model="form.shippingAddress" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="省/市/区 + 街道、门牌号" /></el-form-item>
          </el-form>
        </section>
        <section class="panel-card">
          <div class="panel-card__header"><h2>商品清单</h2><span>{{ source === 'BUY_NOW' ? '立即购买' : '购物车结算' }}</span></div>
          <article v-for="item in items" :key="item.key" class="checkout-item">
            <AppImage :src="item.productCover" :alt="item.productName" />
            <div class="checkout-item__info"><h3>{{ item.productName }}</h3><p>库存 {{ item.stock }} 件</p><span v-if="item.stock < item.quantity" class="danger-text">库存不足</span></div>
            <div class="checkout-item__quantity">× {{ item.quantity }}</div>
            <strong>¥{{ formatPrice(Number(item.unitPrice) * item.quantity) }}</strong>
          </article>
        </section>
      </div>
      <aside class="checkout-summary panel-card">
        <h2>订单汇总</h2>
        <div><span>商品件数</span><b>{{ items.reduce((sum, item) => sum + item.quantity, 0) }} 件</b></div>
        <div><span>商品金额</span><b>¥{{ formatPrice(total) }}</b></div>
        <div><span>配送费用</span><b class="success-text">免运费</b></div>
        <div class="checkout-summary__total"><span>应付金额</span><strong>¥{{ formatPrice(total) }}</strong></div>
        <el-button type="primary" size="large" :loading="submitting" :disabled="!items.length || !!invalidItems.length" @click="submit">提交订单并去支付</el-button>
        <button class="plain-link" type="button" @click="$router.back()">返回修改商品</button>
        <p>提交订单即表示你确认商品、数量和收货信息无误。</p>
      </aside>
    </div>
  </div>
</template>
