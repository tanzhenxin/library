package com.gtcc.library.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.gtcc.library.R;
import com.gtcc.library.ui.customcontrol.RefreshableListView;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.ImageCache.ImageCacheParams;

public abstract class AbstractBookListFragment extends SherlockListFragment {
	private static final String IMAGE_CACHE_DIR = "images";

	protected ImageFetcher mImageFetcher;
	protected Animation mApplaudAnimation;
	
	private int mImageWidth;
	private int mImageHeight;
	
    public interface Callbacks {
        public boolean OnBookSelected(String bookId, int page);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean OnBookSelected(String bookId, int page) {
            return true;
        }
    };

    private Callbacks mCallbacks = sDummyCallbacks;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mImageWidth = getResources().getDimensionPixelSize(R.dimen.list_image_width);
		mImageHeight = getResources().getDimensionPixelSize(R.dimen.list_image_height);
		
        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageWidth, mImageHeight);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
        
        setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_books_list, container, false);

        RefreshableListView listView = (RefreshableListView) rootView.findViewById(android.R.id.list);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
			}
		});
		
		return rootView;
	}

	protected abstract int getPage();
	protected abstract String getSelectedBookId(ListView l, View v, int position, long id);
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String bookId = getSelectedBookId(l,v, position, id);
		mCallbacks.OnBookSelected(bookId, getPage());
	}
	
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
}
