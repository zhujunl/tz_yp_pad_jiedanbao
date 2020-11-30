package com.example.receving_order.bean

import java.io.Serializable

data class order_list(val code:Int,val `data`:List<Data>,val message:String){
    data class Data(
        val orderNo : String,
        val createTime : String,
        val mealTakingNum : String,
        val mealTime: String,
        val mealTimeEnd : String,
        val reserveType :Int,
        val mealTimeStart : String,
        val customerName : String,
        val seatNumber : String,
        val status : Int
    )
}

data class order_detail(
        val code:Int,
        val `data`:Data,
        val message:String
){
    data class Data(
        val branchName: String,
        val orderNo: String,
        val createTime: String,
        val targetDate: String,
        val mealTime: String,
        val status: Int,
        val totalFee: Int,
        val totalCount: Int,
        val realFee:Int,
        val mealTimeStart :String,
        val mealTimeEnd : String,
        val mealTakingNum: String,
        val packFee:Long ,
        val seatNumber: String,
        val mealType: Int,
        val discount: Int,
        val payType: String,
        val note: String,
        val updateTime: String,
        val memberName: String,
        val phone: String,
        val itemList:List<item_meal>
    ):Serializable{
        data class item_meal(
            val name:String,
            val count:Int,
            val fee : Int
        ):Serializable
    }
}

data class branchlist(val code:Int, val `data`:List<Data>, val message:String){
    data class Data(
        val branchId:Int,
        val branchName: String,
        val shopId:Int
    )
}

data class updataorder(val code:Int,val message:String)

data class mealsection(val code: Int,val message: String,val `data`:List<Data>){
    data class Data(
            val name: String,
            val startTime : String,
            val endTime : String
    )
}

data class getMeal(val code: Int,val message: String,val `data`:Data){
    data class Data(
            val name: String,
            val startTime : String,
            val endTime : String
    )
}

data class getCount(val code :Int,val message: String,val data:Int)