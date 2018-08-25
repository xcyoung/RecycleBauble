package com.xcyoung.recyclebaubleview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.xcyoung.recyclebauble.decoration.DividerItemDecoration;
import com.xcyoung.recyclebauble.view.RecycleBaubleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearLayoutActivity extends AppCompatActivity {
    @BindView(R.id.list)
    RecycleBaubleView baubleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ButterKnife.bind(this);

        baubleView.setLayoutManager(new LinearLayoutManager(this));
        baubleView.addItemDecoration(new DividerItemDecoration(this));
        final CommonAdapter commonAdapter=new CommonAdapter();
        baubleView.setAdapter(commonAdapter);
        baubleView.setOnLoadingListener(new RecycleBaubleView.onLoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> list=new ArrayList<>();
                        for(int i=0;i<10;i++){
                            list.add("item"+i);
                        }
                        commonAdapter.replace(list);
                    }
                },2000);
            }

            @Override
            public void onLoad() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> list=new ArrayList<>();
                        for(int i=0;i<2;i++){
                            list.add("item"+i);
                        }
                        commonAdapter.add(list);
                    }
                },2000);
            }
        });

        baubleView.setRefresh(true);
    }
}
