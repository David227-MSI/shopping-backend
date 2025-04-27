<template>
  <div>
    <h1>購物車頁面</h1>

    <div v-if="cartItems.length === 0">
      <p>購物車是空的</p>
    </div>

    <div v-else>
      <ul>
        <li v-for="item in cartItems" :key="item.id">
          {{ item.productName }} - 數量: {{ item.quantity }} - 單價: {{ item.price }} 元
          <button @click="updateQuantity(item.productId, item.quantity - 1)" :disabled="isLoading || item.quantity <= 1">➖</button>
          <button @click="updateQuantity(item.productId, item.quantity + 1)" :disabled="isLoading">➕</button>
          <button @click="removeItem(item.productId)" :disabled="isLoading">🗑️ 刪除</button>
        </li>
      </ul>

      <hr />

      <button @click="clearCart" :disabled="isLoading">🧹 清空購物車</button>

      <div style="margin-top: 20px;">
        <p>💰 總金額：{{ totalAmount }} 元</p>

        <button @click="goToCheckout" :disabled="isLoading || cartItems.length === 0">
          {{ isLoading ? '處理中...' : '🚚 前往結帳' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from '@/services/axios';
import { useCartStore } from '@/stores/cartStore';
import Swal from 'sweetalert2';

const router = useRouter();
const cartStore = useCartStore();
const userId = computed(() => cartStore.userId);
const cartItems = computed(() => cartStore.cartItems);
const isLoading = ref(false);

// 載入購物車
const loadCart = async () => {
  try {
    if (userId.value) {
      const response = await axios.get(`/api/cart/${userId.value}`);
      cartStore.setCartItems(response);
    }
  } catch (error) {
    console.error('載入購物車失敗', error);
    Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: '無法載入購物車資料，請稍後再試！',
    });
  }
};

// 更新商品數量
const updateQuantity = async (productId, newQuantity) => {
  if (newQuantity < 1) return;
  isLoading.value = true;
  try {
    if (userId.value) {
      await axios.put('/api/cart', {
        userId: userId.value,
        productId,
        quantity: newQuantity,
      });
      await loadCart();
    } else {
      cartStore.updateItemQuantity(productId, newQuantity);
    }
    await Swal.fire({
      icon: 'success',
      title: '更新成功！',
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 1000,
      timerProgressBar: true,
    });
  } catch (error) {
    console.error('更新商品數量失敗', error);
  } finally {
    isLoading.value = false;
  }
};

// 刪除單一商品
const removeItem = async (productId) => {
  isLoading.value = true;
  try {
    if (userId.value) {
      await axios.delete(`/api/cart/${userId.value}/${productId}`);
      await loadCart();
    } else {
      cartStore.removeItem(productId);
    }
    await Swal.fire({
      icon: 'success',
      title: '刪除成功！',
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 1000,
      timerProgressBar: true,
    });
  } catch (error) {
    console.error('刪除商品失敗', error);
  } finally {
    isLoading.value = false;
  }
};

// 清空購物車
const clearCart = async () => {
  isLoading.value = true;
  try {
    if (userId.value) {
      await axios.delete(`/api/cart/clear/${userId.value}`);
      await loadCart();
    } else {
      cartStore.clearCart();
    }
    await Swal.fire({
      icon: 'success',
      title: '購物車已清空！',
      showConfirmButton: false,
      timer: 1200,
    });
  } catch (error) {
    console.error('清空購物車失敗', error);
  } finally {
    isLoading.value = false;
  }
};

// 計算總金額
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.subtotal, 0);
});

// 前往結帳頁面
const goToCheckout = async () => {
  if (cartItems.value.length === 0) {
    await Swal.fire({
      icon: 'warning',
      title: '購物車是空的！',
      text: '請先加入商品再結帳',
      confirmButtonText: '了解',
    });
    return;
  }

  try {
    router.push({ name: 'checkout' });
  } catch (error) {
    console.error('跳轉結帳頁失敗', error);
  }
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
button {
  margin: 0 4px;
}
</style>
