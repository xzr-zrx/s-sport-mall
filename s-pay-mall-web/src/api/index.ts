import request, { type ApiResponse } from './request'
import type { CartItem, CreateOrderPayload, Order, Product, ProductPage, ProductQuery, User } from '@/types'

export const authApi = {
  login: (data: { username: string; password: string }) =>
    request.post<ApiResponse<{ token: string }>>('/api/v1/auth/login', data),
  register: (data: { username: string; password: string; nickname?: string }) =>
    request.post<ApiResponse<{ token: string }>>('/api/v1/auth/register', data),
  me: () => request.get<ApiResponse<User>>('/api/v1/auth/me')
}

export const productApi = {
  list: (params: ProductQuery = {}) =>
    request.get<ApiResponse<ProductPage>>('/api/v1/products', { params }),
  detail: (id: number) => request.get<ApiResponse<Product>>(`/api/v1/products/${id}`)
}

export const cartApi = {
  list: () => request.get<ApiResponse<CartItem[]>>('/api/v1/cart'),
  add: (productId: number, quantity = 1) => request.post('/api/v1/cart/items', { productId, quantity }),
  update: (id: number, quantity: number) => request.put(`/api/v1/cart/items/${id}`, { quantity }),
  remove: (id: number) => request.delete(`/api/v1/cart/items/${id}`)
}

export const orderApi = {
  create: (payload: CreateOrderPayload) => request.post<ApiResponse<Order>>('/api/v1/orders', payload),
  list: () => request.get<ApiResponse<Order[]>>('/api/v1/orders'),
  detail: (orderNo: string) => request.get<ApiResponse<Order>>(`/api/v1/orders/${orderNo}`),
  cancel: (orderNo: string) => request.post(`/api/v1/orders/${orderNo}/cancel`),
  pay: (orderNo: string) => request.post<string>(`/api/v1/payments/alipay/${orderNo}`, null, { responseType: 'text' })
}
