package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.NoSuchPlayerException;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class TurnDamWarnOnPerformer implements ActionPerformer {
    public ActionEntry actionEntry;



    public TurnDamWarnOnPerformer() {
        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Turn On Dam. Warn.", "thinking",
                new int[]{
                        6 /* ACTION_TYPE_NOMOVE */,
                        48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                        35 /* DONT CARE WHETHER SOURCE OR TARGET */,

                }).range(4).build();


        ModActions.registerAction(actionEntry);

    }

    @Override
    public boolean action(com.wurmonline.server.behaviours.Action action, Creature performer, Item source, Item target, short num, float counter) {
        return action(action, performer, target, num, counter);
    } // NEEDED OR THE ITEM WILL ONLY ACTIVATE IF YOU HAVE NO ITEM ACTIVE

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    public static boolean canUse(Creature performer, Item target) {
        return performer.isPlayer() && (target.getOwnerName().equals(performer.getName()) || target.getLastOwnerId() == performer.getWurmId()) && !target.isTraded();
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {

    DamageWarning damageWarningToSend = null;
        if (!canUse(performer, target)) {
            performer.getCommunicator().sendAlertServerMessage("Only the Owner of that Item can do that!");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        try {
            boolean damageWarningfound = false;
            float targetDamage = 0;
            boolean warningSetting = false;
            int index=-1;
            for (DamageWarning aDamageWarning: Hook.giveWarning)
            {


                if (aDamageWarning.playerId==performer.getWurmId()&&Hook.giveWarning.indexOf(aDamageWarning)>index)
                {

                    targetDamage=aDamageWarning.targetDamage;
                    warningSetting=aDamageWarning.warningType;
                    damageWarningfound=true;
                    index = Hook.giveWarning.indexOf(aDamageWarning);

                }

            }
            if (!damageWarningfound)
                damageWarningToSend=new DamageWarning(target.getWurmId(), performer.getWurmId(),0.0f,false,90.0f,false);
            else
                damageWarningToSend=new DamageWarning(target.getWurmId(), performer.getWurmId(),0.0f,false,targetDamage,warningSetting);

            DamageWarningQuestion.send(damageWarningToSend);
        } catch (NoSuchPlayerException e) {
            e.printStackTrace();
        }


        return propagate(action,
                ActionPropagation.FINISH_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    public static void readFromDb() {
        try {
            Connection dbconn = ModSupportDb.getModSupportDb();
            PreparedStatement ps = dbconn.prepareStatement("SELECT * FROM ArathoksDamageWarningsV3");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long itemId = rs.getLong("itemId"); // liest quasi den Wert von der Spalte
                long playerId = rs.getLong("playerId"); // liest quasi den Wert von der Spalte
                float targetdDamage = rs.getFloat("targetDamage");
                boolean warningLevel = rs.getBoolean("warningType");
                float previousDamageSetting = rs.getFloat("previousDamageSetting");
                boolean previousWarningLevel = rs.getBoolean("previousWarningType");

                Hook.giveWarning.add(new DamageWarning(itemId,playerId,targetdDamage,warningLevel,previousDamageSetting,previousWarningLevel));

            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(DamageWarning aDamageWarning) throws SQLException {
        try {
            Connection dbconn = ModSupportDb.getModSupportDb();
            PreparedStatement ps = dbconn.prepareStatement("INSERT OR REPLACE INTO ArathoksDamageWarningsV3 (itemId,playerId,targetDamage,warningType) VALUES (?,?,?,?)");
            ps.setLong(1, aDamageWarning.itemId);
            ps.setLong(2, aDamageWarning.playerId);
            ps.setFloat(3, aDamageWarning.targetDamage);
            ps.setBoolean(4, aDamageWarning.warningType);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            ItemDamageWarning.logger.log(Level.WARNING, "something went wrong writing to the DB!", throwables);
            throwables.printStackTrace();
        }


    }

    public static void remove(DamageWarning aDamageWarning) throws SQLException {
        Connection dbconn= ModSupportDb.getModSupportDb();
            PreparedStatement ps = dbconn.prepareStatement("DELETE FROM ArathoksDamageWarningsV3 WHERE itemId = ?");
            ps.setLong(1, aDamageWarning.itemId);
            ps.execute();
            ps.close();

    }


}
