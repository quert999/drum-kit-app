package com.sudip.drumkitapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        long length = getIntent().getLongExtra("recordTimeLength", 0);
        List<SingleRecordBean> data = new Gson().fromJson(getIntent().getStringExtra("recordListData"), new TypeToken<List<SingleRecordBean>>() {
        }.getType());
        RulerView rulerView = ((RulerView) findViewById(R.id.rulerView));
        DrumBeatView drumBeatView = ((DrumBeatView) findViewById(R.id.drumBeatView));
        rulerView.setRulerLength((int) (length/100));
        drumBeatView.setTimeLength((int) (length/100));
        drumBeatView.setDrumPosition(data);

        rulerView.invalidate();
        drumBeatView.invalidate();
    }
}
