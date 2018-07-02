package com.zeitheron.improvableskills.api.treasures;

@FunctionalInterface
public interface DropCondition
{
	boolean canDrop(TreasureContext ctx);
}