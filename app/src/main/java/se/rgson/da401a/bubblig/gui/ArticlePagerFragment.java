package se.rgson.da401a.bubblig.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import se.rgson.da401a.bubblig.R;
import se.rgson.da401a.bubblig.gui.components.ArticleFragmentStatePagerAdapter;
import se.rgson.da401a.bubblig.model.Article;
import se.rgson.da401a.bubblig.model.Category;
import se.rgson.da401a.bubblig.model.CategoryListener;


public class ArticlePagerFragment extends Fragment {

	private static final String TAG = ArticlePagerFragment.class.getSimpleName();
	private static final String BUNDLE_CATEGORY = "BUNDLE_CATEGORY";
	private static final String BUNDLE_ARTICLE = "BUNDLE_ARTICLE";

	private ArticlePagerFragmentListener mListener;
	private ViewPager mViewPager;
	private ArticleFragmentStatePagerAdapter mAdapter;
	private Category mCategory;
	private ArrayList<Article> mArticles;

	public static ArticlePagerFragment newInstance(Category category, Article article) {
		ArticlePagerFragment fragment = new ArticlePagerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_CATEGORY, category);
		bundle.putSerializable(BUNDLE_ARTICLE, article);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCategory = (Category) savedInstanceState.getSerializable(BUNDLE_CATEGORY);
		}
		else if (getArguments() != null) {
			mCategory = (Category) getArguments().getSerializable(BUNDLE_CATEGORY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_article_pager, container, false);

		mViewPager = (ViewPager) root.findViewById(R.id.article_view_pager);

		if (mCategory != null) {
			mCategory.getArticles(new CategoryListener() {
				@Override
				public void onCategoryLoaded(ArrayList<Article> articles) {
					mArticles = articles;
					mAdapter = new ArticleFragmentStatePagerAdapter(getFragmentManager(), mArticles);
					mViewPager.setAdapter(mAdapter);
					if (getArguments() != null) {
						Article currentArticle = (Article) getArguments().getSerializable(BUNDLE_ARTICLE);
						mViewPager.setCurrentItem(articles.indexOf(currentArticle));
					}
				}
			});
		}

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (mListener != null) {
					mListener.onDisplayedArticleChanged(mArticles.get(position));
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(BUNDLE_CATEGORY, mCategory);
		if (mViewPager.getCurrentItem() != -1) {
			outState.putSerializable(BUNDLE_ARTICLE, mArticles.get(mViewPager.getCurrentItem()));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ArticlePagerFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ArticlePagerFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface ArticlePagerFragmentListener {
		void onDisplayedArticleChanged(Article article);
	}

}
