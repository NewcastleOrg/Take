package aex.tgmod.helpers;

import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.utils.proxy.ProxySettings;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class ProxyImportHelper {

    public static void importFromClipboard(BaseFragment fragment) {
        Context context = fragment.getContext();
        if (context == null) {
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        android.content.ClipData clip =
                clipboard != null ? clipboard.getPrimaryClip() : null;

        String text = "";

        if (clip != null && clip.getItemCount() > 0) {
            CharSequence value = clip.getItemAt(0).coerceToText(context);
            text = value != null ? value.toString() : "";
        }

        SharedConfig.loadProxyList();

        HashSet<ProxySettings> known = new HashSet<>();
        for (SharedConfig.ProxyInfo info : SharedConfig.proxyList) {
            known.add(info.settings);
        }

        List<SharedConfig.ProxyInfo> imported = new ArrayList<>();

        for (String line : text.split("\\n")) {
            ProxySettings settings = ProxySettings.fromUri(Uri.parse(line.trim()));

            if (settings != null && settings.isValid() && known.add(settings)) {
                imported.add(new SharedConfig.ProxyInfo(settings));
            }
        }

        if (imported.isEmpty()) {
            BulletinFactory.of(fragment).createErrorBulletin(
                    LocaleController.getString(R.string.ProxyImportNothing)
            ).show();
            return;
        }

        SharedConfig.proxyList.addAll(0, imported);
        SharedConfig.saveProxyList();

        NotificationCenter.getGlobalInstance().postNotificationName(
                NotificationCenter.proxySettingsChanged
        );

        BulletinFactory.of(fragment).createSimpleBulletin(
                R.raw.contact_check,
                LocaleController.formatPluralString("ProxyImported", imported.size())
        ).show();
    }
}
