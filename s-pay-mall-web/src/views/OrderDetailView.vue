<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { orderApi } from '@/api'
import { formatPrice } from '@/composables/useProductPresentation'
import { canCancel, canPay, formatDateTime, orderStatusClass, orderStatusLabel } from '@/composables/useOrderPresentation'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()
const order = ref<Order | null>(null)
const loading = ref(true)
const cancelling = ref(false)

async function load(): Promise<void> {
  loading.value = true
  try { order.value = (await orderApi.detail(String(route.params.orderNo))).data.data } finally { loading.value = false }
}

async function cancel(): Promise<void> {
  if (!order.value) return
  await ElMessageBox.confirm('取消后库存会恢复，确定取消该订单吗？', '取消订单', { type: 'warning' })
  cancelling.value = true
  try { await orderApi.cancel(order.value.orderNo); ElMessage.success('订单已取消'); await load() } finally { cancelling.value = false }
}

onMounted(load)
</script>

<template>
  <div class="container order-detail-page">
    <el-skeleton v-if="loading" :rows="10" animated />
    <template v-else-if="order">
      <div class="order-detail-hero"><div><router-link class="back-link" :to="{ name: 'orders' }">← 返回我的订单</router-link><span :class="['order-status-pill', orderStatusClass(order.status)]">{{ orderStatusLabel(order.status) }}</span><h1>{{ canPay(order.status) ? '订单等待支付' : '订单详情' }}</h1><p v-if="canPay(order.status)">请在 {{ formatDateTime(order.expireAt) }} 前完成支付，超时后系统将自动关单并恢复库存。</p><p v-else>订单状态已确认，可查看完整信息。</p></div><div class="order-detail-amount"><span>订单金额</span><strong>¥{{ formatPrice(order.payAmount) }}</strong></div></div>
      <div class="order-detail-layout">
        <div class="order-detail-main">
          <section class="panel-card"><div class="panel-card__header"><h2>商品明细</h2><span>共 {{ order.items.reduce((sum, item) => sum + item.quantity, 0) }} 件</span></div><article v-for="item in order.items" :key="item.id" class="checkout-item"><AppImage :src="item.productCover" :alt="item.productName" /><div class="checkout-item__info"><h3>{{ item.productName }}</h3><p>商品编号 {{ item.productId }}</p></div><div class="checkout-item__quantity">¥{{ formatPrice(item.unitPrice) }} × {{ item.quantity }}</div><strong>¥{{ formatPrice(item.itemAmount) }}</strong></article></section>
          <section class="panel-card"><div class="panel-card__header"><h2>收货信息</h2><span>订单创建时保存</span></div><div class="detail-grid"><div><span>收货人</span><b>{{ order.recipientName }}</b></div><div><span>联系电话</span><b>{{ order.recipientPhone }}</b></div><div class="is-wide"><span>收货地址</span><b>{{ order.shippingAddress }}</b></div></div></section>
        </div>
        <aside class="panel-card order-info-card"><h2>订单信息</h2><dl><div><dt>订单编号</dt><dd>{{ order.orderNo }}</dd></div><div><dt>订单来源</dt><dd>{{ order.source === 'BUY_NOW' ? '立即购买' : '购物车结算' }}</dd></div><div><dt>创建时间</dt><dd>{{ formatDateTime(order.createTime) }}</dd></div><div><dt>支付时间</dt><dd>{{ formatDateTime(order.payTime) }}</dd></div><div><dt>失效时间</dt><dd>{{ formatDateTime(order.expireAt) }}</dd></div><div><dt>商品金额</dt><dd>¥{{ formatPrice(order.totalAmount) }}</dd></div></dl><div class="order-info-card__total"><span>实付金额</span><strong>¥{{ formatPrice(order.payAmount) }}</strong></div><el-button v-if="canPay(order.status)" type="primary" size="large" @click="router.push({ name: 'payment', params: { orderNo: order.orderNo } })">立即支付</el-button><el-button v-if="canCancel(order.status)" size="large" :loading="cancelling" @click="cancel">取消订单</el-button></aside>
      </div>
    </template>
  </div>
</template>
