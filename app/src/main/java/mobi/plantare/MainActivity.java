package mobi.plantare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import mobi.plantare.fragments.GardenMapFragment;
import mobi.plantare.fragments.SocialFragment;
import mobi.plantare.fragments.UserFragment;
import mobi.plantare.model.Plant;

public class MainActivity extends AppCompatActivity implements FacebookCallback<Sharer.Result> {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, this);

        // Set up the action bar.
        // final ActionBar actionBar = getSupportActionBar();
        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });

        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//            actionBar.addTab(actionBar.newTab()
//                            .setText(mSectionsPagerAdapter.getPageTitle(i))
//                            .setTabListener(this));
//        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new GardenMapFragment(), getString(R.string.title_section1));
        adapter.addFragment(new SocialFragment(), getString(R.string.title_section2));
        adapter.addFragment(new UserFragment(), getString(R.string.title_section3));
        viewPager.setAdapter(adapter);
    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setChecked(true);
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });
//    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
//        Log.d("Main", "tab:" + tab.getPosition());
////        if (tab.getPosition() == 2){
//
////        }
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void shareOnFacebook() {

        Plant mPlant = PlantareApp.getInstance().getLastPlant();

        if(mPlant == null) {
            Log.e("Plantare", "Last plant returns null");
        }else if (mPlant.getPhoto() == null) {
            Log.e("Plantare", "Don't have a plant photo to share");
        } else if(ShareDialog.canShow(SharePhotoContent.class)) {

            byte[] byteArray = Base64.decode(mPlant.getPhoto(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bmp)
                    .build();

            ShareHashtag.Builder hashTagBuiler = new ShareHashtag.Builder();
            hashTagBuiler.setHashtag("#plantare");
            ShareHashtag hashTag = hashTagBuiler.build();

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .setShareHashtag(hashTag)
                    .build();

            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        } else{
            Log.e("Plantare", "It's not possible share a photo on Facebook.\n" +
                  "Verify if application have the necessary permission to perform this action.");
        }
    }

    public Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return  getSupportFragmentManager().findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public void onSuccess(Sharer.Result result) {
        Toast.makeText(this, "Sua boa ação foi compartilhada com sucesso!", Toast.LENGTH_LONG).show();

        Fragment frag = getActiveFragment(mViewPager, 0);

        if(frag == null){
            Log.e("Plantate", "frag is null");
        }else if(frag instanceof GardenMapFragment){
            ((GardenMapFragment) frag).dismissDialog();
        }
    }

    @Override
    public void onCancel() {
        Log.e("Plantate", "The sharing on Facebook was cancelled");
    }

    @Override
    public void onError(FacebookException error) {

        if(error.getMessage().contains("facebookErrorCode: 190") || error.getMessage().contains("facebookErrorCode: 2500")){
            Toast.makeText(this, "Conecte-se ao Facebook para compartilhar", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Falha ao compartilhar", Toast.LENGTH_LONG).show();
        }

        Log.e("Plantare", "FacebookException on share action: "+error.getMessage());
    }
}