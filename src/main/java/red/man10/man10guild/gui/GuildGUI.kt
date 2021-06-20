package red.man10.man10guild.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object GuildGUI {

    val playerGui = ConcurrentHashMap<Player,GUIType>()

    //未加入
    fun mainMenu(p:Player){

        val inv = Bukkit.createInventory(null,27, Component.text("§e§lギルドに参加する"))


        setGUIType(p,GUIType.MAIN_MENU)
    }

    fun guildMenu(p:Player,id:Int){


        setGUIType(p,GUIType.GUILD_MENU)
    }

    fun memberList(p:Player,id:Int){

        setGUIType(p,GUIType.MEMBER_LIST)
    }

    fun memberRequestList(p:Player,id:Int){

        setGUIType(p,GUIType.MEMBER_REQUEST)
    }

    fun fighterRequestList(p:Player,id:Int){

        setGUIType(p,GUIType.FIGHTER_REQUEST)
    }

    fun guildList(p:Player){

    }

    fun setGUIType(p:Player,type:GUIType?){
        if (type == null){
            playerGui.remove(p)
            return
        }
        playerGui[p] = type
    }
}

enum class GUIType{

    MAIN_MENU,
    GUILD_MENU,
    MEMBER_LIST,
    MEMBER_REQUEST,
    FIGHTER_REQUEST,
    GUILD_LIST

}