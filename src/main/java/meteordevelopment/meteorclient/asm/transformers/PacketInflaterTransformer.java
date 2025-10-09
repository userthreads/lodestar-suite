/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.asm.transformers;

import meteordevelopment.meteorclient.asm.AsmTransformer;
import meteordevelopment.meteorclient.asm.Descriptor;
import meteordevelopment.meteorclient.asm.MethodInfo;
import org.objectweb.asm.tree.*;

// Future compatibility
// Future uses @ModifyConstant which does not chain when multiple mods do it and mixins / mixinextra can't target throw
// statements. So using a custom ASM transformer we wrap the throw statement inside another if statement.
public class PacketInflaterTransformer extends AsmTransformer {
    private final MethodInfo decodeMethod;

    public PacketInflaterTransformer() {
        super(mapClassName("net/minecraft/class_2532"));

        decodeMethod = new MethodInfo("net/minecraft/class_2532", "decode", new Descriptor("Lio/netty/channel/ChannelHandlerContext;", "Lio/netty/buffer/ByteBuf;", "Ljava/util/List;", "V"), true);
    }

    @Override
    public void transform(ClassNode klass) {
        try {
            MethodNode method = getMethod(klass, decodeMethod);
            if (method == null) {
                // Silently skip if method doesn't exist (class might not exist in this Minecraft version)
                return;
            }

            // AntiPacketKick module removed - no packet kick protection
            // This transformer is kept for future compatibility but does nothing
        } catch (Exception e) {
            // Silently handle any transformation errors
            // This prevents crashes when the target class doesn't exist or has changed
        }
    }
}
