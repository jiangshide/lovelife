package com.android.sanskrit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.android.utils.LogUtil;
import com.android.widget.ZdToast;

import java.util.List;

/**
 * created by jiangshide on 2020/7/28.
 * email:18311271399@163.com
 */
public class AppChannel {

    /**
     * 遍历手机内应用包名
     *
     * @param context
     */
    public static void loadApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            String packageName = info.activityInfo.packageName;
            CharSequence cls = info.activityInfo.name;
            CharSequence name = info.activityInfo.loadLabel(context.getPackageManager());
            LogUtil.e("wgh！！！！！", name + "----" + packageName + "----" + cls);
        }
    }

    /**
     * 启动到app详情界面
     *
     * @param appPkg    App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + appPkg));
        String[] keys = getKeys(context);
        if (keys != null) {
            intent.setClassName(keys[0], keys[1]);
        }
        LogUtil.e("--------appPkg:",appPkg," | keys:",keys);
        //修复某些老手机会因为找不到任何市场而报错
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            ZdToast.txt("应用市场不存在");
        }
    }

    private static String[] getKeys(Context context) {
        String[] keys = new String[2];
        if (isPackageExist(context, PACKAGE_MI_MARKET)) {
            keys[0] = PACKAGE_MI_MARKET;
            keys[1] = MI_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_VIVO_MARKET)) {
            keys[0] = PACKAGE_VIVO_MARKET;
            keys[1] = VIVO_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_OPPO_MARKET)) {
            keys[0] = PACKAGE_OPPO_MARKET;
            keys[1] = OPPO_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_HUAWEI_MARKET)) {
            keys[0] = PACKAGE_HUAWEI_MARKET;
            keys[1] = HUAWEI_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_ZTE_MARKET)) {
            keys[0] = PACKAGE_ZTE_MARKET;
            keys[1] = ZTE_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_COOL_MARKET)) {
            keys[0] = PACKAGE_COOL_MARKET;
            keys[1] = COOL_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_360_MARKET)) {
            keys[0] = PACKAGE_360_MARKET;
            keys[1] = PACKAGE_360_PAGE;
        } else if (isPackageExist(context, PACKAGE_MEIZU_MARKET)) {
            keys[0] = PACKAGE_MEIZU_MARKET;
            keys[1] = MEIZU_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_TENCENT_MARKET)) {
            keys[0] = PACKAGE_TENCENT_MARKET;
            keys[1] = TENCENT_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_ALI_MARKET)) {
            keys[0] = PACKAGE_ALI_MARKET;
            keys[1] = ALI_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_WANDOUJIA_MARKET)) {
            keys[0] = PACKAGE_WANDOUJIA_MARKET;
            keys[1] = WANDOUJIA_MARKET_PAGE;
        } else if (isPackageExist(context, PACKAGE_UCWEB_MARKET)) {
            keys[0] = PACKAGE_UCWEB_MARKET;
            keys[1] = UCWEB_MARKET_PAGE;
        }
        if (TextUtils.isEmpty(keys[0])) {
            return null;
        } else {
            return keys;
        }
    }

    /**
     * @param context
     * @param packageName
     * @return
     * @Title isPackageExist
     * @Description .判断package是否存在
     * @date 2013年12月31日 上午9:49:59
     */
    public static boolean isPackageExist(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent().setPackage(packageName);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if (infos == null || infos.size() < 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检测 响应某个Intent的Activity 是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    @SuppressLint("WrongConstant")
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    //小米应用商店
    public static final String PACKAGE_MI_MARKET = "com.xiaomi.market";
    public static final String MI_MARKET_PAGE = "com.xiaomi.market.ui.AppDetailActivity";
    //魅族应用商店
    public static final String PACKAGE_MEIZU_MARKET = "com.meizu.mstore";
    public static final String MEIZU_MARKET_PAGE = "com.meizu.flyme.appcenter.activitys.AppMainActivity";
    //VIVO应用商店
    public static final String PACKAGE_VIVO_MARKET = "com.bbk.appstore";
    public static final String VIVO_MARKET_PAGE = "com.bbk.appstore.ui.AppStoreTabActivity";
    //OPPO应用商店
    public static final String PACKAGE_OPPO_MARKET = "com.oppo.market";
    public static final String OPPO_MARKET_PAGE = "a.a.a.aoz";
    //华为应用商店
    public static final String PACKAGE_HUAWEI_MARKET = "com.huawei.appmarket";
    public static final String HUAWEI_MARKET_PAGE = "com.huawei.appmarket.service.externalapi.view.ThirdApiActivity";
    //ZTE应用商店
    public static final String PACKAGE_ZTE_MARKET = "zte.com.market";
    public static final String ZTE_MARKET_PAGE = "zte.com.market.view.zte.drain.ZtDrainTrafficActivity";
    //360手机助手
    public static final String PACKAGE_360_MARKET = "com.qihoo.appstore";
    public static final String PACKAGE_360_PAGE = "com.qihoo.appstore.distribute.SearchDistributionActivity";
    //酷市场 -- 酷安网
    public static final String PACKAGE_COOL_MARKET = "com.coolapk.market";
    public static final String COOL_MARKET_PAGE = "com.coolapk.market.activity.AppViewActivity";
    //应用宝
    public static final String PACKAGE_TENCENT_MARKET = "com.tencent.android.qqdownloader";
    public static final String TENCENT_MARKET_PAGE = "com.tencent.pangu.link.LinkProxyActivity";
    //PP助手
    public static final String PACKAGE_ALI_MARKET = "com.pp.assistant";
    public static final String ALI_MARKET_PAGE = "com.pp.assistant.activity.MainActivity";
    //豌豆荚
    public static final String PACKAGE_WANDOUJIA_MARKET = "com.wandoujia.phoenix2";
    public static final String WANDOUJIA_MARKET_PAGE = "com.pp.assistant.activity.PPMainActivity";
    //UCWEB
    public static final String PACKAGE_UCWEB_MARKET = "com.UCMobile";
    public static final String UCWEB_MARKET_PAGE = "com.pp.assistant.activity.PPMainActivity";
}
