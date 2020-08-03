package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
public class ResolutionInitializer implements SmartInitializingSingleton {
	private final ResolutionRepository resolutions;
	private final UserRepository userRepository;

	public ResolutionInitializer(ResolutionRepository resolutions, UserRepository userRepository) {
		this.resolutions = resolutions;
		this.userRepository = userRepository;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.resolutions.save(new Resolution("Read War and Peace", "user"));
		this.resolutions.save(new Resolution("Free Solo the Eiffel Tower", "user"));
		this.resolutions.save(new Resolution("Hang Christmas Lights", "user"));

		User user = new User("user",
				"Hallo");
		user.setFullName("Us Er");
		user.grantAuthority("resolution:read");
		user.grantAuthority("user:read");
		user.grantAuthority("resolution:write");
		this.userRepository.save(user);

		User hasRead = new User("hasRead",
				"Hallo");
		hasRead.setFullName("Has Read");
		hasRead.grantAuthority("resolution:read");
		this.userRepository.save(hasRead);

		User hasWrite = new User("hasWrite",
				"{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W");
		hasWrite.setFullName("Has Write");
		hasWrite.grantAuthority("resolution:write");
		hasWrite.addFriend(hasRead);
		hasWrite.setSubscription("premium");
		this.userRepository.save(hasWrite);

		User admin = new User("admin",
				"{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W");
		admin.setFullName("Ad Min");
		admin.grantAuthority("ROLE_ADMIN");
		this.userRepository.save(admin);
	}
}
