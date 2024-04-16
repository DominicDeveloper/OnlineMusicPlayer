package com.asadbek.onlinevideoplayer

class FileModel {
    var title:String? =  null
    var videUrl:String? = null
    constructor()
    constructor(title: String?, videUrl: String?) {
        this.title = title
        this.videUrl = videUrl
    }

}