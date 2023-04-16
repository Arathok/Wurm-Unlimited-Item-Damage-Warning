package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.Players;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modsupport.bml.BmlBuilder;
import org.gotti.wurmunlimited.modsupport.bml.BmlNodeBuilder;
import org.gotti.wurmunlimited.modsupport.bml.TextStyle;

import java.util.HashMap;

public class Hook {
    public static HashMap<Long, Long> giveWarning = new HashMap<>();

    public static void insertAfter(Item item,float damage) {

        boolean intrash = false;
        //ItemDamageWarning.logger.log(Level.INFO, ""+item.getWurmId()+intrash);
        Player aplayer = Players.getInstance().getPlayerOrNull(item.getOwnerId());
        if (aplayer == null)
            aplayer = Players.getInstance().getPlayerOrNull(item.getLastOwnerId());
        if (aplayer != null) {

            Item parent = item.getParentOrNull();
            if (parent != null && parent.getTemplate().getName().contains("heap")) {
                intrash = true;

            }

            if (!intrash && damage >= Config.warningAt && damage > item.getDamage() && (giveWarning.containsKey(item.getWurmId()))) {
                //ItemDamageWarning.logger.log(Level.INFO, "item is not null and not in trash"+item.getName() + item.getDamage() + (item.getDamage() >= 90.0 && (!givenWarning.get(item.getWurmId()) || givenWarning.get(item.getWurmId()) == null)));
                BmlBuilder bmlBuilder = BmlBuilder.builder();                        //create a BmL Builder
                BmlNodeBuilder header = BmlBuilder.header("Warning!"); // BIG CHONKER
                BmlNodeBuilder label = BmlBuilder.label("Item Damage Warning!", TextStyle.BOLD);    // create chonkier Absatz
                BmlNodeBuilder text = BmlBuilder.text(" Your Item " + item.getName() + " is above 90 Damage and should be repaired! ");
                BmlNodeBuilder text1 = BmlBuilder.text("\nYour item\n\n");
                BmlNodeBuilder text2 = BmlBuilder.text(item.getName(), TextStyle.BOLD);
                BmlNodeBuilder text3 = BmlBuilder.text("is above 90 Damage and should be repaired!");
                bmlBuilder.withNode(BmlBuilder.varray(true)
                        //       .withNode(header)
                        .withNode(label)
                        .withNode(text1)
                        .withNode(text2)
                        .withNode(text3)
                );

                aplayer.getCommunicator().sendBml(250, 150, true, true, bmlBuilder.buildBml(), 255, 0, 0, "WARNING");

            }
        }
    }


    public static void insertHook() throws NotFoundException, CannotCompileException {
        //private boolean poll(Item parent, int parentTemp, boolean insideStructure, boolean deeded, boolean saveLastMaintained, boolean inMagicContainer, boolean inTrashbin)
        ClassPool classpool = HookManager.getInstance().getClassPool();
        CtClass ctItem = classpool.getCtClass("com.wurmonline.server.items.Item");
       // ctItem.getMethod("poll", "(Lcom/wurmonline/server/items/Item;IZZZZZ)Z").insertAfter("org.arathok.wurmunlimited.mods.itemDamageWarning.Hook.insertAfter(this);");
        ctItem = classpool.getCtClass("com.wurmonline.server.items.DbItem");
        ctItem.getMethod("setDamage", "(FZ)Z").insertBefore("org.arathok.wurmunlimited.mods.itemDamageWarning.Hook.insertAfter(this,$1);");
    }
}
