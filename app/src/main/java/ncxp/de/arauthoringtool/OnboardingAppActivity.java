package ncxp.de.arauthoringtool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.arauthoringtool.ui.onboarding.app.OnboardingAboutFragment;
import ncxp.de.arauthoringtool.ui.onboarding.app.OnboardingArSceneFragment;
import ncxp.de.arauthoringtool.ui.onboarding.app.OnboardingPermissionFragment;
import ncxp.de.arauthoringtool.ui.onboarding.app.OnboardingStudyFragment;
import ncxp.de.arauthoringtool.ui.study.adapter.ViewPagerAdapter;

public class OnboardingAppActivity extends AppCompatActivity {

	private ViewPagerAdapter viewPagerAdapter;
	private ViewPager        viewPager;
	private Button           continueButton;
	private LinearLayout     dotsLayout;
	private List<ImageView>  dots;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding_app);
		viewPager = findViewById(R.id.pager);
		continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener((view) -> {
			SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.ONBOARDING_KEY, MODE_PRIVATE);
			sharedPreferences.edit().putBoolean(MainActivity.ONBOARDING_COMPLETED, true).apply();
			Intent intent = new Intent(this, StudiesActivity.class);
			startActivity(intent);
			finish();
		});
		dotsLayout = findViewById(R.id.dots);
		setupViewPagerAdapter();
		addDots();
	}

	private void setupViewPagerAdapter() {
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPagerAdapter.addFragments(getFragments());
		viewPager.setAdapter(viewPagerAdapter);
	}

	private void addDots() {
		initDots();
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				selectDot(position);
				if (position == viewPagerAdapter.getCount() - 1) {
					setupButton();
				} else {
					continueButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void setupButton() {
		if (viewPager.getCurrentItem() < viewPagerAdapter.getCount() - 1) {
			continueButton.setVisibility(View.GONE);
			return;
		}
		continueButton.setVisibility(View.VISIBLE);
	}

	private void initDots() {
		dots = new ArrayList<>();
		for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
			ImageView dot = new ImageView(this);
			if (viewPager.getCurrentItem() == i) {
				dot.setImageDrawable(getDrawable(R.drawable.pager_dot_selected));
			} else {
				dot.setImageDrawable(getDrawable(R.drawable.pager_dot_not_selected));
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 0, 10, 0);
			dotsLayout.addView(dot, params);
			dots.add(dot);
		}
	}

	private void selectDot(int idx) {
		for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
			int drawableId = (i == idx) ? (R.drawable.pager_dot_selected) : (R.drawable.pager_dot_not_selected);
			Drawable drawable = getDrawable(drawableId);
			dots.get(i).setImageDrawable(drawable);
		}
	}

	private List<Fragment> getFragments() {
		List<Fragment> fragments = new ArrayList<>();
		fragments.add(OnboardingAboutFragment.newInstance());
		fragments.add(OnboardingPermissionFragment.newInstance());
		fragments.add(OnboardingStudyFragment.newInstance());
		fragments.add(OnboardingArSceneFragment.newInstance());
		return fragments;
	}
}
