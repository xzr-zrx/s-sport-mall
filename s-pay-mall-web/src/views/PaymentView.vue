<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { orderApi } from '@/api'
import { formatPrice } from '@/composables/useProductPresentation'
import { canPay, formatDateTime, orderStatusClass, orderStatusLabel } from '@/composables/useOrderPresentation'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()
const order = ref<Order | null>(null)
const loading = ref(true)
const paying = ref(false)
const now = ref(Date.now())
const loadedAt = ref(Date.now())
const remainingAtLoad = ref(0)
let countdownTimer: number | undefined
let pollingTimer: number | undefined
let paymentWindow: Window | null = null

const orderNo = computed(() => String(route.params.orderNo))
const paymentLabel = import.meta.env.VITE_PAYMENT_LABEL || '在线支付'
const remainingSeconds = computed(() => {
  if (!order.value) return 0
  return Math.max(0, remainingAtLoad.value - Math.floor((now.value - loadedAt.value) / 1000))
})
const countdownText = computed(() => {
  const seconds = remainingSeconds.value
  const minutes = Math.floor(seconds / 60).toString().padStart(2, '0')
  const rest = (seconds % 60).toString().padStart(2, '0')
  return `${minutes}:${rest}`
})

async function load(silent = false): Promise<void> {
  if (!silent) loading.value = true
  try {
    const latest = (await orderApi.detail(orderNo.value)).data.data
    order.value = latest
    loadedAt.value = Date.now()
    remainingAtLoad.value = Math.max(0, Number(latest.remainingSeconds ?? 0))
    if (latest.status === 'PAID') {
      stopPolling()
      await router.replace({ name: 'payment-result', query: { orderNo: latest.orderNo } })
    } else if (!canPay(latest.status)) {
      stopPolling()
    }
  } finally {
    if (!silent) loading.value = false
  }
}

async function startPayment(): Promise<void> {
  if (!order.value || !canPay(order.value.status)) return
  if (remainingSeconds.value <= 0) {
    ElMessage.warning('订单已超过支付时间，请刷新订单状态')
    await load()
    return
  }
  paymentWindow = window.open('', '_blank', 'width=760,height=760')
  if (!paymentWindow) {
    ElMessage.warning('浏览器阻止了支付窗口，请允许本站弹出窗口后重试')
    return
  }
  paymentWindow.document.write('<!doctype html><meta charset="utf-8"><title>正在打开支付</title><p style="font-family:sans-serif;padding:40px">正在安全打开支付页面，请稍候…</p>')
  paying.value = true
  try {
    const html = (await orderApi.pay(orderNo.value)).data
    paymentWindow.document.open()
    paymentWindow.document.write(html)
    paymentWindow.document.close()
    startPolling()
    ElMessage.success('支付窗口已打开，完成支付后本页会自动更新')
  } catch (error) {
    paymentWindow.close()
    paymentWindow = null
    throw error
  } finally {
    paying.value = false
  }
}

function startPolling(): void {
  if (pollingTimer) return
  pollingTimer = window.setInterval(() => load(true).catch(() => undefined), 2000)
}

function stopPolling(): void {
  if (pollingTimer) window.clearInterval(pollingTimer)
  pollingTimer = undefined
}

function handlePaymentMessage(event: MessageEvent): void {
  const data = event.data as { type?: string; orderNo?: string } | undefined
  if (data?.type === 'S_PAY_PAYMENT_SUCCESS' && data.orderNo === orderNo.value) {
    load(true).catch(() => undefined)
  }
}

onMounted(async () => {
  await load()
  if (!order.value || order.value.status === 'PAID') return
  countdownTimer = window.setInterval(() => {
    now.value = Date.now()
    if (remainingSeconds.value <= 0 && order.value && canPay(order.value.status)) load(true).catch(() => undefined)
  }, 1000)
  if (order.value && canPay(order.value.status)) startPolling()
  window.addEventListener('message', handlePaymentMessage)
})

onBeforeUnmount(() => {
  if (countdownTimer) window.clearInterval(countdownTimer)
  stopPolling()
  window.removeEventListener('message', handlePaymentMessage)
})
</script>

<template>
  <div class="container payment-page">
    <div class="flow-steps"><span class="is-done">1 选择商品</span><span class="is-done">2 确认订单</span><span class="is-active">3 完成支付</span></div>
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="order">
      <section class="payment-hero">
        <div><span class="section-kicker">SECURE PAYMENT</span><h1>订单支付</h1><p>请核对订单信息，并在倒计时结束前完成支付。</p></div>
        <div class="payment-countdown"><span>剩余支付时间</span><strong>{{ countdownText }}</strong></div>
      </section>
      <div class="payment-layout">
        <section class="panel-card payment-order-card">
          <div class="panel-card__header"><h2>订单信息</h2><span :class="['order-status-pill', orderStatusClass(order.status)]">{{ orderStatusLabel(order.status) }}</span></div>
          <div class="payment-order-meta"><div><span>订单编号</span><b>{{ order.orderNo }}</b></div><div><span>创建时间</span><b>{{ formatDateTime(order.createTime) }}</b></div><div><span>收货人</span><b>{{ order.recipientName }} · {{ order.recipientPhone }}</b></div><div><span>收货地址</span><b>{{ order.shippingAddress }}</b></div></div>
          <article v-for="item in order.items" :key="item.id" class="checkout-item compact">
            <AppImage :src="item.productCover" :alt="item.productName" />
            <div class="checkout-item__info"><h3>{{ item.productName }}</h3><p>¥{{ formatPrice(item.unitPrice) }} × {{ item.quantity }}</p></div>
            <strong>¥{{ formatPrice(item.itemAmount) }}</strong>
          </article>
        </section>
        <aside class="panel-card payment-method-card">
          <h2>支付方式</h2>
          <div class="payment-method is-selected"><span class="payment-method__icon">支</span><div><strong>{{ paymentLabel }}</strong></div><b>✓</b></div>
          <div class="payment-amount"><span>应付金额</span><strong>¥{{ formatPrice(order.payAmount) }}</strong></div>
          <el-button v-if="canPay(order.status)" type="primary" size="large" :loading="paying" :disabled="remainingSeconds <= 0" @click="startPayment">{{ remainingSeconds > 0 ? '打开支付页面' : '订单已超时' }}</el-button>
          <el-button v-else size="large" @click="$router.push({ name: 'order-detail', params: { orderNo: order.orderNo } })">查看订单详情</el-button>
          <button class="plain-link" type="button" @click="load()">刷新支付状态</button>
          <p class="payment-tip">支付结果以系统确认状态为准，请勿重复支付同一订单。</p>
        </aside>
      </div>
    </template>
  </div>
</template>
