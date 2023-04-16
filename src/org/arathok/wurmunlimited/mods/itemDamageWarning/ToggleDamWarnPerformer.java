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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class ToggleDamWarnPerformer implements ActionPerformer {
    public ActionEntry actionEntry;


    public ToggleDamWarnPerformer() {
        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Toggle Dam. Warn.", "closing",
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


        if (!canUse(performer, target)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        if (!Hook.giveWarning.containsKey(target.getWurmId())) {
            Connection dbconn = ModSupportDb.getModSupportDb();
            Hook.giveWarning.put(target.getWurmId(), performer.getWurmId());

            try {
                ToggleDamWarnPerformer.insert(dbconn,target.getWurmId(),performer.getWurmId());
            } catch (SQLException e) {
                ItemDamageWarning.logger.log(Level.WARNING, "something went wrong writing to the DB!", e);
                e.printStackTrace();
            }
            performer.getCommunicator().sendSafeServerMessage("You make a mental note to care about this items Damage.");

        } else {
            Hook.giveWarning.remove(target.getWurmId());
            performer.getCommunicator().sendSafeServerMessage("You no longer care about the damage on this item.");
            Connection dbconn = ModSupportDb.getModSupportDb();

            try {
                ToggleDamWarnPerformer.remove(dbconn,target.getWurmId());
            } catch (SQLException e) {
                ItemDamageWarning.logger.log(Level.WARNING, "Could not remove from DB!", e);
                e.printStackTrace();
            }
        }


        return propagate(action,
                ActionPropagation.FINISH_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    public static void readFromDb(Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ArathoksDamageWarnings");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long itemId = rs.getLong("itemId"); // liest quasi den Wert von der Spalte
                long playerId = rs.getLong("playerId"); // liest quasi den Wert von der Spalte
                Hook.giveWarning.put(itemId, playerId);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Connection dbconn,long itemId,long playerId) throws SQLException {
        try {
            PreparedStatement ps = dbconn.prepareStatement("INSERT OR REPLACE INTO ArathoksDamageWarnings (itemId,playerId) VALUES (?,?)");
            ps.setLong(1, itemId);
            ps.setLong(2, playerId);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            ItemDamageWarning.logger.log(Level.WARNING, "something went wrong writing to the DB!", throwables);
            throwables.printStackTrace();
        }


    }

    public static void remove(Connection dbconn, long itemId) throws SQLException {

            PreparedStatement ps = dbconn.prepareStatement("DELETE FROM FuelStorageV2 WHERE itemId = ?");
            ps.setLong(1, itemId);
            ps.execute();
            ps.close();

    }

}
