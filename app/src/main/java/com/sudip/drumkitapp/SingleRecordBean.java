package com.sudip.drumkitapp;

/**
 * @author: Xu
 * @create: 2021-07-05 10:16
 **/
class SingleRecordBean {
    public long recordTime;
    public int poolId;

    public SingleRecordBean(long recordTime, int poolId) {
        this.recordTime = recordTime;
        this.poolId = poolId;
    }
}