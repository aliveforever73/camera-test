package kerry.express.th.mobile.camera_test

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import com.iamhabib.easy_preference.EasyPreference
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val REQUEST_CAMERA = 301
    val REQUEST_GALLERY = 302
    val REQUEST_PERMISSION = 401
    var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionRequest()
        takePicBtn.setOnClickListener(this)
        galleryBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.takePicBtn -> {
                dispatchTakePictureIntent()
            }
            R.id.galleryBtn -> {
                val galleryIntent = Intent(this@MainActivity,GalleryActivity::class.java)
                startActivity(galleryIntent)
            }
        }
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                mCurrentPhotoPath = photoFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("takePicIntentError", e.message)
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this,
                        "kerry.express.th.mobile.camera_test.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("RESULT", "OK")
//                    val oldPath = EasyPreference.with(this).getString("photoPath", "")
//                    if (oldPath != "") {
//                        val oldFile = File(oldPath)
//                        Log.e("oldPath", oldPath)
//                        oldFile.delete()
//                    }

                    val file = File(mCurrentPhotoPath)
                    Log.e("path", mCurrentPhotoPath)
                    try {
                        val newImage = Compressor(this)
                                .setMaxHeight(480)
                                .setMaxWidth(640)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(getExternalFilesDir("/compressed").toString())
                                .compressToFile(file)
                        //file.delete()
                        EasyPreference.with(this).addString("photoPath", newImage.absolutePath).save()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else
                    Log.e("RESULT", "CANCEL")
            }
        }
    }

    private fun createImageFile(): File {
        val timestamp = System.currentTimeMillis() / 1000
        val storageDir = this.getExternalFilesDir("/source")
        return createTempFile("cam-alive-$timestamp",
                ".jpg",
                storageDir)
    }

    fun permissionRequest(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val writeExtenal = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readExtenal = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionNeeded = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(android.Manifest.permission.CAMERA)
        if (writeExtenal != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (readExtenal != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), REQUEST_PERMISSION)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                val perms = HashMap<String, Int>()

                perms[android.Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[android.Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED &&
                            perms[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED &&
                            perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
//                        dispatchTakePictureIntent()
                        Log.e("PERMISSION", "GRANTED")
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Log.e("PERMISSION", "REQUIRE")
                        }
                    }
                }
            }
        }
    }
}
