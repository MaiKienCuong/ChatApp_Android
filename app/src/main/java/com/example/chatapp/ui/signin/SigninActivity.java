package com.example.chatapp.ui.signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.R;
import com.example.chatapp.cons.Constant;
import com.example.chatapp.dto.UserSummaryDTO;
import com.example.chatapp.ui.main.MainActivity;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.Json;

public class SigninActivity extends AppCompatActivity {

    private EditText edt_sign_in_user_name;
    private EditText edt_sign_in_password;
    private TextView txt_sign_in_error;
    private String username, password;
    private String rfCookie;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_ChatApp_SlidrActivityTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .build();

        Slidr.attach(this, config);

        Toolbar toolbar = findViewById(R.id.toolbar_signin_activity);
        toolbar.setTitle(getString(R.string.sign_in_button));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        /*
        hiện nút mũi tên quay lại trên toolbar
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button btn_sign_in = findViewById(R.id.btn_sign_in);
        edt_sign_in_user_name = findViewById(R.id.edt_sign_in_user_name);
        edt_sign_in_password = findViewById(R.id.edt_sign_in_password);
        txt_sign_in_error = findViewById(R.id.txt_sign_in_error);
        gson = new Gson();

        btn_sign_in.setOnClickListener(v -> {
            username = edt_sign_in_user_name.getText().toString();
            if (TextUtils.isEmpty(username)) {
                txt_sign_in_error.setText(R.string.check_user_name_empty);
                return;
            }
            password = edt_sign_in_password.getText().toString();
            if (TextUtils.isEmpty(password)) {
                txt_sign_in_error.setText(R.string.check_password_empty);
                return;
            }
            sendSignInRequest();
        });

    }

    private void sendSignInRequest() {
        Log.e("signin","request");
        StringRequest request = new StringRequest(Request.Method.POST, Constant.API_AUTH + "signin",
                response -> {
                    try {
                        Log.e("signin-res",response.toString());
                        String res = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"), "UTF-8");
                        JSONObject object = new JSONObject(res);

                        UserSummaryDTO user = gson.fromJson(String.valueOf(object), UserSummaryDTO.class);
                        System.out.println("++++++++++++_____________________------------" + user.toString());

                        SharedPreferences sharedPreferencesUser = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                        editor.putString("user-info", Json.encode(user)).apply();

                        SharedPreferences sharedPreferencesToken = getSharedPreferences("token", MODE_PRIVATE);
                        SharedPreferences.Editor editorToken = sharedPreferencesToken.edit();
                        editorToken.putString("access-token", user.getAccessToken()).apply();

                        SharedPreferences sharedPreferencesStatus = getSharedPreferences("status", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorStatus = sharedPreferencesStatus.edit();
                        editorStatus.putBoolean("status-code", true).apply();

                        SharedPreferences sharedPreferencesIsLogin = getSharedPreferences("is-login", MODE_PRIVATE);
                        SharedPreferences.Editor editorIsLogin = sharedPreferencesIsLogin.edit();
                        editorIsLogin.putBoolean("status-login", true).apply();

                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();

                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e("signin-err",e.getMessage());
                    }
                },
                error -> {
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError) {
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject object = new JSONObject(res);
                            txt_sign_in_error.setText(object.getString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);

                return map;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.i("response", response.headers.toString());
                Map<String, String> responseHeader = response.headers;
                rfCookie = responseHeader.get("Set-Cookie");
                Log.i("rf-token", rfCookie);
                SharedPreferences sharedPreferencesToken = getSharedPreferences("token", MODE_PRIVATE);
                SharedPreferences.Editor editorToken = sharedPreferencesToken.edit();
                editorToken.putString("refresh-token", rfCookie).apply();
                return super.parseNetworkResponse(response);
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}