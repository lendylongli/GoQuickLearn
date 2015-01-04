package net.duokr.goquicklearn.activity;

import java.util.List;

import net.duokr.goquicklearn.config.LearnContent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jemy on 1/25/14.
 */
public class ContentSlidePagerAdapter extends FragmentPagerAdapter {
    List<LearnContent> learnContentList;

	public ContentSlidePagerAdapter(FragmentManager fm, List<LearnContent> learnContentList) {
		super(fm);
        this.learnContentList = learnContentList;
	}

	@Override
	public Fragment getItem(int position) {
		LearnContent content = this.learnContentList.get(position);
        ContentSlidePageFragment fragment = ContentSlidePageFragment.newInstance(content.getChapter());
        return fragment;
	}

	@Override
	public int getCount() {
		return this.learnContentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return this.learnContentList.get(position).getContentName();
	}
}
