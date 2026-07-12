<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppImage from '@/components/AppImage.vue'
import { orderApi } from '@/api'
import { formatPrice } from '@/composables/useProductPresentation'
import { canCancel, canPay, formatDateTime, orderStatusClass, orderStatusLabel } from '@/composables/useOrderPresentation'
import type { Order, OrderStatus } from '@/types'

const router = useRouter()
const orders = ref<Order[]>([])
const loading = ref(true)
const activeStatus = ref<'ALL' | OrderStatus>('ALL')
const page = ref(1)
const pageSize = 6
const cancellingNo = ref('')
const tabs: Array<{ value: 'ALL' | OrderStatus; label: string }> = [
  { value: 'ALL', label: '全部' },
  { value: 'PENDING_PAYMENT', label: '待支付' },
  { value: 'PAID', label: '已支付' },
  { value: 'CANCELLED', label: '已取消' },
  { value: 'CLOSED', label: '已关闭' }
]
const filtered = computed(() => activeStatus.value === 'ALL' ? orders.value : orders.value.filter((order) => order.status === activeStatus.value))
const pagedOrders = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))

async function load(): Promise<void> {
  loading.value = true
  try { orders.value = (await orderApi.list()).data.data } finally { loading.value = false }
}
function changeStatus(value: 'ALL' | OrderStatus): void { activeStatus.value = value; page.value = 1 }
async function cancel(no: string): Promise<void> {
  await ElMessageBox.confirm('取消后库存会恢复，确定取消该订单吗？', '取消订单', { type: 'warning' })
  cancellingNo.value = no
  try { await orderApi.cancel(no); ElMessage.success('订单已取消'); await load() } finally { cancellingNo.value = '' }
}
function goPay(order: Order): void { router.push({ name: 'payment', params: { orderNo: order.orderNo } }) }
onMounted(load)
</script>

<template>
  <div class="container orders-page">
    <div class="page-heading-row"><div><span class="section-kicker">MY ORDERS</span><h1 class="page-title">我的订单</h1><p>查看订单状态、商品明细与支付进度。</p></div><router-link class="text-link" :to="{ name: 'products' }">继续购物 →</router-link></div>
    <div class="order-tabs"><button v-for="tab in tabs" :key="tab.value" :class="{ active: activeStatus === tab.value }" @click="changeStatus(tab.value)">{{ tab.label }}<b>{{ tab.value === 'ALL' ? orders.length : orders.filter((o) => o.status === tab.value).length }}</b></button></div>
    <el-skeleton v-if="loading" :rows="8" animated />
    <el-empty v-else-if="!filtered.length" description="当前分类暂无订单"><router-link :to="{ name: 'products' }"><el-button type="primary">去选购</el-button></router-link></el-empty>
    <template v-else>
      <article v-for="order in pagedOrders" :key="order.orderNo" class="order-card-v2 panel-card">
        <header><div><span>订单号 {{ order.orderNo }}</span><small>{{ formatDateTime(order.createTime) }}</small></div><span :class="['order-status-pill', orderStatusClass(order.status)]">{{ orderStatusLabel(order.status) }}</span></header>
        <div class="order-card-v2__body"><div class="order-card-v2__items"><div v-for="item in order.items" :key="item.id" class="order-mini-item"><AppImage :src="item.productCover" :alt="item.productName" /><div><strong>{{ item.productName }}</strong><span>¥{{ formatPrice(item.unitPrice) }} × {{ item.quantity }}</span></div></div></div><div class="order-card-v2__summary"><span>共 {{ order.items.reduce((sum, item) => sum + item.quantity, 0) }} 件</span><p>实付 <strong>¥{{ formatPrice(order.payAmount) }}</strong></p></div></div>
        <footer><el-button @click="router.push({ name: 'order-detail', params: { orderNo: order.orderNo } })">查看详情</el-button><el-button v-if="canCancel(order.status)" :loading="cancellingNo === order.orderNo" @click="cancel(order.orderNo)">取消订单</el-button><el-button v-if="canPay(order.status)" type="primary" @click="goPay(order)">立即支付</el-button></footer>
      </article>
      <el-pagination v-if="filtered.length > pageSize" v-model:current-page="page" background layout="prev, pager, next" :page-size="pageSize" :total="filtered.length" class="orders-pagination" />
    </template>
  </div>
</template>
