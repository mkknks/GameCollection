package com.gamecollection.data.database

import androidx.room.TypeConverter
import com.gamecollection.data.model.CollectionStatus

class Converters {
    @TypeConverter
    fun fromCollectionStatus(status: CollectionStatus): String = status.name

    @TypeConverter
    fun toCollectionStatus(value: String): CollectionStatus = CollectionStatus.valueOf(value)
}
