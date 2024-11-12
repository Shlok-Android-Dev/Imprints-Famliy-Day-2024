package com.runner.cn.guanmai.scanner;


import android.view.KeyEvent;

import androidx.annotation.NonNull;

public interface IScannerManager {
    void init();

    void recycle();

    void setScannerListener(@NonNull SupporterManager.IScanListener listener);

    void sendKeyEvent(KeyEvent key);

    int getScannerModel();

    void scannerEnable(boolean enable);

    void setScanMode(String mode);

    void setDataTransferType(String type);

    void singleScan(boolean bool);

    void continuousScan(boolean bool);
}
