package com.merlin.core.tool;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.merlin.core.util.MLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ScreenShotManager
 * <功能> 截屏监听管理器
 * <详细>
 * 截屏判断依据：监听媒体数据库的数据改变，在有数据改变时获取最后插入数据库的一条图片数据，如果符合以下规则，则认为截屏了：
 * 1、时间判断，图片的生成时间在开始监听之后，并与当前时间相隔10秒内；
 * 2、尺寸判断，图片的尺寸没有超过屏幕的尺寸；
 * 3、路径判断，图片路径符合包含特定的关键词。
 * <p>
 * Demo:
 * <pre>
 *     {@code
 *      // Requires Permission: android.permission.READ_EXTERNAL_STORAGE
 *
 *      ScreenShotManager manager = ScreenShotManager.newInstance(context);
 *
 *      manager.setListener(
 *          new OnScreenShotListener() {
 *              public void onShot(String imagePath) {
 *                  // do something
 *              }
 *          }
 *      );
 *
 *      manager.startListen();
 *      ...
 *      manager.stopListen();
 *  }
 * </pre>
 */
public class ScreenShotManager {


    // ---------- ---------- ---------- Interfaces ---------- ---------- ----------

    public interface OnScreenShotListener {
        void onShot(String dataPath);
    }


    // ---------- ---------- ---------- Properties ---------- ---------- ----------

    private static final String TAG = "ScreenShotManager";

    /**
     * 读取媒体数据库时需要读取的列
     */
    private static final String[] MEDIA_PROJECTIONS = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN};

    /**
     * 读取媒体数据库时需要读取的列，其中 WIDTH 和 HEIGHT 字段在 API 16 之后才有
     */
    private static final String[] MEDIA_PROJECTIONS_API_16 = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT};

    /**
     * 截屏依据中的路径判断关键字
     */
    private static final String[] PATH_KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap"
    };

    private Context thisContext;

    private static Point thisScreenRealSize;

    /**
     * 已回调过的路径
     */
    private final List<String> thisCallbackPaths = new ArrayList<>();

    private OnScreenShotListener thisOnScreenShotListener;

    private long thisStartListenTime;

    /**
     * 内部存储器内容观察者
     */
    private MediaContentObserver thisInternalObserver;

    /**
     * 外部存储器内容观察者
     */
    private MediaContentObserver thisExternalObserver;

    /**
     * 运行在 UI 线程的 Handler，用于运行监听器回调
     */
    private final Handler thisUiHandler = new Handler(Looper.getMainLooper());


    // ---------- ---------- ---------- Implementations ---------- ---------- ----------


    // ---------- ---------- ---------- Methods ---------- ---------- ----------

    /**
     * 设置截屏监听器
     */
    public void setListener(OnScreenShotListener listener) {
        thisOnScreenShotListener = listener;
    }

    /**
     * 启动监听
     */
    public void startListen() {
        assertInMainThread();

        thisCallbackPaths.clear();

        // 记录开始监听的时间戳
        thisStartListenTime = System.currentTimeMillis();

        // 创建内容观察者
        thisInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, thisUiHandler);
        thisExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, thisUiHandler);

        // 注册内容观察者
        thisContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                thisInternalObserver
        );
        thisContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                thisExternalObserver
        );
    }

    /**
     * 停止监听
     */
    public void stopListen() {
        assertInMainThread();

        // 注销内容观察者
        if (null != thisInternalObserver) {
            try {
                thisContext.getContentResolver().unregisterContentObserver(thisInternalObserver);
            } catch (Exception e) {
                MLog.wtf(e);
            }
            thisInternalObserver = null;
        }
        if (null != thisExternalObserver) {
            try {
                thisContext.getContentResolver().unregisterContentObserver(thisExternalObserver);
            } catch (Exception e) {
                MLog.wtf(e);
            }
            thisExternalObserver = null;
        }

        // 清空数据
        thisStartListenTime = 0;
        thisCallbackPaths.clear();
    }

    /**
     * 处理媒体数据库的内容改变
     */
    private void handleMediaContentChange(Uri contentUri) {
        MLog.d("~~~yanss~~~ >>> ScreenShotManager.handleMediaContentChange() >>> handleMedia Time");
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = thisContext.getContentResolver().query(
                    contentUri,
                    Build.VERSION.SDK_INT < 16 ? MEDIA_PROJECTIONS : MEDIA_PROJECTIONS_API_16,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            );

            if (null == cursor) {
                // LogUtils.e("~~~~~~ >>> ScreenShotManager.handleMediaContentChange() >>> " + "Deviant logic.");
                return;
            }
            if (!cursor.moveToFirst()) {
                // LogUtils.e("~~~~~~ >>> ScreenShotManager.handleMediaContentChange() >>> " + "Cursor no data.");
                return;
            }

            MLog.e("~~~yanss~~~ >>> ScreenShotManager.handleMediaContentChange() >>> handleMedia Time 2");

            // 获取各列的索引
            int dataPathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int widthIndex = -1;
            int heightIndex = -1;
            if (Build.VERSION.SDK_INT >= 16) {
                widthIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
                heightIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT);
            }

            // 获取行数据
            String dataPath = cursor.getString(dataPathIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);
            int width = 0;
            int height = 0;
            if (widthIndex >= 0 && heightIndex >= 0) {
                width = cursor.getInt(widthIndex);
                height = cursor.getInt(heightIndex);
            } else {
                // API 16 之前, 宽高要手动获取
                Point size = getImageSize(dataPath);
                width = size.x;
                height = size.y;
            }

            MLog.e("~~~yanss~~~ >>> ScreenShotManager.handleMediaContentChange() >>> handleMedia Time 3");

            // 处理获取到的第一行数据
            handleMediaRowData(dataPath, dateTaken, width, height);
        } catch (Exception e) {
            MLog.wtf(e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private Point getImageSize(String dataPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(dataPath, options);
        return new Point(options.outWidth, options.outHeight);
    }

    /**
     * 处理获取到的一行数据
     */
    private void handleMediaRowData(String dataPath, long dateTaken, int width, int height) {
        if (checkScreenShot(dataPath, dateTaken, width, height)) {
            // LogUtils.e("~~~~~~ >>> ScreenShotManager.handleMediaRowData() >>> ScreenShot: ", "dataPath = " + dataPath + "; size = " + width + " * " + height + "; dateTaken = " + dateTaken);
            if (null != thisOnScreenShotListener && !checkCallback(dataPath)) {
                MLog.e("~~~yanss~~~ >>> ScreenShotManager.handleMediaRowData() >>> onShot Time");
                thisOnScreenShotListener.onShot(dataPath);
            }
        } else {
            // 如果在观察区间媒体数据库有数据改变，又不符合截屏规则，则输出到 log 待分析
            // LogUtils.e("~~~~~~ >>> ScreenShotManager.handleMediaRowData() >>> Media content changed, but not screenshot: ", "path = " + dataPath + "; size = " + width + " * " + height + "; date = " + dateTaken);
        }
    }

    /**
     * 判断指定的数据行是否符合截屏条件
     */
    private boolean checkScreenShot(String dataPath, long dateTaken, int width, int height) {
        /*
         * 判断依据一: 时间判断
         */
        // 如果加入数据库的时间在开始监听之前, 或者与当前时间相差大于10秒, 则认为当前没有截屏
        if (dateTaken < thisStartListenTime || (System.currentTimeMillis() - dateTaken) > 10 * 1000) {
            return false;
        }

        /*
         * 判断依据二: 尺寸判断
         */
        if (null != thisScreenRealSize) {
            // 如果图片尺寸超出屏幕, 则认为当前没有截屏
            if (
                    !(
                            (width <= thisScreenRealSize.x && height <= thisScreenRealSize.y)
                                    ||
                                    (height <= thisScreenRealSize.x && width <= thisScreenRealSize.y)
                    )) {
                return false;
            }
        }

        /*
         * 判断依据三: 路径判断
         */
        if (TextUtils.isEmpty(dataPath)) {
            return false;
        }
        dataPath = dataPath.toLowerCase();
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (String keyWork : PATH_KEYWORDS) {
            if (dataPath.contains(keyWork)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否已回调过, 某些手机ROM截屏一次会发出多次内容改变的通知; <br/>
     * 删除一个图片也会发通知, 同时防止删除图片时误将上一张符合截屏规则的图片当做是当前截屏.
     */
    private boolean checkCallback(String dataPath) {
        if (thisCallbackPaths.contains(dataPath)) {
            return true;
        }
        // 大概缓存15~20条记录便可
        if (thisCallbackPaths.size() >= 20) {
            for (int i = 0; i < 5; i++) {
                thisCallbackPaths.remove(0);
            }
        }
        thisCallbackPaths.add(dataPath);
        return false;
    }

    /**
     * 获取屏幕分辨率
     */
    private Point getRealScreenSize() {
        Point screenSize = null;
        try {
            screenSize = new Point();
            WindowManager windowManager = (WindowManager) thisContext.getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                defaultDisplay.getRealSize(screenSize);
            } else {
                try {
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    screenSize.set(
                            (Integer) mGetRawW.invoke(defaultDisplay),
                            (Integer) mGetRawH.invoke(defaultDisplay)
                    );
                } catch (Exception e) {
                    screenSize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight());
                    MLog.wtf(e);
                }
            }
        } catch (Exception e) {
            MLog.wtf(e);
        }
        return screenSize;
    }

    /**
     * 验证是否在主线程
     * 由于初始化截屏事件监听写在了 LauncherActivity 的子线程里了，所以不能再这里验证是否在主线程了。
     * yanss 2017/04/05 14:11:51
     */
    private static void assertInMainThread() {
        // if (Looper.myLooper() != Looper.getMainLooper()) {
        //     StackTraceElement[] elements  = Thread.currentThread().getStackTrace();
        //     String              methodMsg = null;
        //     if (null != elements && elements.length >= 4) {
        //         methodMsg = elements[3].toString();
        //     }
        //     throw new IllegalStateException("Call the method must be in main thread: " + methodMsg);
        // }
    }


    // ---------- ---------- ---------- Lifecycle ---------- ---------- ----------


    // ---------- ---------- ---------- Constructors ---------- ---------- ----------

    private ScreenShotManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The context must not be null.");
        }
        thisContext = context;

        // 获取屏幕真实的分辨率
        if (thisScreenRealSize == null) {
            thisScreenRealSize = getRealScreenSize();
            if (thisScreenRealSize != null) {
                // LogUtils.e("~~~~~~ >>> ScreenShotManager.ScreenShotManager() >>> ", "Screen Real Size = " + thisScreenRealSize.x + " * " + thisScreenRealSize.y);
            } else {
                // LogUtils.e("~~~~~~ >>> ScreenShotManager.ScreenShotManager() >>> " + "Get screen real size failed.");
            }
        }
    }

    public static ScreenShotManager newInstance(Context context) {
        assertInMainThread();
        return new ScreenShotManager(context);
    }


    // ---------- ---------- ---------- Getters And Setters ---------- ---------- ----------


    // ---------- ---------- ---------- Classes ---------- ---------- ----------

    /**
     * 媒体内容观察者（观察媒体数据库的改变）
     * yanss 2017/04/05 10:46:58
     */
    private class MediaContentObserver extends ContentObserver {
        private Uri mContentUri;

        public MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MLog.e("~~~yanss~~~ >>> MediaContentObserver.onChange() >>> onChange Time");
            handleMediaContentChange(mContentUri);
        }
    }
}
