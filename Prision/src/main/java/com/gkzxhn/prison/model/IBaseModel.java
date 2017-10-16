package com.gkzxhn.prison.model;

import android.content.SharedPreferences;

/**
 * Created by Raleigh.Luo on 2016/6/12.
 */
public interface IBaseModel {
    SharedPreferences getSharedPreferences();
    void stopAllReuqst();
}
