package org.openstenoproject.dotterel;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import org.openstenoproject.dotterel.Translator.Translator;


/**
 * Created by brent on 01/12/13.
 * This is my main settings activity - which also handles purchase activity results
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int SELECT_DICTIONARY_CODE = 4;

    private DotterelApp App;
    private SwitchPreference keyboardSwitch;
    private SwitchPreference ttsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App = ((DotterelApp) getApplication());
        addPreferencesFromResource(org.openstenoproject.dotterel.R.xml.preferences);
        initializeControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: request:"+requestCode+" result:"+resultCode);
        switch (requestCode) {
            case SELECT_DICTIONARY_CODE : {
                Log.d(TAG, "Dictionaries selected");
                Preference dict_button = findPreference(getString(org.openstenoproject.dotterel.R.string.pref_dictionary_button));
                dict_button.setSummary(getDictionaryList());
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void verifyEnabled() {
        InputMethodManager imeManager = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        for (InputMethodInfo i : imeManager.getEnabledInputMethodList()) {
            if (i.getPackageName().equals(getApplication().getPackageName())) return;
        }
        Log.d(TAG, "Steno Keyboard is not enabled");
        startActivity(new Intent(this, SetupActivity.class));
    }

    private void initializeControls() {
        // tutorial button
        Preference btn_tutorial = findPreference(getString(org.openstenoproject.dotterel.R.string.key_tutorial_button));
        btn_tutorial.setIcon(org.openstenoproject.dotterel.R.drawable.ic_info);
        btn_tutorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SettingsActivity.this, TutorialActivity.class));
                return false;
            }
        });

        // set translator options
        SwitchPreference zoom = (SwitchPreference) findPreference("pref_zoom_enabled");

        ListPreference translator = (ListPreference) findPreference(getString(org.openstenoproject.dotterel.R.string.pref_translator));
        translator.setSummary(translator.getEntry());
        translator.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference translator = (ListPreference) preference;
                translator.setSummary(translator.getEntry());
                Translator.TYPE tType = Translator.TYPE.values()[Integer.parseInt(newValue.toString())];
                App.setTranslatorType(tType);
                Log.d(TAG, "Setting translator type:"+tType);
                findPreference(getResources().getString(org.openstenoproject.dotterel.R.string.pref_optimizer_enabled)).setEnabled(!newValue.equals("0"));
                return true;
            }
        });
        SwitchPreference optimizer = (SwitchPreference) findPreference(getResources().getString(org.openstenoproject.dotterel.R.string.pref_optimizer_enabled));
        optimizer.setEnabled(!(translator.getValue().equals("0")));
        optimizer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean setting = ((SwitchPreference) preference).isChecked();
                App.setOptimizerEnabled(setting);
                return true;
            }
        });
        // list dictionaries
        Preference dict_button = findPreference(getString(org.openstenoproject.dotterel.R.string.pref_dictionary_button));
        dict_button.setSummary(getDictionaryList());
        dict_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingsActivity.this, SelectDictionaryActivity.class);
                startActivityForResult(intent, SELECT_DICTIONARY_CODE);
                return false;
            }
        });
        // hardware switches
        keyboardSwitch = (SwitchPreference) findPreference(getString(org.openstenoproject.dotterel.R.string.pref_kbd_enabled));
        assert keyboardSwitch != null;
        ttsSwitch = (SwitchPreference) findPreference("pref_tts_enabled");
        ttsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean switched = (Boolean) o;
                App.setTts(switched);
                return switched;
            }
        });
    }

    private String getDictionaryList() {
        StringBuilder dictionaryList = new StringBuilder();
        String[] dictionaries = App.getDictionaryNames();
        if (dictionaries.length>0) {
            for (String d : dictionaries) {
                if (d.contains("/")) {
                    dictionaryList.append(" - ").append(d.substring(d.lastIndexOf("/")+1)).append("\n");
                } else {
                    dictionaryList.append(" - ").append(d).append("\n");
                }
            }
        } else {
            dictionaryList.append("Built-in Dictionary");
        }
        return dictionaryList.toString();
    }

}
