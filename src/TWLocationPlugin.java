package com.zsoftware;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.os.Build;

import static android.content.Context.POWER_SERVICE;

public class TWLocationPlugin extends CordovaPlugin {
   private static final String LOG_TAG = "TWLocationPlugin";
   private static String GETLOCATION_ACTION = "getLocation";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    /**
     * Constructor.
     */
    public TWLocationPlugin() {
    }


    /**
     * @param action          The action to execute.
     * @param args            JSONArray of arguments for the plugin.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return True when the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (this.cordova.getActivity().isFinishing()) return true;


        if (action.equalsIgnoreCase(GETLOCATION_ACTION)  ) { //获取定位信息
            final Activity _aty = this.cordova.getActivity();
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
//                        callbackContext.success();

                    PermissionsUtils.getInstance().chekPermissions(
                            _aty, permissions, new PermissionsUtils.IPermissionsResult(){

                                @Override
                                public void passPermissons() {
                                    final GPSUtils instance = GPSUtils.getInstance(_aty);
                                    instance.getLngAndLat(new GPSUtils.OnLocationResultListener() {
                                        @Override
                                        public void onLocationResult(Location location) {

                                            JSONObject retVal = new JSONObject();
                                            try {
                                                retVal.put("code",0);
                                                retVal.put("desc","");
                                                retVal.put("latitude",location.getLatitude());
                                                retVal.put("longitude",location.getLongitude());
                                                retVal.put("provider",location.getProvider());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Log.i(LOG_TAG,"返回坐标:"+retVal.toString());
                                            callbackContext.success(retVal.toString());
                                            instance.removeListener();
                                        }

                                        @Override
                                        public void OnLocationChange(Location location) {

                                        }
                                    });
                                }

                                @Override
                                public void forbitPermissons() {
                                    JSONObject retVal = new JSONObject();
                                    try {
                                        retVal.put("code",-9);
                                        retVal.put("desc","用户未给予权限");
                                        retVal.put("latitude",-1);
                                        retVal.put("longitude",-1);
                                        retVal.put("provider",-1);
                                        callbackContext.error(retVal);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                }
            });
            return true;
        }

//        callbackContext.success();
        return false;
    }


    /**
     * 是否应该检查权限
     * @return
     */
    public boolean showCheckPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        } else {
            return false;
        }
    }


}