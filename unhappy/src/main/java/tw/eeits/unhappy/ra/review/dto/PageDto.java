package tw.eeits.unhappy.ra.review.dto;

import java.util.List;
import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

    private long       totalElements;
    private int        totalPages;
    private int        page;        // 0-based
    private int        size;
    private List<T>    content;

    /** 把 Spring Data 的 Page 轉成 PageDto */
    public static <T> PageDto<T> from(Page<T> p) {
        return PageDto.<T>builder()
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .page(p.getNumber())
                .size(p.getSize())
                .content(p.getContent())
                .build();
    }
}
