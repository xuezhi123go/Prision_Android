package com.gkzxhn.prision.view;

import com.gkzxhn.prision.entity.MeetingEntity;
import com.gkzxhn.prision.entity.VersionEntity;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public interface IMainView extends IBaseView{
    void showProgress();
    void dismissProgress();
    void updateItems(List<MeetingEntity> datas);
    void onCanceled();
    void updateVersion(VersionEntity version);
}
