package org.arathok.wurmunlimited.mods.itemDamageWarning.questions;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.Question;
import org.gotti.wurmunlimited.modsupport.bml.BmlBuilder;
import org.gotti.wurmunlimited.modsupport.bml.BmlNodeBuilder;
import org.gotti.wurmunlimited.modsupport.bml.TextStyle;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import java.util.Properties;

public class ClassSelectionQuestion implements ModQuestion {
    public  Player aplayer;


    public ClassSelectionQuestion(Player aplayer)
    {
        this.aplayer = aplayer;

    }
    @Override
    public void answer(Question question, Properties properties) {

        question.getResponder().getCommunicator().sendSafeServerMessage(properties.getProperty("auswahl"));


    }

    @Override
    public void sendQuestion(Question question) {

        // Modded version of BML builder
        BmlBuilder bmlBuilder = BmlBuilder.builder();                        //create a BmL Builder

        BmlNodeBuilder header = BmlBuilder.header("Warning!");
        BmlNodeBuilder label = BmlBuilder.label("Item Damage Warning!", TextStyle.BOLD);    // create the window text
        BmlNodeBuilder text = BmlBuilder.text("Your Item test is above 90 Damage and should be repaired! ");
        BmlNodeBuilder button = BmlBuilder.button("submit","confirm");
        BmlNodeBuilder radio1 = BmlBuilder.radio("links","links").withAttribute("group","auswahl"); // group decides what property group the value later is in
        BmlNodeBuilder radio2 = BmlBuilder.radio("rechts","rechts").withAttribute("group","auswahl");
        BmlNodeBuilder finish = BmlBuilder.passthough("id",Integer.toString(question.getId())); // basically closes the question and calls the answer function

        bmlBuilder.withNode(BmlBuilder.varray(true).withNode(header).withNode(label).withNode(text).withNode(BmlBuilder.harray(true).withNode(radio1).withNode(radio2)).withNode(button).withNode(finish));

        question.getResponder().getCommunicator().sendBml(300, 300, true, true, bmlBuilder.buildBml(), 255, 0, 0, question.getTitle());
        question.getResponder().getCommunicator().sendSafeServerMessage(bmlBuilder.buildBml());


    }
    public static void send(Player aplayer)
    {
        ModQuestions.createQuestion(aplayer,"Class Selection Question","which class do you want to be in your future journey?",-10L,new ClassSelectionQuestion(aplayer)).sendQuestion();
    }
}
