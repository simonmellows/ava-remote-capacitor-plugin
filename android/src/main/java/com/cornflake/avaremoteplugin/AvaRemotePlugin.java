package com.cornflake.avaremoteplugin;

import android.util.Log;

public class AvaRemotePlugin {

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }
}
