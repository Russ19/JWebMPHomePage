package com.jwebmp.examples.demos.homepage.display.login;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jwebmp.base.ComponentHierarchyBase;
import com.jwebmp.base.ajax.*;
import com.jwebmp.examples.demos.homepage.components.events.DefaultClick;
import com.jwebmp.examples.demos.homepage.entities.Subscribers;
import com.jwebmp.examples.demos.homepage.entities.Visitors;
import com.jwebmp.plugins.bootstrap4.modal.BSModal;

import javax.mail.MessagingException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InvalidAttributeValueException;
import java.util.Map;
import java.util.Optional;

import static com.jwebmp.utilities.StaticStrings.LOCAL_STORAGE_PARAMETER_KEY;

public class RegisterEvent
		extends DefaultClick
{
	@Inject
	@Named("LocalStorage")
	Map<String, String> localStorage;

	public RegisterEvent()
	{
		this(null);
	}

	public RegisterEvent(ComponentHierarchyBase component)
	{
		super(component);
		setID("SubmitSubscribeEvent");
		returnVariable("subscribe");
	}

	@Override
	public void onClick(AjaxCall call, AjaxResponse response)
	{
		if (localStorage.containsKey(LOCAL_STORAGE_PARAMETER_KEY))
		{
			try
			{
				Subscribers newSubs = call.getVariable("subscribe")
				                          .as(Subscribers.class);
				newSubs.isValid();

				String guid = localStorage.get(LOCAL_STORAGE_PARAMETER_KEY);
				Optional<Visitors> returningVisitor = new Visitors().builder()
				                                                    .findByGuid(guid)
				                                                    .inActiveRange()
				                                                    .get();

				Optional<Subscribers> subscriberExists = new Subscribers().builder()
				                                                          .findByEmail(newSubs.getEmailAddress())
				                                                          .inActiveRange()
				                                                          .get();
				if (subscriberExists.isPresent())
				{
					//new LogoutEvent().onClick(call, response);
					throw new InstanceAlreadyExistsException("Email Address already registered");
				}
				Optional<Subscribers> subscriber = newSubs.create(returningVisitor.get());
				if (subscriber.isPresent())
				{

					response.addReaction(new AjaxResponseReaction("Please confirm your email address",
					                                              "We've sent through a confirmation email, You'll have partial access until you verify. The link expires in 2 hours.",
					                                              ReactionType.DialogDisplay, AjaxResponseType.Success));
				}
				else
				{
					response.addReaction(new AjaxResponseReaction("There was an error creating your user",
					                                              "There was an error creating  your user, please try again or contact support on support@jwebswing.com",
					                                              ReactionType.DialogDisplay, AjaxResponseType.Success));
				}
				BSModal<?> modal = new BSModal<>();
				modal.setID("confirmPasswordModal");
				response.getFeatures()
				        .add(modal.createHideFeature());
			}
			catch (InstanceAlreadyExistsException e)
			{
				response.addReaction(new AjaxResponseReaction("Whoops", "You've already been registered. You can use the Forgot Password option to reset your password.",
				                                              ReactionType.DialogDisplay, AjaxResponseType.Danger));
			}
			catch (MessagingException e)
			{
				response.addReaction(
						new AjaxResponseReaction("Whoops", "Please make sure you've filled everything in correctly.", ReactionType.DialogDisplay, AjaxResponseType.Danger));
			}
			catch (InvalidAttributeValueException e)
			{
				response.addReaction(new AjaxResponseReaction("Whoops", e.getMessage(), ReactionType.DialogDisplay, AjaxResponseType.Danger));
			}
			finally
			{
				response.addDto("subscribe", null);
			}
		}
	}
}
