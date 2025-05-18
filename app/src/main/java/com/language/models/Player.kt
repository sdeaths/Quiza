package com.language.models

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Parcel
import android.os.Parcelable
import com.language.R

data class Player(
    val name: String,
    val score: Int = 0,
    val avatar: Int,
    val guessedWords: MutableList<String> = mutableListOf(),
    val notGuessedWords: MutableList<String> = mutableListOf(),
    val lastRoundGuessed: MutableList<String> = mutableListOf(),
    val lastRoundNotGuessed: MutableList<String> = mutableListOf()
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(score)
        parcel.writeInt(avatar)
        parcel.writeStringList(guessedWords)
        parcel.writeStringList(notGuessedWords)
        parcel.writeStringList(lastRoundGuessed)
        parcel.writeStringList(lastRoundNotGuessed)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        const val MAX_NAME_LENGTH = 12

        override fun createFromParcel(parcel: Parcel): Player {
            return Player(
                name = parcel.readString()?.take(MAX_NAME_LENGTH) ?: "",
                score = parcel.readInt(),
                avatar = parcel.readInt(),
                guessedWords = parcel.createStringArrayList() ?: mutableListOf(),
                notGuessedWords = parcel.createStringArrayList() ?: mutableListOf(),
                lastRoundGuessed = parcel.createStringArrayList() ?: mutableListOf(),
                lastRoundNotGuessed = parcel.createStringArrayList() ?: mutableListOf()
            )
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}

