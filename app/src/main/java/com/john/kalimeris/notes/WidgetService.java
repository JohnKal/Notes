package com.john.kalimeris.notes;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by John on 01/10/16.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        WidgetDataProvider dataProvider = new WidgetDataProvider(this, intent);
        return dataProvider;
    }
}
