<template>
  <div class="page-shell page-section">
    <div class="not-found">
      <span>404</span>
      <h1>跑错赛道了</h1>
      <p>你访问的页面不存在，返回商城继续挑选运动装备吧。</p>
      <router-link :to="{ name: 'home' }"><el-button type="primary" size="large">返回首页</el-button></router-link>
    </div>
  </div>
</template>
