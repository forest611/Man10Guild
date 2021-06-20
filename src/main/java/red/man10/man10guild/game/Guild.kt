package red.man10.man10guild.game

import java.util.*

class Guild {


    var id = 0

    var guildName = ""
    var regions = mutableListOf<Int>()

    var leaderUUID : UUID? = null
    var subLeaderUUID : UUID? = null

    fun isLeaderOrSubLeader(uuid: UUID):Boolean{
        if (leaderUUID == uuid || subLeaderUUID == uuid)return true
        return false
    }

}