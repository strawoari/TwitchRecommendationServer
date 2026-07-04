package com.twitch.user;

import com.twitch.db.ContactRepository;
import com.twitch.db.UserRepository;
import com.twitch.db.entity.User;
import com.twitch.db.entity.UserContact;
import com.twitch.model.ContactRequestDto;
import com.twitch.model.ContactResponseDto;
import com.twitch.model.ContactStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ContactResponseDto sendRequest(Long requesterId, ContactRequestDto request) {
        Long targetId = request.targetUserId();

        if (requesterId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a contact request to yourself");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester user not found"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found"));

        if (contactRepository.findByRequesterAndTarget(requester, target).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A contact request already exists for this user");
        }

        if (contactRepository.findByRequesterAndTarget(target, requester).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user has already sent you a contact request");
        }

        UserContact contact = new UserContact();
        contact.setRequester(requester);
        contact.setTarget(target);
        contact.setStatus(ContactStatus.PENDING);
        contact.setRequesterContactInfo(request.contactInfo());
        contact.setUpdatedAt(LocalDateTime.now());

        UserContact saved = contactRepository.save(contact);
        return toResponseDto(saved);
    }

    @Transactional
    public ContactResponseDto acceptRequest(Long userId, Long contactId) {
        UserContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact request not found"));

        if (!contact.getTarget().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only accept requests sent to you");
        }

        if (contact.getStatus() != ContactStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Contact request is not in PENDING state");
        }

        contact.setStatus(ContactStatus.ACCEPTED);
        contact.setUpdatedAt(LocalDateTime.now());

        UserContact saved = contactRepository.save(contact);
        return toResponseDto(saved);
    }

    @Transactional
    public ContactResponseDto rejectRequest(Long userId, Long contactId) {
        UserContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact request not found"));

        if (!contact.getTarget().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only reject requests sent to you");
        }

        if (contact.getStatus() != ContactStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Contact request is not in PENDING state");
        }

        contact.setStatus(ContactStatus.REJECTED);
        contact.setUpdatedAt(LocalDateTime.now());

        UserContact saved = contactRepository.save(contact);
        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ContactResponseDto> getContacts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return contactRepository.findByRequesterOrTarget(user, user).stream()
                .filter(c -> c.getStatus() == ContactStatus.ACCEPTED)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponseDto> getPendingReceivedRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return contactRepository.findByTargetAndStatus(user, ContactStatus.PENDING).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponseDto> getSentRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return contactRepository.findByRequesterAndStatus(user, ContactStatus.PENDING).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private ContactResponseDto toResponseDto(UserContact contact) {
        return new ContactResponseDto(
                contact.getId(),
                contact.getRequester().getId(),
                contact.getTarget().getId(),
                contact.getStatus(),
                contact.getTargetContactInfo(),
                contact.getRequesterContactInfo(),
                contact.getUpdatedAt()
        );
    }
}
