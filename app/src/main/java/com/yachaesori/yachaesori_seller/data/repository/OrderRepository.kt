package com.yachaesori.yachaesori_seller.data.repository


import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yachaesori.yachaesori_seller.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val ordersRef = FirebaseDatabase.getInstance().getReference("orders")
    private val userRef = Firebase.database.getReference("users")


    // 특정 주문 가져오기
//    fun getOrderByOrderUid(orderUid: Long, callback: (Task<DataSnapshot>) -> Unit) {
//        ordersRef.orderByChild("orderUid").equalTo(orderUid.toDouble()).get()
//            .addOnCompleteListener(callback)
//    }

    // 특정 판매자에게 들어온 모든 주문 가져오기
    suspend fun getOrderList(): List<Order> {
        return try {
            val dataSnapshot = ordersRef.get().await()

            if (dataSnapshot.exists()) {
                val orderList: MutableList<Order> = mutableListOf()

                for (orderSnapshot in dataSnapshot.children) {
                    orderSnapshot.getValue(Order::class.java)?.let {
                        it.orderId = orderSnapshot.key
                        orderList.add(it)
                    }
                }

                orderList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // 예외 처리
            throw OrderFetchException("Error fetching all orders", e)
        }
    }

    // 주문 수정
    fun modifyOrder(order: Order, callback: (Task<DataSnapshot>) -> Unit) {
        ordersRef.orderByChild("orderUid").equalTo(order.orderId).get()
            .addOnCompleteListener {
                for (dataSnapshot in it.result.children) {
                    // 판매자용 앱에서는 일단 주문 상태만 변경
                    dataSnapshot.ref.child("status").setValue(order.status)
                }
            }
    }

}

class OrderFetchException(message: String, cause: Throwable? = null) : Exception(message, cause)

