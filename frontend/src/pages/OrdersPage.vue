<template>
  <div>
    <h1>訂單列表</h1>

    <!-- 載入中 -->
    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <!-- 訂單列表 -->
    <ul v-else>
      <li
        v-for="order in orders"
        :key="order.orderId"
        @click="openModal(order)"
      >
        訂單編號：{{ order.orderId }}，金額：{{ order.finalAmount }} 元
      </li>
    </ul>

    <!-- 彈出訂單明細 -->
    <transition name="fade">
      <div class="modal-overlay" v-if="selectedOrder" @click.self="closeModal">
        <div class="modal-content">
          <h2>訂單明細</h2>
          <p><strong>訂單編號：</strong> {{ selectedOrder.orderId }}</p>
          <p><strong>總金額：</strong> {{ selectedOrder.finalAmount }} 元</p>
          <p><strong>付款狀態：</strong> {{ selectedOrder.paymentStatusText }}</p>
          <p><strong>建立時間：</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
          <button @click="closeModal">關閉</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/services/axios';

const userId = 1001; // 暫時寫死，登入功能之後會改
const orders = ref([]);
const isLoading = ref(false);
const selectedOrder = ref(null);

// 載入歷史訂單
const loadOrders = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get(`/api/orders/user/${userId}`);
    orders.value = response;
  } catch (error) {
    console.error('載入訂單失敗', error);
  } finally {
    isLoading.value = false;
  }
};

// 開啟明細
const openModal = (order) => {
  selectedOrder.value = order;
};

// 關閉明細
const closeModal = () => {
  selectedOrder.value = null;
};

// 格式化時間 yyyy-MM-dd HH:mm
const formatDate = (datetime) => {
  if (!datetime) return '';
  const date = new Date(datetime);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
ul {
  list-style: none;
  padding: 0;
}

li {
  cursor: pointer;
  padding: 10px;
  border-bottom: 1px solid #ccc;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content {
  background: #fff;
  padding: 20px 30px;
  border-radius: 8px;
  min-width: 300px;
}

/* 淡入淡出動畫 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Spinner (轉圈圈) */
.loading {
  text-align: center;
  margin-top: 50px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #ccc;
  border-top-color: #3e8ed0;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
