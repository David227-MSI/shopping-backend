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

// 臨時商品圖片對應
const getImageUrl = (productName) => {
  if (productName.includes('香水')) {
    return 'https://www.jomalone.com.tw/media/export/cms/products/1000x1000/jo_sku_L00C01_1000x1000_0.png';
  }
  if (productName.toLowerCase().includes('iphone')) {
    return 'https://d2lfcsub12kx0l.cloudfront.net/tw/product/img/Apple_apple_iphone_plus_0907185907356_360x270.jpg';
  }
  return 'https://via.placeholder.com/80?text=No+Image'; // 預設
};


</script>

<template>
  <div class="cart-container">
    <h1 class="page-title">購物車</h1>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <p>目前購物車內沒有商品</p>
    </div>

    <div v-else class="cart-main">
      <!-- 左邊 商品列表 -->
      <div class="cart-list">
        <ul>
          <li v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="item-image">
              <img :src="getImageUrl(item.productName)" alt="商品圖片" />
            </div>
            <div class="item-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-detail">單價: {{ item.price }} 元</div>
              <div class="item-actions">
                <button @click="updateQuantity(item.productId, item.quantity - 1)" :disabled="isLoading || item.quantity <= 1">-</button>
                <span class="quantity">{{ item.quantity }}</span>
                <button @click="updateQuantity(item.productId, item.quantity + 1)" :disabled="isLoading">+</button>
                <button @click="removeItem(item.productId)" :disabled="isLoading" class="delete-btn">刪除</button>
              </div>
            </div>
          </li>
        </ul>
      </div>

      <!-- 右邊 結帳資訊 -->
      <div class="checkout-summary">
        <div class="summary-card">
          <h2>結帳明細</h2>
          <div class="summary-item">
            <span>商品總金額</span>
            <span>${{ totalAmount }}</span>
          </div>
          <div class="summary-item">
            <span>折價券</span>
            <span class="discount">- $2000</span>
          </div>
          <div class="summary-total">
            <span>結帳金額</span>
            <span>${{ totalAmount - 2000 }}</span>
          </div>
          <button @click="goToCheckout" :disabled="isLoading || cartItems.length === 0" class="checkout-btn">
            {{ isLoading ? '處理中...' : '結帳' }}
          </button>
        </div>

        <div class="coupon-card">
          <h3>折價券</h3>
          <p>已使用 1 張折價券 / 折抵 $2000</p>
        </div>
      </div>
    </div>
  </div>
</template>



<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 60px auto;
  padding: 30px;
  background: #fff;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color: #333;
}

.page-title {
  text-align: center;
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 40px;
}

.cart-main {
  display: flex;
  gap: 40px;
}

.cart-list {
  flex: 2;
}

.cart-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.cart-item {
  display: flex;
  align-items: center;
  background: #fafafa;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #eee;
  border-radius: 12px;
}

.item-image {
  width: 100px;
  height: 100px;
  margin-right: 24px;
  border-radius: 12px;
  overflow: hidden;
  background: #f0f0f0;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-name {
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 6px;
}

.product-detail {
  font-size: 16px;
  color: #666;
  margin-bottom: 12px;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-actions button {
  padding: 6px 14px;
  font-size: 16px;
  border: 1px solid #ccc;
  background: transparent;
  color: #333;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.item-actions button:hover {
  background: #eee;
}

.item-actions .delete-btn {
  border-color: #e53935;
  color: #e53935;
}

.item-actions .delete-btn:hover {
  background: #e53935;
  color: #fff;
}

.quantity {
  min-width: 30px;
  text-align: center;
}

.checkout-summary {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-card, .coupon-card {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0,0,0,0.05);
  border: 1px solid #eee;
}

.summary-card h2, .coupon-card h3 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 18px;
  color: #666;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 22px;
  font-weight: bold;
  margin: 20px 0;
  color: #222;
}

.discount {
  color: #e53935;
}

.checkout-btn {
  width: 100%;
  padding: 16px 0;
  background: #ff5722;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 22px;
  cursor: pointer;
  transition: all 0.3s;
}

.checkout-btn:hover {
  background: #e64a19;
  transform: scale(1.02);
  box-shadow: 0 6px 18px rgba(255, 87, 34, 0.4);
}
</style>


