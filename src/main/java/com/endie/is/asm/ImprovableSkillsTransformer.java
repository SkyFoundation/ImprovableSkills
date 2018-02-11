package com.endie.is.asm;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.endie.is.asm.TransformerSystem.iASMHook;

import net.minecraft.launchwrapper.IClassTransformer;

public class ImprovableSkillsTransformer implements IClassTransformer
{
	public static final ClassnameMap CLASS_MAPPINGS = new ClassnameMap("net/minecraft/entity/player/EntityPlayer", "aed", "net/minecraft/world/World", "amu");
	public static final TransformerSystem asm = new TransformerSystem();
	
	static
	{
		hook((node, obf) ->
		{
			MethodSignature fillWithLoot = new MethodSignature("fillWithLoot", "func_184281_d", "d", "(Lnet/minecraft/entity/player/EntityPlayer;)V");
			
			for(int i = 0; i < node.methods.size(); ++i)
			{
				MethodNode mn = node.methods.get(i);
				
				if(fillWithLoot.isThisMethod(mn))
				{
					String desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V";
					if(obf)
						desc = MethodSignature.obfuscate(desc);
					InsnList fwl = mn.instructions;
					
					MethodInsnNode nd = fwl.get(62) instanceof MethodInsnNode ? (MethodInsnNode) fwl.get(62) : null;
					
					if(node != null)
					{
						InsnList list = new InsnList();
						
						MethodInsnNode nnnd;
						list.add(new VarInsnNode(Opcodes.ALOAD, 4));
						list.add(new VarInsnNode(Opcodes.ALOAD, 1));
						String dsc = obf ? fillWithLoot.obfDesc : fillWithLoot.funcDesc;
						dsc = dsc.substring(0, dsc.length() - 2) + ")L" + nd.owner + ";";
						list.add(nnnd = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nd.owner, obf ? "a" : "withPlayer", dsc, false));
						list.add(new InsnNode(Opcodes.POP));
						fwl.insertBefore(fwl.get(64), list);
					}
					
					asm.info("Modified method 'fillWithLoot': added 'withPlayer(player)' after 'withLuck(player.getLuck())'");
				}
			}
		}, "Patching TileEntityLockableLoot", cv("net.minecraft.tileentity.TileEntityLockableLoot"));
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		return asm.transform(name, transformedName, basicClass);
	}
	
	public static Predicate<String> cv(String c)
	{
		return s -> c.equalsIgnoreCase(s);
	}
	
	public static void hook(BiConsumer<ClassNode, Boolean> handle, String desc, Predicate<String> acceptor)
	{
		asm.addHook(new iASMHook()
		{
			
			@Override
			public void transform(ClassNode node, boolean obf)
			{
				handle.accept(node, obf);
			}
			
			@Override
			public String opName()
			{
				return desc;
			}
			
			@Override
			public boolean accepts(String name)
			{
				return acceptor.test(name);
			}
		});
	}
	
	private static class MethodSignature
	{
		String funcName;
		String srgName;
		String obfName;
		String funcDesc;
		String obfDesc;
		
		public MethodSignature(String funcName, String srgName, String obfName, String funcDesc)
		{
			this.funcName = funcName;
			this.srgName = srgName;
			this.obfName = obfName;
			this.funcDesc = funcDesc;
			this.obfDesc = MethodSignature.obfuscate(funcDesc);
		}
		
		public String toString()
		{
			return "Names [" + this.funcName + ", " + this.srgName + ", " + this.obfName + "] Descriptor " + this.funcDesc + " / " + this.obfDesc;
		}
		
		private static String obfuscate(String desc)
		{
			for(String s : CLASS_MAPPINGS.keySet())
			{
				if(!desc.contains(s))
					continue;
				desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));
			}
			return desc;
		}
		
		public boolean isThisMethod(MethodNode node)
		{
			return (node.name.equals(funcName) || node.name.equals(obfName) || node.name.equals(srgName)) && (node.desc.equals(funcDesc) || node.desc.equals(obfDesc));
		}
	}
}