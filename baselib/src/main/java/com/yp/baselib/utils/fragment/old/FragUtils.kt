package com.yp.baselib.utils.fragment.old

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import java.util.*


/**
 * Created by asus on 2016/7/20.
 * Activity托管Fragment的便捷工具类
 */
class FragUtils<T : Fragment> {

    private var manager: FragmentManager? = null
    var fragments = ArrayList<Fragment>()
    private var contentId: Int = 0

    constructor(a: FragmentActivity, fragment: T, contentId: Int) {
        manager = a.supportFragmentManager
        this.contentId = contentId
        val transaction = manager!!.beginTransaction()
        fragments.add(fragment)
        transaction.replace(contentId, fragment)
        transaction.commit()
    }

    constructor(a: FragmentActivity, list: ArrayList<T>, contentId: Int) {
        manager = a.supportFragmentManager
        this.contentId = contentId
        val transaction = manager!!.beginTransaction()
        fragments.addAll(list)
        transaction.replace(contentId, list[0])
        transaction.commit()
    }

    fun remove(fragment: Fragment) {
        val transaction = manager!!.beginTransaction()
        fragments.remove(fragment)
        transaction.remove(fragment)
        transaction.commit()
    }

    /**
     * 加载、显示、隐藏Fragment的便捷方法
     * @param targetFragment 需要显示的Fragment
     */
    fun switch(targetFragment: Fragment): Boolean {
        fragments.remove(targetFragment)
        val transaction = manager!!.beginTransaction()
        if (!targetFragment.isAdded) {    // 先判断是否被add过
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.add(contentId, targetFragment).commit() // 隐藏当前的fragment，add下一个到Activity中
        } else {
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.show(targetFragment).commit() // 隐藏当前的fragment，显示下一个
        }
        fragments.add(targetFragment)
        return true
    }

    fun switch(index: Int): Boolean {
        val targetFragment = fragments[index]
        fragments.remove(targetFragment)
        val transaction = manager!!.beginTransaction()
        if (!targetFragment.isAdded) {    // 先判断是否被add过
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.add(contentId, targetFragment).commit() // 隐藏当前的fragment，add下一个到Activity中
        } else {
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.show(targetFragment).commit() // 隐藏当前的fragment，显示下一个
        }
        fragments.add(targetFragment)
        return true
    }

    /**
     * 切换到对应的Fragment并将其加入到回退栈中
     */
    fun switchFragmentWithStack(targetFragment: Fragment) {
        fragments.remove(targetFragment)
        val transaction = manager!!.beginTransaction()
        if (!targetFragment.isAdded) {    // 先判断是否被add过
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.add(contentId, targetFragment)// 隐藏当前的fragment，add下一个到Activity中
                    .addToBackStack(null)
                    .commit()

        } else {
            for (i in fragments.indices) {
                if (fragments[i].isAdded) transaction.hide(fragments[i])
            }
            transaction.show(targetFragment).commit() // 隐藏当前的fragment，显示下一个
        }
        fragments.add(targetFragment)
    }

}
