package meldexun.reachfix.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.HashMapClassNodeClassTransformer;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.launchwrapper.IClassTransformer;

public class ReachFixClassTransformer extends HashMapClassNodeClassTransformer implements IClassTransformer {

	@Override
	protected void registerTransformers(IClassTransformerRegistry registry) {
		// @formatter:off
		registry.add("net.minecraft.client.renderer.EntityRenderer", "getMouseOver", "(F)V", "a", "(F)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.FLOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/EntityRendererHook", "getMouseOver", "(F)V", false),
				new InsnNode(Opcodes.RETURN)
			));
		});
		registry.add("net.minecraft.network.NetHandlerPlayServer", "processUseEntity", "(Lnet/minecraft/network/play/client/CPacketUseEntity;)V", "a", "(Lli;)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.first(methodNode).methodInsn(Opcodes.INVOKEVIRTUAL, "oq", "D", "(Lvg;)Z", "net/minecraft/entity/player/EntityPlayerMP", "canEntityBeSeen", "(Lnet/minecraft/entity/Entity;)Z").find();
			targetNode1 = ASMUtil.prev(targetNode1).type(LabelNode.class).find();
			AbstractInsnNode popNode12 = ASMUtil.first(methodNode).methodInsn(Opcodes.INVOKEVIRTUAL, "oq", "h", "(Lvg;)D", "net/minecraft/entity/player/EntityPlayerMP", "getDistanceSq", "(Lnet/minecraft/entity/Entity;)D").find();
			popNode12 = ASMUtil.next(popNode12).type(LabelNode.class).find();

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 3),
				new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/EnumHand", "MAIN_HAND", "Lnet/minecraft/util/EnumHand;"),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "isEntityInRange", "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Z", false),
				new JumpInsnNode(Opcodes.IFNE, (LabelNode) popNode12),
				new InsnNode(Opcodes.RETURN)
			));
		});
		registry.add("net.minecraft.network.NetHandlerPlayServer", "processPlayerDigging", "(Lnet/minecraft/network/play/client/CPacketPlayerDigging;)V", "a", "(Llp;)V", 0, methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.first(methodNode).ldcInsn(1.5D).find();

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "getEyeHeightMinusOnePointFive", "(Lnet/minecraft/network/NetHandlerPlayServer;)D", false),
				new InsnNode(Opcodes.DADD)
			));
		});
		registry.add("net.minecraft.client.network.NetworkPlayerInfo", "setGameType", "(Lnet/minecraft/world/GameType;)V", "a", "(Lams;)V", 0, methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/NetworkPlayerInfoHook", "onUpdateGameMode", "(Lnet/minecraft/client/network/NetworkPlayerInfo;Lnet/minecraft/world/GameType;)V", false)
			));
		});
		registry.add("net.minecraft.server.management.PlayerInteractionManager", "setGameType", "(Lnet/minecraft/world/GameType;)V", "a", "(Lams;)V", 0, methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/PlayerInteractionManagerHook", "onUpdateGameMode", "(Lnet/minecraft/server/management/PlayerInteractionManager;Lnet/minecraft/world/GameType;)V", false)
			));
		});

		registry.add("com.oblivioussp.spartanweaponry.event.EventHandlerClient", "onMouseEvent", "(Lnet/minecraftforge/client/event/MouseEvent;)V", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			ASMUtil.LOGGER.info("Transforming method (SpartanWeaponry): EventHandlerClient#onMouseEvent(MouseEvent)");

			methodNode.instructions.insert(new InsnNode(Opcodes.RETURN));
		});
		// @formatter:on
	}

}
