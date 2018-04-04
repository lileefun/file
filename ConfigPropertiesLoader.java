package cn.owhat.common.utils.configloader;

import cn.owhat.common.utils.Env;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.*;

/**
 * Created by libin on 2017/1/10.
 */
public class ConfigPropertiesLoader {

    /**
     * OW配置文件git root路径
     */
    public static final String CONF_JVM_ROOT_PATH = "conf_root_path";

    public static final String CONF_SYS_ROOT_PATH = "CONF_ROOT_PATH";

    /**
     * OW配置文件环境的项目
     */
    public static final String CONF_ENV_DIR = "conf_env_dir";


    /**
     * 默认OW配置路径
     */
    public static final String DEFAULT_CONF_PATH_WINOS = "d:\\owdata\\confs\\";

    public static final String DEFAULT_CONF_PATH_LINUXOS = "/data/op/owdata/confs/";

    public static final String DEFAULT_CONF_SYSTEM_DIR = "system";
    /**
     * xml 文件后缀
     */
    public static final String XML_FILE_EXTENSION = ".xml";

    private String classPathConfPath;

    /**
     * 配置文件路径
     */


    private String confRootPath;

    private Properties properties;

    private Map<String, Properties> envPropertiesMap;

    private Map<String, Properties> systemPropertiesMap;


    /**
     * 配置文件目录
     */
    private String envConfigDir;

    private String systemConfigDir;

    public Properties getOwhatBusinessProperties() {
        return this.properties;
    }


    private static ConfigPropertiesLoader configPropertiesLoader;

    public static ConfigPropertiesLoader loaderInstance(String confPath) {
        if (configPropertiesLoader == null) {
            if (StringUtils.isNotBlank(confPath)) {
                configPropertiesLoader = new ConfigPropertiesLoader(confPath);

            } else {
                configPropertiesLoader = new ConfigPropertiesLoader();
            }

            configPropertiesLoader.loadConfigFilesProperties();
        }
        return configPropertiesLoader;
    }

    ConfigPropertiesLoader() {
        this(null);
    }


    public ConfigPropertiesLoader(String confRootPath) {
        this.systemPropertiesMap = new HashMap<>();
        this.envPropertiesMap = new HashMap<>();
        this.properties = new Properties();
        //未设置confRootPath参数时候
        if (StringUtils.isBlank(confRootPath)) {
            //读取JVM参数变量
            confRootPath = System.getProperty(CONF_JVM_ROOT_PATH);
            if (StringUtils.isBlank(confRootPath)) {
                //读取环境变量
                confRootPath = System.getenv(CONF_SYS_ROOT_PATH);
            }
            //根据操作系统读取默认变量
            if (StringUtils.isBlank(confRootPath)) {
                //查询项目盘符
                if (SystemUtils.IS_OS_WINDOWS) {
                    confRootPath = DEFAULT_CONF_PATH_WINOS;
                } else if (SystemUtils.IS_OS_LINUX) {
                    confRootPath = DEFAULT_CONF_PATH_LINUXOS;
                } else if (SystemUtils.IS_OS_MAC) {
                    confRootPath = DEFAULT_CONF_PATH_LINUXOS;
                }
            }
        }

        this.confRootPath = confRootPath;


    }


    /**
     * 加载OW配置文件
     */
    private void loadConfigFilesProperties() {
        if (confRootPath != null) {
            File RootFileDir = new File(confRootPath);
            if (!RootFileDir.exists()) {
                throw new RuntimeException("全局配置文件路径不存在 " + confRootPath + " 或者配置JVM环境参数 例如conf_root_path=/data/op/owdata/confs/ ");
            }
            String confEnvPath = System.getProperty(CONF_ENV_DIR);
            if (StringUtils.isBlank(confEnvPath)) {
                if (Env.isDev()) {
                    confEnvPath = "dev";
                } else if (Env.isTest()) {
                    confEnvPath = "test";
                } else if (Env.isProd()) {
                    confEnvPath = "prod";
                }
            }
            File configDir = new File(confRootPath, confEnvPath);
            loadPropertiesFromPath(configDir, "env");
            File systemDir = new File(confRootPath, DEFAULT_CONF_SYSTEM_DIR);
            loadPropertiesFromPath(systemDir, "system");

            //配置文件路径
            try {
                envConfigDir = configDir.getCanonicalPath();
                systemConfigDir = systemDir.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new NullPointerException();
        }
    }



    private void loadPropertiesFromPath(File dir, String env) {
        if (dir == null) {
            return;
        }
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()) {
            return;
        }
        if (dir.isHidden() || !dir.canRead()) {
            return;
        }
        this.loadPropertiesFromDir(dir, env);

    }


    public LinkedList<File> traverseFolder(File path) {
        LinkedList<File> configFile = new LinkedList<>();
        LinkedList<File> list = new LinkedList();
        if (path != null && path.exists()) {
            File[] files = path.listFiles(new ConfigPropertiesFileFilter(true));
            for (File file : files) {
                if (file.isDirectory()) {
                    list.add(file);
                } else {
                    configFile.add(file);
                }
            }
            File tempFile;
            while (!list.isEmpty()) {
                tempFile = list.removeFirst();
                files = tempFile.listFiles(new ConfigPropertiesFileFilter(true));
                for (File file : files) {
                    if (file.isDirectory()) {
                        list.add(file);
                    } else {
                        configFile.add(file);
                    }
                }
            }
        }
        return configFile;
    }

    private void loadPropertiesFromDir(File dirRoot, String env) {
        LinkedList<File> files = traverseFolder(dirRoot);
        for (File file : files) {
            loadPropertiesFromFile(file, env);
        }

    }


    public Properties getPropertiesMap(String env, String fileName) {
        if (env.equals("env")) {
            return envPropertiesMap.get(fileName);
        } else if (env.equals("system")) {
            return systemPropertiesMap.get(fileName);
        }
        throw new RuntimeException("未找到配置文件" + fileName);
    }

    private void loadPropertiesFromFile(File file, String env) {
        Properties p = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            if (file.getName().endsWith(XML_FILE_EXTENSION)) {
                p.loadFromXML(in);
            } else {
                p.load(in);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }

        if (env.equals("env")) {
            if (envPropertiesMap.get(file.getName()) != null) {
                throw new RuntimeException("全局配置文件不能包含相同文件名的配置文件 " + file.getName());
            }
            envPropertiesMap.put(file.getName(), p);
        } else if (env.equals("system")) {
            if (systemPropertiesMap.get(file.getName()) != null) {
                throw new RuntimeException("全局配置文件不能包含相同文件名的配置文件 " + file.getName());
            }
            systemPropertiesMap.put(file.getName(), p);
        }
        mergeProperties(file, p, this.properties);
    }


    /**
     * 合并配置
     *
     * @param properties
     */
    public void mergeProperties(Properties properties) {
        //合并文件属性
        mergeProperties(null, properties, this.properties);
    }

    private static void mergeProperties(File file, Properties from, Properties to) {
        for (Enumeration en = from.propertyNames(); en.hasMoreElements(); ) {
            String key = (String) en.nextElement();

            if (StringUtils.isBlank(key)) {
                continue;
            }
            Object value = System.getProperty(key);
            if (value == null) {
                value = from.getProperty(key);
                if (value == null) {
                    value = from.get(key);
                }
            }

            Object findOld = to.put(key, value == null ? "" : value.toString());

            if (findOld != null) {
                String warning;
                if (file != null) {
                    warning = String.format("file : %s property key :%s new value(%s) override old value(%s) ", file.getAbsolutePath(), key, value, findOld);
                } else {
                    warning = String.format("property key :%s new value(%s) override old value(%s) ", key, value, findOld);
                }

            }
        }
    }

    public String getEnvConfigDir() {
        return envConfigDir;
    }

    public String getSystemConfigDir() {
        return systemConfigDir;
    }

    private static class ConfigPropertiesFileFilter implements FileFilter {

        private boolean inclodeDir;

        public ConfigPropertiesFileFilter(boolean inclodeDir) {
            this.inclodeDir = inclodeDir;
        }

        @Override
        public boolean accept(File file) {
            if (file.isHidden()) {
                return false;
            }
            if (!file.canRead()) {
                return false;
            }

            if (file.isDirectory()) {
                if (this.inclodeDir) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (file.getName().endsWith(".properties") || file.getName().endsWith(".xml")) {
                    return true;
                } else {
                    return false;
                }
            }
        }

    }
}
