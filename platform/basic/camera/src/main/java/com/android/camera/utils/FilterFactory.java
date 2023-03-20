package com.android.camera.utils;

import android.content.Context;
import com.android.camera.filters.AmaroFilter;
import com.android.camera.filters.AntiqueFilter;
import com.android.camera.filters.BaseFilter;
import com.android.camera.filters.BeautyFilter;
import com.android.camera.filters.BlackCatFilter;
import com.android.camera.filters.BlackFilter;
import com.android.camera.filters.BrannanFilter;
import com.android.camera.filters.BrooklynFilter;
import com.android.camera.filters.CalmFilter;
import com.android.camera.filters.CoolFilter;
import com.android.camera.filters.HealthyFilter;
import com.android.camera.filters.LatteFilter;
import com.android.camera.filters.OriginalFilter;
import com.android.camera.filters.RomanceFilter;
import com.android.camera.filters.SakuraFilter;
import com.android.camera.filters.SkinWhitenFilter;
import com.android.camera.filters.SunriseFilter;
import com.android.camera.filters.SunsetFilter;
import com.android.camera.filters.SweetsFilter;
import com.android.camera.filters.WarmFilter;
import com.android.camera.filters.WhiteCatFilter;

public class FilterFactory {

    public enum FilterType {

        SkinWhiten,
        BlackWhite,
        BlackCat,
        WhiteCat,
        Healthy,
        Romance,
        Original,
        Sunrise,
        Sunset,
        Sakura,
        Latte,
        Warm,
        Calm,
        Brooklyn,
        Cool,
        Sweets,
        Amaro,
        Antique,
        Brannan,
        Beauty
    }

    public static BaseFilter createFilter(Context c, FilterType filterType) {

        BaseFilter baseFilter = null;

        switch (filterType) {

            case Sakura:

                baseFilter = new SakuraFilter(c);
                break;
            case Sunset:

                baseFilter = new SunsetFilter(c);
                break;

            case Healthy:

                baseFilter = new HealthyFilter(c);

                break;
            case Romance:

                baseFilter = new RomanceFilter(c);

                break;

            case Sunrise:

                baseFilter = new SunriseFilter(c);

                break;
            case BlackCat:

                baseFilter = new BlackCatFilter(c);

                break;

            case Original:

                baseFilter = new OriginalFilter(c);

                break;

            case WhiteCat:

                baseFilter = new WhiteCatFilter(c);

                break;

            case BlackWhite:

                baseFilter = new BlackFilter(c);

                break;

            case SkinWhiten:

                baseFilter = new SkinWhitenFilter(c);

                break;

            case Calm:

                baseFilter = new CalmFilter(c);
                break;

            case Warm:

                baseFilter = new WarmFilter(c);

                break;
            case Latte:

                baseFilter = new LatteFilter(c);

                break;

            case Cool:

                baseFilter = new CoolFilter(c);
                break;

            case Sweets:

                baseFilter = new SweetsFilter(c);
                break;

            case Brooklyn:

                baseFilter = new BrooklynFilter(c);

                break;
            case Amaro:

                baseFilter = new AmaroFilter(c);
                break;

            case Antique:

                baseFilter = new AntiqueFilter(c);

                break;
            case Brannan:

                baseFilter = new BrannanFilter(c);

                break;
            case Beauty:

                baseFilter = new BeautyFilter(c);

                break;


        }

        return baseFilter;
    }


}
