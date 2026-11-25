package aex.tgmod;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Tgmod per-account settings. Lives in the same per-account
 * userconfig / userconfig&lt;N&gt; SharedPreferences file as the rest of
 * {@link org.telegram.messenger.UserConfig} (pref keys unchanged, no
 * migration); UserConfig owns one instance and calls save/load/reset.
 */
public class tgAccountConfig {

    /** Pinned chats of the All chats list, top first (see aexPins). */
    public final ArrayList<Long> mainPins = new ArrayList<>();

    public void save(SharedPreferences.Editor editor) {
        editor.putString("mainPins", TextUtils.join(",", mainPins));
    }

    public void load(SharedPreferences preferences) {
        mainPins.clear();
        for (String pin : TextUtils.split(preferences.getString("mainPins", ""), ",")) {
            try {
                mainPins.add(Long.parseLong(pin));
            } catch (NumberFormatException ignore) {
            }
        }
    }

    public void reset() {
        mainPins.clear();
    }
}
