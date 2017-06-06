package org.openstenoproject.dotterel;

import android.app.Application;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.widget.ProgressBar;

import org.openstenoproject.dotterel.Input.StenoMachine;
import org.openstenoproject.dotterel.Translator.Dictionary;
import org.openstenoproject.dotterel.Translator.Translator;

/**
 * Created by brent on 30/11/13.
 * Hold some state, such as the loaded dictionary
 * and other various settings
 */
public class DotterelApp extends Application {

    public static final String DELIMITER = ":";
    public static final String TAG = DotterelApp.class.getSimpleName();

    private static final boolean USE_WORD_LIST = true;
    private static final boolean SHOW_PERFORMANCE_NOTIFICATIONS = true;

    private Dictionary mDictionary;
    private StenoMachine mInputDevice = null;
    private UsbDevice mUsbDevice;
    private Translator.TYPE mTranslatorType;
    private StenoMachine.TYPE mMachineType;
    private SharedPreferences prefs;
    private ProgressBar mProgressBar = null;
    private boolean nkro_enabled = false;
    private boolean txbolt_enabled = false;
    private boolean optimizer_enabled = false;
    private boolean tts_enabled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        mDictionary = new Dictionary(getApplicationContext());
        nkro_enabled = prefs.getBoolean(getString(org.openstenoproject.dotterel.R.string.pref_kbd_enabled), false);
        optimizer_enabled = prefs.getBoolean(getString(org.openstenoproject.dotterel.R.string.pref_optimizer_enabled), false);
        tts_enabled = prefs.getBoolean(getString(org.openstenoproject.dotterel.R.string.pref_tts_enabled), false);
        int val = Integer.parseInt(prefs.getString(getString(org.openstenoproject.dotterel.R.string.pref_translator), "1"));
        mTranslatorType = Translator.TYPE.values()[val];
        mMachineType = StenoMachine.TYPE.VIRTUAL;
    }

    // Setters
    public void setInputDevice(StenoMachine sm) {
        mInputDevice = sm;}
    public void setUsbDevice(UsbDevice ud) { mUsbDevice = ud; }
    public void setProgressBar(ProgressBar pb) { mProgressBar = pb; }
    public void setMachineType(StenoMachine.TYPE t) {
        nkro_enabled = prefs.getBoolean(getString(org.openstenoproject.dotterel.R.string.pref_kbd_enabled), false);
        switch (t) {
            case VIRTUAL: mMachineType = t;
                break;
            case KEYBOARD: if (nkro_enabled) mMachineType = t;
                break;
            case TXBOLT: if (txbolt_enabled) mMachineType = t;
        }
        if (mMachineType==null) mMachineType= StenoMachine.TYPE.VIRTUAL;
    }
    public void setTranslatorType(Translator.TYPE t) { mTranslatorType = t; }
    public void setOptimizerEnabled(boolean setting) {optimizer_enabled = setting; }
    public void setTts(boolean enabled){
        tts_enabled = enabled;
        prefs.edit().putBoolean(getString(org.openstenoproject.dotterel.R.string.pref_tts_enabled), enabled).apply();
    }

    // Getters
    public StenoMachine getInputDevice() {return mInputDevice; }
    public UsbDevice getUsbDevice() { return mUsbDevice; }
    public StenoMachine.TYPE getMachineType() { return mMachineType; }
    public Translator.TYPE getTranslatorType() { return mTranslatorType; }
    public ProgressBar getProgressBar() {return mProgressBar; }
    public boolean useWordList() { return USE_WORD_LIST; }
    public boolean showPerformanceNotifications() {return SHOW_PERFORMANCE_NOTIFICATIONS;}
    public boolean isNKRO_enabled() {
        nkro_enabled = prefs.getBoolean(getString(org.openstenoproject.dotterel.R.string.pref_kbd_enabled), false);
        return nkro_enabled;
    }
    public boolean isOptimizerEnabled() {return optimizer_enabled;}
    public boolean getTts(){ return tts_enabled; }

    public Dictionary getDictionary(Dictionary.OnDictionaryLoadedListener listener) {
        // if dictionary is empty, load it - otherwise just return it
        // if listener is null, don't reset it (use last registered listener)
        if ((!isDictionaryLoaded()) && (!mDictionary.isLoading()) ) {
            int size = prefs.getInt(getString(org.openstenoproject.dotterel.R.string.key_dictionary_size), 100000);
            mDictionary.load(getDictionaryNames(), getAssets(), size);
        }
        if (listener != null) {
            mDictionary.setOnDictionaryLoadedListener(listener);
        } else {
            Log.w(TAG, "Dictionary callback is null");
        }

        return mDictionary;
    }

    public void unloadDictionary() {
        if (mDictionary!=null) {
            Log.d(TAG, "Unloading Dictionary");
            mDictionary.clear();
            mDictionary = new Dictionary(getApplicationContext());
        }
    }

    public String[] getDictionaryNames() {
        String data = prefs.getString(getString(org.openstenoproject.dotterel.R.string.key_dictionaries), "");
        if (data.isEmpty()) {
            return new String[0];
        }
        return data.split(DELIMITER);
    }

    public boolean isDictionaryLoaded() {
        return (mDictionary != null && (!mDictionary.isLoading()) && mDictionary.size() > 10);
    }
}
