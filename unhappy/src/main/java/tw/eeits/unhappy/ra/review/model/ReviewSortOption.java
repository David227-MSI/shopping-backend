package tw.eeits.unhappy.ra.review.model;

public enum ReviewSortOption {
    LATEST,        // createdAt DESC
    MOST_LIKED,    // helpfulCount DESC
    WITH_IMAGES    // reviewImages not blank + createdAt DESC
}
