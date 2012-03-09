package br.net.du.fscore.activity.util;

import android.app.AlertDialog;
import android.content.Context;

public class ActivityUtils {
	public void showErrorDialog(Context context, String message) {
		new AlertDialog.Builder(context).setTitle("Error").setMessage(message)
				.setPositiveButton("Ok", null).show();
	}
}
