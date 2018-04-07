package com.abdelmun3m.movie_land.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by abdelmun3m on 24/03/18.
 */

public class FontManger {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "ionicons.ttf";

    public static final String YOUTUBE = "fa-youtube";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}
