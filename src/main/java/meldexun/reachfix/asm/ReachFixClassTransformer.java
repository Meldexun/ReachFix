package meldexun.reachfix.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
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
			AbstractInsnNode popNode1 = new LabelNode();

			methodNode.instructions.insert(ASMUtil.listOf(
				new FieldInsnNode(Opcodes.GETSTATIC, "meldexun/reachfix/config/ReachFixConfig", "enabled", "Z"),
				new JumpInsnNode(Opcodes.IFEQ, (LabelNode) popNode1),
				new VarInsnNode(Opcodes.FLOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/asm/hook/client/EntityRendererHook", "getMouseOver", "(F)V", false),
				new InsnNode(Opcodes.RETURN),
				popNode1
			));
		});
		this.registerMethodTransformer("pa", "a", "(Lli;)V", "net/minecraft/network/NetHandlerPlayServer", "processUseEntity", "(Lnet/minecraft/network/play/client/CPacketUseEntity;)V", methodNode -> {
			AbstractInsnNode targetNode1 = ASMUtil.findFirstMethodCall(methodNode, Opcodes.INVOKEVIRTUAL, "oq", "D", "(Lvg;)Z", "net/minecraft/entity/player/EntityPlayerMP", "canEntityBeSeen", "(Lnet/minecraft/entity/Entity;)Z");
			targetNode1 = ASMUtil.findLastInsnByType(methodNode, AbstractInsnNode.LABEL, targetNode1);
			AbstractInsnNode popNode11 = new LabelNode();
			AbstractInsnNode popNode12 = ASMUtil.findFirstMethodCall(methodNode, Opcodes.INVOKEVIRTUAL, "oq", "h", "(Lvg;)D", "net/minecraft/entity/player/EntityPlayerMP", "getDistanceSq", "(Lnet/minecraft/entity/Entity;)D");
			popNode12 = ASMUtil.findFirstInsnByType(methodNode, AbstractInsnNode.LABEL, popNode12);

			methodNode.instructions.insert(targetNode1, ASMUtil.listOf(
				new FieldInsnNode(Opcodes.GETSTATIC, "meldexun/reachfix/config/ReachFixConfig", "enabled", "Z"),
				new JumpInsnNode(Opcodes.IFEQ, (LabelNode) popNode11),
				new VarInsnNode(Opcodes.ALOAD, 0),
				new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", "player", "Lnet/minecraft/entity/player/EntityPlayerMP;"),
				new VarInsnNode(Opcodes.ALOAD, 3),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "meldexun/reachfix/asm/hook/NetHandlerPlayServerHook", "isEntityInRange", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/entity/Entity;)Z", false),
				new JumpInsnNode(Opcodes.IFNE, (LabelNode) popNode12),
				new InsnNode(Opcodes.RETURN),
				popNode11
			));
		});
		// @formatter:on
	}

}
