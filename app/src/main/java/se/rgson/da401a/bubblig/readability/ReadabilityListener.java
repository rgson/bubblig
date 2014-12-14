package se.rgson.da401a.bubblig.readability;

/**
 * Interface used for parsing callbacks.
 */
public interface ReadabilityListener {
	/**
	 * Called upon successful parsing of an article.
	 *
	 * @param response The parsed response.
	 */
	void onSuccess(ReadabilityResponse response);

	/**
	 * Called upon failure when parsing an article.
	 *
	 * @param e The exception that occurred during parsing.
	 */
	void onError(Exception e);
}