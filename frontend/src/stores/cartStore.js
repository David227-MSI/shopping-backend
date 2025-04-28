import {defineStore} from 'pinia';
import {ref, computed} from 'vue';
import axios from '@/services/axios';

export const useCartStore = defineStore('cart', () => {
  // 狀態
  const cartItems = ref([]);
  const userId = ref(1001); // 有 userId = 會員，null = 訪客

  // 總金額
  const totalAmount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.subtotal, 0)
  );

  // 加入商品
  async function addItem(productId, quantity) {
    if (userId.value) {
      // 登入會員
      await axios.post('/api/cart', {userId: userId.value, productId, quantity});
      await fetchCart();
    } else {
      // 訪客
      const existingItem = cartItems.value.find(item => item.productId === productId);
      if (existingItem) {
        existingItem.quantity += quantity;
        existingItem.subtotal = existingItem.price * existingItem.quantity;
      } else {
        cartItems.value.push({
          id: Date.now(), // 假ID
          productId,
          productName: '商品名稱',
          price: 100,
          quantity,
          subtotal: 100 * quantity,
        });
      }
    }
  }

  // 更新商品數量
  async function updateQuantity(productId, newQuantity) {
    if (userId.value) {
      await axios.put('/api/cart', {userId: userId.value, productId, quantity: newQuantity});
      await fetchCart();
    } else {
      const item = cartItems.value.find(item => item.productId === productId);
      if (item) {
        item.quantity = newQuantity;
        item.subtotal = item.price * newQuantity;
      }
    }
  }

  // 移除商品
  async function removeItem(productId) {
    if (userId.value) {
      await axios.delete(`/api/cart/${userId.value}/${productId}`);
      await fetchCart();
    } else {
      cartItems.value = cartItems.value.filter(item => item.productId !== productId);
    }
  }

  // 清空購物車
  async function clearCart() {
    if (userId.value) {
      await axios.delete(`/api/cart/clear/${userId.value}`);
      await fetchCart();
    } else {
      cartItems.value = [];
    }
  }

  // 從後端重新抓購物車
  async function fetchCart() {
    if (!userId.value) return;
    const response = await axios.get(`/api/cart/${userId.value}`);
    cartItems.value = response;
  }

  // 登入並合併訪客購物車
  async function loginAndMerge(newUserId) {
    if (cartItems.value.length > 0) {
      for (const item of cartItems.value) {
        await axios.post('/api/cart', {
          userId: newUserId,
          productId: item.productId,
          quantity: item.quantity,
        });
      }
    }
    userId.value = newUserId;
    await fetchCart();
  }

  // 手動設定購物車（載入資料）
  function setCartItems(items) {
    cartItems.value = items;
  }

  return {
    cartItems,
    userId,
    totalAmount,
    addItem,
    removeItem,
    clearCart,
    updateQuantity,
    fetchCart,
    loginAndMerge,
    setCartItems,
  };
}, {
  persist: true,
});
