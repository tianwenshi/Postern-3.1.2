package com.tunnelworkshop.postern.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.tunnelworkshop.postern.PosternApp;
import com.tunnelworkshop.postern.PosternVpnService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public abstract class ControllerBase {

    private static final int LAUNCH_TIMEOUT = 5000;

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.tunnelworkshop.postern";

    public UiDevice device;

    public Context context;

    public Parameters parameters;

    public String message = "";

    public int status = 0;

    public void useAppContext() {
        context = ApplicationProvider.getApplicationContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        initParameters();

        beginOperate();

        autoUiOperate();

        endOperate();


    }

    private void initParameters() {
        Bundle extras = InstrumentationRegistry.getArguments();
        parameters = new Parameters();
        parameters.setProxyName(extras.getString("proxyname"));
        parameters.setProxyPass(extras.getString("proxypass"));
        parameters.setProxyServer(extras.getString("proxyserver"));
        parameters.setProxyPort(extras.getString("proxyport"));
        parameters.setPhoneId(extras.getString("phoneid"));
        parameters.setTargetPkg(extras.getString("targetpkg"));
//        String callbackUrl = new String(Base64.decode(extras.getString("callbackurl"), Base64.DEFAULT));
//        callbackUrl = callbackUrl.replaceAll("=", "_");
        parameters.setCallbackUrl("");

        String[] nameArray = parameters.getProxyName().split("-");
        String country = nameArray[nameArray.length - 1];
        parameters.setCountry(country);


        System.out.println(parameters.toString());
    }


    public abstract void autoUiOperate();

    public void beginOperate() {
        //获取信息
        String phoneData = "test";//requestPhoneData(parameters.getPhoneId());

        if (phoneData == null) {
            httpRequest(parameters.getCallbackUrl(), 2, "request phone data error");
            return;
        }
        Cache.PHONE.state = Cache.PhoneData.State.READY;
        Cache.PHONE.data = phoneData;
        //end

        //启动vpn
        startPostern();

        //启动目标App
        startTargetApp();

    }

    private void startPostern() {
        device.pressHome();
        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.setPackage(null);
        intent.putExtra("proxyname", parameters.getProxyName());
        intent.putExtra("proxypass", parameters.getProxyPass());
        intent.putExtra("proxyserver", parameters.getProxyServer());
        intent.putExtra("proxyport", Integer.parseInt(parameters.getProxyPort()));
        context.startActivity(intent);
        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
        UiObject okButton = device.findObject(new UiSelector()
                .textMatches("OK|确定")
                .className("android.widget.Button"));

        // Simulate a user-click on the OK button, if found.
        try {
            if (okButton.exists() && okButton.isEnabled()) {
                okButton.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        device.pressHome();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);
    }

    private void startTargetApp() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(parameters.getTargetPkg());
        if (intent == null) {
            httpRequest(parameters.getCallbackUrl(), 2, "targetpkg " + parameters.getTargetPkg() + " is not install");
            return;
        }
        intent.setPackage(null);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(parameters.getTargetPkg()).depth(0)),
                15000);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void endOperate() {
        if (parameters.getTargetPkg() != null) {
            try {
                device.executeShellCommand("am force-stop " + parameters.getTargetPkg());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PosternApp var7 = (PosternApp) ApplicationProvider.getApplicationContext();
        PosternVpnService var8 = var7.getVpnService();
        var8.revertPosternVpnService();
        httpRequest(parameters.getCallbackUrl(), status, message);
    }


    //向下滑动
    public void swipeDown(int time) {
        int x = device.getDisplayWidth();//获取屏幕的宽
        int y = device.getDisplayHeight();//获取屏幕的高
        device.swipe(x / 2, (int) (y / 2), x / 2, y / 6, time);//上滑
    }


    //向上滑动
    public void swipeUp(int time) {
        int x = device.getDisplayWidth();//获取屏幕的宽
        int y = device.getDisplayHeight();//获取屏幕的高
        device.swipe(x / 2, y / 2, x / 2, (int) (y / 1.2), time);//下滑
    }

    public static void httpRequest(String callbackurl, int status, String message) {
        HttpURLConnection var9 = null;
        try {
            URL var23 = new URL(callbackurl + "&status=" + status + "&msg=" + URLEncoder.encode(message, "UTF-8"));
            (var9 = (HttpURLConnection) var23.openConnection()).setReadTimeout(30000);
            var9.setConnectTimeout(30000);
            var9.setRequestMethod("GET");
            var9.setDoInput(true);
            var9.setDoOutput(true);
//            var9.setRequestProperty("Content-Type", "application/octet-stream");
            int var22 = var9.getResponseCode();

            if (var22 == 200) {
//                byte[] bytes = getBytes(var9.getInputStream());
//                String json = new String(bytes);
                System.out.println("Status 200 ok");
            } else {
                System.out.println("request error code=" + var22);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (var9 != null) {
                var9.disconnect();
            }
        }
    }


    public String requestPhoneData(String phoneid) {
        HttpURLConnection var9 = null;
        try {
            URL var23 = new URL("http://18.141.10.168:54321/get-job?phone-id=" + phoneid);
            (var9 = (HttpURLConnection) var23.openConnection()).setReadTimeout(30000);
            var9.setConnectTimeout(30000);
            var9.setRequestMethod("GET");
            var9.setDoInput(true);
            var9.setDoOutput(true);
//            var9.setRequestProperty("Content-Type", "application/octet-stream");
            int var22 = var9.getResponseCode();

            if (var22 == 200) {
                byte[] bytes = getBytes(var9.getInputStream());
                return new String(bytes);
            } else {
                System.out.println("request error code=" + var22);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (var9 != null) {
                var9.disconnect();
            }
        }
        return null;
    }


    private static byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b, 0, 1024)) != -1) {
            bao.write(b, 0, len);
            bao.flush();
        }
        return bao.toByteArray();
    }
}
