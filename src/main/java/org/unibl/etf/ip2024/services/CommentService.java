package org.unibl.etf.ip2024.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.response.CommentResponse;

import java.security.Principal;

@Service
public interface CommentService {
    CommentResponse addComment(Principal principal, Integer programId, String comment);
    Page<CommentResponse> getComments(Integer programId, Pageable pageable);
}
