package data.repositories

import org.ini4j.Wini
import java.io.File

object ConfigRepo {
    private var dataDirPath = System.getProperty("user.home") + "\\AppData\\Local\\JcNetwork"
    private var dataFilePath = "\\data.ini"
    private var dataIni: Wini = Wini()

    init {
        val dir = File(dataDirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dataFile = File(dataDirPath + dataFilePath)
        if (!dataFile.exists()) {
            dataFile.createNewFile()
        }

        // 初始化 Ini 文件操作类
        dataIni = Wini(dataFile)
    }

    /**
     * 获取配置
     */
    fun getConfig(key: ConfigKey): String {
        return dataIni.get(key.node, key.key) ?: ""
    }

    fun getConfigInt(key: ConfigKey): Int {
        val result = getConfig(key)
        return if (result.isEmpty()) {
            0
        } else {
            result.toInt()
        }
    }

    /**
     * 获取配置
     */
    fun setConfig(key: ConfigKey, value: String) {
        dataIni.put(key.node, key.key, value)
        dataIni.store()
    }
}

/**
 * 配置文件枚举类
 */
enum class ConfigKey(val node: String, val key: String) {
    USERNAME("data", "username"),
    PASSWORD("data", "password"),
    WIDTH("window", "width"),
    HEIGHT("window", "height"),
    X("window", "x"),
    Y("window", "y")
}