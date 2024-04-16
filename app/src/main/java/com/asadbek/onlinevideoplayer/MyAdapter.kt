package com.asadbek.onlinevideoplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView

const val TAG = "MyAdapter"
@UnstableApi
open class MyAdapter(val context: Context,val list:ArrayList<FileModel>) : RecyclerView.Adapter<MyAdapter.VH>() {
    lateinit var exoPlayer: ExoPlayer
    open inner class VH(itemRv: View): RecyclerView.ViewHolder(itemRv){
        fun onBind(fileModel: FileModel){
            val videoTitle = itemView.findViewById<TextView>(R.id.videoTitle)
            var playerView = itemView.findViewById<PlayerView>(R.id.playerView)
            var progressView = itemView.findViewById<ProgressBar>(R.id.progressVideo)
            exoPlayer = ExoPlayer.Builder(context).build() // exo player video ijro etish uchun
            playerView.player = exoPlayer
            val mediaItem = MediaItem.Builder() // bu uri orqali videoni olib kelib beradi va mp4 formatda o`qiydi
                .setUri(fileModel.videUrl)
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build()

            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(mediaItem)
            exoPlayer.setMediaItem(mediaItem)
            // addListener - video jarayonlarini tinglash uchun
            // onIsLoadingChanged = true - bo`lsa video yuklanib bo`lmaganligini bildiradi
            // OnIsLoadingChanged = false - bo`lsa video yuklanib bo`lganligini bildiradi
            exoPlayer.addListener(object :Player.Listener{
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    if(isLoading){
                        progressView.setProgress(100,true)
                        progressView.visibility = View.VISIBLE
                    }else{
                        progressView.setProgress(100,true)
                        progressView.visibility = View.INVISIBLE
                    }
                }

            })


            exoPlayer.prepare() // video ijoro etishga tayyor turadi lekin boshlanib ketmaydi
           // exoPlayer.play()


            videoTitle.setText(fileModel.title)
        }

    }


    fun stopPlayer(){
        exoPlayer.stop()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return  VH(LayoutInflater.from(parent.context).inflate(R.layout.singlerow,parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return  list.size
    }


}
