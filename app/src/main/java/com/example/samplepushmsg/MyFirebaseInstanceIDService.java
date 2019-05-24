package com.example.samplepushmsg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// 클라우드 서버에 단말을 등록하는 역할
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyIID";

    // 단말의 등록 ID를 전달받으면 메소드 호출됨
    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh() 호출됨");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refresh Token : " + refreshedToken);
    }
}
