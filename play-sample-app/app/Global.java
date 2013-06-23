import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import actors.RetrieveTagsAndPhotos;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import models.SecurityRole;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.mvc.Call;

import scala.concurrent.duration.Duration;


public class Global extends GlobalSettings {

	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Application.index();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});

		initialData();
        scheduleJobs();
	}

    /*
    Schedule Actors to retrieve data in the background, asynchronously
     */
    private void scheduleJobs() {
        ActorSystem actorSystem = Akka.system();

        ActorRef retrieveTopicsActor = actorSystem.actorOf(new Props(RetrieveTagsAndPhotos.class));
        // execute every day to get new images
        actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS), Duration.create(1, TimeUnit.DAYS), retrieveTopicsActor, "select",  Akka.system().dispatcher());
    }

	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays
					.asList(controllers.Application.USER_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
	}
}