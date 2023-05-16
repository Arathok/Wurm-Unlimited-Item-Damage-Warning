package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.SQLException;

public class TurnDamWarnOffPerformer implements ActionPerformer {
    public  ActionEntry actionEntry;


    public TurnDamWarnOffPerformer() {
        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Toggle Dam. Warn. Off", "thinking",
                new int[]{
                        6 /* ACTION_TYPE_NOMOVE */,
                        48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                        35 /* DONT CARE WHETHER SOURCE OR TARGET */,

                }).range(4).build();


        ModActions.registerAction(actionEntry);

    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        Connection dbConn = ModSupportDb.getModSupportDb();
        DamageWarning toRemove = null;
        int index=0;
        for (DamageWarning aDamageWarning : Hook.giveWarning)
        {
            if (target.getWurmId()==aDamageWarning.itemId)
            toRemove=aDamageWarning;
            index = Hook.giveWarning.indexOf(toRemove);
        }
        if(toRemove!=null) {
            Hook.giveWarning.remove(index);
            try {
                TurnDamWarnOnPerformer.remove(toRemove);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            performer.getCommunicator().sendSafeServerMessage("You don not care about the damage on this item any more.");
        }
        return propagate(action,
                ActionPropagation.FINISH_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }
    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter)
    {
        return action(action, performer, target, num, counter);
    } // NEEDED OR THE ITEM WILL ONLY ACTIVATE IF YOU HAVE NO ITEM ACTIVE
}
