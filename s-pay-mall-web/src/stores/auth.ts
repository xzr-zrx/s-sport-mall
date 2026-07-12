import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { authApi } from '@/api'
import type { User } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)
  const loggedIn = computed(() => !!token.value)
  function saveToken(value:string) { token.value=value; localStorage.setItem('token', value) }
  async function login(username:string,password:string){ const r=await authApi.login({username,password}); saveToken(r.data.data.token); await loadUser() }
  async function register(username:string,password:string,nickname?:string){ const r=await authApi.register({username,password,nickname}); saveToken(r.data.data.token); await loadUser() }
  async function loadUser(){ if(!token.value)return; user.value=(await authApi.me()).data.data }
  function logout(){ token.value=''; user.value=null; localStorage.removeItem('token') }
  return { token,user,loggedIn,login,register,loadUser,logout }
})
