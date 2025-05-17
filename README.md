# 🛍 E-commerce Backend - Final Project

This is the backend service for our full-stack e-commerce final project, developed as part of a Java Software Engineer Training Program (534 hours).  
It is built using **Spring Boot**, **Hibernate**, and **SQL Server**, and exposes RESTful APIs to support order flow, shopping cart, coupon logic, and payment integration via ECPay.

---

## 🔧 Tech Stack

- Java 17
- Spring Boot 3
- Hibernate (JPA)
- SQL Server
- Spring MVC / RESTful API
- JWT Authentication
- ECPay (credit card payment)
- Ngrok (for payment callback testing)

---

## 📦 Main Features

### 🛒 Shopping Cart
- Add, update, and remove cart items
- Store per-user cart items in DB
- Cart merging after login (guest + member)

### 🧾 Order System
- Create orders from selected cart items
- Support for coupon application (fixed or percentage)
- Retrieve order history and details
- Enum-based order/payment status (mapped to display text)

### 💳 ECPay Payment Integration
- Credit card flow with sandbox test
- Handle frontend redirection and server-side callback
- Update order status via callback notification

### 🔐 Authentication
- Login via JWT token (user ID stored in token)
- Authorization required for protected endpoints

---

## 👤 My Responsibilities

I was responsible for designing and implementing the **core backend modules**, including:

- ✅ Shopping cart logic and API
- ✅ Order creation & retrieval API
- ✅ Coupon application handling
- ✅ Payment integration with ECPay
- ✅ Error handling and response DTO design
- ✅ Collaborated with Vue frontend via RESTful endpoints

---

## 🧪 API Testing

All features were tested via Postman before integration.  
Ngrok was used to simulate payment callback from ECPay during development.

---

## 🚀 How to Run

1. Clone this repo
2. Set up your `application.yml` (see `sample-application.yml`)
3. Run the project via IntelliJ IDEA or any preferred Spring Boot runner

---

## 📝 Project Context

This project was developed as part of the **Java Cross-domain Software Engineer Training Program**, completed in May 2025.  
Total training hours: 534 hrs  
Covered topics: Java, Spring Boot, SQL Server, Hibernate, RESTful APIs, Vue, Git, Azure

