package com.gamecollection.data.database

import androidx.room.TypeConverter
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility

class Converters {
    @TypeConverter
    fun fromOwnershipStatus(value: OwnershipStatus): String = value.name

    @TypeConverter
    fun toOwnershipStatus(value: String): OwnershipStatus = OwnershipStatus.valueOf(value)

    @TypeConverter
    fun fromPlayStatus(value: PlayStatus): String = value.name

    @TypeConverter
    fun toPlayStatus(value: String): PlayStatus = PlayStatus.valueOf(value)

    @TypeConverter
    fun fromPurchaseCondition(value: PurchaseCondition): String = value.name

    @TypeConverter
    fun toPurchaseCondition(value: String): PurchaseCondition = PurchaseCondition.valueOf(value)

    @TypeConverter
    fun fromVisibility(value: Visibility): String = value.name

    @TypeConverter
    fun toVisibility(value: String): Visibility = Visibility.valueOf(value)
}
