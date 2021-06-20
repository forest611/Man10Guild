package red.man10.man10guild.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import red.man10.man10guild.gui.GuildGUI

object InventoryListener : Listener{


    @EventHandler
    fun inventoryClick(e:InventoryClickEvent){

        val p = e.whoClicked

        if(p !is Player)return

        when(GuildGUI.playerGui[p]?:return){



        }


    }

    @EventHandler
    fun inventoryClose(e:InventoryCloseEvent){

        val p = e.player

        if (GuildGUI.playerGui[p]!=null){
            GuildGUI.playerGui.remove(p)
        }
    }


}