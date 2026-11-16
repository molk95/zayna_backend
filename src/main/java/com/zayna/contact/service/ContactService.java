package com.zayna.contact.service;

import com.zayna.exception.ResourceNotFoundException;
import com.zayna.contact.dto.ContactRequest;
import com.zayna.contact.dto.ContactResponse;
import com.zayna.contact.entity.ContactMessage;
import com.zayna.contact.enumeration.ContactStatus;
import com.zayna.contact.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService {

    private final ContactMessageRepository repository;

    public ContactService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContactResponse create(ContactRequest request) {
        ContactMessage entity = new ContactMessage();
        entity.setFullName(request.fullName().trim());
        entity.setEmail(request.email().trim());
        entity.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        entity.setSubject(request.subject().trim());
        entity.setMessage(request.message().trim());
        entity.setStatus(ContactStatus.NEW);
        return ContactResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> findAll(ContactStatus status) {
        List<ContactMessage> messages = status == null
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByStatusOrderByCreatedAtDesc(status);
        return messages.stream().map(ContactResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ContactResponse findById(Long id) {
        return ContactResponse.from(getOrThrow(id));
    }

    @Transactional
    public ContactResponse updateStatus(Long id, ContactStatus status) {
        ContactMessage entity = getOrThrow(id);
        entity.setStatus(status);
        return ContactResponse.from(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private ContactMessage getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message de contact invalide"));
    }
}
