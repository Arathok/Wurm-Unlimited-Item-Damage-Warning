package org.arathok.wurmunlimited.mods.itemDamageWarning;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.LinkedList;

public class Hook {
    public static LinkedList<DamageWarning> giveWarning = new LinkedList<>();
    public static LinkedList<PlayerChoice> playerChoices = new LinkedList<>();




    public static void insertHook() throws NotFoundException, CannotCompileException {
        //private boolean poll(Item parent, int parentTemp, boolean insideStructure, boolean deeded, boolean saveLastMaintained, boolean inMagicContainer, boolean inTrashbin)
        ClassPool classpool = HookManager.getInstance().getClassPool();
        CtClass ctItem = classpool.getCtClass("com.wurmonline.server.items.Item");
       // ctItem.getMethod("poll", "(Lcom/wurmonline/server/items/Item;IZZZZZ)Z").insertAfter("org.arathok.wurmunlimited.mods.itemDamageWarning.Hook.insertAfter(this);");
        ctItem = classpool.getCtClass("com.wurmonline.server.items.DbItem");
        ctItem.getMethod("setDamage", "(FZ)Z").insertBefore("org.arathok.wurmunlimited.mods.itemDamageWarning.InsertStuff.insertStuff(this,$1);");
    }
}
