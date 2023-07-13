package com.gvsoft.myapplication;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gvsoft.iotlib.GFIOT;
import com.gvsoft.iotlib.core.Action;
import com.gvsoft.iotlib.core.GFLOTResult;
import com.gvsoft.iotlib.core.impl.MessageCallBack;
import com.gvsoft.iotlib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

//java代码示例
public class MainActivityForJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1.初始化sdk
        GFIOT.initSDK(getApplication(), "accessToken,此数据根据接入文档流程获取", true, new MessageCallBack() {
            @Override
            public void callBack(String code, GFLOTResult gflotResult) {
                Toast.makeText(MainActivityForJava.this,gflotResult.getMessage(),Toast.LENGTH_LONG).show();
                LogUtil.e(code+gflotResult);
            }
        });
        RecyclerView rec = findViewById(R.id.rec);
        ArrayList<Action> list = new ArrayList<>();
        list.add(Action.POWER_ON);//供电
        list.add(Action.POWER_OFF);//断电
        list.add(Action.OPEN_DOOR);//开门
        list.add(Action.OPEN_DOOR_AND_POWER);//开门供电
        list.add(Action.CLOSE_DOOR);//关门
        list.add(Action.CLOSE_DOOR_AND_POWER);//关门供电

        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(new BaseQuickAdapter<Action, BaseViewHolder>(R.layout.item, list) {

            @Override
            protected void convert(@NonNull BaseViewHolder holder, Action action) {
                int position = holder.getLayoutPosition();
                holder.setBackgroundColor(R.id.text, Color.parseColor(position % 2 == 0 ? "#e0e0e0" : "#f1f1f1"));
                holder.setText(R.id.text, Action.getActionName(action));
                holder.getView(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //2.控制车机
                        GFIOT.control("deviceId,此数据为车机deviceId", action, new MessageCallBack() {
                            @Override
                            public void callBack(String code, GFLOTResult gflotResult) {
                                Toast.makeText(getContext(),gflotResult.getMessage(),Toast.LENGTH_LONG).show();
                                LogUtil.e(code+gflotResult);
                            }
                        });
                    }
                });
            }
        });
        initPermission();

    }

    private void initPermission() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1001);
    }


}