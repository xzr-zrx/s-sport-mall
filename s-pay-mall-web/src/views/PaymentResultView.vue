<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi } from '@/api'
import { formatPrice } from '@/composables/useProductPresentation'
import { orderStatusLabel } from '@/composables/useOrderPresentation'
import type { Order } from '@/types'

const route = useRoute()
const order = ref<Order | null>(null)
const loading = ref(true)
const success = computed(() => order.value?.status === 'PAID')

onMounted(async () => {
  const orderNo = String(route.query.orderNo || route.query.out_trade_no || '')
  if (!orderNo) { loading.value = false; return }
  try { order.value = (await orderApi.detail(orderNo)).data.data } finally { loading.value = false }
})
</script>

<template>
  <div class="container payment-result-page">
    <el-skeleton v-if="loading" :rows="6" animated />
    <el-result v-else-if="order" :icon="success ? 'success' : 'warning'" :title="success ? '支付成功' : `订单${orderStatusLabel(order.status)}`" :sub-title="success ? `订单 ${order.orderNo} 已完成支付，金额 ¥${formatPrice(order.payAmount)}` : '支付状态以系统确认结果为准，请查看订单详情获取最新状态。'">
      <template #extra><div class="result-actions"><router-link :to="{ name: 'order-detail', params: { orderNo: order.orderNo } }"><el-button>查看订单详情</el-button></router-link><router-link :to="{ name: 'products' }"><el-button type="primary">继续购物</el-button></router-link></div></template>
    </el-result>
    <el-result v-else icon="info" title="暂未获取到订单" sub-title="请从订单页面查看最新状态。"><template #extra><router-link :to="{ name: 'orders' }"><el-button type="primary">查看我的订单</el-button></router-link></template></el-result>
  </div>
</template>
