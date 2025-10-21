/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.tabs.builtin;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.tabs.Tab;
import userthreads.lodestarsuite.gui.tabs.TabScreen;
import userthreads.lodestarsuite.gui.tabs.WindowTabScreen;
import userthreads.lodestarsuite.gui.widgets.containers.WHorizontalList;
import userthreads.lodestarsuite.gui.widgets.containers.WTable;
import userthreads.lodestarsuite.gui.widgets.input.WTextBox;
import userthreads.lodestarsuite.gui.widgets.pressable.WMinus;
import userthreads.lodestarsuite.gui.widgets.pressable.WPlus;
import userthreads.lodestarsuite.systems.friends.Friend;
import userthreads.lodestarsuite.systems.friends.Friends;
import userthreads.lodestarsuite.utils.misc.NbtUtils;
import userthreads.lodestarsuite.utils.network.MeteorExecutor;
import net.minecraft.client.gui.screen.Screen;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public class FriendsTab extends Tab {
    public FriendsTab() {
        super("Friends");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new FriendsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof FriendsScreen;
    }

    private static class FriendsScreen extends WindowTabScreen {
        public FriendsScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            // New
            WHorizontalList list = add(theme.horizontalList()).expandX().widget();

            WTextBox nameW = list.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            nameW.setFocused(true);

            WPlus add = list.add(theme.plus()).widget();
            add.action = () -> {
                String name = nameW.get().trim();
                Friend friend = new Friend(name);

                if (Friends.get().add(friend)) {
                    nameW.set("");
                    reload();

                    MeteorExecutor.execute(() -> {
                        friend.updateInfo();
                        mc.execute(this::reload);
                    });
                }
            };

            enterAction = add.action;
        }

        private void initTable(WTable table) {
            table.clear();
            if (Friends.get().isEmpty()) return;

            Friends.get().forEach(friend ->
                MeteorExecutor.execute(() -> {
                    if (friend.headTextureNeedsUpdate()) {
                        friend.updateInfo();
                    }
                })
            );

            for (Friend friend : Friends.get()) {
                table.add(theme.texture(32, 32, friend.getHead().needsRotate() ? 90 : 0, friend.getHead()));
                table.add(theme.label(friend.getName()));

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);
                    reload();
                };

                table.row();
            }
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Friends.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(Friends.get());
        }
    }
}
