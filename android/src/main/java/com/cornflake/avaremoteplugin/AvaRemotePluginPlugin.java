package com.cornflake.avaremoteplugin;

import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;

@CapacitorPlugin(name = "AvaRemotePlugin")
public class AvaRemotePluginPlugin extends Plugin {

    @Override
    public void load(){
        initializePlugin();
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", "from native: " + value);
        call.resolve(ret);
    }

    public static DynkeBroadcastReceiver dynkeBroadcastReceiver;
    private final int NORMAL_CLOSURE_STATUS = 1000;
    private boolean listenToBroadcastsFromOtherApps = true;
    //public static Context context;

    private int receiverFlags = listenToBroadcastsFromOtherApps
            ? ContextCompat.RECEIVER_EXPORTED
            : ContextCompat.RECEIVER_NOT_EXPORTED;

    public void sendAvaBroadcast(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }

    public void initializePlugin(){
        Context context = getContext();
        if(context != null){
            Log.d("AVA Remote Plugin", "Initialize plugin method called");
            dynkeBroadcastReceiver = new DynkeBroadcastReceiver();

            // Initialize broadcast receivers
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.ava.dynke.intent.action.PONG");
            intentFilter.addAction("com.ava.dynke.intent.action.keyboard_configuration_updated");
            intentFilter.addAction("com.ava.dynke.intent.action.keyboard_configuration_invalid");
            intentFilter.addAction("com.ava.dynke.intent.action.keyboard_event");

            ContextCompat.registerReceiver(context, dynkeBroadcastReceiver, intentFilter, receiverFlags);

            Intent pingIntent = new Intent("com.ava.dynke.intent.action.PING");
            pingIntent.setPackage("com.ava.dynke");
            sendAvaBroadcast(context, pingIntent);

            Log.d("AVA Remote Plugin", "Plugin initialized.");

            // Initialize the AVA remote's hard keys with no buttons and green circle animation
            JSObject initialConfig = new JSObject();
            initialConfig.put("version", 1);
            initialConfig.put("namespace", "com.example.avaremoteapp");
            initialConfig.put("backgroundVideo", "red");
            initialConfig.put("configurationId", "uniqueID");
            initialConfig.put("buttons", new JSArray());
            setConfig(context, initialConfig.toString());
        }
        else {
            Log.d("AVA Remote Plugin","Failed to initialize plugin: Context is null.");
        }
    }

    public void setConfig(Context context, String config) {
        Intent configIntent = new Intent("com.ava.dynke.intent.action.configure_keyboard");
        configIntent.putExtra("keyboardConfigurationJSON", config);
        configIntent.setPackage("com.ava.dynke");
        sendAvaBroadcast(context, configIntent);
    }

    @PluginMethod()
    public void sendConfig(PluginCall call){
        JSObject config = call.getObject("config");
        Context context = getContext();

        if (context != null) {
            Log.d("Config", config.toString());
            setConfig(context, config.toString());
        } else {
            call.reject("Failed to send config: Context is null.");
        }
    }

    public void sendHardButtonData(JSObject data) {
        Log.d("Hard button pressed:", data.toString());
        // Notify all listeners for "eventName"
        notifyListeners("hardButtonPressed", data);
    }

    // Declare the DynkeBroadcastReceiver class to handle received events from the DYNKE API
    public class DynkeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || context == null) return;

            String action = intent.getAction();
            switch (action) {
                case "com.ava.dynke.intent.action.PONG":
                    Log.d("DynkeBroadcastReceiver", "Received PONG from DYNKE");
                    break;

                case "com.ava.dynke.intent.action.keyboard_event":
                    Log.d("DynkeBroadcastReceiver", "Received keyboard event from DYNKE");

                    JSObject jsonExtras = getExtrasAsJson(intent);
                    Log.d("DynkeBroadcastReceiver", "Intent Extras as JSON: " + jsonExtras);

                    try {
                        // Forward the event to WebSocket server using WebSocketClient
                        sendHardButtonData(jsonExtras);
                    } catch (Exception e) {
                        Log.e("KeypadEventReceiver", "Error parsing event data", e);
                    }

                    break;

                case "com.ava.dynke.intent.action.keyboard_configuration_updated":
                    Log.d("DynkeBroadcastReceiver", "DYNKE received config: VALID");
                    break;

                case "com.ava.dynke.intent.action.keyboard_configuration_invalid":
                    Log.d("DynkeBroadcastReceiver", "DYNKE received config: INVALID");
                    break;
            }
        }

        public JSObject getExtrasAsJson(Intent intent) {
            JSObject jsonObject = new JSObject();
            if (intent.getExtras() != null) {
                for (String key : intent.getExtras().keySet()) {
                    Object value = intent.getExtras().get(key);
                    //try {
                    // Add to the JSONObject (handling different types of values)
                    if (value instanceof String) {
                        jsonObject.put(key, value);
                    } else if (value instanceof Integer) {
                        jsonObject.put(key, value);
                    } else if (value instanceof Boolean) {
                        jsonObject.put(key, value);
                    } else if (value instanceof Double) {
                        jsonObject.put(key, value);
                    } else if (value instanceof Float) {
                        jsonObject.put(key, value);
                    } else if (value instanceof Long) {
                        jsonObject.put(key, value);
                    } else {
                        jsonObject.put(key, value.toString());  // Convert to string for unsupported types
                    }
                    /*} catch (JSONException e) {
                        Log.e("DynkeBroadcastReceiver", "Error converting extra to JSON", e);
                    }*/
                }
            }
            //return jsonObject.toString();  // Return the JSON as a string
            return jsonObject;  // Return the JSON as a string
        }
    }
}
