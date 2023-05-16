package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.Message;
import com.wurmonline.server.Players;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.bml.BmlBuilder;
import org.gotti.wurmunlimited.modsupport.bml.BmlNodeBuilder;
import org.gotti.wurmunlimited.modsupport.bml.TextStyle;

public class InsertStuff {

    public static void insertStuff(Item item, float damage) {
        boolean warningType= false;
        float damageTarget=0.0F;
        boolean damageWarningFound=false;
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
            for (DamageWarning aWarning : Hook.giveWarning)
            {
                if (aWarning.itemId == item.getWurmId()) {
                    damageWarningFound = true;
                    warningType = aWarning.warningType;
                    damageTarget = aWarning.targetDamage;
                    break;
                }
            }

            if (!intrash && damage >= damageTarget && damage > item.getDamage() && damageWarningFound) {
                if (warningType) {
                    //ItemDamageWarning.logger.log(Level.INFO, "item is not null and not in trash"+item.getName() + item.getDamage() + (item.getDamage() >= 90.0 && (!givenWarning.get(item.getWurmId()) || givenWarning.get(item.getWurmId()) == null)));
                    BmlBuilder bmlBuilder = BmlBuilder.builder();                        //create a BmL Builder
                    BmlNodeBuilder header = BmlBuilder.header("Warning!"); // BIG CHONKER
                    BmlNodeBuilder label = BmlBuilder.label("Item Damage Warning!", TextStyle.BOLD);    // create chonkier Absatz
                    BmlNodeBuilder text = BmlBuilder.text(" Your Item " + item.getName() + " is above 90 Damage and should be repaired! ");
                    BmlNodeBuilder text1 = BmlBuilder.text("\nYour item ");
                    BmlNodeBuilder text2 = BmlBuilder.text(item.getName(), TextStyle.BOLD);
                    BmlNodeBuilder text3 = BmlBuilder.text(" is above "+damageTarget+ " Damage and should be repaired!");
                    bmlBuilder.withNode(BmlBuilder.varray(true)
                            //       .withNode(header)
                            .withNode(label)
                            .withNode(text1)
                            .withNode(text2)
                            .withNode(text3)
                    );

                    aplayer.getCommunicator().sendBml(250, 150, true, true, bmlBuilder.buildBml(), 255, 0, 0, "WARNING");
                    aplayer.playPersonalSound("sound.notification");
                    Message playerMessage = new Message(aplayer,(byte)4,"Item Damage Warning!","Your Item: "+item.getName()+" is above "+damageTarget+ " Damage and should be repaired!");
                    aplayer.getCommunicator().sendMessage(playerMessage);
                }
                if (!warningType)
                {
                    aplayer.playPersonalSound("sound.notification");
                    Message playerMessage = new Message(aplayer,(byte)4,"Item Damage Warning!","Your Item: "+item.getName()+" is above "+damageTarget+ " Damage and should be repaired!");
                    aplayer.getCommunicator().sendMessage(playerMessage);
                }
            }
        }
    }
}
