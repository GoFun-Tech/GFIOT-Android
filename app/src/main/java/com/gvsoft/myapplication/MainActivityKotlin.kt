package com.gvsoft.myapplication

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gvsoft.iotlib.GFIOT
import com.gvsoft.iotlib.core.Action
import com.gvsoft.iotlib.core.GFLOTResult
import com.gvsoft.iotlib.core.impl.MessageCallBack
import com.gvsoft.iotlib.utils.LogUtil

//kotlin代码示例
class MainActivityKotlin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //1.初始化sdk
        GFIOT.initSDK(application, "accessToken,此数据根据接入文档流程获取", true,object : MessageCallBack() {
            override fun callBack(code: String, gflotResult: GFLOTResult) {
                Toast.makeText(baseContext, gflotResult.message, Toast.LENGTH_LONG)
                    .show()
                LogUtil.e(code + gflotResult)
            }
        });
        val rec = findViewById<RecyclerView>(R.id.rec)
        val list = ArrayList<Action>()
        list.add(Action.POWER_ON)//供电
        list.add(Action.POWER_OFF)//断电
        list.add(Action.OPEN_DOOR)//开门
        list.add(Action.OPEN_DOOR_AND_POWER)//开门供电
        list.add(Action.CLOSE_DOOR)//关门
        list.add(Action.CLOSE_DOOR_AND_POWER)//关门供电
        rec.layoutManager = LinearLayoutManager(this)
        rec.adapter = object : BaseQuickAdapter<Action, BaseViewHolder>(R.layout.item, list) {
            override fun convert(holder: BaseViewHolder, action: Action) {
                val position = holder.layoutPosition
                holder.setBackgroundColor(
                    R.id.text,
                    Color.parseColor(if (position % 2 == 0) "#e0e0e0" else "#f1f1f1")
                )
                holder.setText(R.id.text, Action.getActionName(action))
                holder.getView<View>(R.id.text)
                    .setOnClickListener {
                        //2.控制车机
                        GFIOT.control("deviceId,此数据为车机deviceId", action, object : MessageCallBack() {
                            override fun callBack(code: String, gflotResult: GFLOTResult) {
                                Toast.makeText(context, gflotResult.message, Toast.LENGTH_LONG)
                                    .show()
                                LogUtil.e(code + gflotResult)
                            }
                        })
                    }
            }
        }
        initPermission()
    }

    private fun initPermission() {
        val permissions: MutableList<String> = java.util.ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1001)
    }

}