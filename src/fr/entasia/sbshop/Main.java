package fr.entasia.sbshop;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import fr.entasia.sbshop.commands.ShopCmd;
import fr.entasia.sbshop.commands.ShopReloadCmd;
import fr.entasia.sbshop.utils.shop.ShopCat;
import fr.entasia.sbshop.utils.shop.ShopItem;
import fr.entasia.sbshop.utils.shop.ShopProduct;
import fr.entasia.sbshop.utils.shop.SubShop;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

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

	private static void category(SubShop sub, String a) throws Throwable {
		ConfigurationSection cs = main.getConfig().getConfigurationSection(a);
		if (cs == null) {
			main.getLogger().warning("Catégorie de la configuration " + a + " vide !");
			return;
		}
		sub.price_modifier = cs.getInt("price-modifier");

		ShopItem sitem;
		ConfigurationSection cs2 = cs.getConfigurationSection("items");
		if(cs2!=null){
			for (Map.Entry<String, Object> e : cs2.getValues(false).entrySet()) {
				cs2 = (ConfigurationSection) e.getValue();

				sitem = new ShopItem(sub);
				sitem.icon = Material.getMaterial(e.getKey());
				if (sitem.icon == null) warn(sub, e.getKey(), "Type invalide");
				else if(completeProduct(sitem, cs2))sub.items.add(sitem);
			}
		}

		ShopCat scat;
		Field f;
		cs2 = cs.getConfigurationSection("categories");
		if(cs2!=null) {
			for (Map.Entry<String, Object> e : cs2.getValues(false).entrySet()) {
				cs2 = (ConfigurationSection) e.getValue();

				scat = new ShopCat(sub);
				try {
					f = Tag.class.getDeclaredField(e.getKey());
				} catch (NoSuchFieldException ignore) {
					try {
						f = MaterialTags.class.getDeclaredField(e.getKey());
					} catch (NoSuchFieldException ignore2) {
						warn(sub, e.getKey(), "Catégorie invalide : " + e.getKey());
						continue;
					}
				}
				scat.cat = (Tag<Material>) f.get(null);
				scat.icon = scat.cat.getValues().iterator().next();

				if (completeProduct(scat, cs2)) sub.cats.add(scat);

			}
		}
	}

	private static boolean completeProduct(ShopProduct product, ConfigurationSection sec){
		product.by = sec.getInt("by", 1);
		product.modifier = sec.getInt("price-modifier", 1);
		product.buyPrice = sec.getInt("buy") * product.modifier * product.shop.price_modifier * Main.global_modifier;
		product.sellPrice = sec.getInt("sell") * product.modifier * product.shop.price_modifier * Main.global_modifier;
		product.by_mult = 64 / product.by;

		if (product.sellPrice < 0) warn(product.shop, product.icon, "Prie de vente négatif");
		else if (product.buyPrice < 0) warn(product.shop, product.icon, "Prie d'achat négatif");
		else if (product.buyPrice != 0 && product.sellPrice > product.buyPrice)
			warn(product.shop, product.icon, "Prix de vente plus haut que celui d'achat");
		else return true;
		return false;

				/*
				sitem.by = 6;
				6 * x <= 64
				x <= 64/6
				x <= 10
				nombre max = 10
				(ca floor si virgule)
				*/

	}

	private static void warn(SubShop sub, Object identifier, String msg){
		main.getLogger().warning("Erreur sur le produit "+identifier+" (catégorie "+sub.name()+") : "+msg);
	}
}
