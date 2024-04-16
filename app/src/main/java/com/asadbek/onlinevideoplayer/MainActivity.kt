package com.asadbek.onlinevideoplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.asadbek.onlinevideoplayer.MyAdapter.*
import com.asadbek.onlinevideoplayer.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

@UnstableApi
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var myAdapter: MyAdapter
    lateinit var list:ArrayList<FileModel>
    lateinit var firebaseDateBae:FirebaseDatabase
    lateinit var firebaseFireStore: FirebaseFirestore
    lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()

        firebaseDateBae = FirebaseDatabase.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()
        reference = firebaseDateBae.getReference("videos")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val fileMode: FileModel? = child.getValue(FileModel::class.java)
                    list.add(FileModel(fileMode!!.title, fileMode!!.videUrl))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        myAdapter = MyAdapter(this, list)

        binding.btnAddView.setOnClickListener {
            //val intent = Intent(this,UploadActivity::class.java)
            //startActivity(intent)
            myAdapter.notifyDataSetChanged()
        }

        binding.recView.adapter = myAdapter

    }

    override fun onPause() {
        super.onPause()
        myAdapter.stopPlayer()
    }

}