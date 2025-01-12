/**
 * Activity of printer settings
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.runner.printdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.runner.R;
import com.runner.printdemo.common.Common;
import com.runner.printdemo.printprocess.PrinterModelInfo;


public class Activity_ModelSelect extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.model_select);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // initialize the printerModel ListPreference
        ListPreference printerModelPreference = (ListPreference) getPreferenceScreen()
                .findPreference("printerModel");
        printerModelPreference.setEntryValues(PrinterModelInfo.getModelNames());
        printerModelPreference.setEntries(PrinterModelInfo.getModelNames());


        // initialize the settings
        setPreferenceValue("printerModel");
        String printerModel = sharedPreferences.getString("printerModel", "");

        // set paper size & port information
        printerModelChange(printerModel);

        setPreferenceValue("port");
        setEditValue("address");
        setEditValue("macAddress");
        setPreferenceValue("enabledTethering");

        // initialization for printer
        PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                .findPreference("printer");

        String printer = sharedPreferences.getString("printer", "");
        if (!printer.equals("")) {
            printerPreference.setSummary(printer);
        }

        printerPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        String printerModel = sharedPreferences.getString(
                                "printerModel", "");
                        setPrinterList(printerModel);
                        return true;
                    }
                });


        // set the BackgroundForPreferenceScreens to light
        setBackgroundForPreferenceScreens("prefIpMacAddress");

    }

    /**
     * Called when [printer] is tapped
     */
    private void setSavePath() {

        Intent savePath = new Intent(this, Activity_SaveFile.class);
        startActivityForResult(savePath, Common.SAVE_PATH);
    }


    /**
     * Called when a Preference has been changed by the user. This is called
     * before the state of the Preference is about to be updated and before the
     * state is persisted.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (newValue != null) {
            if (preference.getKey().equals("printerModel")) {
                String printerModel = sharedPreferences.getString(
                        "printerModel", "");
                if (printerModel.equalsIgnoreCase(newValue.toString())) {
                    return true;
                }

                // initialize if printer model is changed
                printerModelChange(newValue.toString());

                ListPreference portPreference = (ListPreference) getPreferenceScreen()
                        .findPreference("port");
                portPreference.setValue(portPreference.getEntryValues()[0]
                        .toString());
                portPreference.setSummary(portPreference.getEntryValues()[0]
                        .toString());

                setChangedData();
            }

            if (preference.getKey().equals("port")) {
                setChangedData();
            }

            preference.setSummary((CharSequence) newValue);

            return true;
        }

        return false;

    }

    /**
     * Called when the searching printers activity you launched exits.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Common.PRINTER_SEARCH == requestCode) {
            EditTextPreference addressPreference = (EditTextPreference) getPreferenceScreen()
                    .findPreference("address");
            EditTextPreference macAddressPreference = (EditTextPreference) getPreferenceScreen()
                    .findPreference("macAddress");
            PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                    .findPreference("printer");

            if (resultCode == RESULT_OK) {
                // IP address
                String ipAddress = data.getStringExtra("ipAddress");
                addressPreference.setText(ipAddress);
                if (ipAddress.equalsIgnoreCase("")) {
                    ipAddress = getString(R.string.address_value);
                }
                addressPreference.setSummary(ipAddress);

                // MAC address
                String macAddress = data.getStringExtra("macAddress");
                macAddressPreference.setText(macAddress);
                macAddressPreference.setSummary(macAddress);

                // Printer name
                printerPreference.setSummary(data.getStringExtra("printer"));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("printer", data.getStringExtra("printer"));
                editor.putString("localName", data.getStringExtra("localName"));
                editor.apply();
            }
        } else if (Common.SAVE_PATH == requestCode) {
            if (resultCode == RESULT_OK) {
                PreferenceScreen saveFilePreference = (PreferenceScreen) getPreferenceScreen()
                        .findPreference("savePrnPath");

                saveFilePreference.setSummary(data
                        .getStringExtra("savePrnPath"));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savePrnPath",
                        data.getStringExtra("savePrnPath"));
                editor.apply();
            }
        }
    }

    /**
     * set data of a particular ListPreference
     */
    private void setPreferenceValue(String value) {
        String data = sharedPreferences.getString(value, "");

        ListPreference printerValuePreference = (ListPreference) getPreferenceScreen()
                .findPreference(value);
        printerValuePreference.setOnPreferenceChangeListener(this);
        if (!data.equals("")) {
            printerValuePreference.setSummary(data);
        }
    }

    /**
     * set data of a particular EditTextPreference
     */
    private void setEditValue(String value) {
        String name = sharedPreferences.getString(value, "");
        EditTextPreference printerValuePreference = (EditTextPreference) getPreferenceScreen()
                .findPreference(value);
        printerValuePreference.setOnPreferenceChangeListener(this);

        if (!name.equals("")) {
            printerValuePreference.setSummary(name);
        }
    }

    /**
     * Called when [printer] is tapped
     */
    private void setPrinterList(String printModel) {
        String port = sharedPreferences.getString("port", "");

        // call the Activity_NetPrinterList when port is NET
        if (port.equalsIgnoreCase("NET")) {
            Intent printerList = new Intent(this, Activity_NetPrinterList.class);
            String printTempModel = printModel.replaceAll("_", "-");
            printerList.putExtra("modelName", printTempModel);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        } else if (port.equalsIgnoreCase("BLE")) {
            Intent printerList = new Intent(this, Activity_BLEPrinterList.class);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        } else // call the Activity_BluetoothPrinterList when port is Bluetooth
        {
            Intent printerList = new Intent(this,
                    Activity_BluetoothPrinterList.class);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        }
    }


    /**
     * set paper size & port information with changing printer model
     */
    private void printerModelChange(String printerModel) {

        // paper size
        ListPreference paperSizePreference = (ListPreference) getPreferenceScreen()
                .findPreference("paperSize");
        // port
        ListPreference portPreference = (ListPreference) getPreferenceScreen()
                .findPreference("port");
        if (!printerModel.equals("")) {

            String[] entryPort;
            String[] entryPaperSize;
            entryPort = PrinterModelInfo.getPortOrPaperSizeInfo(printerModel, Common.SETTINGS_PORT);

            portPreference.setEntryValues(entryPort);
            portPreference.setEntries(entryPort);

        }
    }

    /**
     * initialize the address & macAddress information with changing printer
     * model or port
     */
    private void setChangedData() {
        EditTextPreference addressPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference("address");
        EditTextPreference macAddressPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference("macAddress");
        PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                .findPreference("printer");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", "");
        editor.putString("macAddress", "");
        editor.putString("printer", getString(R.string.printer_text));
        editor.apply();

        addressPreference.setText("");
        macAddressPreference.setText("");
        printerPreference.setSummary(getString(R.string.printer_text));
        macAddressPreference.setSummary(getString(R.string.mac_address_value));
        addressPreference.setSummary(getString(R.string.address_value));
    }

    /**
     * set the BackgroundForPreferenceScreens to light it is black when at OS
     * 2.1/2.2
     */
    private void setBackgroundForPreferenceScreens(String key) {
        PreferenceScreen preferenceScreen = (PreferenceScreen) getPreferenceScreen()
                .findPreference(key);

        preferenceScreen
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PreferenceScreen pref = (PreferenceScreen) preference;
                        pref.getDialog()
                                .getWindow()
                                .setBackgroundDrawableResource(
                                        android.R.drawable.screen_background_light);
                        return false;
                    }
                });
    }
}
