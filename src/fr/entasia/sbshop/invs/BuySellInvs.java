package fr.entasia.sbshop.invs;

import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
import fr.entasia.apis.menus.MenuFlag;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.sbshop.utils.links.CatLink;
import fr.entasia.sbshop.utils.links.MenuLink;
import fr.entasia.sbshop.utils.ShopCat;
import fr.entasia.skycore.apis.BaseAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuySellInvs {

    public static MenuCreator buyShopMenu = new MenuCreator() {
        @Override
        public void onMenuClick(MenuClickEvent e) {
            MenuLink ml = (MenuLink) e.data;
            if (e.slot == 0) BaseInvs.openSubShop(e.player, ml.shop, ml.page);
            else{
                int itemNum;
                int pay = ml.sproduct.buyPrice;
                if (e.slot == 11) itemNum = ml.sproduct.by;
                else if (e.slot == 15){
                    itemNum = ml.sproduct.by*ml.sproduct.by_mult;
                    pay*=ml.sproduct.by_mult;
                }
                else return;
                if (ml.sp.getMoney() < pay) {
                    e.player.sendMessage("§cTu n'as pas assez d'argent !");
                    e.player.closeInventory();
                }else{
                    if (e.player.getInventory().firstEmpty() == -1) {
                        int possible = 0;
                        for (Map.Entry<Integer, ? extends ItemStack> slot : e.player.getInventory().all(ml.sproduct.type).entrySet()) {
                            possible += (64 - slot.getValue().getAmount());
                            if (possible >= itemNum) break;
                        }
                        if (possible < itemNum) {
                            e.player.sendMessage("§cTon inventaire ne peut pas contenir cet achat !");
                            return;
                        }
                    }
                    ml.sp.withdrawMoney(pay);
                    e.player.getInventory().addItem(new ItemStack(ml.sproduct.type, itemNum));
                }
            }
        }
    };

    public static void openBuyShop(Player p, MenuLink ml) {
        Inventory inv = buyShopMenu.createInv(3, "§cAchat>>", ml);

        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cRetour au menu précédent");
        item.setItemMeta(meta);
        inv.setItem(0, item);

        item = new ItemStack(ml.sproduct.type, ml.sproduct.by);
        meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        if(ml.sproduct.by==1){
            lore.add("§3Unité: " + ml.sproduct.buyPrice + "$");
            lore.add("§3Stack: " + (ml.sproduct.buyPrice * 64)+ "$");
        }else{
            lore.add("§3"+ml.sproduct.by+": " + ml.sproduct.buyPrice + "$");
            lore.add("§3"+ml.sproduct.by*ml.sproduct.by_mult +": " + (ml.sproduct.buyPrice * ml.sproduct.by_mult)+ "$");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);

        item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        if(ml.sproduct.by==1){
            meta.setDisplayName("§2Acheter à l'unité");
        }else{
            meta.setDisplayName("§2Acheter "+ml.sproduct.by);
        }
        lore = new ArrayList<>();
        lore.add("§3Prix: " +  ml.sproduct.buyPrice + "$");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(11, item);

        item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        if(ml.sproduct.by==1){
            meta.setDisplayName("§2Acheter par stack");
        }else{
            meta.setDisplayName("§2Acheter "+ml.sproduct.by*ml.sproduct.by_mult);
        }
        lore = new ArrayList<>();
        lore.add("§3Prix: " + (ml.sproduct.buyPrice * ml.sproduct.by_mult) + "$");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(15, item);

        item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        meta.setDisplayName("");
        item.setItemMeta(meta);
        for (int i = 0; i < 27; i++) if (!(i == 0 || i == 11 || i == 13 || i == 15)) inv.setItem(i, item);

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
    }

    public static MenuCreator sellShopMenu = new MenuCreator() {
        @Override
        public void onMenuClick(MenuClickEvent e) {
            MenuLink ml = (MenuLink) e.data;
            int itemNum;
            int collect = 0;
            if (e.slot == 0) BaseInvs.openSubShop(e.player, ml.shop, ml.page);
            else{
                if (e.slot == 11) itemNum = ml.sproduct.by;
                else if (e.slot == 15) itemNum = ml.sproduct.by*ml.sproduct.by_mult;
                else return;
                HashMap<Integer, ItemStack> real = new HashMap<>();
                for(Map.Entry<Integer, ? extends ItemStack> item : e.player.getInventory().all(ml.sproduct.type).entrySet()) {
                    collect += item.getValue().getAmount();
                    real.put(item.getKey(), item.getValue());
                    if (collect >= itemNum) break;

                }
                if(collect>=itemNum){
                    for	(Map.Entry<Integer, ItemStack> slot: real.entrySet()) {
                        if (slot.getValue().getAmount() < itemNum){
                            e.player.getInventory().setItem(slot.getKey(), null);
                            itemNum-=slot.getKey();
                        }else{
                            e.player.getInventory().setItem(slot.getKey(), slot.getValue().subtract(itemNum));
                            break;
                        }
                    }
                    ml.sp.addMoney(ml.sproduct.sellPrice * itemNum);
                }else e.player.sendMessage("§cTu n'as pas assez d'items dans ton inventaire !");
            }
        }
    };

    public static void openSellShop(Player p, MenuLink ml) {
        Inventory inv = sellShopMenu.createInv(3, "§cVente>>", ml);

        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cRetour au menu précédent");
        item.setItemMeta(meta);
        inv.setItem(0, item);

        item = new ItemStack(ml.sproduct.type, ml.sproduct.by);
        meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        if(ml.sproduct.by==1){
            lore.add("§3Unité: §b" + ml.sproduct.sellPrice + "§3$");
            lore.add("§3Stack: §b" + (ml.sproduct.sellPrice * 64)+ "§3$");
        }else{
            lore.add("§3"+ml.sproduct.by+": §b" + ml.sproduct.sellPrice + "§3$");
            lore.add("§3"+ml.sproduct.by*ml.sproduct.by_mult +": §b" + (ml.sproduct.sellPrice * ml.sproduct.by_mult)+ "§3$");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);

        item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        if(ml.sproduct.by==1){
            meta.setDisplayName("§2Vendre à l'unité");
        }else{
            meta.setDisplayName("§2Vendre "+ml.sproduct.by);
        }
        lore = new ArrayList<>();
        lore.add("§3Prix: " + ml.sproduct.sellPrice + "$");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(11, item);

        item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        if(ml.sproduct.by==1){
            meta.setDisplayName("§2Vendre par stack");
        }else{
            meta.setDisplayName("§2Vendre "+ml.sproduct.by*ml.sproduct.by_mult);
        }
        lore = new ArrayList<>();
        lore.add("§3Prix: " + (ml.sproduct.sellPrice * ml.sproduct.by_mult) + "$");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(15, item);

        item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        meta.setDisplayName("");
        item.setItemMeta(meta);
        for (int i = 0; i < 27; i++) if (!(i == 0 || i == 11 || i == 13 || i == 15)) inv.setItem(i, item);

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
    }


    public static MenuCreator metasShopMenu = new MenuCreator() {
        @Override
        public void onMenuClick(MenuClickEvent e) {
            MenuLink ml = (MenuLink)e.data;
            if (e.slot == 31)BaseInvs.openSubShop(e.player, ml.shop, 0);
            else if(e.slot<17){
                ml.sproduct = ml.shop.getItem(e.item.getType());
                if(ml.sproduct ==null){
                    e.player.sendMessage("§cUne erreur s'est produite ! Merci de contacter un membre du Staff");
                    ServerUtils.permMsg("errorlog", "Item invalide demandé dans le shop ! "+e.item.getType()+":"+e.item.getDurability());
                }else{
                    ml.sp = BaseAPI.getOnlineSP(e.player);
                    if(ml.sproduct.buyPrice<=1){
                        e.player.sendMessage("§cUne erreur s'est produite, contacte un membre du Staff ! (Invalid buy price)");
                        e.player.closeInventory();
                        ServerUtils.permMsg("errorlog", "§cShop : L'item "+ml.sproduct +" à un prix invalide !");
                    }
                    if(e.click==MenuClickEvent.ClickType.LEFT) {
                        if (ml.sproduct.buyPrice != 0) openBuyShop(e.player, ml);
                    }else if(e.click==MenuClickEvent.ClickType.RIGHT){
                        if (ml.sproduct.sellPrice != 0) openSellShop(e.player, ml);
                    }else{
                        ServerUtils.permMsg("log.shoperror", "§cShop : Action de click non reconnue !");
                        e.player.sendMessage("§cUne erreur s'est produite, contacte un membre du Staff ! (No such action)");
                        e.player.closeInventory();
                    }
                }
            }
        }
    }.setFlags(MenuFlag.AllItemsTrigger);

    public static void openCatShop(Player p, CatLink ml) {
        Inventory inv = metasShopMenu.createInv(4, "§5Shop>> §2Types", ml);

        ArrayList<String> lore;
        ItemStack item;
        ItemMeta meta;
        ShopCat cat = (ShopCat) ml.scat;
        for (int i = 0; i < ml.sproduct.maxMeta; i++) {
            item = new ItemStack(ml.sproduct.type, ml.sproduct.by, (short) i);

            meta = item.getItemMeta();
            lore = new ArrayList<>();
            if (ml.sproduct.buyPrice == 0) lore.add("§2Achat Impossible");
            else lore.add("§2Prix: §a" + ml.sproduct.buyPrice + "§2$ (Click gauche pour acheter)");
            if (ml.sproduct.sellPrice == 0) lore.add("§2Vente impossible");
            else lore.add("§2Vente: §a" + ml.sproduct.sellPrice + "§2$ (Click droit pour vendre)");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        // footer | deux lignes
        item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for(int i=18;i<27;i++)inv.setItem(i, item);

        // return button
        item = new ItemStack(Material.WRITABLE_BOOK);
        meta = item.getItemMeta();
        meta.setDisplayName("§cRetour au menu précédent");
        item.setItemMeta(meta);
        inv.setItem(31, item);

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
    }

}
