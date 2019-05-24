package com.example.samplepushmsg;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// 푸시메시지를 전달받는 역할
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMSG";

    // 구글 클라우드 서버에서 보내오는 메시지를 받음
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageRecieved() 호출됨");

        // 푸시 메시지 받아옴
        String from = remoteMessage.getFrom();  // 발신자 코드 확인
        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");  // Map 객체 안에 있는 데이터 꺼냄

        Log.v(TAG, "from : " + from + ", contents : " + contents);

        sendToActivity(getApplicationContext(), from, contents);
    }

    // 액티비티 쪽으로 데이터를 보냄
    private void sendToActivity(Context context, String from, String contents) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("contents", contents);

        // 서비스에서 액티비티를 띄울 때는 인텐트에 플래그 줘야함
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }
}
