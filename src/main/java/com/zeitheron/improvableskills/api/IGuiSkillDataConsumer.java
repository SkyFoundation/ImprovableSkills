package com.zeitheron.improvableskills.api;

@FunctionalInterface
public interface IGuiSkillDataConsumer
{
	void applySkillData(PlayerSkillData data);
}