package org.unibl.etf.ip2024.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Integer id;
    private Integer userId;
    private String username;
    private String userImageUrl;
    private String content;
    private LocalDateTime postedAt;
}
