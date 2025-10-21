/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.utils.entity;

import userthreads.lodestarsuite.systems.friends.Friends;
import userthreads.lodestarsuite.utils.Utils;
import userthreads.lodestarsuite.utils.entity.fakeplayer.FakePlayerEntity;
import userthreads.lodestarsuite.utils.entity.fakeplayer.FakePlayerManager;
import userthreads.lodestarsuite.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public class TargetUtils {
    private static final List<Entity> ENTITIES = new ArrayList<>();

    private TargetUtils() {
    }

    @Nullable
    public static Entity get(Predicate<Entity> isGood, SortPriority sortPriority) {
        ENTITIES.clear();
        getList(ENTITIES, isGood, sortPriority, 1);
        if (!ENTITIES.isEmpty()) {
            return ENTITIES.getFirst();
        }

        return null;
    }

    public static void getList(List<Entity> targetList, Predicate<Entity> isGood, SortPriority sortPriority, int maxCount) {
        targetList.clear();

        for (Entity entity : mc.world.getEntities()) {
            if (entity != null && isGood.test(entity)) targetList.add(entity);
        }

        FakePlayerManager.forEach(fp -> {
            if (fp != null && isGood.test(fp)) targetList.add(fp);
        });

        targetList.sort(sortPriority);
        // fast list trimming
        if (targetList.size() > maxCount) {
            targetList.subList(maxCount, targetList.size()).clear();
        }
    }

    @Nullable
    public static PlayerEntity getPlayerTarget(double range, SortPriority priority) {
        if (!Utils.canUpdate()) return null;
        return (PlayerEntity) get(entity -> {
            if (!(entity instanceof PlayerEntity) || entity == mc.player) return false;
            if (((PlayerEntity) entity).isDead() || ((PlayerEntity) entity).getHealth() <= 0) return false;
            if (!PlayerUtils.isWithin(entity, range)) return false;
            if (!Friends.get().shouldAttack((PlayerEntity) entity)) return false;
            return EntityUtils.getGameMode((PlayerEntity) entity) == GameMode.SURVIVAL || entity instanceof FakePlayerEntity;
        }, priority);
    }

    public static boolean isBadTarget(PlayerEntity target, double range) {
        if (target == null) return true;
        return !PlayerUtils.isWithin(target, range) || !target.isAlive() || target.isDead() || target.getHealth() <= 0;
    }
}
