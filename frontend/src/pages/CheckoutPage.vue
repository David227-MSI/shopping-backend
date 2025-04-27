<template>
  <div>
    <h1>結帳頁面</h1>

    <!-- 收件人資訊 -->
    <div class="info-block">
      <h2>收件人資訊</h2>
      <p>姓名: {{ userInfo.name }}</p>
      <p>電話: {{ userInfo.phone }}</p>
      <p>地址: {{ userInfo.address }}</p>
    </div>

    <!-- 購物車商品清單 -->
    <div class="info-block">
      <h2>購物車明細</h2>
      <ul>
        <li v-for="item in cartItems" :key="item.id">
          {{ item.productName }} - 數量: {{ item.quantity }} - 小計: {{ item.subtotal }} 元
        </li>
      </ul>
    </div>

    <!-- 總金額 -->
    <div class="info-block">
      <h2>總金額：{{ totalAmount }} 元</h2>
    </div>

    <!-- 送出訂單按鈕 -->
    <button :disabled="isSubmitting" @click="submitOrder">
      {{ isSubmitting ? '送出中...' : '送出訂單' }}
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import axios from '@/services/axios';
import Swal from 'sweetalert2';

const router = useRouter();
const cartStore = useCartStore();
const userId = computed(() => cartStore.userId);
const cartItems = computed(() => cartStore.cartItems);
const isSubmitting = ref(false);

const userInfo = ref({
  name: '測試使用者',
  phone: '0912345678',
  address: '台北市信義區',
});

// 載入購物車
const loadCart = async () => {
  if (userId.value) {
    const response = await axios.get(`/api/cart/${userId.value}`);
    cartStore.setCartItems(response);
  }
};

// 計算總金額
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.subtotal, 0);
});

// 送出訂單
const submitOrder = async () => {
  const result = await Swal.fire({
    title: '確認送出訂單？',
    text: '請再次確認購物車內容與收件資訊。',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '確認送出',
    cancelButtonText: '取消',
  });

  if (!result.isConfirmed) {
    return;
  }

  try {
    isSubmitting.value = true;

    const orderRequest = {
      userId: userId.value,
      couponPublishedId: null,
      recipientName: userInfo.value.name,
      recipientPhone: userInfo.value.phone,
      recipientAddress: userInfo.value.address,
    };

    const response = await axios.post('/api/orders', orderRequest);

    await Swal.fire({
      icon: 'success',
      title: '訂單成立成功！',
      text: '即將跳轉到完成頁面',
      timer: 1500,
      showConfirmButton: false,
    });

    router.push({ name: 'order-complete', state: { order: response } });
  } catch (error) {
    console.error('送出訂單失敗', error);
    await Swal.fire({
      icon: 'error',
      title: '訂單送出失敗',
      text: '請稍後再試！',
    });
  } finally {
    isSubmitting.value = false;
  }
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.info-block {
  margin-bottom: 20px;
}
button {
  margin-top: 20px;
}
</style>
