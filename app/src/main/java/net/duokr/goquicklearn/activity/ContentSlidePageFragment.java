package net.duokr.goquicklearn.activity;

import net.duokr.goquicklearn.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jemy on 1/25/14.
 */
public class ContentSlidePageFragment extends Fragment {

    public static ContentSlidePageFragment newInstance(String chapter) {
        ContentSlidePageFragment fragment = new ContentSlidePageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chapter", chapter);
        fragment.setArguments(bundle);
        return fragment;
    }

    private WebView contentWebView;
    private SharedPreferences mSP;

    private String chapter;
    private float scrollPercent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(
				R.layout.fragment_content_slide_page, container, false);
	}

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View progressBar = view.findViewById(R.id.contentLoadingProgressBar);

        contentWebView = (WebView) view.findViewById(R.id.contentWebView);
        contentWebView.setVisibility(View.INVISIBLE);
        WebSettings settings = contentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        contentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(final WebView webView, String url) {
                if(scrollPercent != 0) {
                    Handler handler = new Handler() {
                        int times = 0;
                        @Override
                        public void handleMessage(Message msg) {
                            float contentHeight = webView.getContentHeight() - webView.getTop();
                            if(contentHeight == 0) {
                                sendEmptyMessage(0);
                                return;
                            }
                            if(scrollPercent == 0) {
                                showWebView();
                                removeMessages(0);
                                return;
                            }
                            int scrollY = contentWebView.getScrollY();
                            float positionInWV = contentHeight * scrollPercent;
                            int positionY = Math.round(webView.getTop() + positionInWV);

                            if(times >= 2 || scrollY == positionY) {
                                showWebView();
                                removeMessages(0);
                            } else {
                                times ++;
                                webView.scrollTo(0, positionY);
                                sendEmptyMessage(0);
                            }
                        }
                    };
                    handler.sendEmptyMessage(0);
                } else {
                    showWebView();
                }
            }

            private void showWebView() {
                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                contentWebView.setVisibility(View.VISIBLE);
            }

        });
        // fix the white background which switching
        contentWebView.setBackgroundColor(0);
        contentWebView.loadUrl("file:///android_asset/tutorial/" + chapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        float contentHeight = contentWebView.getContentHeight() - contentWebView.getTop();
        int scrollY = contentWebView.getScrollY();
        if(contentHeight > 0) {
            scrollPercent = ((float) scrollY / contentHeight);
            mSP.edit().putFloat("scroll_" + chapter, scrollPercent).commit();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chapter = getArguments().getString("chapter");
        mSP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        scrollPercent = mSP.getFloat("scroll_" + chapter, 0);
    }
}
