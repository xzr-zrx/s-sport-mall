import type { OrderStatus } from '@/types'

const LABELS: Record<OrderStatus, string> = {
  PENDING_PAYMENT: '待支付',
  PAYING: '支付处理中',
  PAID: '已支付',
  CANCELLED: '已取消',
  CLOSED: '已关闭',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
  REFUNDING: '退款中',
  REFUNDED: '已退款'
}

export function orderStatusLabel(status: OrderStatus): string {
  return LABELS[status] || status
}

export function orderStatusClass(status: OrderStatus): string {
  if (status === 'PAID' || status === 'COMPLETED' || status === 'SHIPPED') return 'is-success'
  if (status === 'PENDING_PAYMENT' || status === 'PAYING') return 'is-warning'
  return 'is-muted'
}

export function canPay(status: OrderStatus): boolean {
  return status === 'PENDING_PAYMENT' || status === 'PAYING'
}

export function canCancel(status: OrderStatus): boolean {
  return status === 'PENDING_PAYMENT' || status === 'PAYING'
}

export function formatDateTime(value?: string): string {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value.replace('T', ' ')
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
  }).format(date).replaceAll('/', '-')
}
