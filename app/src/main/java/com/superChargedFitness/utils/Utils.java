package com.superChargedFitness.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import com.superChargedFitness.R;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static void setPref(Context c, String pref, Boolean val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putBoolean(pref, val);
        e.apply();
    }

    public static boolean getPref(Context c, String pref, Boolean val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(pref, val);
    }

    public static void setPref(Context context, String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value);
        editor.apply();

    }

    public static String getPref(Context context, String key, String value) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, value);
    }

    public static void setPref(Context context, String key, Integer value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value);
        editor.apply();
    }

    public static Integer getPref(Context context, String key, Integer value) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, value);
    }

    public static class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.rcy_divider_line);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public static int REQUEST_WRITE_STORAGE_REQUEST_CODE = 111;
    public static boolean checkPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        if (hasReadPermissions(context) && hasWritePermissions(context)) {
            return true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
            return false;
        }
    }

    private static boolean hasReadPermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean hasWritePermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static File createPackageDir(Context context, String dirName) {
        File file = new File(context.getFilesDir() + File.separator + dirName);
        if (!file.exists()) {
            file.mkdir();
            setPref(context, ConstantString.SHARE_IMAGE_PATH, file.getAbsolutePath());
        }
        return file;
    }


    /**
     * TODO Get Images From Assets
     */
    public static ArrayList<String> getAssetItems(Context mContext, String categoryName) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] imgPath;
        AssetManager assetManager = mContext.getAssets();
        try {
            imgPath = assetManager.list(categoryName);
            if (imgPath != null) {
                for (String anImgPath : imgPath) {
                    arrayList.add("///android_asset/" + categoryName + "/" + anImgPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /*
     * Dynamic adMod ad initialize , show and request to load methods
     * */
    static Boolean showAdd = false;

    public static void initAdd(final Context mcontext, final AdView adView) {
//        MobileAds.initialize(mcontext, mcontext.getResources().getString(R.string.ad_main_id));
//        final AdRequest adRequest = new AdRequest.Builder().build();
//
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                adView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
////                adView.setVisibility(View.GONE);
//                adView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
////                adView.setVisibility(View.VISIBLE);
//            }
//        });
//        adView.loadAd(adRequest);

    }

    public static void initFullAdd(Context mcontext) {
//        MobileAds.initialize(mcontext, mcontext.getResources().getString(R.string.ad_main_id));
//        mInterstitialAd = new InterstitialAd(mcontext);
//        mInterstitialAd.setAdUnitId(mcontext.getResources().getString(R.string.INTERSTITIAL));
//        reloadFullAdd(mcontext);
    }

    public static void showFullAdd(Context mcontext) {

//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            Log.d("TAG", "The interstitial wasn't loaded yet.");
//            showAdd = true;
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }

    }


    public static String ReplaceSpacialCharacters(String string) {
        return string.replace(" ", "").replace("&", "").replace("-", "").replace("'", "");
    }

    public static void openWebsite(Context c, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        c.startActivity(browserIntent);
    }


}
