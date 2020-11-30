package com.yp.baselib.base

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager
import android.widget.Toast
import com.githang.statusbar.StatusBarCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlinlib.view.base.ViewEx
import com.kotlinlib.view.textview.TextViewEx
import com.yp.baselib.*
import com.yp.baselib.annotation.*
import com.yp.baselib.ex.BaseEx
import com.yp.baselib.ex.ContextEx
import com.yp.baselib.utils.DensityUtils
import me.yokeyword.fragmentation.SupportActivity
import org.greenrobot.eventbus.EventBus


/**
 * 最基类Activity
 */
abstract class BaseActivity : SupportActivity(), BaseEx {

    val ACTIVITY_NAME = "ac_name"
    var startEventBus = false//是否启用EventBus
    var viewInject: LayoutId? = null//布局ID注解
    var colorInject: StatusBarColor?=null//状态栏颜色注解
    var orientationInject: Orientation?=null//是否是竖直方向
    var notFitSystemWindowInject: NotFitSystemWindow?=null
    var noReqOrientation: NoReqOrientation? = null
    var notFitSystemWindow = false
    var isRegistered = false//是否注册网络状态监听广播接收器
    var dont_reqest_orientation = false
    var immersion = false
    var topBarId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        //初始化注解
        initAnnotation()

        //设置屏幕方向
        if (!dont_reqest_orientation) {
            try{
                requestedOrientation = if (orientationInject == null) {
                    //默认是竖直方向
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    if (orientationInject?.isVertical!!)
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    else
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }catch (e:IllegalStateException){

            }
        }

        //加载布局
        if(viewInject!=null) {
            setContentView(viewInject!!.id)
        }

        if(colorInject!=null){
            StatusBarCompat.setStatusBarColor(this, Color.parseColor(colorInject?.color))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, Color.WHITE)
        }
        beforeInit()
        init(savedInstanceState)

        if(startEventBus){
            EventBus.getDefault().register(this)
        }

        actList.add(this)
    }


    private fun initAnnotation() {
        val annotations = this::class.annotations
        annotations.forEachIndexed { _, it->
            when(it.annotationClass){
                LayoutId::class->{
                    viewInject = it as LayoutId
                }
                StatusBarColor::class->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) colorInject = it as StatusBarColor
                }
                Orientation::class->{
                    orientationInject = it as Orientation
                }
                Bus::class->{
                    startEventBus = true
                }
                StatusBarBlackText::class->{
                    setStatusBarTextBlack(true)
                }
                FullScreen::class->{
                    fullscreen(true)
                }
                NotFitSystemWindow::class->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) notFitSystemWindow = true
                }
                NoReqOrientation::class -> {
                    dont_reqest_orientation = true
                }
                Immersion::class -> {
                    immersion = true
                    topBarId = (it as Immersion).topBarId
                }
            }
        }
    }

    protected open fun beforeInit() {}

    override fun onResume() {
        super.onResume()
        currAct = javaClass.simpleName
    }

    override fun onDestroy() {
        Log.d("ActivityName", "移除了 "+javaClass.simpleName)
        actList.remove(this)
        if(startEventBus){
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    protected abstract fun init(bundle: Bundle?)

    companion object {
        var gson = Gson()//所有的Activity共享一个gson
        var actList = ArrayList<BaseActivity>()//用于储存所有的Activity实例
        var currAct:String = ""

        /**
         * 判断在Activity栈中是否包含某个Activity，传入简名即可
         */
        fun containActivity(simpleName:String) : Boolean{
            return actList.find { it.javaClass.simpleName == simpleName } != null
        }

        /**
         * 根据Activity的SimpleName来关闭它
         * @param actName String
         */
        fun finishActivityByName(actName:String){
            for(activity in actList){
                if(activity.javaClass.simpleName==actName){
                    activity.finish()
                    //return
                }
            }
        }

        /**
         *  根据1~N个Activity的SimpleName来关闭它（们）
         * @param actName Array<out String>
         */
        fun finishActivityByName(vararg actName: String) {
            for (activity in actList) {
                if (activity.javaClass.simpleName in actName) {
                    activity.finish()
                }
            }
        }

        /**
         * 关闭所有的Activity
         */
        fun finishAllActivities(){
            Log.d("ActivityName", "size is "+ actList.size)
            for(activity in actList){
                Log.d("ActivityName", activity.javaClass.simpleName)
                activity.finish()
            }
        }

        /**
         * 关闭所有的Activity除了指定的Activity
         */
        fun finishAllActivitiesExcept(vararg actName: String) {
            val list = actName.toMutableList()
            for (activity in actList) {
                if (!list.contains(activity.javaClass.simpleName)) {
                    activity.finish()
                }
            }
        }

        /**
         * 关闭栈中最上一层Activity
         */
        fun finishLast(){
            actList.last().finish()
        }

        /**
         * 根据类名来在栈中查找Activity，可能为空，可替代EventBus来直接调用要查找的Activity的方法
         */
        fun <T: BaseActivity> findActivity(simpleName:String): T? {
            return actList.find { it.javaClass.simpleName == simpleName } as T
        }

    }

    /**
     * 设置状态栏颜色为黑色，仅对6.0以上版本有效
     * @param isDark Boolean
     */
    fun setStatusBarTextBlack(isDark:Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val decor = window.decorView
            if(isDark){
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)//设置绘画模式
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)//设置半透明模式
                }
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }else{
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)//清除绘画模式
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)//清除半透明模式
                }
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    /**
     * 隐藏或显示状态栏
     * @param enable Boolean
     */
    protected fun fullscreen(enable: Boolean) {
        if (enable) { //显示状态栏
            val lp = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else { //隐藏状态栏
            val lp = window.attributes
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = lp
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 方便在Activity中弹出Toast
     */
    fun Any.toast(isLong: Boolean=false){
        Toast.makeText(this@BaseActivity, "$this",
            if(!isLong)Toast.LENGTH_SHORT else Toast.LENGTH_LONG).apply {
            setGravity(Gravity.CENTER, 0, 0)
        }.show()
    }

    /**
     * 方便在Activity中直接使用相关单位的数字
     */
    fun Number.px2dp():Int{
        return DensityUtils.px2dip(this@BaseActivity, this.toFloat())
    }

    fun Number.dp2px():Int{
        return DensityUtils.dip2px(this@BaseActivity, this.toFloat())
    }

    val Number.dp get() = DensityUtils.dip2px(this@BaseActivity, this.toFloat())

    fun Number.px2sp():Int{
        return DensityUtils.px2sp(this@BaseActivity, this.toFloat())
    }

    fun Number.sp2px():Int{
        return DensityUtils.sp2px(this@BaseActivity, this.toFloat())
    }

    /**
     * 设置窗口变灰
     * @param alpha Float
     */
    fun setWindowAlpha(alpha:Float=0.4f){
        val attr = window.attributes
        attr.alpha = alpha
        window.attributes = attr
    }

    /**
     * 将JSON字符串转换为JSON对象数组
     * @param jsonStr String
     * @return List<T>
     */
    fun <T> strToJsonList(jsonStr:String): List<T> {
        return gson.fromJson(jsonStr, object : TypeToken<List<T>>(){}.type) as List<T>
    }

    /**
     * 启动Activity
     */
    inline fun <reified T : Activity> goTo() {
        startActivity( Intent(this, T::class.java))
    }

    /**
     * 启动Activity，可带参数和跳转动画
     */
    inline fun <reified T: Activity> goTo(vararg pairs:Pair<String,Any>, anims:Pair<Int, Int> = 0 to 0){
        val intent = Intent(this, T::class.java)
        pairs.forEach {
            when(it.second){
                is String -> {
                    intent.putExtra(it.first,it.second.toString())
                }
                is Int -> {
                    intent.putExtra(it.first,it.second as Int)
                }
                is Boolean -> {
                    intent.putExtra(it.first,it.second as Boolean)
                }
                is Double -> {
                    intent.putExtra(it.first,it.second as Double)
                }
                is java.io.Serializable -> intent.putExtra(it.first,it.second as java.io.Serializable)
                is Parcelable -> intent.putExtra(it.first,it.second as Parcelable)
            }
            Log.d("T_BUNDLE", ("${it.first} ${it.second}"))
        }
        startActivity(intent)
        if(anims != 0 to 0) overridePendingTransition(anims.first, anims.second)
    }

    /**
     * 全屏并且隐藏状态栏
     */
    fun hideStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }

    /**
     * 全屏并且状态栏透明显示
     */
    fun showStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
    }

    /**
     * 设置底部导航栏是是否显示
     */
    fun setNavigationBar(visible:Boolean){
        if (!visible){
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_HIDE_NAVIGATION
        } else {
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    fun inflate(id:Int): View {
        return LayoutInflater.from(this).inflate(id, null)
    }

}