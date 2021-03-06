package red.man10.man10guild.game

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import red.man10.man10guild.Man10Guild
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object GameSystem {


    val guilds = ConcurrentHashMap<Int,Guild>()

    val gson = Gson()
    lateinit var mysql : MySQLManager


    fun loadGuilds(){

        mysql = MySQLManager(Man10Guild.plugin,"loadGuilds")

        val rs = mysql.query("select * from guilds;")?:return

        while (rs.next()){
            val guild = Guild()

            guild.id = rs.getInt("guild")

            guild.guildName = rs.getString("guild_name")
            guild.leaderUUID = UUID.fromString(rs.getString("leader_uuid"))
            guild.subLeaderUUID = UUID.fromString(rs.getString("sub_leader_uuid"))

            guild.regions = gson.fromJson(rs.getString("region"),Array<Int>::class.java).toMutableList()

            guilds[guild.id] = guild
        }

        mysql.close()
        rs.close()

    }

    //Q.新規ギルドを作るとき、必ずどこか一つ土地を持っておくかどうか
    fun createGuild(leader:Player,subLeader:Player,guildName:String){

        val initRegion = gson.toJson(mutableListOf<Int>())

        mysql.execute("INSERT INTO guild_list (" +
                "guild_name, leader_player, leader_uuid, sub_leader_player, sub_leader_uuid, region) VALUES " +
                "('${guildName}', '${leader.name}', '${leader.uniqueId}', '${subLeader.name}', '${subLeader.uniqueId}', '${initRegion}')")

    }

    fun deleteGuild(id:Int){
        mysql.execute("delete from guilds where id=$id;")
    }

    //ユーザーが参加してるギルドのテーブルに保存するが、メンバーフラグは立てない
    fun joinRequest(p: Player,id:Int){

        mysql.execute("INSERT INTO guild_player_list " +
                "(player, uuid, guild_id, accept) VALUES ('${p.name}', '${p.uniqueId}', ${id}, 0);")
    }

    fun cancelRequest(user: UUID){
        leaveGuild(user)
    }

    fun acceptRequest(user:UUID,id:Int){
        mysql.execute("UPDATE guild_player_list SET accept = 1 " +
                "WHERE id=${id} and uuid='${user}';")
    }

    //ギルド参加のリクエストを却下する
    fun rejectRequest(user:UUID){
        leaveGuild(user)
    }

    fun getCurrentGuild(user:UUID):Int{

        val rs = mysql.query("SELECT guild_id,accept from guild_player_list where uuid='${user}';")?:return -1
        rs.next()

        val id = if (rs.getInt("accept") == 0) -2 else rs.getInt("guild_id")

        rs.close()
        mysql.close()

        return  id
    }

    fun leaveGuild(user:UUID){
        mysql.execute("DELETE FROM guild_player_list where uuid='${user}';")
    }

    fun memberList(id:Int):MutableList<OfflinePlayer>{

        val list = mutableListOf<OfflinePlayer>()

        val rs = mysql.query("select uuid from guild_player_list where id=${id};")?:return list

        while (rs.next()){
            list.add(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid"))))
        }

        rs.close()
        mysql.close()

        return list
    }


}
