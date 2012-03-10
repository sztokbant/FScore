package br.net.du.fscore.activity.util;

import android.app.AlertDialog;
import android.content.Context;
import br.net.du.fscore.R;

public class ActivityUtils {
	private static final int ADAPTER_HORIZONTAL_PADDING = 4;
	private static final int ADAPTER_VERTICAL_PADDING = 8;
	private static final int ADAPTER_FONT_SIZE = 18;

	public void showErrorDialog(Context context, String message) {
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.error))
				.setMessage(message)
				.setPositiveButton(context.getString(R.string.ok), null).show();
	}

	public int getAdapterHorizontalPadding() {
		return ADAPTER_HORIZONTAL_PADDING;
	}

	public int getAdapterVerticalPadding() {
		return ADAPTER_VERTICAL_PADDING;
	}

	public int getAdapterFontSize() {
		return ADAPTER_FONT_SIZE;
	}
}
