package se.rgson.da401a.bubblig.gui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.rgson.da401a.bubblig.R;
import se.rgson.da401a.bubblig.model.Article;

public class ArticleFragment extends Fragment {

	private static final String TAG = ArticleFragment.class.getSimpleName();
	private static final String BUNDLE_ARTICLE = "BUNDLE_ARTICLE";

	private Article mArticle;
	private TextView mArticleContent;

	public static ArticleFragment newInstance(Article article) {
		ArticleFragment fragment = new ArticleFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_ARTICLE, article);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mArticle = (Article) savedInstanceState.getSerializable(BUNDLE_ARTICLE);
		}
		else if (getArguments() != null) {
			mArticle = (Article) getArguments().getSerializable(BUNDLE_ARTICLE);
		}
		else {
			throw new IllegalArgumentException("An article must be provided.");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_article, container, false);
		mArticleContent = (TextView) root.findViewById(R.id.article_content);
		new AsyncContentHandler().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		Activity activity = getActivity();
		activity.getActionBar().setTitle(mArticle.getCategory().toString());
		activity.getActionBar().setBackgroundDrawable(new ColorDrawable(GuiUtility.findColorFor(activity, mArticle.getCategory())));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(BUNDLE_ARTICLE, mArticle);
	}

	private class AsyncContentHandler extends AsyncTask<Void, Void, Spanned> {
		@Override
		protected Spanned doInBackground(Void... params) {
			Spanned content = Html.fromHtml(mArticle.getContent());
			if (content == null) {
				content = new SpannableString(getResources().getString(R.string.article_loading_failed));
			}
			return content;
		}

		@Override
		protected void onPostExecute(Spanned result) {
			//TODO Dangerous time sink for long articles. Process in parts while scrolling, using append().
			mArticleContent.setText(result);
		}
	}

}
