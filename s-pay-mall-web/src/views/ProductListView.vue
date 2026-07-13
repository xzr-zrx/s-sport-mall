<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import ProductGridSkeleton from '@/components/ProductGridSkeleton.vue'
import { productApi } from '@/api'
import { SPORT_CATEGORIES } from '@/constants/categories'
import type { Product, ProductSort } from '@/types'

const route = useRoute()
const router = useRouter()
const products = ref<Product[]>([])
const loading = ref(false)
const failed = ref(false)
const total = ref(0)
const keyword = ref('')
const category = ref('')
const sort = ref<ProductSort>('DEFAULT')
const page = ref(1)
const pageSize = 12

const activeCategoryLabel = computed(() =>
  SPORT_CATEGORIES.find((item) => item.value === category.value)?.label ?? '全部格斗装备'
)

function syncFromRoute(): void {
  keyword.value = typeof route.query.keyword === 'string' ? route.query.keyword : ''
  category.value = typeof route.query.category === 'string' ? route.query.category : ''
  const querySort = typeof route.query.sort === 'string' ? route.query.sort : 'DEFAULT'
  sort.value = ['DEFAULT', 'PRICE_ASC', 'PRICE_DESC', 'NEWEST'].includes(querySort)
    ? querySort as ProductSort
    : 'DEFAULT'
  page.value = Math.max(Number(route.query.page) || 1, 1)
}

async function loadProducts(): Promise<void> {
  loading.value = true
  failed.value = false
  try {
    const response = await productApi.list({
      keyword: keyword.value || undefined,
      category: category.value || undefined,
      sort: sort.value,
      page: page.value,
      pageSize
    })
    products.value = response.data.data.records
    total.value = response.data.data.total
  } catch {
    failed.value = true
  } finally {
    loading.value = false
  }
}

function updateRoute(overrides: Record<string, string | number | undefined> = {}): void {
  const next = {
    keyword: keyword.value.trim() || undefined,
    category: category.value || undefined,
    sort: sort.value === 'DEFAULT' ? undefined : sort.value,
    page: page.value > 1 ? page.value : undefined,
    ...overrides
  }
  const query = Object.fromEntries(Object.entries(next).filter(([, value]) => value !== undefined && value !== ''))
  router.push({ name: 'products', query })
}

function submitSearch(): void {
  page.value = 1
  updateRoute({ page: undefined })
}

function selectCategory(value: string): void {
  category.value = value
  page.value = 1
  updateRoute({ category: value || undefined, page: undefined })
}

function changeSort(): void {
  page.value = 1
  updateRoute({ sort: sort.value === 'DEFAULT' ? undefined : sort.value, page: undefined })
}

function changePage(value: number): void {
  page.value = value
  updateRoute({ page: value > 1 ? value : undefined })
}

function clearFilters(): void {
  keyword.value = ''
  category.value = ''
  sort.value = 'DEFAULT'
  page.value = 1
  router.push({ name: 'products' })
}

watch(() => route.fullPath, async () => {
  syncFromRoute()
  await loadProducts()
})

onMounted(async () => {
  syncFromRoute()
  await loadProducts()
})
</script>

<template>
  <div class="catalog-page page-shell page-section">
    <div class="catalog-hero">
      <div>
        <span class="section-kicker">FIGHT GEAR COLLECTION</span>
        <h1>{{ activeCategoryLabel }}</h1>
        <p>按格斗类别、关键词与价格快速筛选，找到适合你的训练装备。</p>
      </div>
      <div class="catalog-hero__mark">F<br>IGHT</div>
    </div>

    <div class="catalog-toolbar">
      <div class="catalog-search">
        <el-input
          v-model="keyword"
          clearable
          size="large"
          placeholder="搜索拳套、护齿、道服、训练器材……"
          @keyup.enter="submitSearch"
          @clear="submitSearch"
        >
          <template #append><el-button @click="submitSearch">搜索</el-button></template>
        </el-input>
      </div>
      <el-select v-model="sort" class="catalog-sort" size="large" aria-label="商品排序" @change="changeSort">
        <el-option label="综合排序" value="DEFAULT" />
        <el-option label="最新上架" value="NEWEST" />
        <el-option label="价格从低到高" value="PRICE_ASC" />
        <el-option label="价格从高到低" value="PRICE_DESC" />
      </el-select>
    </div>

    <div class="category-tabs" role="navigation" aria-label="运动分类">
      <button :class="{ active: !category }" type="button" @click="selectCategory('')">全部</button>
      <button
        v-for="item in SPORT_CATEGORIES"
        :key="item.value"
        :class="{ active: category === item.value }"
        type="button"
        @click="selectCategory(item.value)"
      >
        <span>{{ item.icon }}</span>{{ item.label }}
      </button>
    </div>

    <div class="catalog-summary">
      <span>共找到 <strong>{{ total }}</strong> 件商品</span>
      <button v-if="keyword || category || sort !== 'DEFAULT'" type="button" @click="clearFilters">清除筛选</button>
    </div>

    <ProductGridSkeleton v-if="loading" :count="10" />
    <div v-else-if="failed" class="state-panel">
      <strong>无法获取商品列表</strong>
      <p>网络连接异常，请检查后重新加载。</p>
      <el-button type="primary" @click="loadProducts">重新加载</el-button>
    </div>
    <div v-else-if="!products.length" class="state-panel state-panel--empty">
      <span class="state-panel__icon">🔎</span>
      <strong>没有找到符合条件的商品</strong>
      <p>换一个关键词或清除分类筛选试试。</p>
      <el-button @click="clearFilters">查看全部商品</el-button>
    </div>
    <div v-else class="product-grid">
      <ProductCard v-for="product in products" :key="product.id" :product="product" />
    </div>

    <div v-if="total > pageSize" class="catalog-pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        @current-change="changePage"
      />
    </div>
  </div>
</template>
