<script setup lang="ts">
import { reactive,ref } from 'vue'; import { useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { useAuthStore } from '@/stores/auth'
const form=reactive({username:'',nickname:'',password:''});const loading=ref(false);const auth=useAuthStore();const router=useRouter()
async function submit(){loading.value=true;try{await auth.register(form.username,form.password,form.nickname);ElMessage.success('注册成功');router.push('/')}finally{loading.value=false}}
</script>
<template><div class="auth"><el-card><h2>创建账号</h2><el-form label-position="top" @submit.prevent="submit"><el-form-item label="用户名"><el-input v-model="form.username"/></el-form-item><el-form-item label="昵称"><el-input v-model="form.nickname"/></el-form-item><el-form-item label="密码（至少 6 位）"><el-input v-model="form.password" type="password" show-password/></el-form-item><el-button type="primary" :loading="loading" native-type="submit" style="width:100%">注册</el-button></el-form></el-card></div></template>
<style scoped>.auth{max-width:420px;margin:70px auto}.auth h2{text-align:center}</style>
