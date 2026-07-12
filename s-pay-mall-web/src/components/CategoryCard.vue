<script setup lang="ts">
import type { SportCategory } from '@/constants/categories'

defineProps<{ category: SportCategory }>()
</script>

<template>
  <router-link
    class="category-card"
    :to="{ name: 'products', query: { category: category.value } }"
  >
    <span class="category-card__icon" aria-hidden="true">{{ category.icon }}</span>
    <div>
      <h3>{{ category.label }}</h3>
      <p>{{ category.description }}</p>
    </div>
    <span class="category-card__arrow" aria-hidden="true">→</span>
  </router-link>
</template>
