package com.asadbek.onlinevideoplayer


import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.asadbek.onlinevideoplayer.databinding.ActivityUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * Created by: Asadbek Azimov
 * Subscribe to my youtube: @Azimov_Development
 * Subscribe to my telegram channel: @Azimov_Development
 * Date: 2024.04.15
 * Must be added to the manifest:
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.READ_MEDIA_VIDEO"/>
 * And also used: Dexter library add it to the gradle:app
 */

const val VIDEO_REQUEST_CODE = 303

class UploadActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadBinding
    var videoUri:Uri? = null // telefon hotirasidan olingan video uri manzili
    lateinit var mediaController: MediaController
    lateinit var storageReference: StorageReference
    lateinit var databaseReference: DatabaseReference
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().getReference()
        databaseReference = FirebaseDatabase.getInstance().getReference("videos")

        mediaController = MediaController(this)

        // mediaController ni videoView niki bilan bog`lash
        binding.videoView.setMediaController(mediaController)
        binding.videoView.start()

        // get video from phone storage
        // videoni telefon xotirasidan olish
        binding.browse.setOnClickListener {
            requestStoragePermission()
        }
         // upload the video to firebase
        // videoni firebasega yuklash
        binding.upload.setOnClickListener {
            processVideoUploading()
        }



    }

    private fun getExtension():String{
        var mimeTypeMap:MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri!!))!!
    }

    private fun processVideoUploading() {
        val progressDialog = ProgressDialog(this) // foydalanuvchiga xabar chiqarib turish uchun dialog
        progressDialog.setTitle("Media uploader")
        progressDialog.show()
        // uploader = video qanday nomda va formatda saqlanashi
        var uploader = storageReference.child("videos/"+System.currentTimeMillis()+"."+getExtension())
        // video ni uri bo`yicha olib firebasega yuborish
        uploader.putFile(videoUri!!)
            .addOnSuccessListener {
                // video yuborilib bo`lgandan so`ng realtimedatabase ga fayl modelini yuborish
                uploader.downloadUrl.addOnSuccessListener {
                    // yuborilgan video linki va video title-i
                    val fileModel = FileModel(binding.videoTitle.text.toString(),it.toString())
                    // key bilan birgalikda database ga yuborilishi
                    databaseReference.child(databaseReference.push().key!!).setValue(fileModel)
                        .addOnSuccessListener {
                            // muvaffaqiyatli yuborilganda dialog yo`qoladi va yuklanganlik xaqidagi xabar chiqadi
                            progressDialog.dismiss()
                            Toast.makeText(this, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            // muvaffaqiyatsiz yuborilganda dialog yo`qoladi va yuklanmaganlik xaqidagi xabar chiqadi
                            progressDialog.dismiss()
                            Toast.makeText(this, "Failed to upload!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnProgressListener {
                var pros:Float = ((100*it.bytesTransferred)/it.totalByteCount).toFloat()
                progressDialog.setMessage("Uploaded: "+pros.toInt()+"%")
                // video yuklanayotgan paytda qancha foyizi yuklanganligini ko`rib turish uchun progress listenerga yozib chiqildi
            }
    }

    // read storage permission ishlamanligi hisobiga read media video permission yozildi va bu yerda foydalanish uchun ro`xsat so`ralyabd
    // TIRMASU - API version 33 ga teng yoki katta bo`lganda
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_VIDEO)){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                101)
        }else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission has been granted
                getVideo()
            }else{
                // Permission request was denied
                Toast.makeText(this, "Allow read storage!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getVideo() {
        val intent = Intent()
        intent.setType("video/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, VIDEO_REQUEST_CODE) // 303

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 303 && resultCode == RESULT_OK){
            videoUri = data?.data // video uri manzil o`zgaruvchiga tenglanadi
            binding.videoView.setVideoURI(videoUri) // videoview ga videoni uri orqali yuborib ijro ettirish
        }
    }
}