<script setup lang="ts">
import { reactive, ref } from 'vue'; import { useRoute,useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { useAuthStore } from '@/stores/auth'
const form=reactive({username:'',password:''}); const loading=ref(false); const auth=useAuthStore(); const router=useRouter(); const route=useRoute()
async function submit(){loading.value=true;try{await auth.login(form.username,form.password);ElMessage.success('登录成功');router.push(String(route.query.redirect||'/'))}finally{loading.value=false}}
</script>
<template><div class="auth"><el-card><h2>斗牛士综合格斗商城</h2><h3 style="margin:-16px 0 22px;color:#909399;font-weight:400;font-size:13px;text-align:center;">Ilia Topuria — 账号登录</h3><el-form label-position="top" @submit.prevent="submit"><el-form-item label="用户名"><el-input v-model="form.username"/></el-form-item><el-form-item label="密码"><el-input v-model="form.password" type="password" show-password/></el-form-item><el-button type="primary" :loading="loading" native-type="submit" style="width:100%">登录</el-button></el-form><p>没有账号？<router-link to="/register">立即注册</router-link></p></el-card></div></template>
<style scoped>.auth{max-width:420px;margin:70px auto}.auth h2{text-align:center}.auth p{text-align:center;color:#909399}</style>
