package com.example.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.service.INewPersonListener;
import com.example.service.bean.IMyAidl;
import com.example.service.bean.Person;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    IMyAidl mAidl;
    TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = (TextView) findViewById(R.id.name);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.service","com.example.service.MyAidlService"));
        bindService(intent,mConnection,BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mAidl = IMyAidl.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mAidl = null;
        }
    };

    public void addPerson(View view){

        Random random = new Random();
        Person person = new Person("qian"+random.nextInt(20));

        try{
            mAidl.registerListener(mOnNewPersonListener);

            mAidl.addPerson(person);
            List<Person> mPersons = mAidl.getPersonList();

            tvName.setText(mPersons.toString());


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private INewPersonListener mOnNewPersonListener = new INewPersonListener.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void onNewPerson(Person newPerson) throws RemoteException {

            mHandler.obtainMessage(666,newPerson).sendToTarget();
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 666:
                    Log.i("xx","person::"+msg.obj);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mAidl.unregisterListener(mOnNewPersonListener);
            unbindService(mConnection);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
