package com.example.distancecal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private long firstTime = 0;
    private EditText heightInput;
    private boolean isRight = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        permissions();
        init();
    }

    //初始化数据
    public void init() {
        heightInput = (EditText) findViewById(R.id.input_text);
        Button cejuButton = (Button) findViewById(R.id.ceju);
        Button huituButton = (Button) findViewById(R.id.huitu);
        cejuButton.setOnClickListener(this);
        huituButton.setOnClickListener(this);

    }

    //权限申请模块
    public void permissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        String input = heightInput.getText().toString();
        if (isRight(input)) {
            switch (view.getId()) {
                case R.id.ceju:
                    String tipsCeju = "请站立并将靶心对准物体底部拍摄";
                    DistanceCalActivity.setCeju();
                    DistanceCalActivity.actionStart(MainActivity.this, input, tipsCeju);
                    break;
                case R.id.huitu:
                    String tipsHuizhi = "请站立并将靶心对准物体底部后，旋转一周拍摄9张照片";
                    DistanceCalActivity.setHuizhi();
                    DistanceCalActivity.actionStart(MainActivity.this, input, tipsHuizhi);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "请输入合法身高！", Toast.LENGTH_SHORT).show();
            isRight = true;
        }
    }

    public boolean isRight(String string) {
        if (string.isEmpty() == true || string.equals("")) {
            isRight = false;
        } else {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher matcher = pattern.matcher(string);
            if (!matcher.matches()) {
                isRight = false;
            }
        }
        return isRight;
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            ActivityCollector.finishAllActivity();
        }
    }
}
