package com.emreberkgoger.scoutbook.models

class Team(val name: String,
           val id: Int,
           val country: String,
           val league: String,
           val founded: String,
           val ground: String,
           val capacity: String,
           val chairman: String,
           val headCoach: String,
           val image: ByteArray?) {
}
