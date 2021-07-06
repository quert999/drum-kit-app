package com.sudip.drumkitapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

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
        RulerView rulerView = findViewById(R.id.rulerView);
        DrumBeatView drumBeatView = findViewById(R.id.drumBeatView);
        CursorView cursorView = findViewById(R.id.cursor);
        rulerView.setRulerLength((int) (length / 100));
        drumBeatView.setTimeLength((int) (length / 100));
        cursorView.setStartTime(System.currentTimeMillis());
        cursorView.setLength((int) (length/100));
        cursorView.setLengthExact(length);
        drumBeatView.setDrumPosition(data);

        cursorView.setOnTimeListener(drumBeatView::onTime);

        rulerView.invalidate();
        drumBeatView.invalidate();
        cursorView.invalidate();
    }
}
