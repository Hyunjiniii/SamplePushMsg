package com.example.samplepushmsg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String key = "AAAAT2MmiYA:APA91bGraJSJ6rGUqonnW1rPzQMl5SK2vaDMswBQseX-g58_2qiq5zJayoLlYoxXZzkwcLb0nF0OPhKMOtF458mmSKBb9Ml0O75ZpKI521frcdYUPQyqD_2BiTKtvzZIESoQ7b8CeRUA";
    TextView messageOutput;
    EditText messageInput;
    TextView log;
    String regId;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageOutput = (TextView) findViewById(R.id.messageOutput);
        messageInput = (EditText) findViewById(R.id.messageInput);
        log = (TextView) findViewById(R.id.log);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = messageInput.getText().toString();
                send(input);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        getRegistrationId();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent() called");

        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            println("from is null");
            return;
        }

        String contents = intent.getStringExtra("contents");

        println("DATA : " + from + ", " + contents);
        messageOutput.setText("[" + from + "] 로부터 수신한 데이터 : " + contents);
    }

    // 단말의 등록 ID를 확인하는 데 사용
    public void getRegistrationId() {
        println("getRegistrationId() 호출됨");

        regId = FirebaseInstanceId.getInstance().getToken();  // 현재 동록 ID값 확인
        println("regId : " + regId);
    }

    public void send(String input) {
        JSONObject requestData = new JSONObject();  // 전송 정보를 담아두는 객체

        try {
            requestData.put("priority", "high");

            // 전송할 데이터 추가
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestData.put("data", dataObj);

            // 메시지를 수신할 단말 ID를 JSONArray에 추가한 뒤 객체에 추가
            JSONArray idArray = new JSONArray();
            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 푸시 전송을 위해 정의한 메소드 호출
        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨");
            }

            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨");
            }
        });
    }

    public interface SendResponseListener {
        public void onRequestStarted();

        public void onRequestCompleted();

        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,  // POST방식으로 요청
                "https://fcm.googleapis.com/fcm/send",  // 클라우드 서버의 요청 주소
                requestData,  // 요청 데이터가 들어있는 객체
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 성공 응답 받았을 경우
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 오류 응답 받았을 경우
                listener.onRequestWithError(error);
            }
        }
        ) {
            // 요청 파라미터 설정
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", key);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    private void println(String msg) {
        log.append(msg);
    }


}
