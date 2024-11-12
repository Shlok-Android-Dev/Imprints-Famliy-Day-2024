package com.runner.tvs.Manaer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.runner.RuheApp;
import com.runner.tvs.Activity.PrintfBlueListActivity;

import com.runner.R;
import com.runner.tvs.Utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrintfManager {

    public final static int NAME_CHANG = 104;
    public static int ORDINARY = 1, SERIALIZE = 2;
    protected String TAG = "PrintfManager";
    protected List<BluetoothChangLister> bluetoothChangListerList = new ArrayList<>();
    protected Context context;
    protected PrinterInstance mPrinter;
    private ConnectSuccess connectSuccess;
    /**
     * 是否正在连接
     */
    private volatile boolean CONNECTING = false;
    private boolean isHasPrinter = false;
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String bluetoothName = context.getString(R.string.no_connect_blue_tooth);
            String bluetoothAddress = bluetoothName;
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS://成功
                    isHasPrinter = true;
                    Util.ToastText(context, context.getString(R.string.connection_success));


                    bluetoothName = SharedPreferencesManager.getBluetoothName(context);
                    bluetoothAddress = SharedPreferencesManager.getBluetoothAddress(context);
                    if (connectSuccess != null) {
                        SharedPreferences prefeMain =context. getSharedPreferences("FRIENDS",Context. MODE_PRIVATE);
                        SharedPreferences.Editor editor= prefeMain.edit();
                        editor.putString("PRINTER_TYPE", "TVS");
                        editor.putString("PRINTER_TYPE_TSC", "");
                        editor.commit();
                        connectSuccess.success();

                        RuheApp.Companion.getInstance().createFunction();
                    }
                    break;
                case PrinterConstants.Connect.FAILED://失败
                    disConnect(context.getString(R.string.connection_fail));
                    break;
                case PrinterConstants.Connect.CLOSED://关闭
                    disConnect(context.getString(R.string.bluetooth_disconnect));
                    break;
                case NAME_CHANG://名称改变
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    bluetoothAddress = device.getAddress();
                    bluetoothName = device.getName();
                    break;

            }

            for (BluetoothChangLister bluetoothChangLister : bluetoothChangListerList) {
                if (bluetoothChangLister != null) {
                    bluetoothChangLister.chang(bluetoothName, bluetoothAddress);
                }
            }
            CONNECTING = false;
        }
    };

    private PrintfManager() {
    }

    public static PrintfManager getInstance(Context context) {
        if (PrintfManagerHolder.instance.context == null) {
            PrintfManagerHolder.instance.context = context.getApplicationContext();
        }
        return PrintfManagerHolder.instance;
    }

    /**
     * Picture two valued
     * Convert pictures into byte arrays
     * @param bmp
     * @return
     */
    public static byte[] convertToBMW(Bitmap bmp, int concentration) {
        if (concentration <= 0 || concentration >= 255) {
            concentration = 128;
        }
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        byte[] bytes = new byte[(width) / 8 * height];
        int[] p = new int[8];
        int n = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 8; j++) {
                for (int z = 0; z < 8; z++) {
                    int grey = bmp.getPixel(j * 8 + z, i);
                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    int gray = (int) (0.29900 * red + 0.58700 * green + 0.11400 * blue); // 灰度转化公式
                    if (gray <= concentration) {
                        gray = 0;
                    } else {
                        gray = 1;
                    }
                    p[z] = gray;
                }
                byte value = (byte) (p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7]);
                bytes[width / 8 * i + j] = value;
            }
        }
        return bytes;
    }

    public void setConnectSuccess(ConnectSuccess connectSuccess) {
        this.connectSuccess = connectSuccess;
    }

    public boolean isCONNECTING() {
        return CONNECTING;
    }

    /**
     * 添加蓝牙改变监听
     *
     * @param bluetoothChangLister
     */
    public void addBluetoothChangLister(BluetoothChangLister bluetoothChangLister) {
        bluetoothChangListerList.add(bluetoothChangLister);
    }

    /**
     * 解除观察者
     *
     * @param bluetoothChangLister
     */
    public void removeBluetoothChangLister(BluetoothChangLister bluetoothChangLister) {
        if (bluetoothChangLister == null) {
            return;
        }
        if (bluetoothChangListerList.contains(bluetoothChangLister)) {
            bluetoothChangListerList.remove(bluetoothChangLister);
        }
    }

    public void connection() {
        if (mPrinter != null) {
            CONNECTING = true;
            mPrinter.openConnection();
        }
    }

    public PrinterInstance getPrinter() {
        return mPrinter;
    }

    public void setPrinter(PrinterInstance mPrinter) {
        this.mPrinter = mPrinter;
    }

    public boolean isConnect() {
        return isHasPrinter;
    }

    public void disConnect(final String text) {
        RuheApp.Companion.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                isHasPrinter = false;
                if (mPrinter != null) {
                    mPrinter.closeConnection();
                    mPrinter = null;
                }
                Util.ToastTextThread(context, text);
            }
        });
    }

    public void changBlueName(final String name) {
         RuheApp.Companion.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Util.ToastTextThread(context, context.getString(R.string.chang_bluetooth_name_now));
                    String AT = "$OpenFscAtEngine$";
                    mPrinter.sendByteData(AT.getBytes());
                    Thread.sleep(500);
                    byte[] read = mPrinter.read();
                    if (read == null) {
                        Util.ToastTextThread(context, context.getString(R.string.chang_bluetooth_name_fail));
                    } else {
                        String readString = new String(read);
                        if (readString.contains("$OK,Opened$")) {//进入空中模式
                            mPrinter.sendByteData(("AT+NAME=" + name +"\r\n").getBytes());
                            Thread.sleep(500);
                            byte[] isSuccess = mPrinter.read();
                            if(new String(isSuccess).contains("OK")){
                                Util.ToastTextThread(context, context.getString(R.string.chang_bluetooth_name_success));
                                SharedPreferencesManager.saveBluetoothName(context,name);
                            }else{
                                Util.ToastTextThread(context, context.getString(R.string.chang_bluetooth_name_fail));
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void printf(final int width, final int height, final Bitmap bitmap, final Activity activity) {
         RuheApp.Companion.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (isConnect()) {
                    Log.e("@@print","3");
                    Util.ToastTextThread(activity, context.getString(R.string.print_now));
                    realPrintfBitmapByLabelView(width,height,bitmap,128,1);
                } else {
                    Util.ToastTextThread(activity, context.getString(R.string.please_connect_bluetooth));
                    PrintfBlueListActivity.startActivity(activity);
                }
            }
        });

    }

    public void defaultConnection() {
        String bluetoothName = SharedPreferencesManager.getBluetoothName(context);
        if (bluetoothName == null) {
            return;
        }

        String bluetoothAddress = SharedPreferencesManager.getBluetoothAddress(context);
        if (bluetoothAddress == null) {
            return;
        }

        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            if (device.getAddress().equals(bluetoothAddress)) {
                mPrinter = new PrinterInstance(context, device, mHandler);
                connection();
                return;
            }
        }
    }

    /**
     * clear Canvas
     */
    public void clearCanvas() {
        mPrinter.sendByteData("CLS\r\n".getBytes());
    }

    /**
     * init Canvas
     *
     * @param w
     * @param h
     */
    public void initCanvas(int w, int h) {
        byte[] data = new StringBuilder().append("SIZE ").append(w + " mm").append(",")
                .append(h + " mm").append("\r\n").toString().getBytes();
        mPrinter.sendByteData(data);
    }

    /**
     * @param x    ：image printf X coordinate
     * @param y    ：image printf Y coordinate
     * @param data ：Picture resources
     */
    public void printfBitmap(int x, int y, Bitmap data, int concentration) {
        //newline
        byte[] crlf = {0x0d, 0x0a};
        StringBuilder BITMAP = new StringBuilder()
                .append("BITMAP ")
                .append(x)
                .append(",")
                .append(y)
                .append(",")
                .append((data.getWidth()) / 8)
                .append(",")
                .append(data.getHeight())
                .append(",1,");
        byte[] bitmapByte = convertToBMW(data, concentration);
        mPrinter.sendByteData(BITMAP.toString().getBytes());
        mPrinter.sendByteData(bitmapByte);
        mPrinter.sendByteData(crlf);
        Log.e("@@print","1");
    }


    public void beginPrintf() {
        beginPrintf(1, 1);
    }

    /**
     * begin printf
     *
     * @param sequence    : Sequence
     * @param groupNumber : Group number
     */
    public void beginPrintf(int sequence, int groupNumber) {
        String PRINT = "PRINT " + sequence + "," + groupNumber + "\r\n";
        mPrinter.sendByteData(PRINT.getBytes());
        Log.e("@@print","2");
    }

    /**
     * Connect
     *
     * @param mDevice
     */
    public void openPrinter(final BluetoothDevice mDevice) {
         RuheApp.Companion.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                setPrinter(new PrinterInstance(context, mDevice, mHandler));
                // default is gbk...
                connection();
                //Connection Save Address + Name
                SharedPreferencesManager.updateBluetooth(context, mDevice);
            }
        });
    }

    /**
     * real printf
     *
     * @param concentration
     * @param number
     */
    private boolean realPrintfBitmapByLabelView(int width,int height,Bitmap bitmap, int concentration, int number) {
        try {
            initCanvas(width, height);
            clearCanvas();
            printfBitmap(0, 0, bitmap, concentration);
            beginPrintf(1, number);
            Log.e("@@print","4");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@@print","5"+e.getMessage());
            Util.ToastTextThread(context, context.getString(R.string.printf_error_check));
            return false;
        }
    }

    public interface BluetoothChangLister {
        void chang(String name, String address);
    }

    public interface ConnectSuccess {
        void success();
    }

    static class PrintfManagerHolder {
        private static PrintfManager instance = new PrintfManager();
    }
}
