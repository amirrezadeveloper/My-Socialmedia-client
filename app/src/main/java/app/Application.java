package app;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.noxsocial.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import es.dmoral.toasty.Toasty;

public class Application extends android.app.Application {

    public static Context context ;
    public static ImageLoaderConfiguration config;
    public static DisplayImageOptions options ;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this ;


        config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.person) // resource or drawable
                .showImageForEmptyUri(R.drawable.person) // resource or drawable
                .showImageOnFail(R.drawable.person) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default

                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default

                .build();


        Toasty.Config.getInstance().apply();
    }

    public static Context getContext(){return context ;}
}
