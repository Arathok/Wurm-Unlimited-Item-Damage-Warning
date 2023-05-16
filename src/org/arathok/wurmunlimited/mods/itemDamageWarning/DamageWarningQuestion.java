package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.questions.Question;
import org.gotti.wurmunlimited.modsupport.bml.BmlBuilder;
import org.gotti.wurmunlimited.modsupport.bml.BmlNodeBuilder;
import org.gotti.wurmunlimited.modsupport.bml.TextStyle;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class DamageWarningQuestion implements ModQuestion {
    public long playerId=0;
    public long itemId=0;
    public float previousDamageSetting=0;
    public boolean  previousWarningType=false;


    public DamageWarningQuestion(DamageWarning damageWarning)
    {
        this.itemId=damageWarning.itemId;
        this.playerId=damageWarning.playerId;
        this.previousDamageSetting=damageWarning.previousDamageSetting;
        this.previousWarningType= damageWarning.previousWarningType;
    }

    @Override
    public void answer(Question question, Properties properties ) {


        float targetDamage = Float.parseFloat(properties.getProperty("setDamageInput"));
        boolean warningType = Boolean.parseBoolean(properties.getProperty("warningTypeWindow"));
        previousDamageSetting=targetDamage;
        previousWarningType=warningType;
        DamageWarning aDamageWarning = new DamageWarning(itemId,playerId,targetDamage,warningType,previousDamageSetting,previousWarningType);
        //PlayerChoice aPlayerChoice = new PlayerChoice(previousDamageSetting,previousWarningType,playerId);
        if (targetDamage<99.0F) {
            Hook.giveWarning.add(aDamageWarning);
            //TODO:check if player made achoice already before

          //  Hook.playerChoices.add(aPlayerChoice);
            try {
                TurnDamWarnOnPerformer.insert(aDamageWarning);
                question.getResponder().getCommunicator().sendSafeServerMessage("You make a mental note to not let this item get damaged too much.");
            } catch (SQLException e) {
                ItemDamageWarning.logger.log(Level.WARNING, "Could not insert into DB!", e);
                e.printStackTrace();
            }
        }
        else
            question.getResponder().getCommunicator().sendAlertServerMessage("You have to set a damage Value between 1 and 99!");
    }

    @Override
    public void sendQuestion(Question question) {
        BmlBuilder messageBuilder=BmlBuilder.builder();
        BmlNodeBuilder header = BmlBuilder.header("Damage Warning Reminder Setup");
        BmlNodeBuilder label = BmlBuilder.label("Please select how you wanted to be reminded of the item being critically damaged below!", TextStyle.BOLD);    // create the window text
        BmlNodeBuilder setDamageText = BmlBuilder.text("Send warning at a damage Value above:");
        BmlNodeBuilder setDamageInput = BmlBuilder.input("setDamageInput").withAttribute("text",Float.toString(previousDamageSetting));
        BmlNodeBuilder reminderTypeText = BmlBuilder.text("how do you want to be reminded? Normally a Chat message is sent and a sound warning can be heard, but you can also additionally get a red glaring window! ");
        BmlNodeBuilder checkboxGlaringWindow = BmlBuilder.checkbox("warningTypeWindow","I want a red glaring window!").withAttribute("selected",Boolean.toString(previousWarningType));
        BmlNodeBuilder finishButton = BmlBuilder.button("confirm","Send");
        BmlNodeBuilder finish = BmlBuilder.passthough("id",Integer.toString(question.getId())); // basically closes the question and calls the answer function




        messageBuilder.withNode(BmlBuilder.scroll(false,true,
                (BmlBuilder.varray(true).withNode(header).withNode(label).withNode(BmlBuilder.text(""))
                        .withNode(setDamageText)
                        .withNode(BmlBuilder.text(""))
                        .withNode(BmlBuilder.harray()
                            .withNode(setDamageInput)
                            .withNode(BmlBuilder.text(""))
                        )
                        .withNode(BmlBuilder.text(""))
                        .withNode(reminderTypeText)
                        .withNode(BmlBuilder.text(""))
                        .withNode(BmlBuilder.harray(false)

                            .withNode(checkboxGlaringWindow)
                        )
                        .withNode(BmlBuilder.text(""))
                        .withNode(BmlBuilder.harray(false)

                            .withNode(finishButton)
                            .withNode(BmlBuilder.text(""))
                        )
                        .withNode(finish))));


        question.getResponder().getCommunicator().sendBml(520, 270, true, true, messageBuilder.buildBml(), 255, 255, 255, question.getTitle());

    }
    public static void send(DamageWarning damageWarning) throws NoSuchPlayerException {
        ModQuestions.createQuestion(Players.getInstance().getPlayer(damageWarning.playerId),"Damage Warning Toggle Question","Please set up your Damage Warning for your item",-10L,new DamageWarningQuestion(damageWarning)).sendQuestion();
    }
}
