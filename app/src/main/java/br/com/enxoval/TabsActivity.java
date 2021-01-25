package br.com.enxoval;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import br.com.enxoval.fragments.CategoriasFragment;
import br.com.enxoval.fragments.MapsFragment;

public class TabsActivity extends MenuEnxovalActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static GoogleMap mMap;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, R.layout.tabs_activity, R.id.tabs_lly);

        //setContentView(R.layout.tabs_activity);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try
        {
            if(Sistema.getStatusConfigSistema(getApplicationContext()) != null)
                if(Sistema.getStatusConfigSistema(getApplicationContext()) == 1)
                {
                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    setupViewPager(viewPager);

                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
                    return;
                }
            Intent it = new Intent(TabsActivity.this, ConfigBebeActivity.class);
            startActivity(it);
            finish();
        }
        catch(Exception e)
        {
            Intent it = new Intent(TabsActivity.this, ConfigBebeActivity.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CategoriasFragment(), getString(R.string.Tabs_tab_list));
        adapter.addFragment(new MapsFragment(), getString(R.string.tabs_tab_store));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed(){
        if(viewPager.getCurrentItem() == 1){
            viewPager.setCurrentItem(0);
        }
        else
            super.onBackPressed();
    }

    public static GoogleMap getmMap() {
        return mMap;
    }

    public static void setmMap(GoogleMap mMap) {
        TabsActivity.mMap = mMap;
    }
}
