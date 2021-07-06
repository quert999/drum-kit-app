package com.sudip.drumkitapp;

import android.media.AudioManager;
import android.media.SoundPool;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Xu
 * @create: 2021-07-05 10:19
 **/
class SoundPoolUtil {


    private static final SoundPool soundPool;
    private static final Map<Integer, Integer> map = new HashMap<>();

    static {
        soundPool = new SoundPool(200, AudioManager.STREAM_MUSIC, 0);

    }

    static void initResource(ViewGroup group) {
        map.clear();
        for (int i = 0; i < group.getChildCount(); i++) {
            int resourceId = group.getResources().getIdentifier(group.getResources().getResourceEntryName(group.getChildAt(i).getId()), "raw", "com.sudip.drumkitapp");
            map.put(resourceId, soundPool.load(group.getContext(), resourceId, 1));
        }
    }

    static Integer getSoundPoolId(int id) {
        return map.get(id);
    }

    static void playByResourceId(int resourceId) {
        int soundId = getSoundPoolId(resourceId);
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }

    static void playByPoolId(int poolId){
        soundPool.play(poolId, 1, 1, 1, 0, 1);
    }
}