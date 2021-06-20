package red.man10.man10guild

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

import red.man10.man10guild.game.GameSystem
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Man10Guild : JavaPlugin() {

    companion object{

        lateinit var plugin: JavaPlugin
        lateinit var vault : VaultManager
        lateinit var es : Executor

        var guildCost = 0.0


    }

    override fun onEnable() {
        // Plugin startup logic

        saveDefaultConfig()

        plugin = this
        vault = VaultManager(this)
        es = Executors.newCachedThreadPool()

        loadConfig()

        GameSystem.loadGuilds()


    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){

            sender.sendMessage("")

            return true
        }

        if (!sender.hasPermission("guild.user"))return false

        when(args[0]) {

            "request" ->{
                if (sender !is Player)return false

                if (GameSystem.getJoinGuild(sender.uniqueId) != -1){
                    sender.sendMessage("§c§lあなたはすでにギルドに入っています！")
                    return true
                }

                if (GameSystem.getJoinGuild(sender.uniqueId) != -2){
                    sender.sendMessage("§c§lあなたはすでにギルドに申請しています！")
                    return true
                }

                val id = args[1].toIntOrNull()?:return true

                if (!GameSystem.guilds.keys().toList().contains(id)){
                    sender.sendMessage("§c§l存在しないギルドです！")
                    return true
                }

                es.execute {
                    GameSystem.joinRequest(sender.uniqueId,id)
                    sender.sendMessage("§a§l参加申請をしました")
                }

            }

            "leave" ->{

                if (sender !is Player)return false

                val id = GameSystem.getJoinGuild(sender.uniqueId)

                if (id== -1){
                    sender.sendMessage("§c§lあなたはどのギルドにも属していません！")
                    return true
                }

                es.execute {

                    if (id == -2){
                        GameSystem.cancelRequest(sender.uniqueId)
                        sender.sendMessage("§a§lギルドの参加申請をキャンセルしました")
                        return@execute
                    }

                    GameSystem.leaveGuild(sender.uniqueId)
                    sender.sendMessage("§a§lギルドから退会しました")
                }

            }

            "create" ->{//リーダー(コマンド発行者)と副リーダー(引数)を必須にする

                if (sender !is Player)return false

                if (!vault.withdraw(sender.uniqueId,guildCost)){

                    sender.sendMessage("ギルドを設立するための資金が足りません！")

                    return true
                }

                if (args.size != 3){

                    sender.sendMessage("§c§l/guild create <サブリーダー> <ギルド名(後で変更可能)>")
                    return true
                }

                val subLeader = Bukkit.getPlayer(args[1])
                val guildName = args[2]

                if (subLeader == null){

                    sender.sendMessage("§c§l指定したサブリーダーがオフラインの可能性があります")
                    return true
                }

                sender.sendMessage("§e§lギルドを設立中・・・§k§lX")

                es.execute {
                    GameSystem.createGuild(sender,subLeader,guildName)

                    sender.sendMessage("§a§lギルドの設立ができました！")
                    //TODO:設立後にまずやることをチャットに表示する
                }
            }

            "accept" ->{//guild accept id <uuid>

                if (sender !is Player)return false

                val id = args[1].toIntOrNull()?:return false

                val guild = GameSystem.guilds[id]?:return false

                if (!guild.isLeaderOrSubLeader(sender.uniqueId))return false

                val uuid : UUID

                try {
                    uuid = UUID.fromString(args[2])
                }catch (e:Exception){
                    sender.sendMessage("§c§lコマンドの使用方法に問題があります！")
                    return true
                }

                es.execute {
                    GameSystem.acceptRequest(uuid,id)
                    sender.sendMessage("§a§l申請を許可しました")
                }

            }

            "reject" ->{//guild reject id <uuid>

                if (sender !is Player)return false

                val id = args[1].toIntOrNull()?:return false

                val guild = GameSystem.guilds[id]?:return false

                if (!guild.isLeaderOrSubLeader(sender.uniqueId))return false

                val uuid : UUID

                try {
                    uuid = UUID.fromString(args[2])
                }catch (e:Exception){
                    sender.sendMessage("§c§lコマンドの使用方法に問題があります！")
                    return true
                }

                es.execute {
                    GameSystem.rejectRequest(uuid,id)
                    sender.sendMessage("§a§l申請を拒否しました")
                }

            }


        }




        return false
    }

    fun loadConfig(){

        guildCost = config.getDouble("guild_cost")

    }
}