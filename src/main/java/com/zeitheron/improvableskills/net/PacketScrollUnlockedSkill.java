package com.zeitheron.improvableskills.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.client.rendering.ItemToBookHandler;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTEBook;
import com.zeitheron.improvableskills.client.rendering.ote.OTEItemScroll;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketScrollUnlockedSkill implements IPacket
{
	private ResourceLocation[] skills;
	private ItemStack used;
	private int slot;
	
	static
	{
		IPacket.handle(PacketScrollUnlockedSkill.class, PacketScrollUnlockedSkill::new);
	}
	
	public PacketScrollUnlockedSkill(int slot, ItemStack used, ResourceLocation... skills)
	{
		this.skills = skills;
		this.used = used;
		this.slot = slot;
	}
	
	public PacketScrollUnlockedSkill()
	{
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList tags = new NBTTagList();
		for(ResourceLocation s : skills)
			tags.appendTag(new NBTTagString(s.toString()));
		nbt.setTag("s", tags);
		nbt.setInteger("i", slot);
		nbt.setTag("u", used.serializeNBT());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList tags = nbt.getTagList("s", NBT.TAG_STRING);
		skills = new ResourceLocation[tags.tagCount()];
		for(int i = 0; i < skills.length; ++i)
			skills[i] = new ResourceLocation(tags.getStringTagAt(i));
		slot = nbt.getInteger("i");
		used = new ItemStack(nbt.getCompoundTag("u"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		List<PlayerSkillBase> base = new ArrayList<>();
		
		for(ResourceLocation skill : skills)
		{
			PlayerSkillBase sk = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(skill);
			base.add(sk);
			Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("chat.improvableskills.page_unlocked", sk.getLocalizedName(SyncSkills.getData())));
		}
		
		Random rand = new Random();
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		Vec2f v = ItemToBookHandler.getPosOfHandSlot(slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, sr);
		OTEBook.show(100 + 10 + 40 + 10 * base.size());
		OnTopEffects.effects.add(new OTEItemScroll(v.x, v.y, sr.getScaledWidth() - 20 - 48 + rand.nextFloat() * 32, sr.getScaledHeight() - 12 - 24 - rand.nextFloat() * 32, 100, used, base.toArray(new PlayerSkillBase[0])));
		return null;
	}
}