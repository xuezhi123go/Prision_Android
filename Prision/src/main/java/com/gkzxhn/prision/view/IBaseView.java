package com.gkzxhn.prision.view;

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public interface IBaseView {
    void startRefreshAnim();
    void stopRefreshAnim();
    void showToast(int testResId);
    void showToast(String showText);
}
