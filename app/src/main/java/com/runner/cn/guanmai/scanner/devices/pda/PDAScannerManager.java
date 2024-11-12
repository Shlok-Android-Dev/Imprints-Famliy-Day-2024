package com.runner.cn.guanmai.scanner.devices.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import com.runner.cn.guanmai.scanner.IScannerManager;
import com.runner.cn.guanmai.scanner.SupporterManager;
import com.runner.cn.guanmai.scanner.UBXScannerManager;


public class PDAScannerManager implements IScannerManager {
    private static final String ACTION_DATA_CODE_RECEIVED = "scan.rcv.message";
    private static PDAScannerManager instance;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context mContext;
    private SupporterManager.IScanListener listener;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    byte[] broadCode = intent.getByteArrayExtra("barocode");
                    String code = new String(broadCode);
                    if (!code.isEmpty()) {
                        listener.onScannerResultChange(code);
                    }
                }
            });
        }
    };
    private ScanDevice mScanDevice;

    private PDAScannerManager(Context activity) {
        this.mContext = activity;
    }

    public static PDAScannerManager getInstance(Context activity) {
        if (instance == null) {
            synchronized (UBXScannerManager.class) {
                if (instance == null) {
                    instance = new PDAScannerManager(activity);
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        mScanDevice = new ScanDevice();
        mScanDevice.openScan();
        mScanDevice.setOutScanMode(0);
        registerReceiver();
        listener.onScannerServiceConnected();
    }

    @Override
    public void recycle() {

    }

    @Override
    public void setScannerListener(@NonNull SupporterManager.IScanListener listener) {
        this.listener = listener;
    }

    @Override
    public void sendKeyEvent(KeyEvent key) {

    }

    @Override
    public int getScannerModel() {
        return 0;
    }

    @Override
    public void scannerEnable(boolean enable) {

    }

    @Override
    public void setScanMode(String mode) {

    }

    @Override
    public void setDataTransferType(String type) {

    }

    @Override
    public void singleScan(boolean bool) {
        mScanDevice.startScan();
    }

    @Override
    public void continuousScan(boolean bool) {

    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_CODE_RECEIVED);
        mContext.registerReceiver(receiver, intentFilter);
    }
}
