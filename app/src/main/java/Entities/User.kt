package Entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class User (uid : String?,
            name : String?,
            pass : String?,
            email: String?,
            weight: Int?,
            height: Float?,
            imc: Float?,
            photoUrl: String?):
    Parcelable{

    var uid : String
    var name : String
    var pass : String
    var email : String
    var weight : Int
    var height : Float
    var imc : Float
    var photoUrl : String

    init {
        this.uid = uid!!
        this.name = name!!
        this.pass = pass!!
        this.email = email!!
        this.weight = weight!!
        this.height = height!!
        this.imc = imc!!
        this.photoUrl = photoUrl!!
    }

    constructor() : this("","","","",0,0.0f,0.0f,"")

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readFloat(),
        source.readFloat(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(uid)
        writeString(name)
        writeString(pass)
        writeString(email)
        writeInt(weight)
        writeFloat(height)
        writeFloat(imc)
        writeString(photoUrl)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

    fun getNombre() : String{
        return (this.name)
    }

    fun getClave() : String{
        return (this.pass)
    }
}