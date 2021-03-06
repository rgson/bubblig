package se.rgson.da401a.bubblig.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import se.rgson.da401a.bubblig.Preferences;
import se.rgson.da401a.bubblig.R;
import se.rgson.da401a.bubblig.gui.components.CategoryListAdapter;
import se.rgson.da401a.bubblig.model.Category;

public class CategoryListFragment extends Fragment {

	private static final String TAG = CategoryListFragment.class.getSimpleName();
	private static final String BUNDLE_SELECTED = "BUNDLE_SELECTED";

	private CategoryListFragmentListener mListener;
	private ListView mCategoryList;
	private Preferences.PreferenceListener mPreferenceListener;

	public static CategoryListFragment newInstance() {
		return new CategoryListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPreferenceListener = new Preferences.PreferenceListener() {
			@Override
			public void onTextSizePreferenceChanged(float textSize) {
				if (mCategoryList != null) {
					((CategoryListAdapter)mCategoryList.getAdapter()).notifyDataSetChanged();
				}
			}
		};
		Preferences.attachPreferenceListener(mPreferenceListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_category_list, container, false);

		mCategoryList = (ListView) root.findViewById(R.id.category_list);
		mCategoryList.setAdapter(new CategoryListAdapter(getActivity()));

		if (savedInstanceState != null) {
			mCategoryList.setSelection(savedInstanceState.getInt(BUNDLE_SELECTED));
		}

		mCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setSelected(true);
				if (mListener != null) {
					mListener.onCategorySelected((Category) mCategoryList.getItemAtPosition(position));
				}
			}
		});

		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(BUNDLE_SELECTED, mCategoryList.getSelectedItemPosition());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Preferences.detachPreferenceListener(mPreferenceListener);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (CategoryListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement CategoryListFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface CategoryListFragmentListener {
		public void onCategorySelected(Category category);
	}

}
