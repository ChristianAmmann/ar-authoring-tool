package ncxp.de.arauthoringtool.ui.study.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fragments = new ArrayList<>();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	public void addFragment(Fragment fragment) {
		fragments.add(fragment);
	}

	public void addFragments(List<Fragment> newFragments) {
		fragments.addAll(newFragments);
	}

	@Override
	public int getCount() {
		return fragments != null ? fragments.size() : 0;
	}
}
