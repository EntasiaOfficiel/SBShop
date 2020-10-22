package fr.entasia.sbshop.commands;

import fr.entasia.sbshop.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopReloadCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("skyblock.shop.reload")){
			try{
				Main.loadConfig();
				sender.sendMessage("§aConfiguration rechargée avec succès !");
			}catch(Throwable e){
				e.printStackTrace();
				sender.sendMessage("§cUne erreur s'est produite !");
			}
		}else sender.sendMessage("§cTu n'as pas accès à cette commande !");

		return true;
	}

}
