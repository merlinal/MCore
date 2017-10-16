package com.merlin.core.worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.webkit.MimeTypeMap;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MUtil;
import com.merlin.core.util.MVerify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Locale;

/**
 * //在SD卡中创建与删除文件权限
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 * //向SD卡写入数据权限
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <p>
 * Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容。
 * Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
 * Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
 * Context.MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；
 * Context.MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
 */

/**
 * Created by ncm on 16/12/14.
 */

public class FileWorker {

    public static FileWorker inst() {
        return InstHolder.fileWorker;
    }

    private static class InstHolder {
        private final static FileWorker fileWorker = new FileWorker();
    }

    private FileWorker() {
    }

    /**
     * 内部存储
     *
     * @param folderName
     * @return
     */
    public String newFolder(String folderName) {
        return MContext.app().getCacheDir() + File.separator + folderName;
    }

    /**
     * SD卡存储
     *
     * @param folderName
     * @return
     */
    public String newSDFolder(String folderName) {
        return getSDPath() + File.separator + MContext.appName() + File.separator + folderName;
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public String getSDPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取缓存路径
     *
     * @param folderName
     * @return
     */
    public String getDiskCacheDir(String folderName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //路径为/sdcard/Android/data/<application package>/cache；存储在SD卡上，app卸载时自动删除
            cachePath = MContext.app().getExternalCacheDir().getPath();
        } else {
            //路径为/data/data/<application package>/cache；内部存储
            cachePath = MContext.app().getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + folderName).getPath();
    }

    public void save(String fileName, byte[] value, int mode) {
        FileOutputStream fos = null;
        try {
            // 打开文件获取输出流，文件不存在则自动创建
//            int mode = (modes != null && modes.length > 0 ? modes[0] : Context.MODE_PRIVATE);
            fos = MContext.app().openFileOutput(fileName, mode);
            fos.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            flush(fos);
            close(fos);
        }
    }

    public void save(String filePath, byte[] value) {
        File file = createFile(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            flush(out);
            close(out);
        }
    }

    public byte[] getBytes(String filePath) {
        RandomAccessFile raf = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            raf = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[(int) raf.length()];
            raf.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(raf);
        }
    }

    public void save(String filePath, String value) {
        File file = createFile(filePath);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            flush(out);
            close(out);
        }
    }

    public String getString(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String String = "";
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String += currentLine;
            }
            return String;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(br);
        }
    }

    public void save(String filePath, Serializable value) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            save(filePath, bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(oos);
            close(bos);
        }
    }

    public Object getObject(String filePath) {
        byte[] data = getBytes(filePath);
        if (data != null) {
            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;
            try {
                bis = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bis);
                return ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                close(bis);
                close(ois);
            }
        }
        return null;
    }

    public void save(String filePath, Bitmap bm) {
        save(filePath, bitmap2Bytes(bm));
    }

    public Bitmap getBitmap(String filePath) {
        return bytes2Bitmap(getBytes(filePath));
    }

    /**
     * 删除文件或目录
     *
     * @param filePath
     */
    public void remove(String filePath) {
        if (MVerify.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File childFile : files) {
                    remove(childFile.getPath());
                }
            } else {
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public boolean existSD() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 是否存在文件
     *
     * @param filePath
     * @return
     */
    public boolean existFile(String filePath) {
        if (MVerify.isBlank(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * SD可用容量
     *
     * @return Byte
     */
    public long getSDFreeSize() {
        if (!existSD()) {
            return 0;
        }
        StatFs sf = new StatFs(getSDPath());
        //返回SD卡空闲大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return sf.getAvailableBytes();
        } else {
            //获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            //空闲的数据块的数量
            long freeBlocks = sf.getAvailableBlocks();
            return (freeBlocks * blockSize); //byte
            //return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
        }
    }

    /**
     * SD总容量
     *
     * @return
     */
    public long getSDTotalSize() {
        if (!existSD()) {
            return 0;
        }
        StatFs sf = new StatFs(getSDPath());
        //返回SD卡空闲大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return sf.getTotalBytes();
        } else {
            //获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            //获取所有数据块数
            long allBlocks = sf.getBlockCount();
            return (allBlocks * blockSize); //byte
            //return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
        }
    }

    private Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private byte[] bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(bos);
        }
    }

    private void flush(Flushable flushable) {
        try {
            if (flushable != null) {
                flushable.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File createFile(String filePath) {
        if (MVerify.isEmpty(filePath)) {
            return null;
        }
        // 获得文件对象
        File f = new File(filePath);
        try {
            if (!f.exists()) {
                // 如果路径不存在,则创建
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                f.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    private String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null || !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }

}
