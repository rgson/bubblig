package se.rgson.da401a.bubblig.model.readability;

import android.os.AsyncTask;
import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A static helper class for handling Readability API requests.
 */
public class Readability {

	private static final String TAG = Readability.class.getSimpleName();

	private static final String BASE_URL = "http://bubblig.rgson.se/readability.php?url=";

	/**
	 * Parses the content of the article at the supplied URL.
	 * The parsed article content is returned through the supplied ReadabilityListener upon completion.
	 *
	 * @param articleUrl The article's URL.
	 * @param listener   The ReadabilityListener containing the callback methods.
	 */
	public static void parse(String articleUrl, ReadabilityListener listener) {
		if (articleUrl == null || listener == null) {
			throw new IllegalArgumentException("Arguments must not be null.");
		}
		new AsyncParser().execute(articleUrl, listener);
	}

	/**
	 * Private constructor to disallow instantiation (static class).
	 */
	private Readability() {
	}

	/**
	 * Performs the parsing as an AsyncTask.
	 */
	private static class AsyncParser extends AsyncTask<Object, Void, ReadabilityResponse> {
		private ReadabilityListener listener;

		@Override
		protected ReadabilityResponse doInBackground(Object... params) {
			String articleUrl = (String) params[0];
			listener = (ReadabilityListener) params[1];

			try {
				ReadabilityResponse response = null;

				URL url = new URL(BASE_URL + articleUrl);
				URLConnection urlConnection = url.openConnection();
				InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

				try {
					Serializer serializer = new Persister();
					response = serializer.read(ReadabilityResponse.class, inputStream);
				} finally {
					inputStream.close();
				}

				return response;

			} catch (Exception e) {
				if (e.getMessage() != null) {
					Log.e(TAG, e.getMessage() + " (" + articleUrl + ")");
				} else {
					Log.e(TAG, e.toString());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(ReadabilityResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				listener.onSuccess(result);
			} else {
				listener.onError();
			}
		}
	}

}