package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.players.Player;
import org.arathok.wurmunlimited.mods.itemDamageWarning.questions.ClassSelectionQuestion;


public class Test {
   public static ClassSelectionQuestion cs;

   public static void testMe(Player aplayer)
   {
        cs = new ClassSelectionQuestion(aplayer);
        ClassSelectionQuestion.send(aplayer);
   }
}
