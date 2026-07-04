package com.twitch.user;

import com.twitch.model.ContactRequestDto;
import com.twitch.model.ContactResponseDto;
import com.twitch.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/requests")
    public ResponseEntity<ContactResponseDto> sendRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ContactRequestDto request
    ) {
        ContactResponseDto response = contactService.sendRequest(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{contactId}/accept")
    public ResponseEntity<ContactResponseDto> acceptRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contactId
    ) {
        ContactResponseDto response = contactService.acceptRequest(userDetails.getId(), contactId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{contactId}/reject")
    public ResponseEntity<ContactResponseDto> rejectRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contactId
    ) {
        ContactResponseDto response = contactService.rejectRequest(userDetails.getId(), contactId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDto>> getContacts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(contactService.getContacts(userDetails.getId()));
    }

    @GetMapping("/requests/received")
    public ResponseEntity<List<ContactResponseDto>> getPendingReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(contactService.getPendingReceivedRequests(userDetails.getId()));
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<List<ContactResponseDto>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(contactService.getSentRequests(userDetails.getId()));
    }
}
