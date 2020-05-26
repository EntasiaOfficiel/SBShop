package fr.entasia.sbshop.commands;

import fr.entasia.sbshop.Main;
import fr.entasia.sbshop.MenusManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopReloadCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;

		if(sender.hasPermission("admin.shopreload")){
			Main.loadConfig();
			sender.sendMessage("§aConfiguration rechargée ! (Voir les logs pour erreurs potencielles)");
		}else sender.sendMessage("§cTu n'as pas accès à cette commande !");

		return true;
	}

}
