package br.net.du.fscore.persist;

import android.os.Environment;

public class DataConstants {

	private static final String APP_PACKAGE_NAME = "br.net.du.fscore";

	public static final String DATABASE_NAME = "fscore";
	public static final String DATABASE_PATH = Environment.getDataDirectory()
			+ "/data/" + DataConstants.APP_PACKAGE_NAME + "/databases/"
			+ DataConstants.DATABASE_NAME;

	public static final String DEBUG_DATABASE_NAME = "fscoredebug";
}
