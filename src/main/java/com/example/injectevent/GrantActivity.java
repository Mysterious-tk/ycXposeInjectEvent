package com.example.injectevent;

import android.app.Activity; // 添加缺失的Activity类导入
import android.os.Bundle;
import android.widget.Toast;

public class GrantActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pkg = getIntent().getStringExtra("pkg");
        if (pkg != null) {
            Toast.makeText(this, "已授权: " + pkg, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}