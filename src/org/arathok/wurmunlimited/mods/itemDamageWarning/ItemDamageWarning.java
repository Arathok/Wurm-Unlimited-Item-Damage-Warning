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
       return false;
    }

    @Override
    public void configure(Properties properties) {

    }

    @Override
    public void onServerPoll() {

        if (!finishedReadingDb)
        {

            dbconn=ModSupportDb.getModSupportDb();
            logger.log(Level.INFO,"reading from the SQL Database");
            try {
                if (ModSupportDb.hasTable(dbconn, "ArathoksDamageWarnings")) {
                    // table create
                     PreparedStatement ps = dbconn.prepareStatement("DROP TABLE ArathoksDamageWarnings");
                        ps.execute();


                    }
                if (ModSupportDb.hasTable(dbconn, "ArathoksDamageWarningsV2")) {
                    // table create
                    PreparedStatement ps = dbconn.prepareStatement("DROP TABLE ArathoksDamageWarningsV2");
                    ps.execute();


                }
                if (!ModSupportDb.hasTable(dbconn, "ArathoksDamageWarningsV3")) {
                    // table create
                        PreparedStatement ps = dbconn.prepareStatement("CREATE TABLE ArathoksDamageWarningsV3 (itemId LONG PRIMARY KEY NOT NULL DEFAULT 0,playerId LONG NOT NULL DEFAULT 0, targetDamage REAL NOT NULL DEFAULT 0, warningType BOOLEAN NOT NULL DEFAULT 0)");
                        ps.execute();

                }
                TurnDamWarnOnPerformer.readFromDb();
                finishedReadingDb=true;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onServerStarted() {
        ModActions.registerBehaviourProvider(new DamWarnBehaviour());
    }
}
