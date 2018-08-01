package kerry.express.th.mobile.camera_test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import kerry.express.th.mobile.camera_test.adapter.RvAdapter
import kotlinx.android.synthetic.main.activity_gallery.*
import java.io.File

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        rv_origin.layoutManager = LinearLayoutManager(this)
        getAllImage()
    }

    private fun getAllImage() {
        val originPath = getExternalFilesDir("/source").toString()
        val originDirectory = File(originPath)
        val originFiles = originDirectory.listFiles()

        val compressedPath = getExternalFilesDir("/compressed").toString()
        val compressedDirectory = File(compressedPath)
        val compressedFiles = compressedDirectory.listFiles()
        if (originFiles.isNotEmpty() && compressedFiles.isNotEmpty())
            rv_origin.adapter = RvAdapter(originFiles, compressedFiles)
    }

//    fun size(size: Long): String {
//        var hrSize = ""
//        val m = size / 1024.0
//
//        if (m > 1) {
//            //KB
//            hrSize = String.format("%.1f",m) + " KB"
//        } else {
//            hrSize = String.format("%.1f",m) + " KB"
//        }
//        return hrSize
//    }
}
