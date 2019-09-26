package com.zeitheron.improvableskills.api;

import com.zeitheron.hammercore.cfg.file1132.io.ConfigEntryCategory;
import com.zeitheron.hammercore.utils.math.ExpressionEvaluator;
import com.zeitheron.hammercore.utils.math.functions.ExpressionFunction;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class SkillCostConfig extends ExpressionFunction
{
	public static final String DEF_FORMULA = "(%lvl%+1)^%xpv%";
	public String baseFormula = DEF_FORMULA, formula = DEF_FORMULA;
	public int xpValue;
	
	public String clientFormula;
	
	public SkillCostConfig(int xpValue)
	{
		super("skill");
		this.xpValue = xpValue;
	}
	
	public void load(ConfigEntryCategory cfg)
	{
		formula = cfg.getStringEntry("formula", baseFormula).setDescription("Cost calculator for this skill.\nAvailable variables:\n- %lvl% = the level we want to calculate XP value for.\n- %xpv% preset value (" + xpValue + ") for current skill.").getValue();
	}
	
	public void writeServerNBT(NBTTagCompound nbt)
	{
		if(formula != null)
			nbt.setString("Formula", formula);
	}
	
	public void readClientNBT(NBTTagCompound nbt)
	{
		resetClient();
		if(nbt.hasKey("Formula", NBT.TAG_STRING))
			clientFormula = nbt.getString("Formula");
	}
	
	public void resetClient()
	{
		clientFormula = null;
	}
	
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		if(clientFormula != null)
		{
			String formula = this.clientFormula.replaceAll("%lvl%", Short.toString(targetLvl)).replaceAll("%xpv%", Integer.toString(xpValue));
			return (int) Math.ceil(ExpressionEvaluator.evaluateDouble(formula, this));
		}
		
		if(formula != null)
		{
			String formula = this.formula.replaceAll("%lvl%", Short.toString(targetLvl)).replaceAll("%xpv%", Integer.toString(xpValue));
			return (int) Math.ceil(ExpressionEvaluator.evaluateDouble(formula, this));
		}
		
		return (int) Math.pow(targetLvl + 1, xpValue);
	}
}