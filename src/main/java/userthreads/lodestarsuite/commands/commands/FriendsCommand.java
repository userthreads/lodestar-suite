/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.commands.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import userthreads.lodestarsuite.commands.Command;
import userthreads.lodestarsuite.commands.arguments.FriendArgumentType;
import userthreads.lodestarsuite.commands.arguments.PlayerListEntryArgumentType;
import userthreads.lodestarsuite.systems.friends.Friend;
import userthreads.lodestarsuite.systems.friends.Friends;
import userthreads.lodestarsuite.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class FriendsCommand extends Command {
    public FriendsCommand() {
        super("friends", "Manages friends.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add")
            .then(argument("player", PlayerListEntryArgumentType.create())
                .executes(context -> {
                    GameProfile profile = PlayerListEntryArgumentType.get(context).getProfile();
                    Friend friend = new Friend(profile.getName(), profile.getId());

                    if (Friends.get().add(friend)) {
                        ChatUtils.sendMsg(friend.hashCode(), Formatting.GRAY, "Added (highlight)%s (default)to friends.".formatted(friend.getName()));
                    }
                    else error("Already friends with that player.");

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("friend", FriendArgumentType.create())
                .executes(context -> {
                    Friend friend = FriendArgumentType.get(context);
                    if (friend == null) {
                        error("Not friends with that player.");
                        return SINGLE_SUCCESS;
                    }

                    if (Friends.get().remove(friend)) {
                        ChatUtils.sendMsg(friend.hashCode(), Formatting.GRAY, "Removed (highlight)%s (default)from friends.".formatted(friend.getName()));
                    }
                    else error("Failed to remove that friend.");

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list").executes(context -> {
                info("--- Friends ((highlight)%s(default)) ---", Friends.get().count());
                Friends.get().forEach(friend -> ChatUtils.info("(highlight)%s".formatted(friend.getName())));
                return SINGLE_SUCCESS;
            })
        );
    }
}
