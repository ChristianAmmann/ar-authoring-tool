package ncxp.de.mobiledatacollection.ui.study.adapter;

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

	@Override
	public int getCount() {
		return fragments != null ? fragments.size() : 0;
	}
}
