package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.creatures.Communicator;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDamageWarning implements WurmServerMod, Initable, PlayerMessageListener, Configurable, ServerPollListener,ServerStartedListener {
    public static Connection dbconn;
    public static boolean finishedReadingDb = false;
    public static Logger logger = Logger.getLogger("ItemDamageWarning");
    @Override
    public void init() {
        try {
            logger.log(Level.INFO,"injecting Item Damage Warning Hook");
            Hook.insertHook();
        } catch (NotFoundException e) {
            logger.log(Level.SEVERE,"class Item not found",e);
            e.printStackTrace();
        } catch (CannotCompileException e) {
            logger.log(Level.SEVERE,"compiler error",e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String message) {
        if (message.contains("bml")) {
           Test.testMe(communicator.getPlayer());

            return true;

        }
        else
            return false;
    }

    @Override
    public void configure(Properties properties) {
        Config.warningAt=Float.parseFloat(properties.getProperty("warningAt","90.0"));
    }

    @Override
    public void onServerPoll() {

        if (!finishedReadingDb)
        {
            dbconn=ModSupportDb.getModSupportDb();
            logger.log(Level.INFO,"reading from the SQL Database");
            try {
                if (!ModSupportDb.hasTable(dbconn, "ArathoksDamageWarnings")) {
                    // table create
                     PreparedStatement ps = dbconn.prepareStatement("CREATE TABLE ArathoksDamageWarnings (itemId LONG PRIMARY KEY NOT NULL DEFAULT 0,playerId LONG NOT NULL DEFAULT 0 )");
                        ps.execute();

                }
                ToggleDamWarnPerformer.readFromDb(dbconn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finishedReadingDb=true;
        }

    }

    @Override
    public void onServerStarted() {
        ModActions.registerBehaviourProvider(new DamWarnBehaviour());
    }
}
