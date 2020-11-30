package com.example.receving_order

import android.os.Bundle
import android.util.Log
import com.example.receving_order.bean.branchlist
import com.example.receving_order.bean.order_list
import com.example.receving_order.net.RQ.getBranchlist
import com.example.receving_order.net.SP
import com.yp.baselib.annotation.FullScreen
import com.yp.baselib.annotation.LayoutId
import com.yp.baselib.annotation.Orientation
import com.yp.baselib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_branch.*
import java.util.ArrayList

@LayoutId(R.layout.activity_branch)
@Orientation(false)
@FullScreen
class BranchActivity : BaseActivity() {

    override fun init(bundle: Bundle?) {
Log.d("SP.getShopId(this)===",SP.getShopId(this).toString())
        getBranchlist(this,SP.getShopId(this)){
            if(it.code==200){
                Log.d("branchlist====",it.data.toString())
                var user:MutableList<branchlist.Data> = ArrayList()
                it.data.forEach {
                    user.add(it)
                }
                branch_RV.wrap.rvMultiAdapter(
                        user,{h,p->
                    h.tv(R.id.branch_name).text=user[p].branchName
                    h.tv(R.id.branch_name).click{
                        SP.putBranchId(this,user[p].branchId)
                        SP.putBranchName(this,user[p].branchName)
                        goTo<OrderActivity>()
                        finish()
                    }
                },{0},R.layout.item_branch
                )
            }
        }

    }

}