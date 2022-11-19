package meldexun.reachfix.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil.ASMUtil;
import meldexun.asmutil.transformer.clazz.AbstractClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class ReachFixClassTransformer extends AbstractClassTransformer implements IClassTransformer {

	@Override
	protected void registerTransformers() {
		// @formatter:off
		this.registerMethodTransformer("buq", "a", "(F)V", "net/minecraft/client/renderer/EntityRenderer", "getMouseOver", "(F)V", methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.FLOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/EntityRendererHook", "getMouseOver", "(F)V", false),
				new InsnNode(Opcodes.RETURN)
			));
		});
		this.registerMethodTransformer("pa", "a", "(Lli;)V", "net/minecraft/network/NetHandlerPlayServer", "processUseEntity", "(Lnet/minecraft/network/play/client/CPacketUseEntity;)V", methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.findFirstMethodCall(methodNode, Opcodes.INVOKEVIRTUAL, "oq", "D", "(Lvg;)Z", "net/minecraft/entity/player/EntityPlayerMP", "canEntityBeSeen", "(Lnet/minecraft/entity/Entity;)Z");
			targetNode1 = ASMUtil.findLastInsnByType(methodNode, AbstractInsnNode.LABEL, targetNode1);
			AbstractInsnNode popNode12 = ASMUtil.findFirstMethodCall(methodNode, Opcodes.INVOKEVIRTUAL, "oq", "h", "(Lvg;)D", "net/minecraft/entity/player/EntityPlayerMP", "getDistanceSq", "(Lnet/minecraft/entity/Entity;)D");
			popNode12 = ASMUtil.findFirstInsnByType(methodNode, AbstractInsnNode.LABEL, popNode12);

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 3),
				new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/EnumHand", "MAIN_HAND", "Lnet/minecraft/util/EnumHand;"),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "isEntityInRange", "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Z", false),
				new JumpInsnNode(Opcodes.IFNE, (LabelNode) popNode12),
				new InsnNode(Opcodes.RETURN)
			));
		});
		this.registerMethodTransformer("pa", "a", "(Llp;)V", "net/minecraft/network/NetHandlerPlayServer", "processPlayerDigging", "(Lnet/minecraft/network/play/client/CPacketPlayerDigging;)V", methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.findFirstInsnByType(methodNode, AbstractInsnNode.LDC_INSN);
			while (!((LdcInsnNode) targetNode1).cst.equals(1.5D)) {
				targetNode1 = ASMUtil.findFirstInsnByType(methodNode, AbstractInsnNode.LDC_INSN, targetNode1);
			}

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/NetHandlerPlayServerHook", "getEyeHeightMinusOnePointFive", "(Lnet/minecraft/network/NetHandlerPlayServer;)D", false),
				new InsnNode(Opcodes.DADD)
			));
		});
		this.registerMethodTransformer("bsc", "a", "(Lams;)V", "net/minecraft/client/network/NetworkPlayerInfo", "setGameType", "(Lnet/minecraft/world/GameType;)V", methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/client/NetworkPlayerInfoHook", "onUpdateGameMode", "(Lnet/minecraft/client/network/NetworkPlayerInfo;Lnet/minecraft/world/GameType;)V", false)
			));
		});
		this.registerMethodTransformer("or", "a", "(Lams;)V", "net/minecraft/server/management/PlayerInteractionManager", "setGameType", "(Lnet/minecraft/world/GameType;)V", methodNode -> {
			methodNode.instructions.insert(ASMUtil.listOf(
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/hook/PlayerInteractionManagerHook", "onUpdateGameMode", "(Lnet/minecraft/server/management/PlayerInteractionManager;Lnet/minecraft/world/GameType;)V", false)
			));
		});

		this.registerMethodTransformer("?", "?", "?", "com/oblivioussp/spartanweaponry/event/EventHandlerClient", "onMouseEvent", "(Lnet/minecraftforge/client/event/MouseEvent;)V", methodNode -> {
			ASMUtil.LOGGER.info("Transforming method (SpartanWeaponry): EventHandlerClient#onMouseEvent(MouseEvent)");

			methodNode.instructions.insert(new InsnNode(Opcodes.RETURN));
		});

		this.registerMethodTransformer("?", "?", "?", "com/mujmajnkraft/bettersurvival/client/ModClientHandler", "onEvent", "(Lnet/minecraftforge/fml/common/gameevent/InputEvent;)V", methodNode -> {
			ASMUtil.LOGGER.info("Transforming method (BetterSurvival): ModClientHandler#onEvent(InputEvent)");

			methodNode.instructions.insert(new InsnNode(Opcodes.RETURN));
		});
		// @formatter:on
	}

}
