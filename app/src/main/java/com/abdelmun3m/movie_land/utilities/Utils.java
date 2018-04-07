package com.abdelmun3m.movie_land.utilities;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by abdelmun3m on 07/04/18.
 */

public class Utils {



    public static void showAlert(Context context, String title , String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if(title != null)
            dialog.setTitle(title);
        if(message != null)
            dialog.setMessage(message);
        dialog.setPositiveButton("OK",null);
        dialog.create().show();
    }
}
