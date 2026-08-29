package com.skillbridge.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private PageMetadata page;

    public static <T> PageResponse<T> of(List<T> content, PageMetadata page) {
        return PageResponse.<T>builder()
                .content(content)
                .page(page)
                .build();
    }

    public static <T> PageResponse<T> from(Page<T> springPage) {
        return PageResponse.<T>builder()
                .content(springPage.getContent())
                .page(PageMetadata.from(springPage))
                .build();
    }
}
