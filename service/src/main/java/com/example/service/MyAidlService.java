package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.service.bean.IMyAidl;
import com.example.service.bean.Person;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuqianqian on 2017/12/25.
 */

public class MyAidlService extends Service {

    private final String  TAG = this.getClass().getSimpleName();

    private ArrayList<Person> mPersons;

    /**
     * 创建生成本地的binder ,实现AIDL的方法
     */
    private IBinder mIBinder = new IMyAidl.Stub(){

        @Override
        public void addPerson(Person person) throws RemoteException {
            mPersons.add(person);
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return mPersons;
        }
    };

    /**
     * 客户端与服务端绑定时的回调，返回IBinder对象后客户端就可以通过它远程调用服务端的方法，即实现了通讯
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPersons = new ArrayList<>();
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
