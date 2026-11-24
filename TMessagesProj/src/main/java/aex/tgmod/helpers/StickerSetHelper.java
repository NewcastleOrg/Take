package aex.tgmod.helpers;

import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;

public final class StickerSetHelper {

    private StickerSetHelper() {
        // Utility class
    }

    public static void refreshStickerSet(
            int accountId,
            FrameLayout container,
            Theme.ResourcesProvider resourcesProvider,
            TLRPC.TL_messages_stickerSet stickerSet,
            Utilities.Callback<TLRPC.TL_messages_stickerSet> onLoaded
    ) {
        if (stickerSet == null || stickerSet.set == null) {
            return;
        }

        TLRPC.TL_messages_getStickerSet req =
                new TLRPC.TL_messages_getStickerSet();

        req.stickerset =
                MediaDataController.getInputStickerSet(stickerSet.set);

        ConnectionsManager.getInstance(accountId).sendRequest(req, (response, error) -> {

            AndroidUtilities.runOnUIThread(() -> {

                if (!(response instanceof TLRPC.TL_messages_stickerSet)) {
                    BulletinFactory.of(container, resourcesProvider)
                            .createErrorBulletin(
                                    LocaleController.getString(
                                            R.string.StickerSetRefreshFailed
                                    )
                            )
                            .show();

                    return;
                }

                TLRPC.TL_messages_stickerSet newSet =
                        (TLRPC.TL_messages_stickerSet) response;

                MediaDataController.getInstance(accountId)
                        .putStickerSet(newSet, true);

                if (onLoaded != null) {
                    onLoaded.run(newSet);
                }

                BulletinFactory.of(container, resourcesProvider)
                        .createSimpleBulletin(
                                R.raw.contact_check,
                                LocaleController.getString(
                                        R.string.StickerSetRefreshed
                                )
                        )
                        .show();
            });
        });
    }
}
