package com.example.receving_order

import android.os.Bundle
import com.example.receving_order.net.SP
import com.yp.baselib.annotation.FullScreen
import com.yp.baselib.annotation.LayoutId
import com.yp.baselib.annotation.Orientation
import com.yp.baselib.base.BaseActivity
import com.yp.baselib.utils.SPUtils
import kotlinx.android.synthetic.main.activity_set.*


@LayoutId(R.layout.activity_set)
@FullScreen
@Orientation(false)
class SetActivity : BaseActivity() {
    override fun init(bundle: Bundle?) {
        set_back.click{
            finish()
        }
        set_out.click{
            SP.putUserid(this,"")
            SP.putPassword(this,"")
            goTo<LoginActivity>()
            finish()
        }
    }

}