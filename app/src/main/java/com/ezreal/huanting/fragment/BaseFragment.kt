package com.ezreal.huanting.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.present.BasePresent
import com.ezreal.huanting.view.BaseView

abstract class BaseFragment<V:BaseView,P:BasePresent<V>> : Fragment(),BaseView {

    var present:P? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(setupViewLayout(),container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    abstract fun setupViewLayout():Int

    abstract fun createPresent():P
}