package com.example.chatapp.cons;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.R;
import com.example.chatapp.ui.HomePageActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetNewAccessToken {

    //    AppDatabase appDatabase;
    Context context;


    public GetNewAccessToken(Context context) {
//       this.appDatabase = Room.databaseBuilder(context,AppDatabase.class,"database-name")
//               .allowMainThreadQueries().build();
        this.context = context;
    }

    public void sendGetNewTokenRequest() {
//        TokenDAO tokenDAO = appDatabase.tokenDAO();
        SharedPreferences sharedPreferencesToken = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.API_AUTH + "refreshtoken",
                response -> {
                    try {

                        /*String res = URLDecoder.decode(URLEncoder.encode(response,"iso8859-1"),"UTF-8");
                        JSONObject object = new JSONObject(res);*/
                        Log.i("get new access token = ", response.toString());
                        SharedPreferences.Editor editorToken = sharedPreferencesToken.edit();
                        editorToken.putString("access-token", response.toString()).apply();
                    } catch (/*JSONException | UnsupportedEncodingException*/ Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError) {
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject object = new JSONObject(res);
                            SharedPreferences sharedPreferencesStatus = context.getSharedPreferences("status", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorStatus = sharedPreferencesStatus.edit();
                            editorStatus.putBoolean("status-code", false).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferencesToken = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                String rfCookie = sharedPreferencesToken.getString("refresh-token", null);
                HashMap<String, String> map = new HashMap<>();
                map.put("Cookie", rfCookie);
                Log.i("cookie ==== ", rfCookie);
                return map;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeader = response.headers;
                String newrfCookie = responseHeader.get("Set-Cookie");
                Log.i(" new rf-token", newrfCookie);
                SharedPreferences sharedPreferencesToken = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorToken = sharedPreferencesToken.edit();
                editorToken.putString("refresh-token", newrfCookie).apply();
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);


    }


    private void showDialogSignupSuccess() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.login_session_expired_title)
                .setMessage(R.string.login_session_expired)
//                .setNegativeButton(R.string.accept, null)
                .setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferencesIsLogin = context.getSharedPreferences("is-login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorIsLogin = sharedPreferencesIsLogin.edit();
                        editorIsLogin.putBoolean("status-login", false).apply();
                        Intent i = new Intent(context, HomePageActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("EXIT", true);
                        context.startActivity(i);
                        ((Activity) context).finish();
                    }
                })
                .create();
        alertDialog.show();
    }

}
