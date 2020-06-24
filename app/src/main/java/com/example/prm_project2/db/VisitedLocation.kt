package com.example.prm_project2.db

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class VisitedLocation(
    var locationId: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var name: String? = null,
    var diameter: Double? = null,
    var photoUri: String? = null,
    var description: String? = null
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(locationId)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(name)
        parcel.writeValue(diameter)
        parcel.writeString(photoUri)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisitedLocation> {
        override fun createFromParcel(parcel: Parcel): VisitedLocation {
            return VisitedLocation(parcel)
        }

        override fun newArray(size: Int): Array<VisitedLocation?> {
            return arrayOfNulls(size)
        }
    }


}
