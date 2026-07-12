export interface Product {
  id: number
  name: string
  category: string
  description?: string
  coverUrl?: string
  price: number
  stock: number
  status: string
  createTime?: string
}

export type ProductSort = 'DEFAULT' | 'PRICE_ASC' | 'PRICE_DESC' | 'NEWEST'

export interface ProductQuery {
  keyword?: string
  category?: string
  sort?: ProductSort
  page?: number
  pageSize?: number
}

export interface ProductPage {
  records: Product[]
  total: number
  page: number
  pageSize: number
  pages: number
}

export interface CartItem {
  id: number
  productId: number
  productName: string
  productCover?: string
  unitPrice: number
  quantity: number
  stock: number
  selected?: boolean
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productCover?: string
  unitPrice: number
  quantity: number
  itemAmount: number
}

export type OrderSource = 'CART' | 'BUY_NOW'
export type OrderStatus = 'PENDING_PAYMENT' | 'PAYING' | 'PAID' | 'CANCELLED' | 'CLOSED' | 'SHIPPED' | 'COMPLETED' | 'REFUNDING' | 'REFUNDED'

export interface Order {
  orderNo: string
  source: OrderSource
  recipientName: string
  recipientPhone: string
  shippingAddress: string
  totalAmount: number
  payAmount: number
  status: OrderStatus
  expireAt: string
  remainingSeconds?: number
  payTime?: string
  closeTime?: string
  createTime: string
  items: OrderItem[]
}

export interface CreateOrderItemPayload {
  productId: number
  quantity: number
}

export interface CreateOrderPayload {
  source: OrderSource
  cartItemIds?: number[]
  items?: CreateOrderItemPayload[]
  recipientName: string
  recipientPhone: string
  shippingAddress: string
}

export interface CheckoutDisplayItem {
  key: string
  productId: number
  productName: string
  productCover?: string
  unitPrice: number
  quantity: number
  stock: number
}

export interface User {
  id: number
  username: string
  nickname: string
  role: string
  status: string
}
