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
			getLogger().info("Démarrage du plugin...");
			getCommand("shop").setExecutor(new ShopCmd());
			getCommand("shopreload").setExecutor(new ShopReloadCmd());
			loadConfig();
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("Une erreur est survenue ! ARRET DU SERVEUR");
			getServer().shutdown();
		}
	}

	public static void loadConfig() throws Throwable {
		main.reloadConfig();
		global_modifier = main.getConfig().getInt("global_modifier");
		category(SubShop.BLOCKS, "shops.blocks");
		category(SubShop.ORES, "shops.ores");
		category(SubShop.FOOD, "shops.food");
		category(SubShop.HARVEST, "shops.harvest");
		category(SubShop.LOOTS, "shops.loots");

	}

	private static void category(SubShop cat, String a) throws Throwable {
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
				sitem.maxMeta = (short) cs2.getInt("maxMeta");
				if(sitem.maxMeta==0) sitem.meta = (short) cs2.getInt("meta");
				sitem.by = cs2.getInt("by", 1);
				sitem.modifier = cs2.getInt("price-modifier", 1);
				sitem.buyPrice = cs2.getInt("buy") * sitem.modifier * sitem.shop.price_modifier * Main.global_modifier;
				sitem.sellPrice = cs2.getInt("sell") * sitem.modifier * sitem.shop.price_modifier * Main.global_modifier;

				// sitem.by = 6;
				// 6 * x <= 64
				// x <= 64/6
				// x <= 10
				// nombre max = 10

				sitem.by_mult = 64/sitem.by;

				if(sitem.type==null)warn(cat, cs2.getString("type"), sitem.meta, "Type invalide");
				else if(sitem.sellPrice <0)warn(cat, cs2.getString("type"), sitem.meta, "Prie de vente négatif");
				else if(sitem.buyPrice <0)warn(cat, cs2.getString("type"), sitem.meta, "Prie d'achat négatif");
				else if(sitem.sellPrice >sitem.buyPrice)warn(cat, cs2.getString("type"), sitem.meta, "Prix de vente plus haut que celui d'achat");
				else{
					cat.items.add(sitem);
				}
			}
		}
	}

	private static void warn(SubShop cat, String type, short meta, String msg){
		main.getLogger().warning("Erreur sur l'item "+type+":"+meta+" (catégorie "+cat.name()+") : "+msg);
	}
}
