package com.zayna.contact.controller;

import com.zayna.contact.dto.ContactRequest;
import com.zayna.contact.dto.ContactResponse;
import com.zayna.contact.dto.ContactStatusRequest;
import com.zayna.contact.enumeration.ContactStatus;
import com.zayna.contact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    /** Appelé par le formulaire Flutter. */
    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest request) {
        ContactResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // Les routes ci-dessous sont destinées à l'administration (à protéger quand l'authentification existera).

    @GetMapping
    public List<ContactResponse> list(@RequestParam(required = false) ContactStatus status) {
        return service.findAll(status);
    }

    @GetMapping("/{id}")
    public ContactResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/status")
    public ContactResponse updateStatus(@PathVariable Long id, @Valid @RequestBody ContactStatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
