package com.keyboy.mytest;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.keyboy.mytest.Model.Phone;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    OkHttpClient client = new OkHttpClient();
    Handler mainHandler=new Handler();
    private TextView PhoneNoText;
    private TextView ProvinceText;
    private TextView AttributionOperatorText;
    private TextView OperatorText;
    private EditText PhoneNoEdt;
    private Button SearchBtn;
    Phone p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //更新UI
        mainHandler= new Handler() {
            public void dispatchMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    Toast.makeText(getApplicationContext(),"查询成功",Toast.LENGTH_SHORT).show();
                    PhoneNoText.setText(p.getPhoneNo());
                    ProvinceText.setText(p.getProvince());
                    AttributionOperatorText.setText(p.getAttributionOperator());
                    OperatorText.setText(p.getOperator());

                } else {
                    Toast.makeText(getApplicationContext(),"您输入的手机号无效",Toast.LENGTH_SHORT).show();
                }
            }};

    }

    private void initView() {
        p=new Phone();
        PhoneNoText= (TextView) findViewById(R.id.text_PhoneNo);
        ProvinceText= (TextView) findViewById(R.id.text_Province);
        AttributionOperatorText= (TextView) findViewById(R.id.text_AttributionOperator);
        OperatorText= (TextView) findViewById(R.id.text_Operator);
        PhoneNoEdt= (EditText) findViewById(R.id.edt_PhoneNo);
        SearchBtn= (Button) findViewById(R.id.btn_Search);
        SearchBtn.setOnClickListener(this);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        System.out.println(PhoneNoEdt.getText().toString().length());
        if(PhoneNoEdt.getText().toString().length()<11)
            Toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
        else {
            try {
                okHttpRequest(PhoneNoEdt.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void okHttpRequest(String tel) throws Exception {
        String url="https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="+ tel;
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                System.out.println(false);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try {
                    Message msg=new Message();
                    String tmp=response.body().string();
                    int length=tmp.length();
                    String json=tmp.substring(19,length-1);
                    if(length<50)
                        msg.what=0;
                    else {
                        JSONObject jsonObj = new JSONObject(json);
                        p.setPhoneNo(jsonObj.getString("telString"));
                        p.setAttributionOperator(jsonObj.getString("carrier"));
                        p.setOperator(jsonObj.getString("catName"));
                        p.setProvince(jsonObj.getString("province"));
                        msg.what = 1;
                    }
                    mainHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}