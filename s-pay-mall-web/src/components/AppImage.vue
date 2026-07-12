<script setup lang="ts">
import { ref, watch } from 'vue'
import { DEFAULT_PRODUCT_IMAGE } from '@/composables/useProductPresentation'

const props = withDefaults(defineProps<{
  src?: string
  alt: string
  fallback?: string
  lazy?: boolean
}>(), {
  fallback: DEFAULT_PRODUCT_IMAGE,
  lazy: true
})

const currentSrc = ref(props.src || props.fallback)

watch(() => props.src, (value) => {
  currentSrc.value = value || props.fallback
})

function handleError(): void {
  if (currentSrc.value !== props.fallback) currentSrc.value = props.fallback
}
</script>

<template>
  <img
    :src="currentSrc"
    :alt="alt"
    :loading="lazy ? 'lazy' : 'eager'"
    @error="handleError"
  />
</template>
