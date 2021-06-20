package red.man10.man10guild.game

import org.bukkit.Bukkit
import java.util.*

/**
 *
 * 値を変更するときにdbに書き込むのでスレッドを使う
 *
 */
class Guild {

    var id = 0

    var guildName = ""
    set(value) {
        GameSystem.mysql.execute("UPDATE guild_list SET guild_name='${value}' WHERE id=$id")
        field = value
    }


    var regions = mutableListOf<Int>()
    set(value) {

        val json = GameSystem.gson.toJson(value)

        GameSystem.mysql.execute("UPDATE guild_list SET region='${json}' WHERE id=$id")
        field = value
    }

    var leaderUUID : UUID? = null
    set(value) {
        val p = Bukkit.getOfflinePlayer(value!!)

        GameSystem.mysql.execute("UPDATE guild_list " +
                "SET leader_player='${p.name}',leader_uuid='${value}' WHERE id=$id")
        field = value
    }


    var subLeaderUUID : UUID? = null
        set(value) {
            val p = Bukkit.getOfflinePlayer(value!!)

            GameSystem.mysql.execute("UPDATE guild_list " +
                    "SET sub_leader_player='${p.name}',sub_leader_uuid='${value}' WHERE id=$id")
            field = value
        }



    fun isLeaderOrSubLeader(uuid: UUID):Boolean{
        if (leaderUUID == uuid || subLeaderUUID == uuid)return true
        return false
    }

}