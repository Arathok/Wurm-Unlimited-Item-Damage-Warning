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

    private final List<ActionEntry> toggleWarn;

    private final ToggleDamWarnPerformer warnPerformer;

    public DamWarnBehaviour() {
        this.warnPerformer = new ToggleDamWarnPerformer();
        this.toggleWarn = Collections.singletonList(warnPerformer.actionEntry);
        ModActions.registerActionPerformer(warnPerformer);


    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
       if (ToggleDamWarnPerformer.canUse(performer,target))
        return new ArrayList<>(toggleWarn);
        else
            return null;

    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return getBehavioursFor(performer, target);
    }
}
