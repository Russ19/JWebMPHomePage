package com.jwebmp.examples.demos.homepage.entities.builders;

import com.google.inject.Key;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import com.jwebmp.examples.demos.homepage.db.HomePageDB;
import com.jwebmp.examples.demos.homepage.entities.UserActivity;

import javax.persistence.EntityManager;

public class UserActivityBuilder extends QueryBuilderCore<UserActivityBuilder, UserActivity, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(Key.get(EntityManager.class, HomePageDB.class));
	}

	@Override
	protected void onCreate(UserActivity entity)
	{

	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}
}
