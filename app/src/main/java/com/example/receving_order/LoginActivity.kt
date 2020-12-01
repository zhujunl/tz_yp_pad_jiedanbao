package com.example.receving_order

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.receving_order.net.APi
import com.example.receving_order.net.RQ.loginJieDan
import com.example.receving_order.net.SP
import com.example.receving_order.utils.SoftHideKeyBoardUtil
import com.yp.baselib.annotation.LayoutId
import com.yp.baselib.annotation.Orientation
import com.yp.baselib.base.BaseActivity
import com.yp.baselib.utils.DialogUtils.createCustomViewDialog
import com.yp.baselib.utils.SPUtils
import com.yp.baselib.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*


@LayoutId(R.layout.activity_main)
@Orientation(false)
class LoginActivity :BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoftHideKeyBoardUtil.assistActivity(this)

        val user= SP.getUserid(this)
        val password=SP.getPassword(this)
        if(!user.equals("")&&!password.equals("")){
            goTo<OrderActivity>()
            finish()
        }
    }

    override fun init(bundle: Bundle?) {
        click()
        versionname.text="v "+BuildConfig.VERSION_NAME
    }

    private fun click(){

        login_eye.click {



            if(login_eye.tag==1){
                login_eye.setImageResource(R.drawable.seeing)
                login_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance())
                login_eye.tag=0
            }else{
                login_eye.setImageResource(R.drawable.seeing_no)
                login_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                login_eye.tag=1
            }
        }
        login_btn.click {
            val user=login_user_edit.text.toString().trim()
            val password=login_password_edit.text.toString().trim()
            loginJieDan(this,user,password,{
                if(it.code==200){
                    SP.putUserid(this,user)
                    SP.putPassword(this,password)
                    SP.putShopId(this,it.data.shopId)
                    goTo<BranchActivity>()
                    finish()
                }else{
                    ToastUtils().toast(this,it.message)
                }
            })

        }
        login_set.click {
            val dia:AlertDialog=createCustomViewDialog(this@LoginActivity,R.layout.dialog_ip_set)
            dia.show()
            dia.setCanceledOnTouchOutside(false)
            dia.window!!.findViewById<EditText>(R.id.ip_edt).setText(APi.BASE_HTTP)
            dia.window!!.findViewById<Button>(R.id.ip_set).click {
                APi.BASE_HTTP=dia.window!!.findViewById<EditText>(R.id.ip_edt).text.toString().trim()
                APi.Base=dia.window!!.findViewById<EditText>(R.id.ip_edt).text.toString().trim()
                APi.BASE_HTTP2=dia.window!!.findViewById<EditText>(R.id.ip_edt).text.toString().trim()
                dia.dismiss()
            }
        }
    }




    var pendingCollapseKeyword = false
    var focusedView: View? = null

    // 点击非输入框位置优先隐藏软键盘
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            pendingCollapseKeyword = isShouldHideInput(ev)
            if (pendingCollapseKeyword) focusedView = currentFocus
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (pendingCollapseKeyword) {
                hideInputMethod(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(event: MotionEvent): Boolean {
        val v = currentFocus
        if (v is EditText) {
            val location = intArrayOf(0, 0)
            v!!.getLocationInWindow(location)
            return event.x < location[0] || event.x > location[0] + v.width || event.y < location[1] || event.y > location[1] + v.height
        }
        return false
    }

    // 抬起手指时如果焦点还在原来的EditText则收起键盘
    private fun hideInputMethod(context: Context) {
        val v = currentFocus
        if (v === focusedView) {
            focusedView!!.clearFocus()
            val imm: InputMethodManager = context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                imm.hideSoftInputFromWindow(focusedView!!.windowToken, 0)
            }
        }
    }


}