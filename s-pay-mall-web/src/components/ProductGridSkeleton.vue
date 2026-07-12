<script setup lang="ts">
withDefaults(defineProps<{ count?: number }>(), { count: 8 })
</script>

<template>
  <div class="product-grid" aria-label="商品加载中">
    <div v-for="index in count" :key="index" class="product-skeleton">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="image" class="product-skeleton__image" />
          <div class="product-skeleton__body">
            <el-skeleton-item variant="h3" style="width: 72%" />
            <el-skeleton-item variant="text" style="width: 95%" />
            <el-skeleton-item variant="text" style="width: 62%" />
            <el-skeleton-item variant="button" style="width: 100%; height: 42px" />
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>
