package com.example.receving_order

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.MotionEventCompat
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.receving_order.bean.order_list
import com.example.receving_order.net.RQ
import com.example.receving_order.net.SP
import com.yp.baselib.annotation.FullScreen
import com.yp.baselib.annotation.LayoutId
import com.yp.baselib.annotation.Orientation
import com.yp.baselib.base.BaseActivity
import com.yp.baselib.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_order.*
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@LayoutId(R.layout.activity_order)
@Orientation(false)
@FullScreen
class OrderActivity : BaseActivity(){

    private var currentSelectIndex=0
    private var pos2 :String=""
    private var item_pos=0
    private var count=0
    private var mealtime : String = ""
    private var mstyle= arrayListOf<String>("全部")
    private var order_user_list:MutableList<order_list.Data> = ArrayList()//发生改变用于的列表
    private var cancel_list:MutableList<String> = ArrayList()
    private var count_list:MutableList<order_list.Data> = ArrayList()//不发生改变用于对比长度的列表
    private var last_OrderNO= String()
//    private val mItems = arrayOf("全部", "堂食", "自提")
    private val mrovke= arrayOf("全部","未核销","已核销")
    var scrollflag=false


    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 ->{
                    Log.d("获取详情==",msg.obj as String)
                    getOrderMeal(msg.obj as String)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter=IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//为接收器指定action，使之用于接收同action的广播
        registerReceiver(timeReceiver,intentFilter);//动态注册广播接收器

    }
    val mHandler = Handler()
    var r: Runnable = object : Runnable {
        override fun run() {
            //do something
            //每隔1s循环执行run方法
            auto_refresh(mealtime,pos2)
            mHandler.postDelayed(this, 5000)
        }
    }
    var r2:Runnable=object :Runnable{
        override fun run() {
            if(scrollflag==true){
                auto_cancel(mealtime,pos2)
            }
            mHandler.postDelayed(this, 10000)
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            scrollflag=false
            pendingCollapseKeyword = isShouldHideInput(ev)
            if (pendingCollapseKeyword) focusedView = currentFocus
        } else if (ev.action == MotionEvent.ACTION_UP) {
            scrollflag=true
            if (pendingCollapseKeyword) {
                hideInputMethod(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun init(bundle: Bundle?) {
        mHandler.postDelayed(r, 10000);//延时100毫秒
        mHandler.postDelayed(r2,5000)
//        val adapter= ArrayAdapter<String>(this, R.layout.item_order_spinner, mItems)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        order_spinner.setAdapter(adapter)
        val adapter1= ArrayAdapter<String>(this, R.layout.item_order_spinner, mrovke)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        revoke_spinner.setAdapter(adapter1)
        mealTime()
        click()

        shopname.text= SP.getBranchName(this)

        val currentTime = System.currentTimeMillis()
        val timeNow =
                SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentTime)
        head_time.text=timeNow

    }
    private val timeReceiver: BroadcastReceiver =object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_TIME_TICK) {
                val currentTime = System.currentTimeMillis()
                val timeNow =
                        SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentTime)
                head_time.text=timeNow
            }
        }
    }


    private fun click(){
        order_set.click{
            goTo<SetActivity>()
        }

        search_txt.click {
            RQ.getOrderList(this,search_edt.text.toString().trim(),mealtime,pos2){
                if(it.code==200){
                    if(it.data.size>0) {
                        getOrderMeal(it.data[0].orderNo)
                    }
                    var user:MutableList<order_list.Data> = ArrayList()
                    it.data.forEach {user.add(it) }
                    order_user_list.clear()
                    count_list.clear()
                    if(user.size==0) {
                        order_right.visibility = View.INVISIBLE
                        order_list_refresh.visibility=View.INVISIBLE
                        nonono.visibility=View.VISIBLE
                    }else{
                        order_right.visibility = View.VISIBLE
                        order_list_refresh.visibility=View.VISIBLE
                        nonono.visibility=View.GONE
                        last_OrderNO=user[user.size-1].orderNo
                        order_user_list=user
                        count_list=user
                    }
                    Log.d("sear_order_user_list.size_last===",order_user_list.size.toString())
                    Log.d("sear_last_OrderNO===",last_OrderNO)
                    orderlist_RV(user,true)
                }else{
                    ToastUtils().toast(this,it.message)
                }

            }
        }

//        order_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                currentSelectIndex=0
//                when(position){
//                    0->{
//                        pos=""
//                    }
//                    1->{
//                        pos=position.toString()
//                    }
//                    2->{
//                        pos=position.toString()
//                    }
//                }
//                search_edt.text.clear()
//                getOrderUser(pos,mealtime,pos2)
//                order_user_list.clear()
//            }
//        })

        revoke_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSelectIndex=0
                when(position){
                    0->{
                        pos2=""
                    }
                    1->{
                        pos2="0"
                    }
                    2->{
                        pos2="1"
                    }
                }
                search_edt.text.clear()
                getOrderUser(mealtime,pos2)
                order_user_list.clear()
                count_list.clear()
            }
        })


        /**
         * 下拉刷新
         */
        order_list_refresh.setEnableRefresh(false)
        order_list_refresh.setEnableLoadMore(false)
//        order_list_refresh.setOnRefreshListener(object : OnRefreshListener {
//            override fun onRefresh(refreshlayout: RefreshLayout) {
//                currentSelectIndex=0
//                getOrderUser(pos,mealtime,pos2)
//                order_user_list.clear()
//                refreshlayout.finishRefresh(1500 /*,false*/) //传入false表示刷新失败
//            }
//        })

        /**
         * 上拉加载
         */
//            order_list_refresh.setOnLoadMoreListener(OnLoadMoreListener { refreshlayout ->
//                tishi.visibility=View.GONE
//                Log.d("order_user_list.size===",order_user_list.size.toString())
//                if (order_user_list.size!=0){
//                    last_OrderNO=order_user_list[order_user_list.size-1].orderNo
//                }
//                Log.d("_last_OrderNO===",last_OrderNO)
//                order_list_refresh.setEnableLoadMore(true)
//                RQ.refrestOrderList(this,search_edt.text.toString().trim(), pos, mealtime, pos2, last_OrderNO) {
//                    Log.d("search_edt.text.toString().trim()==",search_edt.text.toString().trim())
//                    it.data.forEach { order_user_list.add(it) }
//                    orderlist_RV(order_user_list)
//                    refreshlayout.finishLoadMore(1000) //传入false表示加载失败
//                    order_list.scrollToPosition(order_user_list.size-1)
//                }
//                // 这个方法最重要，当在最后一页调用完上一个完成加载并标记没有更多数据的方法时，需要将refreshLayout的状态更改为还有更多数据的状态，此时就需要调用此方法，参数为false代表还有更多数据，true代表没有更多数据
//
//            })

//

    }
    var mediaPlayer: MediaPlayer? = null
    /**
     * 根据长度判断是否有新订单
     */
    fun auto_refresh(mealtime:String,pos2:String){
        RQ.getorderNum(this,mealtime,pos2){
            Log.d("长度==",count.toString())
            if(count<it.data.len){
                count=it.data.len
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer!!.setOnPreparedListener { mp -> mp.start() }
                }
                mediaPlayer!!.reset()
                try {
                    mediaPlayer!!.setDataSource(this, Uri.parse("android.resource://" + packageName.toString() + "/" + R.raw.neworder))
                    mediaPlayer!!.prepareAsync()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (order_user_list.size!=0){
                    last_OrderNO=order_user_list[order_user_list.size-1].orderNo
                }
                Log.d("_last_OrderNO===",last_OrderNO)
                RQ.refrestOrderList(this,search_edt.text.toString().trim(),mealtime, pos2, last_OrderNO) {
                    Log.d("search_edt.text.toString().trim()==",search_edt.text.toString().trim())
                    it.data.forEach { order_user_list.add(it) }
                    orderlist_RV(order_user_list,false)
                    order_list.scrollToPosition(order_user_list.size-1)
                }
            }else if (count>it.data.len){
                count=it.data.len
            }
            Log.d("刷新count====",count.toString())

        }
    }

    /**
     * 获取取消的订单号列表
     */
    fun auto_cancel(mealtime:String,pos2:String){
        cancel_list.clear()
        RQ.getorderNum(this,mealtime,pos2){
            if(it.data.cancelList.size>0){
                for (i in 0 until it.data.cancelList.size){
                    cancel_list.add(it.data.cancelList[i])
                }
                if (order_user_list.size!=0){
                    last_OrderNO=order_user_list[order_user_list.size-1].orderNo
                }
                Log.d("取消_last_OrderNO===",last_OrderNO)
                RQ.refrestOrderList(this,search_edt.text.toString().trim(),mealtime, pos2, last_OrderNO) {
                    it.data.forEach { order_user_list.add(it) }
                    it.data.forEach { count_list.add(it) }
                    Log.d("取消中order_user_list=======",order_user_list.size.toString())
                    for (i in cancel_list){
                        for (li in 0 until order_user_list.size){
                            if(li<order_user_list.size){
                                if(i.equals(order_user_list[li].orderNo)){
                                    order_user_list.remove(order_user_list[li])
                                }
                            }
                        }
                    }
                    Log.d("count中取消的长度===",order_user_list.size.toString())
                    orderlist_RV(order_user_list,false)
                    if (currentSelectIndex>2){
                        order_list.scrollToPosition(currentSelectIndex-2)
                    }
                }
            }
        }
    }
    fun mealTime(){
        var p : Int=0
        val style_adapter2=ArrayAdapter<String>(this,R.layout.item_orderstyle_spinner,mstyle)
        style_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        order_style.adapter=style_adapter2
        RQ.getMealSectionList(this){
            if(it.code==200){
                for (i in it.data){
                    mstyle.add(i.name)
                }
                val style_adapter=ArrayAdapter<String>(this,R.layout.item_orderstyle_spinner,mstyle)
                style_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                order_style.adapter=style_adapter
                RQ.getMealNow(this){
                    if (it.data.name!=null){
                        val meal=it.data
                        for (i in 0 until mstyle.size){
                            if(meal.name.equals(mstyle[i])){
                                p=i
                            }
                        }
                    }
                    order_style.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            currentSelectIndex=0
                            if(position!=0){
                                mealtime=mstyle[position]
                            }else{
                                mealtime=""
                            }
                            getOrderUser(mealtime,pos2)
                            search_edt.text.clear()
                            order_user_list.clear()
                            count_list.clear()
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    })
                    order_style.setSelection(p,true)
                }
            }
        }
    }

    /**
     * 获得订单用户信息
     */
    fun getOrderUser(mealtime:String,pos2:String){
        System.out.println("meal=="+mealtime+"pos2=="+pos2)
        var user:MutableList<order_list.Data> = ArrayList()
        RQ.getOrderList(this,mealtime,pos2){
            if(it.code==200){
                Log.d("it.data====",it.data.toString())
                it.data.forEach {
                    Log.d("it====",it.toString())
                    user.add(it)
                }
                Log.d("user===",user.toString())
                if(user.size>0) {
                    getOrderMeal(user.get(0).orderNo)

//                Thread(Runnable {
//                    val msg: Message = Message.obtain()
//                    msg.what = 1
//                    msg.obj = user.get(0).orderNo
//                    mHandler.sendMessage(msg)
//                }).start()
                    val msg: Message = Message.obtain()
                    msg.what = 1
                    msg.obj = user.get(0).orderNo
                    mHandler.sendMessage(msg)

                    last_OrderNO=user[user.size-1].orderNo
                    order_user_list=user
                    count_list=user
                }
                RQ.getorderNum(this,mealtime,pos2){
                    count=it.data.len
                    Log.d("获得订单用户信息count===",count.toString())
                    orderlist_RV(user,true)
                }

                if(user.size==0) {
                    order_right.visibility = View.INVISIBLE
                    nonono.visibility=View.VISIBLE
                }else{
                    order_right.visibility = View.VISIBLE
                    nonono.visibility=View.GONE


                }
            }
        }
    }
    fun orderlist_RV(user:List<order_list.Data>,scroll:Boolean){
        item_pos=0
        var mealList:MutableList<String> = ArrayList()
        Log.d("orderlist_RV===",user.toString())
        Log.d("orderlist_RV.size===",user.size.toString())
        Log.d("meal====",mealtime)
        order_list.wrap.rvMultiAdapter(
                user,
                {h,p->
                    if(mealtime.equals("")){
                        h.tv(R.id.mealname).visibility=View.VISIBLE
                        h.tv(R.id.mealname).text=user[p].mealTime
                    }else{
                        if(p==0){
                            h.tv(R.id.mealname).visibility=View.VISIBLE
                            h.tv(R.id.mealname).text=user[p].mealTime
                        }else{
                            h.tv(R.id.mealname).visibility=View.GONE
                        }
                    }
                    h.tv(R.id.item_order_user).text=user[p].customerName
                    h.tv(R.id.item_order_time).text=switchCreateTime(user[p].createTime)
                    if(user[p].reserveType==1){
                        h.tv(R.id.item_order_user_num_txt).text="座位号:"
                        h.tv(R.id.item_order_user_num).text=user[p].seatNumber
                    }else if(user[p].reserveType==2){
                        h.tv(R.id.item_order_user_num_txt).text="取餐号:"
                        h.tv(R.id.item_order_user_num).text=user[p].mealTakingNum
                    }
//                    h.tv(R.id.item_order_type_btn).text="堂食"
                    if(user[p].status==10||user[p].status==11){
                        h.tv(R.id.item_order_cancle).tag=1
                        h.tv(R.id.item_order_cancle).text="已核销"
                        h.tv(R.id.item_order_cancle).setTextColor(resources.getColor(R.color.cancled))
                    }else{
                        h.tv(R.id.item_order_cancle).tag=0
                        h.tv(R.id.item_order_cancle).text="未核销"
                        h.tv(R.id.item_order_cancle).setTextColor(resources.getColor(R.color.red))
                    }
                    h.tv(R.id.item_user).click {
                        currentSelectIndex=p
                        item_pos=p
                        Log.d("点击的orderNo===",user[p].orderNo)
                        getOrderMeal(user[p].orderNo)

                        val msg: Message = Message.obtain()
                        msg.what = 1
                        msg.obj = user[p].orderNo
                        mHandler.sendMessage(msg)

                        order_list.update()
                        mealList.clear()
                    }
                    if(h.tv(R.id.item_order_cancle).tag==0){
                        h.tv(R.id.item_order_cancle).text="未核销"
                        h.tv(R.id.item_order_cancle).setTextColor(resources.getColor(R.color.red))
                    }else{
                        h.tv(R.id.item_order_cancle).tag=1
                        h.tv(R.id.item_order_cancle).setTextColor(resources.getColor(R.color.cancled))
                        h.tv(R.id.item_order_cancle).text="已核销"
                    }
                },
                { p->if(p==currentSelectIndex) 1 else 0 },
                R.layout.item_order_user_info,R.layout.item_order_user_info_checked
        )
        if (scroll==true) {order_list.scrollToPosition(currentSelectIndex)}
    }


    /**
     * 获得订单详情
     */
    private fun getOrderMeal(orderNo:String) {

        RQ.getOrderDetail(this,orderNo){
            Log.d("it.data<<<<<",it.data.toString())
            if(it.code==200){
                if(it.data.mealType==2){
                    Log.d("取餐号===",it.data.mealTakingNum)
                    meal_num_txt.text="取餐号："
                    meal_num.text=it.data.mealTakingNum
                    packLin.visibility=View.VISIBLE
                    order_pack.text="￥"+fenToYuan(it.data.packFee.toString())
                }else if (it.data.mealType==1){
                    Log.d("座位号===",it.data.seatNumber)
                    meal_num_txt.text="座位号："
                    meal_num.text=it.data.seatNumber
                    packLin.visibility=View.GONE
                }
                packLin.visibility=View.GONE
                order_num.text=it.data.orderNo
                order_time.text=switchCreateTime(it.data.createTime)
                if(it.data.targetDate!=null) have_date.text=switchCreateTime2(it.data.targetDate)
                have_cate.text=it.data.mealTime
                if(it.data.mealTimeStart!=null&&it.data.mealTimeEnd!=null)
                    have_time.text=switchCreateTime3(it.data.mealTimeStart)+" — "+switchCreateTime3(it.data.mealTimeEnd)
//                when(it.data.mealType){
//                    1 -> meal_style.text="堂食"
//                    2 -> meal_style.text="自提"
//                    else -> meal_style.text="外卖"
//                }
                if(it.data.status==10){
                    revoke_time.text=switchCreateTime(it.data.updateTime)
                    revoke_btn.text="已核销"
                    revoke_btn.setBackgroundResource(R.drawable.order_revoked_btn)
                    revoke_btn.isEnabled=false
                    revoke_time.visibility= View.VISIBLE
                    revoke.visibility=View.VISIBLE
                }else{
                    revoke_btn.text="核销"
                    revoke_btn.setBackgroundResource(R.drawable.order_revoke_btn)
                    revoke_btn.isEnabled=true
                    revoke_btn.click {
                        RQ.updataOrder(this,10,orderNo){
                            if(it.code==200){
                                Log.d("${orderNo}订单状态更新",it.toString())
                                //getOrderUser(pos,mealtime,pos2)
                                RQ.getOrderList(this,mealtime,pos2){
                                    orderlist_RV(it.data,false)
                                }
                                revoke_btn.text="已核销"
                                revoke_btn.setBackgroundResource(R.drawable.order_revoked_btn)
                                revoke_btn.isEnabled=false
                                revoke_time.visibility= View.VISIBLE
                                revoke.visibility=View.VISIBLE
                            }else{
                                ToastUtils().toast(this,it.message,true,Gravity.CENTER,200)
                            }
                        }
                    }

                    revoke_time.visibility= View.GONE
                    revoke.visibility=View.GONE
                }


                pay.text=it.data.payType
                order_user.text=it.data.memberName
                order_phone.text=it.data.phone
                if(it.data.itemList==null){

                }else{
                    order_info_RV.wrap.rvMultiAdapter(it.data.itemList,{
                        h,p->
                        h.tv(R.id.item_order_name).text=it.data.itemList[p].name
                        h.tv(R.id.item_order_num).text="X"+it.data.itemList[p].count
                        h.tv(R.id.item_order_price).text="￥"+fenToYuan(it.data.itemList[p].fee.toString())
                    },{
                        0
                    },R.layout.item_order_meal_info)
                }

                order_remarks.text=it.data.note
                order_discount.text="￥"+fenToYuan(it.data.discount.toString())
                order_total.text="￥"+fenToYuan(it.data.totalFee.toString())
            }

        }
    }


    /**
     * 时间格式转换
     */
    fun switchCreateTime(createTime: String?): String? {
        var formatStr2: String? = null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") //注意格式化的表达式
        try {
            val time = format.parse(createTime)
            val date = time.toString()
            //将西方形式的日期字符串转换成java.util.Date对象
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            val datetime = sdf.parse(date) as Date
            //再转换成自己想要显示的格式
            formatStr2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatStr2
    }

    fun switchCreateTime2(createTime: String?): String? {
        var formatStr2: String? = null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") //注意格式化的表达式
        try {
            val time = format.parse(createTime)
            val date = time.toString()
            //将西方形式的日期字符串转换成java.util.Date对象
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            val datetime = sdf.parse(date) as Date
            //再转换成自己想要显示的格式
            formatStr2 = SimpleDateFormat("yyyy-MM-dd").format(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatStr2
    }

    fun switchCreateTime3(createTime: String?): String? {
        var formatStr2: String? = null
        val format = SimpleDateFormat("HH:mm:ss") //注意格式化的表达式
        try {
            val time = format.parse(createTime)
            val date = time.toString()
            //将西方形式的日期字符串转换成java.util.Date对象
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            val datetime = sdf.parse(date) as Date
            //再转换成自己想要显示的格式
            formatStr2 = SimpleDateFormat("HH:mm").format(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatStr2
    }

    /**
     * 分转元
     */
    private fun fenToYuan(amount: String): String? {
        var amount: String? = amount
        val format: NumberFormat = NumberFormat.getInstance()
        try {
            val number: Number = format.parse(amount)
            val temp: Double = number.toDouble()/100.0
            format.setGroupingUsed(false)
            // 设置返回的小数部分所允许的最大位数
            format.setMaximumFractionDigits(2)
            amount = format.format(temp)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return amount
    }

    var pendingCollapseKeyword = false
    var focusedView: View? = null

    // 点击非输入框位置优先隐藏软键盘


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

    /*
     * 将时间转换为时间戳
     */
    @Throws(ParseException::class)
    fun dateToStamp(s: String?): Long {
        val res: Long
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val date = simpleDateFormat.parse(s)
        val ts = date.time
        res = ts
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
    }
}