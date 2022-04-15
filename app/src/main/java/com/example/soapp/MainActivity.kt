package com.example.soapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.SoFile
import com.example.nativelib.NativeLib
import com.getkeepsafe.relinker.ReLinker
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.File

/**
 *
 * 注意：而这里加载的文件路径只能加载两个目录下的 so 文件。那就是：
/system/lib
应用程序安装包的路径，即：/data/data/packagename/…
所以，so 文件动态加载的文件目录不能随便放。这是需要注意的一点。

 参考关于动态加载So文件： https://blog.csdn.net/weixin_44715716/article/details/116244496
 参考关于ReLinker： https://blog.csdn.net/jackson_ke/article/details/49950719
 参考生成so文件 ： https://www.freesion.com/article/84111418914/
 */

class MainActivity : AppCompatActivity() {

    // Used to load the 'nativelib' library on application startup.
//    companion object {
//        init {
//            System.loadLibrary("nativelib")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }

    private fun requestPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe {
                if (it) {
                    val dir: File = applicationContext.getDir("libs", Context.MODE_PRIVATE)
                    SoFile.copy("/sdcard/Download/", dir.toString())
                    val currentFiles: Array<File> = dir.listFiles()
                    for (i in currentFiles.indices) {
                        System.load(currentFiles[i].absolutePath)
                    }
                    findViewById<TextView>(R.id.tv).text = NativeLib()
                        .stringFromJNI()
                } else {
                    Toast.makeText(this@MainActivity, "请赋予权限", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
