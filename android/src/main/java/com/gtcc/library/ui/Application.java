package com.gtcc.library.ui;

import com.avos.avoscloud.AVOSCloud;
import com.gtcc.library.util.Configs;

/**
 * Created by Administrator on 2015/1/7.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this, Configs.AVOS_API_ID, Configs.AVOS_API_KEY);
    }
}
