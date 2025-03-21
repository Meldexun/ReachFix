package meldexun.reachfix.asm;

import java.lang.reflect.Field;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.BiMap;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.HashMapClassNodeClassTransformer;
import meldexun.asmutil2.IClassTransformerRegistry;
import meldexun.asmutil2.NonLoadingClassWriter;
import meldexun.asmutil2.reader.ClassUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class ReachFixClassTransformer extends HashMapClassNodeClassTransformer implements IClassTransformer {

	private static final ClassUtil REMAPPING_CLASS_UTIL;
	static {
		try {
			Class<?> FMLDeobfuscatingRemapper = Class.forName("net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper", true, Launch.classLoader);
			Field _INSTANCE = FMLDeobfuscatingRemapper.getField("INSTANCE");
			Field _classNameBiMap = FMLDeobfuscatingRemapper.getDeclaredField("classNameBiMap");
			_classNameBiMap.setAccessible(true);
			@SuppressWarnings("unchecked")
			BiMap<String, String> deobfuscationMap = (BiMap<String, String>) _classNameBiMap.get(_INSTANCE.get(null));
			REMAPPING_CLASS_UTIL = ClassUtil.getInstance(new ClassUtil.Configuration(Launch.classLoader, deobfuscationMap.inverse(), deobfuscationMap));
		} catch (ReflectiveOperationException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	protected void registerTransformers(IClassTransformerRegistry registry) {
		// @formatter:off
		registry.addObf("net.minecraft.client.renderer.EntityRenderer", "getMouseOver", "func_78473_a", "(F)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			LabelNode label = new LabelNode();
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.FLOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/EntityRendererHook", "getMouseOver", "(F)V", false),
				new InsnNode(Opcodes.ICONST_1),
				new JumpInsnNode(Opcodes.IFEQ, label),
				new InsnNode(Opcodes.RETURN),
				label
			));
		});
		registry.addObf("net.minecraft.network.NetHandlerPlayServer", "processUseEntity", "func_147340_a", "(Lnet/minecraft/network/play/client/CPacketUseEntity;)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.first(methodNode).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/player/EntityPlayerMP", "canEntityBeSeen", "func_70685_l", "(Lnet/minecraft/entity/Entity;)Z").find();
			targetNode1 = ASMUtil.prev(methodNode, targetNode1).type(LabelNode.class).find();
			AbstractInsnNode popNode12 = ASMUtil.first(methodNode).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/player/EntityPlayerMP", "getDistanceSq", "func_70068_e", "(Lnet/minecraft/entity/Entity;)D").find();
			popNode12 = ASMUtil.next(methodNode, popNode12).type(LabelNode.class).find();

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 3),
				new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/EnumHand", "MAIN_HAND", "Lnet/minecraft/util/EnumHand;"),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "isEntityInRange", "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Z", false),
				new JumpInsnNode(Opcodes.IFNE, (LabelNode) popNode12),
				new InsnNode(Opcodes.RETURN)
			));
		});
		registry.addObf("net.minecraft.network.NetHandlerPlayServer", "processPlayerDigging", "func_147345_a", "(Lnet/minecraft/network/play/client/CPacketPlayerDigging;)V", 0, methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.first(methodNode).ldcInsn(1.5D).find();

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "getEyeHeightMinusOnePointFive", "(Lnet/minecraft/network/NetHandlerPlayServer;)D", false),
				new InsnNode(Opcodes.DADD)
			));
		});
		registry.addObf("net.minecraft.client.network.NetworkPlayerInfo", "setGameType", "func_178839_a", "(Lnet/minecraft/world/GameType;)V", 0, methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/NetworkPlayerInfoHook", "onUpdateGameMode", "(Lnet/minecraft/client/network/NetworkPlayerInfo;Lnet/minecraft/world/GameType;)V", false)
			));
		});
		registry.addObf("net.minecraft.server.management.PlayerInteractionManager", "setGameType", "func_73076_a", "(Lnet/minecraft/world/GameType;)V", 0, methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/PlayerInteractionManagerHook", "onUpdateGameMode", "(Lnet/minecraft/server/management/PlayerInteractionManager;Lnet/minecraft/world/GameType;)V", false)
			));
		});

		registry.add("com.oblivioussp.spartanweaponry.event.EventHandlerClient", "onMouseEvent", "(Lnet/minecraftforge/client/event/MouseEvent;)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			methodNode.instructions.insert(new InsnNode(Opcodes.RETURN));
		});
		// @formatter:on
	}

	@Override
	protected ClassWriter createClassWriter(int flags) {
		return new NonLoadingClassWriter(flags, REMAPPING_CLASS_UTIL);
	}

}
