package com.example.receving_order.net

import android.content.Context
import com.yp.baselib.utils.SPUtils

object SP {
    /**
     * 商户ID
     */
    private const val SHOP_ID = "shopId"

    /**
     * 分店ID
     */
    private const val BRANCH_ID = "branchId"
    private const val employeeId ="employeeId"
    /**
     * 商户名
     */
    private const val SHOPNAME="shopname"

    /**
     * 分店名
     */
    private const val BRANCHNAME="branchname"

    /**
     * 用户名
     */
    private const val USER_ID="userid"
    /**
     * 密码
     */
    private const val PASSWORD="password"

    fun putShopId(context: Context,ShopId:Int){
        SPUtils.put(context, SHOP_ID,ShopId)
    }
    fun getShopId(context: Context):Int{
        return SPUtils.get(context, SHOP_ID,-1).toString().toInt()
    }

    fun putBranchId(context: Context,BranchId:Int){
        SPUtils.put(context, BRANCH_ID,BranchId)
    }
    fun getBranchId(context: Context):Int{
        return SPUtils.get(context, BRANCH_ID,-1).toString().toInt()
    }

    fun putShopName(context: Context,ShopName:String){
        SPUtils.put(context, SHOPNAME,ShopName)
    }
    fun getShopName(context: Context):String{
        return SPUtils.get(context, SHOPNAME,"-1").toString()
    }

    fun putBranchName(context: Context,BranchName:String){
        SPUtils.put(context, BRANCHNAME,BranchName)
    }
    fun getBranchName(context: Context):String{
        return SPUtils.get(context, BRANCHNAME,"").toString()
    }

    fun putUserid(context: Context,userid:String){
        SPUtils.put(context, USER_ID,userid)
    }
    fun getUserid(context: Context):String{
        return SPUtils.get(context, USER_ID,"").toString()
    }

    fun putPassword(context: Context,password:String){
        SPUtils.put(context, PASSWORD,password)
    }
    fun getPassword(context: Context):String{
        return SPUtils.get(context, PASSWORD,"").toString()
    }
}