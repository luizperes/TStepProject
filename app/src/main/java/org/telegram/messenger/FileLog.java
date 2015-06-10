/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package org.telegram.messenger;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.telegram.android.time.FastDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class FileLog {
    private OutputStreamWriter streamWriter = null;
    private FastDateFormat dateFormat = null;
    private DispatchQueue logQueue = null;
    private File currentFile = null;

    private static volatile FileLog Instance = null;
    public static FileLog getInstance(Context appCtx) {
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileLog(appCtx);
                }
            }
        }
        return localInstance;
    }

    public FileLog(Context appCtx) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
        try {
            File sdCard = appCtx.getExternalFilesDir(null);
            if (sdCard == null) {
                return;
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            if (dir == null) {
                return;
            }
            dir.mkdirs();
            currentFile = new File(dir, dateFormat.format(System.currentTimeMillis()) + ".txt");
            if (currentFile == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            logQueue = new DispatchQueue("logQueue");
            currentFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(currentFile);
            streamWriter = new OutputStreamWriter(stream);
            streamWriter.write("-----start log " + dateFormat.format(System.currentTimeMillis()) + "-----\n");
            streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(final Context appCtx, final String tag, final String message, final Throwable exception) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.e(tag, message, exception);
        if (getInstance(appCtx).streamWriter != null) {
            getInstance(appCtx).logQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " E/" + tag + "? " + message + "\n");
                        getInstance(appCtx).streamWriter.write(exception.toString());
                        getInstance(appCtx).streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void e(final Context appCtx, final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.e(tag, message);
        if (getInstance(appCtx).streamWriter != null) {
            getInstance(appCtx).logQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " E/" + tag + "? " + message + "\n");
                        getInstance(appCtx).streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void e(final Context appCtx, final String tag, final Throwable e) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        e.printStackTrace();
        if (getInstance(appCtx).streamWriter != null) {
            getInstance(appCtx).logQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " E/" + tag + "? " + e + "\n");
                        StackTraceElement[] stack = e.getStackTrace();
                        for (StackTraceElement el : stack) {
                            getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " E/" + tag + "? " + el + "\n");
                        }
                        getInstance(appCtx).streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            e.printStackTrace();
        }
    }

    public static void d(final Context appCtx, final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.d(tag, message);
        if (getInstance(appCtx).streamWriter != null) {
            getInstance(appCtx).logQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " D/" + tag + "? " + message + "\n");
                        getInstance(appCtx).streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void w(final Context appCtx, final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.w(tag, message);
        if (getInstance(appCtx).streamWriter != null) {
            getInstance(appCtx).logQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        getInstance(appCtx).streamWriter.write(getInstance(appCtx).dateFormat.format(System.currentTimeMillis()) + " W/" + tag + ": " + message + "\n");
                        getInstance(appCtx).streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void cleanupLogs(Context appCtx) {
        ArrayList<Uri> uris = new ArrayList<>();
        File sdCard = appCtx.getExternalFilesDir(null);
        File dir = new File (sdCard.getAbsolutePath() + "/logs");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (getInstance(appCtx).currentFile != null && file.getAbsolutePath().equals(getInstance(appCtx).currentFile.getAbsolutePath())) {
                continue;
            }
            file.delete();
        }
    }
}
