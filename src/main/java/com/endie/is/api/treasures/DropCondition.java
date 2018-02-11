package com.endie.is.api.treasures;

@FunctionalInterface
public interface DropCondition
{
	boolean canDrop(TreasureContext ctx);
}