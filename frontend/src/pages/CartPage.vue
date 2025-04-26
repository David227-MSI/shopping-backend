<template>
  <div>
    <h1>購物車頁面</h1>

    <div v-if="cartItems.length === 0">
      <p>購物車是空的</p>
    </div>

    <div v-else>
      <ul>
        <li v-for="item in cartItems" :key="item.id">
          {{ item.productName }} - 數量 : {{ item.quantity }} - 單價 : {{ item.price }} 元
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';　
import axios from '@/services/axios';　

const cartItems = ref([]); // 購物車商品列表
const userId = 1001 // 暫時寫死

// 載入時打API抓購物車
onMounted(async () => {
  try {
    const response = await axios.get(`/api/cart/${userId}`);
    cartItems.value = response; // 成功時直接是資料，不用 .data
    console.log('購物車資料', cartItems.value)
  } catch (error) {
    console.error('載入購物車失敗', error);
  }
});
</script>

<style scoped>
</style>
