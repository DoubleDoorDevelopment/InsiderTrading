package net.doubledoordev.insidertrading.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import net.doubledoordev.insidertrading.util.Constants;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Dries007
 */
public class ClassTransformer implements IClassTransformer
{
    private static final Logger logger = LogManager.getLogger(Constants.MODID_ASM);

    @Override
    public byte[] transform(String s, String s1, byte[] bytes)
    {
        if (s1.equals("net.minecraft.entity.passive.EntityVillager"))
        {
            ClassReader cr = new ClassReader(bytes);
            ClassNode classNode = new ClassNode(ASM5);
            cr.accept(classNode, EXPAND_FRAMES);

            for (MethodNode methodNode : classNode.methods)
            {
                String name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
                if (name.equals("func_70950_c")) addHook(methodNode, false);
                else if (name.equals("addDefaultEquipmentAndRecipies")) addHook(methodNode, true);
            }

            ClassWriter cw = new ClassWriter(COMPUTE_MAXS);
            classNode.accept(cw);
            return cw.toByteArray();
        }
        return bytes;
    }

    private void addHook(MethodNode methodNode, boolean deob)
    {
        logger.info("Found methods, looking for insertion point...");

        ListIterator<AbstractInsnNode> i = methodNode.instructions.iterator();
        while (i.hasNext())
        {
            AbstractInsnNode abstractInsnNodenode = i.next();
            if (abstractInsnNodenode.getOpcode() == Opcodes.INVOKESTATIC)
            {
                MethodInsnNode node = ((MethodInsnNode) abstractInsnNodenode);
                if (node.name.equals("shuffle") && node.owner.equals("java/util/Collections") && node.desc.equals("(Ljava/util/List;)V"))
                {
                    InsnList list = new InsnList();

                    list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/passive/EntityVillager", deob ? "getProfession" : "func_70946_n", "()I"));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/passive/EntityVillager", deob ? "rand" : "field_70146_Z", "Ljava/util/Random;"));
                    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/doubledoordev/insidertrading/asm/Hooks", "manipulate", "(Lnet/minecraft/village/MerchantRecipeList;Lnet/minecraft/entity/passive/EntityVillager;ILjava/util/Random;)V"));

                    methodNode.instructions.insertBefore(i.previous(), list);
                    return;
                }
            }
        }

        logger.fatal("Did not found insertion point. ASM failure.");
        throw new RuntimeException();
    }
}
