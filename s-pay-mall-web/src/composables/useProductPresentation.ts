import { getCategoryLabel } from '@/constants/categories'

export const DEFAULT_PRODUCT_IMAGE = '/images/products/default-product.svg'

export function formatPrice(value: number | string | undefined): string {
  const amount = Number(value ?? 0)
  return Number.isFinite(amount) ? amount.toFixed(2) : '0.00'
}

export function productStockText(stock: number): string {
  if (stock <= 0) return '暂时缺货'
  if (stock <= 10) return `仅剩 ${stock} 件`
  return '现货速发'
}

export function productStockClass(stock: number): string {
  if (stock <= 0) return 'is-out'
  if (stock <= 10) return 'is-low'
  return 'is-ready'
}

export { getCategoryLabel }
