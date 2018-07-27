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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.example.service.bean.INewPersonAddListener;


/**
 * Created by xuqianqian on 2017/12/25.
 */

public class MyAidlService extends Service {

    private final String  TAG = this.getClass().getSimpleName();

    private ArrayList<Person> mPersons;

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<INewPersonListener> mListener =
            new CopyOnWriteArrayList<>();

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

        @Override
        public void registerListener(INewPersonListener listener) throws RemoteException {

                if(!mListener.contains(listener)){
                    mListener.add(listener);
                }else{
                    Log.i("xx","already exists");
                }
                Log.i("xx","registerListener.size::"+mListener.size());
        }

        @Override
        public void unregisterListener(INewPersonListener listener) throws RemoteException {

            if(mListener.contains(listener)){
                mListener.remove(listener);
                Log.i("xx","unregister listener succeed.");
            }else{
                Log.i("xx","not found, can not unregister.");
            }
            Log.i("xx","unregisterListener current size:"+mListener.size());
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

        new Thread(new ServiceWorker()).start();
    }

    private void onNewPersonAdd(Person person) throws RemoteException{

        mPersons.add(person);

        for(int i=0;i<mListener.size();i++){
            INewPersonListener listener = mListener.get(i);
            listener.onNewPerson(person);
        }
    }

    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()){
                try{
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                int personName = mPersons.size() + 1;
                Person person = new Person("qianqian"+personName);

                try{
                    onNewPersonAdd(person);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }
}
