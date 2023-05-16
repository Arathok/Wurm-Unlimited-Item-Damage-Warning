package org.arathok.wurmunlimited.mods.itemDamageWarning;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DamWarnBehaviour implements BehaviourProvider {

    private List<ActionEntry> toggleWarnOn;
    private List<ActionEntry> toggleWarnOff;

    private final TurnDamWarnOnPerformer warnOnPerformer;
    private final TurnDamWarnOffPerformer warnOffPerformer;


    public DamWarnBehaviour() {

        this.warnOnPerformer = new TurnDamWarnOnPerformer();
        this.warnOffPerformer= new TurnDamWarnOffPerformer();
        this.toggleWarnOn = Collections.singletonList(warnOnPerformer.actionEntry);
        this.toggleWarnOff = Collections.singletonList(warnOffPerformer.actionEntry);
        ModActions.registerActionPerformer(warnOnPerformer);
        ModActions.registerActionPerformer(warnOffPerformer);


    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        boolean alreadyInList = false;
      DamageWarning aDamageWarning = null;
        for (DamageWarning anotherDamageWarning : Hook.giveWarning)
      {
          if (target.getWurmId() == anotherDamageWarning.itemId) {
              alreadyInList = true;

              break;
          }
      }

      if (alreadyInList)
      {

          return new ArrayList<>(toggleWarnOff);
      }
      else
      {

          return new ArrayList<>(toggleWarnOn);
      }

    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return getBehavioursFor(performer, target);
    }
}
