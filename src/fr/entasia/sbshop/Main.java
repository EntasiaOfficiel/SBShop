package fr.entasia.sbshop;

import fr.entasia.sbshop.commands.ShopCmd;
import fr.entasia.sbshop.commands.ShopReloadCmd;
import fr.entasia.sbshop.utils.ShopItem;
import fr.entasia.sbshop.utils.SubShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {

	public static Main main;
	public static int global_modifier;


	@Override
	public void onEnable() {
		try{
			main = this;
			saveDefaultConfig();
			getLogger().info("Starting version 1.0 (Beta)");
			global_modifier = main.getConfig().getInt("global_modifier");
			getCommand("shop").setExecutor(new ShopCmd());
			getCommand("shopreload").setExecutor(new ShopReloadCmd());
			loadConfig();
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("Une erreur est survenue ! ARRET DU SERVEUR");
			getServer().shutdown();
		}
	}

	public static void loadConfig() {
		main.reloadConfig();
		category(SubShop.BLOCKS, "shops.blocks");
		category(SubShop.ORES, "shops.ores");
		category(SubShop.FOOD, "shops.food");
		category(SubShop.HARVEST, "shops.harvest");
		category(SubShop.LOOTS, "shops.loots");

	}

	public static void category(SubShop cat, String a){
		cat.items = new ArrayList<>();
		ConfigurationSection cs = main.getConfig().getConfigurationSection(a);
		if(cs==null){
			main.getLogger().warning("Catégorie de la configuration "+a+" vide !");
		} else{
			cat.price_modifier = cs.getInt("price-modifier");

			ShopItem sitem;
			ConfigurationSection cs2;
			for(String s : cs.getConfigurationSection("items").getKeys(false)){

				cs2 = main.getConfig().getConfigurationSection(a+".items."+s);

				sitem = new ShopItem();
				sitem.shop = cat;

				sitem.type = Material.getMaterial(cs2.getString("type").toUpperCase());
				sitem.meta = (short) cs2.getInt("meta");
				sitem.buy = cs2.getInt("buy");
				sitem.sell = cs2.getInt("sell");
				sitem.modifier = cs2.getInt("price-modifier", 1);
				sitem.metas = (short) cs2.getInt("metas");

				if(sitem.type==null)main.getLogger().warning("Erreur sur l'item "+cs.getString("type")+":"+sitem.meta+" : type invalide");
				else if(sitem.sell>sitem.buy)main.getLogger().warning("Erreur sur l'item "+sitem.type+":"+sitem.meta+" : PRIX DE VENTE PLUS HAUT QUE L'ACHAT");
				else{
					cat.items.add(sitem);
				}
			}
		}
	}

	@Override
	public void onDisable() {
		getLogger().info("Stopping plugin");
	}

}
