package com.xcyoung.recyclebaubleview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.linear_layout)
    Button linearLayout;
    @BindView(R.id.grid_layout)
    Button gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.linear_layout, R.id.grid_layout,R.id.progress_reresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_layout:
                Intent intent1 = new Intent(MainActivity.this, LinearLayoutActivity.class);
                startActivity(intent1);
                break;
            case R.id.grid_layout:
                Intent intent2 = new Intent(MainActivity.this, GridLayoutActivity.class);
                startActivity(intent2);
                break;
            case R.id.progress_reresh:
                Intent intent3 = new Intent(MainActivity.this, ProgressRefreshActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
