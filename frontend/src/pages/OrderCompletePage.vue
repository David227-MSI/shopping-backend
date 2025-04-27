<template>
  <div class="order-complete">
    <h1>訂單完成！🎉</h1>

    <div class="order-info" v-if="order">
      <p><strong>訂單編號：</strong> {{ order.orderId }}</p>
      <p><strong>總金額：</strong> {{ order.finalAmount }} 元</p>
      <p><strong>付款狀態：</strong> {{ order.paymentStatusText }}</p>
      <p><strong>訂單建立時間：</strong> {{ formatDate(order.createdAt) }}</p>
    </div>

    <div v-else>
      <p>未取得訂單資訊，請回首頁重新操作。</p>
    </div>

    <div class="buttons">
      <button @click="goHome" :disabled="isNavigating">🏠 回首頁</button>
      <button @click="goOrders" :disabled="isNavigating">📋 查看訂單列表</button>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const isNavigating = ref(false);

const order = route.state?.order || null;

// 返回首頁
const goHome = async () => {
  if (isNavigating.value) return;
  isNavigating.value = true;
  try {
    await router.push({ name: 'home' });
  } catch (error) {
    console.error('跳轉首頁失敗', error);
    await Swal.fire('錯誤', '跳轉失敗，請稍後再試', 'error');
  } finally {
    isNavigating.value = false;
  }
};

// 前往訂單列表
const goOrders = async () => {
  if (isNavigating.value) return;
  isNavigating.value = true;
  try {
    await router.push({ name: 'orders' });
  } catch (error) {
    console.error('跳轉訂單列表失敗', error);
    await Swal.fire('錯誤', '跳轉失敗，請稍後再試', 'error');
  } finally {
    isNavigating.value = false;
  }
};

// 格式化時間
const formatDate = (datetime) => {
  if (!datetime) return '';
  const date = new Date(datetime);
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}
  -${date.getDate().toString().padStart(2,'0')} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`;
};
</script>

<style scoped>
.order-complete {
  max-width: 600px;
  margin: 50px auto;
  padding: 30px;
  text-align: center;
  background: #f9f9f9;
  border-radius: 8px;
}

.order-info {
  margin: 20px 0;
  text-align: left;
}

.order-info p {
  margin: 10px 0;
}

.buttons {
  margin-top: 30px;
}

button {
  margin: 0 10px;
  padding: 10px 20px;
  cursor: pointer;
}
</style>
