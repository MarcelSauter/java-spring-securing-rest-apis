package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;
	private final UserRepository userRepository;

	public ResolutionController(ResolutionRepository resolutions, UserRepository userRepository) {
		this.resolutions = resolutions;
		this.userRepository = userRepository;
	}

	@CrossOrigin(allowCredentials = "true")
	@GetMapping("/resolutions")
	@PreAuthorize("hasAuthority('READ')")
	@PostFilter("@post.filter(#root)")
	public Iterable<Resolution> read() {
		Iterable<Resolution> resolutions = this.resolutions.findAll();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("user:read"))) {
			for (Resolution resolution : resolutions)  {
				String fullName = this.userRepository.findByUsername(resolution.getOwner())
						.map(User::getFullName).orElse("Anonymous");
				resolution.setText(resolution.getText() + ", by " + fullName);
			}
		}
		return resolutions;
	}

	@GetMapping("/resolution/{id}")
	@PreAuthorize("hasAuthority('READ')")
	@PostAuthorize("@post.filter(#root)")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	@PreAuthorize("hasAuthority('resolution:write')")
	public Resolution make(@CurrentUsername String owner, @RequestBody String text) {
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	// @Transactional
	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.filter(#root)")
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	// @Transactional
	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.filter(#root)")
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
