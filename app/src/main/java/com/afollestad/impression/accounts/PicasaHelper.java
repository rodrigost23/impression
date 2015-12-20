package com.afollestad.impression.accounts;

import android.content.Context;

import com.afollestad.impression.api.MediaEntry;
import com.afollestad.impression.api.MediaFolderEntry;
import com.afollestad.impression.media.MediaAdapter;

import java.util.List;
import java.util.Set;

import rx.Single;

public class PicasaHelper extends AccountHelper {

    public PicasaHelper(Account account) {
        super(account);
    }

    @Override
    public Single<? extends Set<? extends MediaFolderEntry>> getMediaFolders(Context context, @MediaAdapter.SortMode int sortMode, @MediaAdapter.FileFilterMode int filter) {
        return null;
    }

    @Override
    public Single<List<MediaEntry>> getEntries(Context context, String albumPath, boolean explorerMode, @MediaAdapter.SortMode int sort, @MediaAdapter.FileFilterMode int filter) {
        return null;
    }

    @Override
    public boolean supportsExplorerMode() {
        return false;
    }
}
