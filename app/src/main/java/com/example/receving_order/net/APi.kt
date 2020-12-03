package com.example.receving_order.net

object APi {
     var BASE_HTTP="https://tzapi.yunpengai.com"
    var BASE_HTTP2="https://tzapi.yunpengai.com"
    var Base="https://tzapi.yunpengai.com"
    var CESHI="http://192.168.10.48:9096"


    val ORDER_LIST="$BASE_HTTP/api/wxapp/tz/pad/order/list"

    val ORDER_DETAIL="$BASE_HTTP/api/wxapp/tz/pad/order/detail"

    val UPDATA_ORDER="$BASE_HTTP/api/wxapp/tz/order/modifyMealStatus"

    val MEAL_SECTION="$BASE_HTTP/api/wxapp/mealHourConfig/list"

    val GETMEAL="$BASE_HTTP/api/wxapp/mealHourConfig/getNow"

    val COUNT="$BASE_HTTP/api/wxapp/tz/pad/order/list/count"
}