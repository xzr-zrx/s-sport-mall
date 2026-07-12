import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { cartApi } from '@/api'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const loading = ref(false)
  const count = computed(() => items.value.reduce((total, item) => total + item.quantity, 0))

  async function load(): Promise<void> {
    if (!localStorage.getItem('token')) {
      items.value = []
      return
    }
    loading.value = true
    try {
      items.value = (await cartApi.list()).data.data
    } finally {
      loading.value = false
    }
  }

  async function addProduct(productId: number, quantity = 1): Promise<void> {
    await cartApi.add(productId, quantity)
    await load()
  }

  function reset(): void {
    items.value = []
  }

  return { items, loading, count, load, addProduct, reset }
})
