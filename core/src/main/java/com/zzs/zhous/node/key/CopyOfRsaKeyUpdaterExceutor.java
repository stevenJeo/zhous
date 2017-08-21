package com.zzs.zhous.node.key;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class CopyOfRsaKeyUpdaterExceutor implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(CopyOfRsaKeyUpdaterExceutor.class);


    private Pattern fileNamePattern = Pattern.compile("(([0-9,a-z]+_[0-9,a-z]+_[publickey|privatekey]+)_([0-9]{8})).([cer|pfx|keystore]+)");
    private String absolutePath;
    private int updateInterval = 2 * 60 * 60; //2小时。

    private Thread thread;
    private boolean stopFlag = false;


    //private Map<String,KeyFile> map;
    //private KeyMap<KeyFile> keyMap;
    private KeyMap<Key> keyMap;

    private Map<String, WrapperBuffer<KeyCipher>> wrapBufferMap;

    /**
     *
     */
//	public RsaKeyUpdater(String absolutePath,int updateInterval,KeyMap<KeyFile> keyMap) {
    public CopyOfRsaKeyUpdaterExceutor(String absolutePath, int updateInterval, KeyMap<Key> keyMap, Map<String, WrapperBuffer<KeyCipher>> wrapBufferMap) {
        this.absolutePath = absolutePath;
        this.updateInterval = updateInterval;
        this.keyMap = keyMap;
        this.wrapBufferMap = wrapBufferMap;
    }


    public void start() throws Exception {
        if (thread == null) {
            synchronized (this) {
                if (thread == null) {
                    thread = new Thread(this);
                    stopFlag = false;
                    refreshKeyFiles(absolutePath);
                    log.info(keyMap.toString());
                    thread.start();
                }
            }
        }
    }

    public void stop() {
        stopFlag = true;
    }


    private void printWrapperStatus(String mapKey) {
        if (wrapBufferMap.containsKey(mapKey))
            log.warn(wrapBufferMap.get(mapKey).statistics());
    }


    /**
     * 遍历给定的目录，更新key文件。
     * <p>
     * 不能保证线程安全。
     *
     * @param path
     * @throws Exception
     */
    private void refreshKeyFiles(String path) throws Exception {
        File dir = new File(path);
        log.warn("RsaKeyUpdater scans path=" + dir.getAbsolutePath());
        File[] files = dir.listFiles();

        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshKeyFiles(files[i].getAbsolutePath());
            } else {
                String fileName = files[i].getAbsolutePath();
                String fileNameKey = files[i].getName().toLowerCase();

                Matcher matcher = fileNamePattern.matcher(fileNameKey);
                if (!matcher.matches()) continue;

                fileNameKey = matcher.group(3);
                String key = matcher.group(2);
                String type = matcher.group(4);
                boolean isPrivateKey = "pfx".equals(type) || "keystore".equals(type);

                KeyFile keyFile = (KeyFile) keyMap.get(key);
                if (keyFile == null) {
                    keyFile = new KeyFile(fileName, fileNameKey, isPrivateKey);
                    //map.put(key, keyFile);
                    keyMap.add(key, keyFile);
                } else {
                    if (!keyFile.contains(fileNameKey)) {
                        keyFile.addKeyFile(fileName, fileNameKey, isPrivateKey);
                        if (log.isInfoEnabled())
                            log.info("add key=" + fileNameKey + " fileName=" + fileName + keyFile.toString());
                    }
                }
            }
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (!stopFlag) {
            try {
                log.warn("RsaKeyUpdater thread=" + thread + " is running...");
                if (log.isDebugEnabled()) log.debug("begin sleep=" + updateInterval);
                Thread.currentThread().sleep(updateInterval);
                if (log.isDebugEnabled()) log.debug("refreshKeyFiles=" + absolutePath);
                refreshKeyFiles(absolutePath);
                log.info(keyMap.toString());

                printWrapperStatus("003_001_DES_2");
                printWrapperStatus("003_001_DES_1");

            } catch (Exception e) {
                log.error("RsaKeyUpdater thread error", e);
            }

            if (keyMap == null) break;


        }
        log.warn("RsaKeyUpdater thread=" + thread + " has been terminated");

    }
}
