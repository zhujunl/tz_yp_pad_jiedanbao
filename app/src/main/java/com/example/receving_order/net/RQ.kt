package com.example.receving_order.net

import android.content.Context
import android.widget.Toast
import com.example.receving_order.bean.*
import com.example.receving_order.request.InitRequest
import com.example.receving_order.response.BranchVO
import com.example.receving_order.response.InitResponse
import com.example.receving_order.utils.Jc_Utils
import com.yp.baselib.utils.SPUtils
import com.yp.baselib.utils.ToastUtils

object RQ {
    /**https://www.baidu.com/
      * 订单列表
      */
    fun getOrderList(context:Context,mealHourName:String,finish:String,sucess:(order_list)-> Unit){
        OKhttp.post<order_list>(APi.ORDER_LIST,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(context).toString(),
                "branchId" to SP.getBranchId(context).toString(),
                "mealHourName" to mealHourName,
                "finish" to finish)
    }
    /**
     *   按会员名称/座位号/取餐号 搜索订单
     */
    fun getOrderList(context:Context,mixParam:String,mealHourName:String,finish:String,sucess:(order_list)-> Unit){
        OKhttp.post<order_list>(APi.ORDER_LIST,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(context).toString(),
                "branchId" to SP.getBranchId(context).toString(),
                "mixParam" to mixParam,
                "mealHourName" to mealHourName,
                "finish" to finish)
    }

    /**
     * 获取订单数量
     */
    fun getorderNum(context:Context,mealtime:String,pos2:String,sucess:(getCount)-> Unit){
        OKhttp.post<getCount>(APi.COUNT,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(context).toString(),
                "branchId" to SP.getBranchId(context).toString(),
                "mealHourName" to mealtime,
                "finish" to pos2)
    }
    /**
     * 加载新订单
     */
    fun refrestOrderList(context:Context,mixParam:String,mealHourName:String,finish:String,lastOrderNo:String,sucess:(order_list)-> Unit){
        OKhttp.post<order_list>(APi.ORDER_LIST,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(context).toString(),
                "branchId" to SP.getBranchId(context).toString(),
                "mixParam" to mixParam,
                "lastOrderNo" to lastOrderNo,
                "mealHourName" to mealHourName,
                "finish" to finish)
    }

    /**
    *   订单详情
    */
    fun getOrderDetail(context:Context,orderNo:String,sucess:(order_detail)-> Unit){
        OKhttp.post<order_detail>(APi.ORDER_DETAIL,{
            sucess.invoke(it)
        },"orderNo" to orderNo,
                "shopId" to SP.getShopId(context).toString())
    }

    /**
     * 更新订单状态
     */

    fun updataOrder(context: Context,status:Int,orderNo: String,sucess: (updataorder) -> Unit){
        OKhttp.post<updataorder>(APi.UPDATA_ORDER,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(context).toString(),
                "orderNo" to orderNo,
                "status" to status.toString())
    }

    /**
     * 接单模块 - 登录
     */
    fun loginJieDan(ctx: Context, username: String, password: String, callback1: (InitResponse) -> Unit) {

        val loginRequest = InitRequest()
        loginRequest.deviceId = Jc_Utils.getMacFromHardware(ctx)
        loginRequest.username = username
        loginRequest.password = password
        OK.post<InitResponse>(APi.BASE_HTTP2+"/zjypg/shangmishouchiInit", loginRequest){
            response ->
            if (response.code == 200) {
                JieDanConstant.shopId = response.data.shopId
                JieDanConstant.shopName = response.data.shopName
                JieDanConstant.employeeId = response.data.employeeId
                callback1.invoke(response)
            } else {
                Toast.makeText(ctx,"登录失败", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 获取分店列表
     */
    fun getBranchlist(ctx:Context,shopid:Int,sucess:(branchlist) -> Unit){
        OKhttp.get<branchlist>(
                APi.Base+"/api/branch/list/"+shopid,{
                sucess.invoke(it)
            }
        )
    }

    /**
     * 获取餐段列表
     */
    fun getMealSectionList(ctx: Context,sucess: (mealsection) -> Unit){
        OKhttp.post<mealsection>(
            APi.MEAL_SECTION,{
            sucess.invoke(it)
            },"shopId" to SP.getShopId(ctx).toString(),
                "branchId" to SP.getBranchId(ctx).toString()
        )
    }

    /**
     * 获取当前餐段
     */
    fun getMealNow(ctx: Context,sucess: (getMeal) -> Unit){
        OKhttp.post<getMeal>(
                APi.GETMEAL,{
            sucess.invoke(it)
        },"shopId" to SP.getShopId(ctx).toString(),
                "branchId" to SP.getBranchId(ctx).toString()
        )
    }

}